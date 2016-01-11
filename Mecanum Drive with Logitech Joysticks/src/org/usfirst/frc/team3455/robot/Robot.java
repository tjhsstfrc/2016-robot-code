package org.usfirst.frc.team3455.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive class.
 */
public class Robot extends SampleRobot {
	
    RobotDrive robotDrive;
    Joystick leftStick;
    Joystick rightStick;

    // Channels for the wheels
//    final int frontLeftChannel	= 4;
//    final int rearLeftChannel	= 2;
//    final int frontRightChannel	= 3;
//    final int rearRightChannel	= 1;
    
    Talon frontLeft;
    Talon frontRight;
    Talon backLeft;
    Talon backRight;
    
    // The channel on the driver station that the joystick is connected to
    final int rightJoystickChannel	= 0;
    final int leftJoystickChannel	= 1;


    public Robot() {
//    	robotDrive = new RobotDrive(frontLeftChannel, rearLeftChannel, frontRightChannel, rearRightChannel);
//    	robotDrive.setExpiration(0.1);
//        robotDrive.setInvertedMotor(MotorType.kFrontLeft, true);	// invert the left side motors
//    	robotDrive.setInvertedMotor(MotorType.kRearLeft, true);		// you may need to change or remove this to match your robot
    	frontLeft = new Talon(2);
    	frontRight = new Talon(5);
    	backLeft = new Talon(3);
    	backRight = new Talon(4);
        rightStick = new Joystick(rightJoystickChannel);
        leftStick = new Joystick(leftJoystickChannel);
    }
        

    /**
     * Runs the motors with Mecanum drive.
     */
    public void operatorControl() {
    	double ctrlThresh = .2;
    	double minPower = .05;
    	double maxPower = .75;
    	double recip = (1 - ctrlThresh);
    	double mult = (maxPower - minPower);
    	double forward;
    	double strafe;
    	double rotate;
    	double fAxis;
    	double sAxis;
    	double rAxis;
    	boolean rotateLeft;
    	boolean rotateRight;
    	double fDir;
    	double sDir;
    	double rDir;
    	double frontLeftPower;
    	double frontRightPower;
    	double backLeftPower;
    	double backRightPower;
    
    	
        //robotDrive.setSafetyEnabled(false);
        while (isOperatorControl() && isEnabled()) {
        	
        ctrlThresh = .2;
   	     recip = 1-ctrlThresh;
   	     fAxis = rightStick.getRawAxis(1);
         sAxis = rightStick.getRawAxis(0);
         rAxis = -1 * leftStick.getRawAxis(0);
//         if(stick.getRawButton(4)){
//        	 rAxis = 0.5;
//         }
//         else if(stick.getRawButton(5)){
//        	 rAxis = -0.5;
//         }
//         else{
//        	 rAxis = 0;
//         }

   	     fDir = fAxis/Math.abs(fAxis);
   	     sDir = sAxis/Math.abs(sAxis);
   	     rDir = rAxis/Math.abs(rAxis);
   	     
   	     forward = 0;
   	     rotate = 0;
   	     strafe = 0;
   	     
   	     if(Math.abs(fAxis) > ctrlThresh)
   		{forward = (fAxis-ctrlThresh*fDir)/recip;}
   	     if(Math.abs(sAxis) > ctrlThresh)
   		{strafe = (sAxis-ctrlThresh*sDir)/recip;}
   	     if(Math.abs(rAxis) > ctrlThresh)
   		{rotate = (rAxis-ctrlThresh*rDir)/recip;}
              
   	     
   	     frontLeftPower = (-1*forward - rotate + strafe);
   	     fDir = frontLeftPower/Math.abs(frontLeftPower);
                frontLeftPower = (Math.abs(frontLeftPower*mult) + minPower) * fDir;
               
                frontRightPower = (forward - rotate + strafe);
                fDir = frontRightPower/Math.abs(frontRightPower);
                frontRightPower = (Math.abs(frontRightPower*mult) + minPower) * fDir;

                backLeftPower = (-1*forward - rotate - strafe);
                fDir = backLeftPower/Math.abs(backLeftPower);
                backLeftPower = (Math.abs(backLeftPower*mult) + minPower) * fDir;

                backRightPower = (forward - rotate - strafe);
                fDir = backRightPower/Math.abs(backRightPower);
                backRightPower = (Math.abs(backRightPower*mult) + minPower) * fDir;

                frontRight.set(frontRightPower);
                frontLeft.set(frontLeftPower);
               backRight.set(backRightPower);
                backLeft.set(backLeftPower); 

   	    
        	//robotDrive.mecanumDrive_Cartesian(stick.getX(), stick.getY(), stick.getZ(), 0);
            
            Timer.delay(0.02);	// wait to avoid hogging CPU cycles
        }
    }
    
}
