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

public class CalibrateColor
{
	public static void main(String[] args)
	{
		@SuppressWarnings("resource")

		EV3 BRICK = (EV3) BrickFinder.getLocal();

		Sound.beepSequenceUp();
		LCD.drawString("Press my button,", 0, 0);
		LCD.drawString("turn me on ;)", 0, 1);

		Keys buttons = BRICK.getKeys();
		Button.LEDPattern(2);
		buttons.waitForAnyPress();

		Button.LEDPattern(3);
		LCD.drawString("              ", 0, 1);

		LCD.drawString("This is working", 0, 0);

		@SuppressWarnings("resource")
		ColorSensor LEFT_COLOR = new ColorSensor(BRICK.getPort("S2"));
		@SuppressWarnings("resource")
		ColorSensor RIGHT_COLOR = new ColorSensor(BRICK.getPort("S3"));

		LCD.clear();
		Sound.setVolume(1);

		while (buttons.getButtons() != Keys.ID_ESCAPE)
		{
			
			//main delay loop
			Delay.msDelay(100);
			LCD.clear();
			LCD.drawString("LR " + Integer.toString(LEFT_COLOR.getRed()), 0, 0);
			LCD.drawString("LG " + Integer.toString(LEFT_COLOR.getGreen()), 0, 1);
			LCD.drawString("LB " + Integer.toString(LEFT_COLOR.getBlue()), 0, 2);
			LCD.drawString("RR " + Integer.toString(RIGHT_COLOR.getRed()), 0, 3);
			LCD.drawString("RG " + Integer.toString(RIGHT_COLOR.getGreen()), 0, 4);
			LCD.drawString("RB " + Integer.toString(RIGHT_COLOR.getBlue()), 0, 5);
			
		}
		Sound.beepSequence();
		

	}
	
}