package org.usfirst.frc.team3455.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.usfirst.frc.team3455.robot.commands.ExampleCommand;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	static final int XBOX_1				= 0;
	static final int XBOX_2				= 1;
	static final int LEFT_ANALOG_X		= 4;
	static final int LEFT_ANALOG_Y		= 1;
	
	//=====================================================
	
	static Joystick controller1 = new Joystick(XBOX_1);                                          
	static Joystick controller2 = new Joystick(XBOX_2); 
	
	static double controlThresh = 0.2;
	static double recipOfThresh = (1-controlThresh);

	public static double getController1LeftAnalogX(){
		return formatAxisValue(controller1.getRawAxis(LEFT_ANALOG_X));
	}
	
	public static double getController1LeftAnalogY(){
		return formatAxisValue(controller1.getRawAxis(LEFT_ANALOG_Y));
	}
	
	private static double formatAxisValue(double ax){
		double dir = ax/Math.abs(ax);
		
		double val = 0;
		if(Math.abs(ax) > controlThresh){
			val = (ax - controlThresh*dir)/recipOfThresh;
		}
		return val;
	}
}

