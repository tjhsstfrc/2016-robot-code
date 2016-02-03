
package org.usfirst.frc.team3455.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    RobotDrive myRobot;  // class that handles basic drive operations                                                                      
                                                                                                  
    Talon left1;
    Talon left2;
    Talon right1;
    Talon right2; 
    
	/**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	left1  = new Talon(RobotMap.LEFT_MOTOR_1);
    	left2  = new Talon(RobotMap.LEFT_MOTOR_2);
    	right1 = new Talon(RobotMap.RIGHT_MOTOR_1);
    	right2 = new Talon(RobotMap.RIGHT_MOTOR_2);      
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
		System.out.println("Starting autonomous mode...");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	System.out.println("Starting teleop mode...");
    	
    	double minPower = .05;
    	double maxPower = .75;
    	double mult = (maxPower - minPower);
    	double right;
    	double rotate;
    	double rightDir;
    	double rotateDir;
    	double rightPower;
    	double rotatePower;
    	
        right = OI.getController1LeftAnalogY();
    	rotate = OI.getController1LeftAnalogX();		// I already incorporated functions from here into OI.java, I could do the next part too.
                                                                                                  
        rightDir = right/Math.abs(right);
    	rightPower = (Math.abs(right*mult) + minPower) * rightDir;			// re-proportion for power's range, strip direction,
    																		///   and then add back threshold and direction.                                                                                                        
    	rotateDir = rotate/Math.abs(rotate);
    	rotatePower = (Math.abs(rotate*mult) + minPower) * rotateDir;
    	
    	if(Math.abs(rightPower)>0.2){
    		left1.set(-1*rightPower);
    		left2.set(-1*rightPower);
    		right1.set(rightPower);
    		right2.set(rightPower);
    	}
    	else{
    		left1.set(rotatePower);
    		left2.set(rotatePower);
    		right1.set(rotatePower);
    		right1.set(rotatePower);
    	}
	}
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	System.out.println("Starting test mode...");
    }
    
}
