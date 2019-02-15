import java.io.File;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class LineyLiney
{
	public static void main(String[] args)
	{
		@SuppressWarnings("resource")
		EV3LargeRegulatedMotor SPIN_MOTOR = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);

		SPIN_MOTOR.setSpeed(10000);
		SPIN_MOTOR.backward();

		EV3 BRICK = (EV3) BrickFinder.getLocal();

		Sound.beepSequenceUp();

		LCD.drawString("Press my button,", 0, 0);
		LCD.drawString("turn me on ;)", 0, 1);
		LCD.drawString("vJuan.3.31.9", 0, 2);

		Keys buttons = BRICK.getKeys();
		Button.LEDPattern(2);
		buttons.waitForAnyPress();
		Button.LEDPattern(3);

		LCD.clear();
		LCD.drawString("This is working", 0, 0);

		SPIN_MOTOR.stop();

		//wheel offset from centre
		double offset = 6;

		Wheel LEFT_WHEEL = WheeledChassis.modelWheel(LEFT_MOTOR, 5.6).offset(-offset);
		Wheel RIGHT_WHEEL = WheeledChassis.modelWheel(RIGHT_MOTOR, 5.6).offset(offset);

		Chassis chassis = new WheeledChassis(new Wheel[] { LEFT_WHEEL, RIGHT_WHEEL }, WheeledChassis.TYPE_DIFFERENTIAL);
		MovePilot pilot = new MovePilot(chassis);

		@SuppressWarnings("resource")
		ColorSensor LEFT_COLOR = new ColorSensor(BRICK.getPort("S2"));
		@SuppressWarnings("resource")
		ColorSensor RIGHT_COLOR = new ColorSensor(BRICK.getPort("S3"));
		@SuppressWarnings("resource")
		TouchSensor TOUCH = new TouchSensor(BRICK.getPort("S1"));
		@SuppressWarnings("resource")
		IRSensor IR_SENSOR = new IRSensor(BRICK.getPort("S4"));

		double SPEED = 10;
		pilot.setLinearSpeed(SPEED);
		pilot.setAngularSpeed(SPEED * SPEED);

		LCD.clear();
		Sound.setVolume(1);

		Boolean leftWasOnGreen;
		Boolean rightWasOnGreen;
		Boolean bothBlack;

		while (buttons.getButtons() != Keys.ID_ESCAPE)
		{
			//main loop
			LCD.drawString("Moving on...", 0, 0);
			Button.LEDPattern(4);
			pilot.forward();
			LCD.drawString("Entering while", 0, 6);

			leftWasOnGreen = false;
			rightWasOnGreen = false;

			while (!LEFT_COLOR.onBlack() && !RIGHT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE)
			{
				//main delay loop
				LCD.drawString("in while", 0, 6);

				//tests left for green and white
				if (LEFT_COLOR.onGreen())
				{
					leftWasOnGreen = true;
				}
				else if (LEFT_COLOR.onWhite())
				{
					leftWasOnGreen = false;
				}

				//tests right for green and white 
				if (RIGHT_COLOR.onGreen())
				{
					rightWasOnGreen = true;
				}
				else if (RIGHT_COLOR.onWhite())
				{
					rightWasOnGreen = false;
				}

				//tests if should finish
				if (LEFT_COLOR.onRed() && RIGHT_COLOR.onRed() && buttons.getButtons() != Keys.ID_ESCAPE)
				{
					finishThing(buttons, pilot, SPIN_MOTOR);
				}

				//tests if touch sensor is pressed
				if (TOUCH.isPressed())
				{
					getAroundAThingThing(pilot, IR_SENSOR, LEFT_COLOR, RIGHT_COLOR);
				}
				LCD.drawString(LEFT_COLOR.getColorName() + "< >" + RIGHT_COLOR.getColorName() + "             ", 0, 1);
				LCD.drawString(Boolean.toString(leftWasOnGreen) + "< >" + Boolean.toString(rightWasOnGreen), 0, 2);
				Delay.msDelay(20);

			}
			
			pilot.stop();
			Button.LEDPattern(5);

			//if both sensors are on black then we are at a junction
			if (LEFT_COLOR.onBlack() && RIGHT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE)
			{
				greenTurnThing(buttons, pilot, leftWasOnGreen, rightWasOnGreen);
			}

			//else we will try to fix it and get back, centred to the line
			else if (LEFT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE)
			{
				//left sensor on black - turns left
				LCD.drawString("Turning left", 0, 0);
				pilot.setAngularAcceleration(1000);
				pilot.setLinearAcceleration(1000);

				bothBlack = false;
				
				LCD.drawString("leftwhile", 0, 6);
				
				while (LEFT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE && !bothBlack)
				{
					Delay.msDelay(20);
					pilot.rotate(-2.5);
					
					if (LEFT_COLOR.onBlack() && RIGHT_COLOR.onBlack())
					{
						bothBlack = true;
					}
				}

				//if while fixing the rotation we encountered black, we test if we need to turn due to green
				if (bothBlack && buttons.getButtons() != Keys.ID_ESCAPE)
				{
					greenTurnThing(buttons, pilot, leftWasOnGreen, rightWasOnGreen);
				}

				pilot.stop();

			}

			else if (RIGHT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE)
			{
				//right sensor on black - turns right
				LCD.drawString("Turning right", 0, 0);
				pilot.setAngularAcceleration(1000);
				pilot.setLinearAcceleration(1000);

				bothBlack = false;
				
				LCD.drawString("rightwhile", 0, 6);
				
				while (RIGHT_COLOR.onBlack() && buttons.getButtons() != Keys.ID_ESCAPE && !bothBlack)
				{
					Delay.msDelay(20);
					pilot.rotate(2.5);
					
					if (LEFT_COLOR.onBlack() && RIGHT_COLOR.onBlack())
					{
						bothBlack = true;
					}
				}
				
				//if while fixing the rotation we encountered black, we test if we need to turn due to green
				if (bothBlack && buttons.getButtons() != Keys.ID_ESCAPE)
				{
					greenTurnThing(buttons, pilot, leftWasOnGreen, rightWasOnGreen);
				}

				pilot.stop();
				
			}
			
			Sound.beepSequenceUp();
			
		}

		Sound.beepSequence();

	}

	//just what happens when it finishes
	private static void finishThing(Keys buttons, MovePilot pilot, EV3LargeRegulatedMotor SPIN_MOTOR)
	{
		pilot.stop();
		SPIN_MOTOR.backward();
		pilot.setAngularSpeed(1000000);
		//Sound.playSample(new File("landofhope.wav"), Sound.VOL_MAX);
		Button.LEDPattern(10);
		pilot.rotate(1440);
		System.exit(0);

	}

	//takes into account of if we were on green and acts accordingly
	private static void greenTurnThing(Keys buttons, MovePilot pilot, boolean leftWasOnGreen, boolean rightWasOnGreen)
	{
		//both sensors on black when it hits a junction - move on forwards
		LCD.drawString("junction", 0, 6); 
		Delay.msDelay(500);
		LCD.drawString("Both on black", 0, 0);
		pilot.travel(3);
		
		if (leftWasOnGreen && rightWasOnGreen)
		{
			//turn 180
			pilot.rotate(190);
		}
		else if (leftWasOnGreen)
		{
			//rotate left
			pilot.rotate(-80);
		}
		else if (rightWasOnGreen)
		{
			//rotate right
			pilot.rotate(80);
		}
	}

	//will use the IR sensor to help get around an object
	private static void getAroundAThingThing(MovePilot pilot, IRSensor IR_SENSOR, ColorSensor LEFT_COLOR, ColorSensor RIGHT_COLOR)
	{
		//moves backwards after hits object and rotate 90 degrees
		LCD.drawString("get around...", 0, 6);
		pilot.travel(-5);
		pilot.rotate(-90);
		pilot.forward();
		
		//goes forwards while the object is to the right of the robot
		while (IR_SENSOR.getDistance() < 200)
		{
			Delay.msDelay(20);
		}
		
		//once past the object, robot stops, travels forward and then rotates 90 degrees
		pilot.stop();
		pilot.travel(15);
		pilot.rotate(90);
		pilot.forward();
		
		//goes forward until we can see the object
		while (IR_SENSOR.getDistance() > 200)
		{
			Delay.msDelay(20);
		}
		 
		//goes forward while we can see object
		while (IR_SENSOR.getDistance() < 200)
		{
			Delay.msDelay(20);
		}
		
		//stop when we have gone past the object, rotate 60 degrees
		pilot.stop();
		pilot.travel(15);
		pilot.rotate(90);
		
		//find the line and get back onto it turning in the correct direction
		findLineFromLeft(pilot, LEFT_COLOR, RIGHT_COLOR);

	}

	//is able to find a line from the left hand side of direction
	private static void findLineFromLeft(MovePilot pilot, ColorSensor LEFT_COLOR, ColorSensor RIGHT_COLOR)
	{
		pilot.setLinearSpeed(3);
		pilot.forward();
		Boolean rightBeenOnBlack = false;
		
		//while left colour sensor is not on black
		while (!LEFT_COLOR.onBlack())
		{
			Delay.msDelay(20);
			
			//finds if the right colour is on black
			if (RIGHT_COLOR.onBlack())
			{
				//sets boolean to true
				rightBeenOnBlack = true;
			}
		}
		
		//if it has not been on black
		if (!rightBeenOnBlack)
		{
			//rotates by -2.5 until right colour sensor is on black
			while (!RIGHT_COLOR.onBlack())
			{
				pilot.rotate(-2.5);
			}
		}
		
		//while the right colour sensor is not on white, rotate until it is
		while (!RIGHT_COLOR.onWhite())
		{
			pilot.rotate(-2.5);
		}
		pilot.setLinearSpeed(5);
		pilot.forward();
	}
}