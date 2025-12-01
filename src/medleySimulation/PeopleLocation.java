// Class for storing  locations of people (swimmers only for now, but could add other types) in the simulation

package medleySimulation;

import java.awt.Color;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PeopleLocation  { // this is made a separate class so don't have to access thread
	
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	private Lock lock = new ReentrantLock();  // ReentrantLock for mutual exclusion

	private boolean inStadium; //are they here?
	private boolean arrived; //have they arrived at the event?
	private GridBlock location; //which GridBlock are they on?
	private boolean occupied;
	//constructor
	PeopleLocation(int ID , Color c) {
		myColor = c;
		inStadium = false; //not in pool
		arrived = false; //have not arrived
		this.ID=ID;
	  	occupied=false;
	}
	
	public boolean occupy() {
        lock.lock();
        try {
            if (!occupied) {
                occupied = true;
                return true;
            }
            return false;  // Block is already occupied
        } finally {
            lock.unlock();
        }
    }

	public void leave() {
        lock.lock();
        try {
            occupied = false;
        } finally {
            lock.unlock();
        }
    }

	//setter
	public  void setInStadium(boolean in) {
		inStadium = in;
	}
	
	//getter and setter
	public boolean getArrived() {
		return arrived;
	}
	public void setArrived() {
		arrived=true;
	}

//getter and setter
	public GridBlock getLocation() {
		return location;
	}
	public  void setLocation(GridBlock location) {
		this.location = location;
	}

	//getter
	public  int getX() { return location.getX();}	
	
	//getter
	public  int getY() {	return location.getY();	}
	
	//getter
	public  int getID() {	return ID;	}

	//getter
	public  boolean inPool() {
		return inStadium;
	}
	//getter and setter
	public  Color getColor() { return myColor; }
	public  void setColor(Color myColor) { this.myColor= myColor; }
}
