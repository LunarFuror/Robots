import java.util.Arrays;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.Color;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * @author Grayson Lorenz
 */

public class CaptureFlag2 {
	private final TouchSensor tFront = new TouchSensor(SensorPort.S1);
	private final ColorSensor cRight = new ColorSensor(SensorPort.S3);
	private final ColorSensor cLeft = new ColorSensor(SensorPort.S2);
	private final TouchSensor tFlag = new TouchSensor(SensorPort.S4);
	private final static DifferentialPilot p = new DifferentialPilot(1.70079, 4.75, Motor.A, Motor.C);
	
	private int black; // used for coordinate tracking
	private int toFlag; // used to back up into the center of the squard in getFlag()
	private boolean rFirst; // used in the square() method that doesnt work
	private boolean lFirst; // same thing
	private boolean squared; // also same thing
	int[] coord = new int[2]; // coordinates
	int nBound; // boundaries declared later, can be changed to fit any size board (12 inch square tiles, black with white border)
	int wBound; // boundaries declared later, can be changed to fit any size board (12 inch square tiles, black with white border)
	int sBound; // boundaries declared later, can be changed to fit any size board (12 inch square tiles, black with white border)
	int eBound; // boundaries declared later, can be changed to fit any size board (12 inch square tiles, black with white border)
	String facing; // facing used for turning and moving later
	boolean hasFlag; // if the robot has the flag this is true
	boolean northContact; // if north was blocked
	boolean eastContact; // if east was blocked
	boolean southContact; // if south was blocked
	boolean westContact; // if west was blocked
	boolean white; // used for coordinate tracking
		
	/**
	 * constructor
	 */
	public CaptureFlag2(){
		p.setTravelSpeed(9.5);
		p.setAcceleration((int) (4*p.getTravelSpeed()));
		coord[0] = 1; 
		coord[1] = 1;
		nBound = 7;
		wBound = 6;
		sBound = 1;
		eBound = 1;
		facing = "north";
		hasFlag = false;
		northContact = false;
		eastContact = false;
		southContact = false;
		westContact = false;
		white = false;
		black = 0;
		toFlag = 0;
		rFirst = false;
		lFirst = false;
		squared = false;
	}
	
	/**
	 * turns north and moves a specified distance while keeping track of where it is on the grid.
	 * @param dist Distance in inches you want the robot to move
	 */
	public void moveNorth(int dist){
			face("north");

			p.travel(dist,true);
			while(p.isMoving())
			{
				coordCount();
				if(tFront.isPressed()){
					p.stop();
					p.travel(-3);
					northContact = true;
					return;
				}
				if(coord[0]==nBound){
					p.stop();
					p.travel(6);
					northContact = true;
					eastContact = false;
					return;
				}
			}	
			eastContact = false;
		}
	/**
	 * turns east and moves a specified distance while keeping track of where it is on the grid.
	 * @param dist Distance in inches you want the robot to move
	 */
	public void moveEast(int dist){
		face("east");
		
		p.travel(dist,true);
		while(p.isMoving())
		{
			coordCount();
			if(tFront.isPressed()){
				p.stop();
				p.travel(-3);
				eastContact = true;
				return;
			}
			if(coord[1]==eBound){
				p.stop();
				p.travel(6);
				southContact = false;
				eastContact = true;
				return;
			}
		}	
		southContact = false;
	}
	/**
	 * turns south and moves a specified distance while keeping track of where it is on the grid.
	 * @param dist Distance in inches you want the robot to move
	 */
	public void moveSouth(int dist){
		face("south");
		
		p.travel(dist,true);
		while(p.isMoving())
		{
			coordCount();
			if(tFront.isPressed()){
				p.stop();
				p.travel(-3);
				southContact = true;
				return;
			}
			if(coord[0]==sBound){
				p.stop();
				p.travel(6);
				westContact = false;
				southContact = true;
				return;
			}
		}	
		westContact = false;
	}
	/**
	 * turns west and moves a specified distance while keeping track of where it is on the grid.
	 * @param dist Distance in inches you want the robot to move
	 */
	public void moveWest(int dist){
		face("west");
		
		p.travel(dist,true);
		while(p.isMoving())
		{
			coordCount();
			if(tFront.isPressed()){
				p.stop();
				p.travel(-3);
				westContact = true;
				return;
			}
			if(coord[1]==wBound){
				p.stop();
				p.travel(6);
				northContact = false;
				westContact = true;
				return;
			}
		}	
		northContact = false;
	}
    /**
     * Was supposed to square up but doesn't work, this isnt implimented feel free to tweak to get working. Was intended to fit anywhere.
     */
	public void square(){
		System.out.println("Squaring...");
		p.travel(8,true);
		
		while(p.isMoving())
		{
			if(cLeft.getColor().getGreen()>180 && cLeft.getColor().getBlue()>180 && cLeft.getColor().getRed()>180)
			{
				p.stop();
				lFirst = true;
			}
			if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
			{
				p.stop();
				rFirst = true;
			}
		}
		
		if(lFirst){
			Motor.C.forward();
			if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
				{Motor.C.stop(); p.reset(); p.travel(-3);}
		}
		if(rFirst){
			Motor.A.forward();
			if(cLeft.getColor().getGreen()>180 && cLeft.getColor().getBlue()>180 && cLeft.getColor().getRed()>180)
				{Motor.A.stop(); p.reset(); p.travel(-3);}
		}
		p.reset();
		lFirst = false;
		rFirst = false;
		p.travel(-6);
	}
	/**
	 * Used to tell the robot which direction to face, based on it's current facing.
	 * @param direction This is the direction you want it to face. When the program is run, robot is considered facing north, everything else is based on inital facing.
	 * @param direction north, south, east, west
	 */
	public void face(String direction){
		switch(facing){
			case "north":
				switch(direction){
					case "north": facing="north"; break;
					case "south": facing="south"; p.rotate(180); p.travel(-3); break;
					case "east": facing="east"; p.rotate(-90); p.travel(-3); break;
					case "west": facing="west"; p.rotate(90); p.travel(-3); break;
				}
				break;
				
			case "south":
				switch(direction){
					case "north": facing="north"; p.rotate(180); p.travel(-3); break;
					case "south": facing="south"; break;
					case "east": facing="east"; p.rotate(90); p.travel(-3); break;
					case "west": facing="west"; p.rotate(-90); p.travel(-3); break;
				}
				break;
				
			case "east":
				switch(direction){
					case "north": facing="north"; p.rotate(90); p.travel(-3); break;
					case "south": facing="south"; p.rotate(-90); p.travel(-3); break;
					case "east": facing="east"; break;
					case "west": facing="west"; p.rotate(180); p.travel(-3); break;
				}
				break;
				
			case "west":
				switch(direction){
					case "north": facing="north"; p.rotate(-90); p.travel(-3); break;
					case "south": facing="south"; p.rotate(90); p.travel(-3); break;
					case "east": facing="east"; p.rotate(180); p.travel(-3); break;
					case "west": facing="west"; break;
				}
				break;
				
			default: break;
		}
	}
	/**
	 * This is the method where all logic for getting to the flag is. It includes how to avoid obstacles,
	 * checking if at boundaries, and an algorithm to get the flag at the end. Shows facing and coordinates every move.
	 */
	public void getFlag(){
		while(!tFlag.isPressed())
		{
			// prioritizes north untill at the north boundary
			System.out.println(Arrays.toString(coord) + facing);
			if(coord[0] != nBound){
				if(!northContact)
					moveNorth(1000);
				else if (!westContact && coord[1] != wBound)
					moveWest(15);
				else if (!southContact && coord[0] != sBound)
					moveSouth(15);
				else if (!eastContact && coord[1] != eBound)
					moveEast(15);
			}
			//once north boundary is hit prioritizes moving west to the opposite corner
			else if(coord[1] != wBound){
				if(!westContact)
					moveWest(1000);
				else if (!southContact && coord[0] != sBound)
					moveSouth(15);
				else if (!eastContact && coord[1] != eBound)
					moveEast(15);
				else if (!northContact && coord[0] != nBound)
					moveNorth(15);
			}
			//Algorithm to find the flag. Squares off each side to put it (hopefully) center of the square, rotates based on where it came in, grabs flag.
			//Then it reverses and faces to a new north and resets the coordinates, this is to act as if it was home again to simplify logic.
			else if(coord[0]==nBound && coord[1]==wBound)
			{
				System.out.println(facing);
				switch(facing){
				case"north": 
					p.travel(12,true);
					while(p.isMoving()){
						if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
							p.stop();
					}
					p.travel(-6);
					p.rotate(90);
					p.travel(12,true);
					while(p.isMoving()){
						if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
							p.stop();
					}
					p.travel(-6);
					p.rotate(-45);
					break;
					
				case"west": 
					p.travel(12,true);
					while(p.isMoving()){
						if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
							p.stop();
					}
					p.travel(-6);
					p.rotate(-90);
					p.travel(12,true);
					while(p.isMoving()){
						if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180)
							p.stop();
					}
					p.travel(-6);
					p.rotate(45);
					break;
					}
				
				while(!tFlag.isPressed()){
					p.travel(3);
					toFlag  ++;
				}
				System.out.println(toFlag);
				
				p.travel(-3*toFlag);
				p.rotate(135);
				
				
				facing = "north";
				squared = false;
				coord[0] = 1;
				coord[1] = 1;
				return;
			}
			else
				return;
		}	
			return;
	}
	/**
	 * Logic to head home after flag is obtained. Almost exactly the same as the getFlag() except once it gets back home, it just spins.
	 */
	public void goHome(){
		while(tFlag.isPressed())
		{
			System.out.println(Arrays.toString(coord) + facing);
			if(coord[0] != nBound){
				if(!northContact)
					moveNorth(1000);
				else if (!westContact && coord[1] != wBound)
					moveWest(15);
				else if (!southContact && coord[0] != sBound)
					moveSouth(15);
				else if (!eastContact && coord[1] != eBound)
					moveEast(15);
			}
			
			else if(coord[1] != wBound){
				if(!westContact)
					moveWest(1000);
				else if (!southContact && coord[0] != sBound)
					moveSouth(15);
				else if (!eastContact && coord[1] != eBound)
					moveEast(15);
				else if (!northContact && coord[0] != nBound)
					moveNorth(15);
			}
			
			else if(coord[0]==nBound && coord[1]==wBound)
			{
				p.rotate(5000);
			}
			else
				return;
		}	
			return;
	}
	/**
	 * algorithm to track coordinates. Based on finding the white line in the grid, then checking if black for 4 counts
	 * to remove most false positives.
	 */
	public void coordCount(){
		if(cRight.getColor().getGreen()<180 && cRight.getColor().getBlue()<180 && cRight.getColor().getRed()<180 && white){
			if(white && black == 4)
			{
				switch(facing){
				case"north": coord[0] ++; break;
				case"south": coord[0] --; break;
				case"west": coord[1] ++; break;
				case"east": coord[1] --; break;
			}
				System.out.println(Arrays.toString(coord) + facing);
				white = false;
				black = 0;
			}
			black ++;
		}
		else if(cRight.getColor().getGreen()>180 && cRight.getColor().getBlue()>180 && cRight.getColor().getRed()>180 && !white)
			white = true;		
		return;
	}
	/**
	 * used for easy testing
	 */
	public void test(){
		//Just for testing put random B.S. here.
		
		//Waits again so the screen doesnt clear and you can double check your output.
		while(!Button.ENTER.isDown()){}
	}

	public static void main(String[] args) {
		CaptureFlag2 c = new CaptureFlag2();
		//Waits for button press.
		System.out.println("Ready to rock.");
		while(!Button.ENTER.isDown()){}
		
		c.getFlag();
		c.goHome();
	}
}

