import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
 

public class LineFollower{
 
 /**
  * @author Grayson Lorenz
  */
 public static void main(String[] args) {
	 //Waits for input
  System.out.println("Waiting..");
  while(!Button.ENTER.isDown()){}
  DifferentialPilot pilot = new DifferentialPilot(3.25, 6.5, Motor.A, Motor.C);
  ColorSensor sensor = new ColorSensor(SensorPort.S1);
  TouchSensor touch = new TouchSensor(SensorPort.S2);
  boolean tr = true;
  pilot.setTravelSpeed(20);
  pilot.setRotateSpeed(80);
  
  
  //runs while not on the green square
  while(sensor.getColorID()!=Color.GREEN){
	  
	  //moves when on black and not toutching anything
   while(sensor.getColorID()==Color.BLACK && !touch.isPressed())
   {
	   System.out.println("Move.");
		pilot.travel(50,true);
		while(pilot.isMoving())
		{
			if(sensor.getColorID()!=Color.BLACK)
			{
				pilot.stop();
			}
			if(touch.isPressed())
			{
				pilot.stop();
			}
		}
   }
   
   //searches keeping track of if the last turn to find black was right or left
   while(sensor.getColorID()== Color.WHITE)
   {
	   System.out.println("Searching.");
		if(tr)
		{
			pilot.rotate(-60,true);
			while(pilot.isMoving())
			{
				if(sensor.getColorID()==Color.BLACK)
				{
					pilot.stop();
				}
				if(touch.isPressed())
				{
					pilot.stop();
				}
			}
			if(sensor.getColorID()!=Color.BLACK)
			{
				pilot.rotate(180,true);
				while(pilot.isMoving())
				{
					if(sensor.getColorID()==Color.BLACK)
					{
						pilot.stop();
						tr = false;
					}
					if(touch.isPressed())
					{
						pilot.stop();
					}
				}
			}
		}
		if(!tr) // tr is turn right if the last time black was found turning right, 
			    //it will always turn right untill it has to turn left to find black line
		{
			pilot.rotate(45,true);
			while(pilot.isMoving())
			{
				if(sensor.getColorID()==Color.BLACK)
				{
					pilot.stop();
				}
				if(touch.isPressed())
				{
					pilot.stop();
				}
			}
			if(sensor.getColorID()!=Color.BLACK)
			{
				pilot.rotate(-180,true);
				while(pilot.isMoving())
				{
					if(sensor.getColorID()==Color.BLACK)
					{
						pilot.stop();
						tr = true;
					}
					if(touch.isPressed())
					{
						pilot.stop();
					}
				}
			}
		}
   }
   
   //this is the algorithm i used to get around the brick
   if(touch.isPressed())
   {
	   System.out.println("Brick.");
		pilot.travel(-3);
		pilot.rotate(-90);
		pilot.travelArc(10, 50, true);
		while(pilot.isMoving())
		{
			if(sensor.getColorID()==Color.BLACK)
			{
				pilot.stop();
			}
		}
		pilot.rotate(-100,true);
		while(pilot.isMoving())
		{
			if(sensor.getColorID()==Color.BLACK)
			{
				pilot.stop();
			}
		}
   }
  }
  
  //dance
 System.out.println("This is Green");
 pilot.travel(3);
 pilot.rotate(-10);
 pilot.rotate(10);
 pilot.rotate(-10);
 pilot.rotate(10);
 pilot.rotate(-10);
 pilot.rotate(10);
 pilot.travel(-3);
 pilot.travel(3);
 pilot.rotate(360);
 }
}