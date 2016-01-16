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
    
    public AutonomousTest() {                                                                              
        // myRobot = new RobotDrive(0, 1);                                                         
//         myRobot.setExpiration(0.1);                                                             
     	winchEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k1X);
     	//winchEncoder.setMaxPeriod(.1);
     	winchEncoder.setMinRate(10);
     	//winchEncoder.setDistancePerPulse(5);
     	winchEncoder.setReverseDirection(true);
     	winchEncoder.setSamplesToAverage(7);                                                                                          
            
     	
     	//PRACTICE CHASSIS
     	frontLeft = new Talon(1);  //not spinning                                                               
     	frontRight = new Talon(7);                                                                
     	backLeft = new Talon(2);                                                                  
     	backRight = new Talon(6);                                                                 
     	frontStrafe = new Talon(0);                                                               
     	backStrafe = new Talon(5);   //not spinning                                                             
                                                                                                   
     	winchCim1 = new Talon(8);                                                               
     	winchCim2 = new Talon(9);                                               
     }   
    
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
