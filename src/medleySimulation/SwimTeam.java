//Class to represent a swim team - which has four swimmers
package medleySimulation;
import java.util.concurrent.Semaphore;
import medleySimulation.Swimmer.SwimStroke;
import java.util.concurrent.CyclicBarrier;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number 
	private Semaphore semaphore; 
	
	public static final int sizeOfTeam=4;
	private CyclicBarrier barrier;


	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr ,  Semaphore semaphore_ , CyclicBarrier barrier  ) {
		this.teamNo=ID;
		this.semaphore=semaphore_;
		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);
		this.barrier=barrier;
	
		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds 
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s] , semaphore , barrier  ); //hardcoded speed for now
		}
	}
	
	private static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }
	
	public void run() {
    	try {    
       

		System.out.println(getCurrentTime());

		for (int i = 0; i < sizeOfTeam; i++) {
            swimmers[i].start();  // Start swimmer thread
            System.out.println("Swimmer " + swimmers[i].ID + " started.");
            Thread.sleep(1000);  // Wait for 1 second before starting the next swimmer
        }
				
		Thread.sleep(69751);
		Helper.setThreadComplete(true); 

        // Then wait for each swimmer thread to finish
        	for (int s = 0; s < sizeOfTeam; s++) {
            	swimmers[s].join();  // Wait for each thread to finish
        	}

    	} catch (InterruptedException e) {
        	e.printStackTrace();
    	}
	}	

}
	

