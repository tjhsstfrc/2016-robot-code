package org.usfirst.frc.team3455.robot;                                                           
                                                                                                  
                                                                                                  
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;                                                         
import edu.wpi.first.wpilibj.RobotDrive;                                                          
import edu.wpi.first.wpilibj.Joystick;                                                            
import edu.wpi.first.wpilibj.Talon;                                                               
import edu.wpi.first.wpilibj.Timer;                                                               
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.vision.AxisCamera;
                                                                                                  
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
    Encoder winchEncoder;
        
    AxisCamera camera;
    
    Talon winchCim1;                                                                            
    Talon winchCim2;                                                                            
                                                                                                  
    // The channel on the driver station that the joystick is connected to                        
    final int rightJoystickChannel	= 0;                                                          
    final int leftJoystickChannel	= 1;                                                          
                                                                                                  
    public Robot() {                                                                              
       // myRobot = new RobotDrive(0, 1);                                                         
//        myRobot.setExpiration(0.1);                                                             
    	winchEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k1X);
    	//winchEncoder.setMaxPeriod(.1);
    	winchEncoder.setMinRate(10);
    	//winchEncoder.setDistancePerPulse(5);
    	winchEncoder.setReverseDirection(true);
    	winchEncoder.setSamplesToAverage(7);                                                                                          
           
    	
    	//PRACTICE CHASSIS
//    	frontLeft = new Talon(1);  //not spinning                                                               
//    	frontRight = new Talon(7);                                                                
//    	backLeft = new Talon(2);                                                                  
//    	backRight = new Talon(6);                                                                 
//    	frontStrafe = new Talon(0);                                                               
//    	backStrafe = new Talon(5);   //not spinning                                                             
//                                                                                                  
//    	winchCim1 = new Talon(8);                                                               
//    	winchCim2 = new Talon(9);   
//    	
    	frontLeft = new Talon(2);  //not spinning                                                               
    	frontRight = new Talon(5);                                                                
    	backLeft = new Talon(3);                                                                  
    	backRight = new Talon(4);                                                                 
//    	frontStrafe = new Talon(0);                                                               
//    	backStrafe = new Talon(5);   //not spinning                                                             
                                                                                                  
    	winchCim1 = new Talon(0);                                                               
    	winchCim2 = new Talon(1);   
    	
        camera = new AxisCamera("10.34.55.11");
    	
        System.out.println("Brightness: "+camera.getBrightness());
        
    	rightStick = new Joystick(rightJoystickChannel);                                          
    	leftStick = new Joystick(leftJoystickChannel);                                            
    }                                                                                             
                                                                                                  
    public void autonomous() {
		
		System.out.println("Starting autonomous code...");
			
		frontLeft.set(0.2);
		frontRight.set(-0.2);
		backLeft.set(0.2);
		backRight.set(-0.2);
		
		Timer.delay(3.5);
		
		frontLeft.set(0.3);
		frontRight.set(0.3);
		backLeft.set(0.3);
		backRight.set(0.3);
		
		Timer.delay(1.2);
		
		frontLeft.set(0);
		frontRight.set(0);
		backLeft.set(0);
		backRight.set(0);
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
    	double rotate;                                                                              
    	double rightAxis;                                                                         
    	double strafeAxis;                                                                        
    	double rotateAxis;                                                                          
    	//    	boolean leftLeft;                                                                 
    	//    	boolean leftRight;                                                                
    	double rightDir;                                                                          
    	double strafeDir;                                                                         
    	double rotateDir;                                                                           
    	double rightPower;                                                                        
    	double rotatePower;                                                                         
    	double strafePower;       
    	
    	int winchCount;
    	boolean winchDirection;
                                                                                                  
    	//    	double frontLeftPower;                                                            
    	//    	double frontRightPower;                                                           
    	//    	double backLeftPower;                                                             
    	//    	double backRightPower;                                                            
                                                                                                  
    	//myRobot.setSafetyEnabled(true);    
    	
    	Accelerometer test = new BuiltInAccelerometer();
    	
    	while (isOperatorControl() && isEnabled()) {      
    		// ACCEL TEST CODE
//    		System.out.println(test.getX() + ", " + test.getY() + ", " + test.getZ());
    		// END ACCEL TEST CODE
                                                                                                  
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
                                                                                                  
    		ctrlThresh = .2;                                    // the point at which we want to sense joystick action                                 
    		recip = 1-ctrlThresh;              		            // = 0.8                                                      
                                                                                                  
    		rightAxis = rightStick.getRawAxis(1);				// right joystick, y (up/down) axis
    		
    		//System.out.println(""+rightAxis);
    		strafeAxis = rightStick.getRawAxis(0);				// right joystick, x (left/right) axis; this is 
    															///   arbitrary and could have been the left stick
    		rotateAxis = rightStick.getRawAxis(4);					// left joystick, y axis
                                                                  
    		rightDir = rightAxis/Math.abs(rightAxis);			// forward or backward (+1 or -1)
    		strafeDir = strafeAxis/Math.abs(strafeAxis);       	// 			''                                   
    		rotateDir = rotateAxis/Math.abs(rotateAxis);              // 			''                                  
                                                                                                  
    		right = 0;                                          // right input formatted for range of detected values [0.2,1]                                
    		rotate = 0;                                           // left input formatted                          
    		strafe = 0;                                         // strafe input formatted        
                                                                                                  
    		if(Math.abs(rightAxis) > ctrlThresh)               	// user moved stick beyond threshold                       
    		{right = (rightAxis-ctrlThresh*rightDir)/recip;}    // format right: scale back, set direction, proportion it                                
    		if(Math.abs(strafeAxis) > ctrlThresh)                                                 
    		{strafe = (strafeAxis-ctrlThresh*strafeDir)/recip;} // format left...                                  
    		if(Math.abs(rotateAxis) > ctrlThresh)                                                   
    		{rotate = (rotateAxis-ctrlThresh*rotateDir)/recip;}		// format strafe...                                         
                                                                                                  
                                                                                                  
    		rightDir = right/Math.abs(right);                                                     
    		rightPower = (Math.abs(right*mult) + minPower) * rightDir; 		// re-proportion for power's range, strip direction,
																			///   and then add back threshold and direction.                                                                                                        
    		rotateDir = rotate/Math.abs(rotate);                                                        
    		rotatePower = (Math.abs(rotate*mult) + minPower) * rotateDir;			// 		''                               
                                                                                                  
    		strafeDir = strafe/Math.abs(strafe);                                                  
    		strafePower = (Math.abs(strafe*mult) + minPower) * strafeDir;	// 		''                         
                                                                                                  
    		if(Math.abs(rightPower)>0.2){
    			frontRight.set(rightPower);						// set all the motors with the powers we                                                           
    			backRight.set(rightPower);						///   calculated above : drive!                                                            
    			frontLeft.set(-1*rightPower);                                                             
    			backLeft.set(-1*rightPower);
    		}
    		else{
    			frontRight.set(rotatePower);
    			backRight.set(rotatePower);
    			frontLeft.set(rotatePower);
    			backLeft.set(rotatePower);
    		}
    		
//    		frontStrafe.set(strafePower);                                                         
//    		backStrafe.set(-1*strafePower);                                                          
                                                                                                  
//    		myRobot.tankDrive(leftStick, rightStick);                                           
    		                                                                                      
    		// ********** END OF DRIVING CODE **********                                          
    		//=======================================================================             
    		// ************ BEGIN WINCH CODE ***********                                          
    		// We need to code the winch to help raise and lower the elevator                     
    		// This is just one option between the winch and the lead screw.                      
    		 
    		
    		//second winch test
    		/*
    		winchCount = winchEncoder.get();				// get the pulse count from the encoder
       	 	System.out.println("Count: "+ winchCount);		// print the pulse count   
       	 	if(winchCount<240){
       	 		
       	 		winchCim1.set(.1);
       	 		winchCim2.set(.1);
       	 	}
       	 	else{
       	 		winchCim1.set(0);
       	 		winchCim2.set(0);
       	 		
       	 	}
       	 if(rightStick.getRawButton(5))                                                      
         {     
			 winchEncoder.reset();// reset the pulse count
			 winchCount = 0;
			 winchCim1.set(0);
			 winchCim2.set(0);
			                                                           
         } */ //end of second winch test 
       	 	
    		
    		 if(rightStick.getRawButton(5))                                                      
             {     
    			 //winchEncoder.reset();// reset the pulse count
            	System.out.println("A");
            	 winchCim1.set(.5);                         	// set winchCim1 to 0.5 power (upwards)                                     
            	 winchCim2.set(.5); 							// set winchCim2 to 0.5 power
    			                                                           
             }  
    		else if(rightStick.getRawButton(4))
    		{
    			System.out.println("B");
    			//winchEncoder.reset();							// reset the pulse count
           	 	winchCim1.set(-.3);                             // set winchCim1 to -0.5 power (downwards)                               
           	 	winchCim2.set(-.3);	 							// set winchCim2 to -0.5 power
    		}
                                                                                              
       	 	else// if(rightStick.getRawButton(2))                                                       
             {                                                                                    
            	 winchCim1.set(0);  							// set winchCim1 to 0 power (off/stop)                                                          
            	 winchCim2.set(0);								// set winchCim2 to 0 pwoer (off/stop)
//            	 winchCount = winchEncoder.get();				// get the pulse count from the encoder
//            	 System.out.println("Count: "+ winchCount);		// print the pulse count                                                            
             } 
                                                                                                                                                                   
    		// ********** END OF WINCH CODE **********                                            
    		//=======================================================================             
    		                                                                                      
    		                                                                                      
    		Timer.delay(0.005);		// wait for a motor update time    
        }                                                                                         
    }                                                                                             
                                                                                                  
}                                                                                                 
                                                                                                  