package org.usfirst.frc.team3455.robot;  
import edu.wpi.first.wpilibj.*;

public class AutonomousTest extends SampleRobot{
    RobotDrive myRobot;  // class that handles basic drive operations                             
    Joystick leftStick;  // set to ID 1 in DriverStation                                          
    Joystick rightStick; // set to ID 2 in DriverStation                                          
                                                                                                  
    Talon frontLeft;                                                                              
    Talon frontRight;                                                                             
    Talon backLeft;                                                                               
    Talon backRight;                                                                              
    Talon frontStrafe;                                                                            
    Talon backStrafe;       
    Encoder winchEncoder;
                                                                                                  
    Talon winchCim1;                                                                            
    Talon winchCim2; 
    
	public void autonomous() {
		
		System.out.println("Starting autonomous code...");
			
		frontLeft.set(0.5);
		frontRight.set(-0.5);
		backLeft.set(0.5);
		backRight.set(-0.5);
		
		Timer.delay(2.5);
		
		frontLeft.set(0);
		frontRight.set(0);
		backLeft.set(0);
		backRight.set(0);
	}
}