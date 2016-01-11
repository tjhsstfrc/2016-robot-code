package org.usfirst.frc.team3455.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically it 
 * contains the code necessary to operate a robot with tank drive.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
    RobotDrive myRobot;  // class that handles basic drive operations
    Joystick leftStick;  // set to ID 1 in DriverStation
    Joystick rightStick; // set to ID 2 in DriverStation
    
    Talon frontLeft;
    Talon frontRight;
    Talon backLeft;
    Talon backRight;
    Talon frontStrafe;
    Talon backStrafe;
    
    //Talon winchCim1;
    //Talon winchCim2;
    
    // The channel on the driver station that the joystick is connected to
    final int rightJoystickChannel	= 0;
    final int leftJoystickChannel	= 1;
    
    public Robot() {
        //myRobot = new RobotDrive(0, 1);
        //myRobot.setExpiration(0.1);


    	frontLeft = new Talon(7);
    	frontRight = new Talon(1);
    	backLeft = new Talon(6);
    	backRight = new Talon(2);
    	frontStrafe = new Talon(8);
    	backStrafe = new Talon(5);

    	//winchCim1 = new Talon(3);
    	//winchCim2 = new Talon(2);

    	rightStick = new Joystick(rightJoystickChannel);
    	leftStick = new Joystick(leftJoystickChannel);
    }


    /**
     * Runs the motors with tank steering.
     */
    public void operatorControl() {
    	double ctrlThresh = .2;
    	double minPower = .05;
    	double maxPower = .75;
    	double recip = (1 - ctrlThresh);
    	double mult = (maxPower - minPower);
    	double right;
    	double strafe;
    	double left;
    	double rightAxis;
    	double strafeAxis;
    	double leftAxis;
    	//    	boolean leftLeft;
    	//    	boolean leftRight;
    	double rightDir;
    	double strafeDir;
    	double leftDir;
    	double rightPower;
    	double leftPower;
    	double strafePower;

    	//    	double frontLeftPower;
    	//    	double frontRightPower;
    	//    	double backLeftPower;
    	//    	double backRightPower;

    	myRobot.setSafetyEnabled(true);
    	while (isOperatorControl() && isEnabled()) {

    		// ********** BEGIN DRIVING CODE **********
    		// Code for driving using omniwheels and two wheels for strafing
    		// Diagram for the wheels of the robot below.

    		//      L         S         R
    		//   /--------------------------\
    		//	 ||------------------------||
    		//   ||          [][]          ||
    		//   || []                  [] ||
    		//   || []                  [] ||
    		//   ||                        ||
    		//   ||                        ||
    		//   ||                        ||
    		//   ||                        ||
    		//   ||                        ||
    		//   || []                  [] ||
    		//   || []                  [] ||
    		//   ||          [][]          ||
    		//   ||------------------------||
    		//   \--------------------------/
    		//
    		// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    		
    		ctrlThresh = .2;									// the point at which we want to sense joystick action
    		recip = 1-ctrlThresh;								// = 0.8

    		rightAxis = rightStick.getRawAxis(0);				// right joystick, y (up/down) axis
    		strafeAxis = rightStick.getRawAxis(1);				// right joystick, x (left/right) axis; this is 
    															///   arbitrary and could have been the left stick
    		leftAxis = leftStick.getRawAxis(0);					// left joystick, y axis

    		rightDir = rightAxis/Math.abs(rightAxis);			// forward or backward (+1 or -1)
    		strafeDir = strafeAxis/Math.abs(strafeAxis);		// 			''
    		leftDir = leftAxis/Math.abs(leftAxis);				//			''

    		right = 0;											// right input formatted for range of detected values [0.2,1]
    		left = 0;											// left input formatted
    		strafe = 0;											// strafe input formatted

    		if(Math.abs(rightAxis) > ctrlThresh)				// user moved stick beyond threshold
    		{right = (rightAxis-ctrlThresh*rightDir)/recip;}	// format right: scale back, set direction, proportion it
    		if(Math.abs(strafeAxis) > ctrlThresh)				
    		{strafe = (strafeAxis-ctrlThresh*strafeDir)/recip;} // format left...
    		if(Math.abs(leftAxis) > ctrlThresh)
    		{left = (leftAxis-ctrlThresh*leftDir)/recip;}		// format strafe...


    		rightDir = right/Math.abs(right);
    		rightPower = (Math.abs(right*mult) + minPower) * rightDir; 		// re-proportion for power's range, strip direction,
    																		///   and then add back threshold and direction.
    		leftDir = left/Math.abs(left);
    		leftPower = (Math.abs(left*mult) + minPower) * leftDir;			// 		''

    		strafeDir = strafe/Math.abs(strafe);
    		strafePower = (Math.abs(strafe*mult) + minPower) * strafeDir;	// 		''
    		

    		frontRight.set(rightPower);							// set all the motors with the powers we
    		backRight.set(rightPower);							///   calculated above : drive!
    		frontLeft.set(leftPower);
    		backLeft.set(leftPower); 
    		frontStrafe.set(strafePower);
    		backStrafe.set(strafePower);

    		//myRobot.tankDrive(leftStick, rightStick);
    		
    		// ********** END OF DRIVING CODE **********
    		//=======================================================================
    		// ************ BEGIN WINCH CODE ***********
    		// We need to code the winch to help raise and lower the elevator
    		// This is just one option between the winch and the lead screw.
    		
    		/*if(rightStick.getRawButton(4))
             {
            	 winchCim1.set(.5);
            	 winchCim2.set(.5);
             }

             if(rightStick.getRawButton(5))
             {
            	 winchCim1.set(-.5);
            	 winchCim2.set(-.5);
             } */
    		
    		// ********** END OF WINCH CODE **********
    		//=======================================================================
    		
    		
    		Timer.delay(0.005);		// wait for a motor update time
        }
    }

}
