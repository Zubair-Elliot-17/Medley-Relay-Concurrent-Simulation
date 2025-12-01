//Class to represent a swimmer swimming a race
//Swimmers have one of four possible swim strokes: backstroke, breaststroke, butterfly and freestyle
package medleySimulation;

import java.awt.Color;
import java.util.concurrent.BrokenBarrierException;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Swimmer extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private FinishCounter finish; //shared

	private final Lock lock = new ReentrantLock(); // Example
    private static final Condition[] conditions = new Condition[4];

	private   Semaphore semaphore; 
	private CyclicBarrier barrier;
	private static int count=0;

	private static int currentSwimmer = 0;
    private static final int numSwimmers = 4;  // Example relay team of 4

	GridBlock currentBlock;
	private Random rand;
	private int movingSpeed;
	
	private PeopleLocation myLocation;
	public int ID; //thread ID 
	private int team; // team ID
	private GridBlock start;

	public int getID(){
		return ID;
	}

	public enum SwimStroke { 
		Backstroke(1,2.5,Color.black),
		Breaststroke(2,2.1,new Color(255,102,0)),
		Butterfly(3,2.55,Color.magenta),
		Freestyle(4,2.8,Color.red);
	    	
	     private final double strokeTime;
	     private final int order; // in minutes
	     private final Color colour;   

	     SwimStroke( int order, double sT, Color c) {
	            this.strokeTime = sT;
	            this.order = order;
	            this.colour = c;
	        }
	  
	        public int getOrder() {return order;}

	        public  Color getColour() { return colour; }
	    }  
	    private final SwimStroke swimStroke;
	
	//Constructor
	Swimmer( int ID, int t, PeopleLocation loc, FinishCounter f, int speed, SwimStroke s , Semaphore semaphore_ , CyclicBarrier barrier  ) {
		this.swimStroke = s;
		this.ID=ID;
		movingSpeed=speed; //range of speeds for swimmers
		this.myLocation = loc;
		this.team=t;
		start = stadium.returnStartingBlock(team);
		finish=f;
		rand=new Random();
		this.semaphore=semaphore_;
		this.barrier=barrier;
	}
	
	
	//getter
	public   int getX() { return currentBlock.getX();}	
	
	//getter
	public   int getY() {	return currentBlock.getY();	}
	
	//getter
	public   int getSpeed() { return movingSpeed; }

	
	public SwimStroke getSwimStroke() {
		return swimStroke;
	}

	//!!!You do not need to change the method below!!!
	//swimmer enters stadium area
	public void enterStadium() throws InterruptedException {
		currentBlock = stadium.enterStadium(myLocation);  //
		sleep(200);  //wait a bit at door, look around
	}
	
	//!!!You do not need to change the method below!!!
	//go to the starting blocks
	//printlns are left here for help in debugging
	public void goToStartingBlocks() throws InterruptedException {		
		int x_st= start.getX();
		int y_st= start.getY();
	//System.out.println("Thread "+this.ID + " has start position: " + x_st  + " " +y_st );
	// System.out.println("Thread "+this.ID + " at " + currentBlock.getX()  + " " +currentBlock.getY() );
	 while (currentBlock!=start) {
		//	System.out.println("Thread "+this.ID + " has starting position: " + x_st  + " " +y_st );
		//	System.out.println("Thread "+this.ID + " at position: " + currentBlock.getX()  + " " +currentBlock.getY() );
			
			sleep(movingSpeed*3);  //not rushing 
			currentBlock=stadium.moveTowards(currentBlock,x_st,y_st,myLocation); //head toward starting block
		//	System.out.println("Thread "+this.ID + " moved toward start to position: " + currentBlock.getX()  + " " +currentBlock.getY() );
		}
	//System.out.println("-----------Thread "+this.ID + " at start " + currentBlock.getX()  + " " +currentBlock.getY() );
	}
	
	//!!!You do not need to change the method below!!!
	//dive in to the pool
	private void dive() throws InterruptedException {
		int x= currentBlock.getX();
		int y= currentBlock.getY();
		currentBlock=stadium.jumpTo(currentBlock,x,y-2,myLocation);
	}
	
	//!!!You do not need to change the method below!!!
	//swim there and back
	private void swimRace() throws InterruptedException {
		int x= currentBlock.getX();
		while((boolean) ((currentBlock.getY())!=0)) {
			currentBlock=stadium.moveTowards(currentBlock,x,0,myLocation);
			//System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX()  + " " +currentBlock.getY() );
			sleep((int) (movingSpeed*swimStroke.strokeTime)); //swim
			//System.out.println("Thread "+this.ID + " swimming  at speed" + movingSpeed );	
		}

		while((boolean) ((currentBlock.getY())!=(StadiumGrid.start_y-1))) {
			currentBlock=stadium.moveTowards(currentBlock,x,StadiumGrid.start_y,myLocation);
			//System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX()  + " " +currentBlock.getY() );
			sleep((int) (movingSpeed*swimStroke.strokeTime));  //swim
		}
		
	}
	
	//!!!You do not need to change the method below!!!
	//after finished the race
	public void exitPool() throws InterruptedException {		
		int bench=stadium.getMaxY()-swimStroke.getOrder(); 			 //they line up
		int lane = currentBlock.getX()+1;//slightly offset
		currentBlock=stadium.moveTowards(currentBlock,lane,currentBlock.getY(),myLocation);
	   while (currentBlock.getY()!=bench) {
		 	currentBlock=stadium.moveTowards(currentBlock,lane,bench,myLocation);
			sleep(movingSpeed*3);  //not rushing 
		}
	}
	
	private static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }


	public void run() {
		try {
			myLocation.setArrived(); 
		
			enterStadium();
			goToStartingBlocks();  
		

			while (Helper.isThreadComplete()==false ){
				Thread.sleep(100);
			}

			GridBlock block = myLocation.getLocation();  
			while (!block.get(ID)) { 
				Thread.sleep(100);  
			}
			
			
			semaphore.acquire(); 
			dive();
			swimRace();
			semaphore.release();  

			block.release();  
		

			if (swimStroke.order == 4 && team!=0) {
				finish.finishRace(ID, team); 
				System.out.println(getCurrentTime());
			} else {
				exitPool();  
			}
	
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
		}
	}
}	