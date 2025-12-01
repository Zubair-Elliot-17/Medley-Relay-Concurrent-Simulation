// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GridBlock {
	
	private int isOccupied; 
	
	private final boolean isStart;  //is this a starting block?
	private int [] coords; // the coordinate of the block.
	private final Lock lock; // to control access to the blocks by threads

	GridBlock(boolean startBlock) throws InterruptedException {
		isStart=startBlock;
		isOccupied= -1;
		this.lock = new ReentrantLock(); // ensures only one swimmer can occupy a block at a time
	}
	
	GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
		this(startBlock);
		coords = new int [] {x,y};
	}
	
	public int getX() {return coords[0];}  
	public int getY() {return coords[1];}
	
	public  boolean isStart() {
		return isStart;	
	}

	public boolean get(int threadID) throws InterruptedException {
		lock.lock(); 
		try {
			if (isOccupied == threadID) {
				return true;  
			}
			if (isOccupied >= 0) {
				return false;  
			}
			isOccupied = threadID;  
			return true;
		} finally {
			lock.unlock(); 
		}
	}

	
	public void release() {
		lock.lock(); 
		try {
			isOccupied = -1; 
		} finally {
			lock.unlock(); 
		}
	}

	public boolean occupied() {
		lock.lock(); 
		try {
			return isOccupied != -1;  
		} finally {
			lock.unlock(); 
		}
	}

}
