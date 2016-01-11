/*
 * SHIT TO DO
 * -AUTONYMOOSE (DRIVE STRAIGHT GET DA BAIT, HATERS GONNA HATE)
 * -PORGRAM DA ROLLA INTAKE (HOLLA HOLLA ROLLA IN DA DOLLA) - DONE
 * -PROGREM DA CAHTAPALUT (PEW PEW BOLLA SHOT COLLA) - SORTA DONE ALREADY, NEED TO FIGURE OUT ENCODER STUFF TO DO
 * -VISHINORY INPUT (WOW MUCH SEE VERY INPUT SO ANALYSIS) - DONE
 */

/* FROM THE ELECTRONICS PEOPLE. 
 * 10 - Encoder A
 * 9 - Encoder B
 * 8 - Talon 1
 * 7 - Talon 2
 * 6 - Talon 3
 * 5 - Talon 4
 */

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team3455.robot;

import edu.wpi.first.wpilibj.*;
//import edu.wpi.first.wpilibj.camera.AxisCamera;
//import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.*;
//import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
				
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Team3455Robot extends SampleRobot {
    //instantiate necessary components

    Talon frontLeft;
    Talon frontRight;
    Talon backLeft;
    Talon backRight;
    Talon leftRoller;
    Talon rightRoller;
    Talon winchCim1;
    Talon winchCim2;
    Encoder winchEncoder;
    DigitalInput winchLimit;
    Joystick joystick1; // controller1
    Joystick joystick2; // controller2
    Servo cameraServoUpDn;
    Servo cameraServoLR;
    
    Compressor compressor; //Compressor
    NetworkTable smartDashboard; // smart dashboard
    int imageHeight; // image height for camera
    int imageWidth; // image width for camera

    DoubleSolenoid intakeSolenoid;
    DoubleSolenoid shifterSolenoid;
    Joystick[] xboxControllers = new Joystick[2];
    int activeController; //0 for Drive, 1 for shooter. only one control works at a time.
    boolean collectorSolenoidActive = false;
    boolean winchSolenoidActive = false;
//Camera constants used for distance calculation
    final int Y_IMAGE_RES = 480;		//X Image resolution in pixels, should be 120, 240 or 480
    final double VIEW_ANGLE = 49;		//Axis M1013
    //final double VIEW_ANGLE = 41.7;		//Axis 206 camera
    //final double VIEW_ANGLE = 37.4;  //Axis M1011 camera
    final double PI = 3.141592653;

    //Score limits used for target identification
    final int  RECTANGULARITY_LIMIT = 40;
    final int ASPECT_RATIO_LIMIT = 55;

    //Score limits used for hot target determination
    final int TAPE_WIDTH_LIMIT = 50;
    final int  VERTICAL_SCORE_LIMIT = 50;
    final int LR_SCORE_LIMIT = 50;

    //Minimum area of particles to be considered
    final int AREA_MINIMUM = 150;

    //Maximum number of particles to process
    final int MAX_PARTICLES = 8;

    //AxisCamera camera;          // the axis camera object (connected to the switch)
    //CriteriaCollection cc;      // the criteria for doing the particle filter operation
    
    public class Scores {
        double rectangularity;
        double aspectRatioVertical;
        double aspectRatioHorizontal;
    }
    
    public class TargetReport {
		int verticalIndex;
		int horizontalIndex;
		boolean Hot;
		double totalScore;
		double leftScore;
		double rightScore;
		double tapeWidthScore;
		double verticalScore;
    }
    
    
    /**
     * This function is called once when the robot is initialized.
     */
    public void robotInit() {	
        System.out.println("Initializing...");
        //initializes our components*/
	
        //connects to smart dashboard
	smartDashboard = NetworkTable.getTable("SmartDashboard");

        //camera setup
        //cameraSetup();

        //update smartdashboard info
        smartDashboardSetup();


        //set up target criteria
        //visionProcessingSetup();

        
	
	intakeSolenoid = new DoubleSolenoid(7,8);
	intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	System.out.println("INTAKE DEPLOYED HOPEFULLY");
	
	shifterSolenoid = new DoubleSolenoid(2,4);
	shifterSolenoid.set(DoubleSolenoid.Value.kForward);
	
	
	//RED IS SHOOT, BLACK IS COLLECT
	xboxControllers[0] = new Joystick(2);
	xboxControllers[1] = new Joystick(1);
	frontLeft = new Talon(6);
	frontRight = new Talon(7);
	backLeft = new Talon(1);
	backRight = new Talon(4);
	
	winchEncoder = new Encoder(13, 14);
	winchCim1 = new Talon(3);
	winchCim2 = new Talon(2);
	leftRoller = new Talon(5);
	rightRoller = new Talon(8);
	
	winchLimit = new DigitalInput(10);
	
	cameraServoUpDn = new Servo(10);
	cameraServoLR = new Servo(9);
	
	//compressor = new Compressor(1,7); //Plugged into Digital I/O 2nd # is pressure switch, 4th # is spike, other #s are module
	//compressor.start();
	//System.out.println("Compressor Enabled: " + compressor.enabled());
	//System.out.println("Compressor Pressure Gauge: " + compressor.getPressureSwitchValue());
	//System.out.println("Solenoid Enabled: " + intakeSolenoid.get());
	
	
	//camera = AxisCamera.getInstance();  // get an instance of the camera
        //cc = new CriteriaCollection();      // create the criteria for the particle filter
        //cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, AREA_MINIMUM, 65535, false);
	
	Timer.delay(6);
        System.out.println("Done initializing...");
    }

    public void smartDashboardSetup() { //Sets all the default messages in smart dashboard
        smartDashboard.putBoolean("Tape in Sight?:", false);
        smartDashboard.putBoolean("Shooter is on?: ", false);
	smartDashboard.putBoolean("Ball in Position?: ", false);
	smartDashboard.putString("Current control: ", "Collector");
    }

//    public void cameraSetup() {//connects to camera
//        //camera = AxisCamera.getInstance("10.34.55.11");
//        imageHeight = camera.getResolution().height;
//        imageWidth = camera.getResolution().width;
//        smartDashboard.putNumber("Image Height: ", imageHeight);
//        smartDashboard.putNumber("Image Width : ", imageWidth);
//    }

//    public void visionProcessingSetup() { // create the criteria for the particle filter
//        cc = new CriteriaCollection();
//        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, 500, 65535, false);
//    }

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
	shifterSolenoid.set(DoubleSolenoid.Value.kReverse);/*
	intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	int count = 0;
	while(winchLimit.get() || (count < 40)){
	    winchCim1.set(-0.80);
	    winchCim2.set(-0.80);
	    Timer.delay(.1);
	    count++;
	}
	winchCim1.set(0);
	winchCim2.set(0);
	
	System.out.println("Finished autonomous code...");*/
    }

   public void operatorControl() {
	//compressor.start();
        System.out.println("Teleoperated...");
        //Took out watchdog for 2015 control system
        //Watchdog.getInstance().setEnabled(false);
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
	double fDir;
	double sDir;
	double rDir;
	double frontLeftPower;
	double frontRightPower;
	double backLeftPower;
	double backRightPower;
	double cameraUpDn;
	double cameraLR;
	
//	TargetReport target = new TargetReport();
//	int verticalTargets[] = new int[MAX_PARTICLES];
//	int horizontalTargets[] = new int[MAX_PARTICLES];
//	int verticalTargetCount, horizontalTargetCount;
	
        while (isOperatorControl() && isEnabled()) {
	    
	     ctrlThresh = .2;
	     recip = 1-ctrlThresh;
	     fAxis = xboxControllers[activeController].getRawAxis(2);
             sAxis = xboxControllers[activeController].getRawAxis(1);
             rAxis = -1 * xboxControllers[activeController].getRawAxis(4);
	     cameraUpDn = xboxControllers[1 - activeController].getRawAxis(2);
	     cameraLR = xboxControllers[1 - activeController].getRawAxis(4);
	     System.out.println("Input from xbox controller: " + fAxis + " " + sAxis + " " + rAxis);
	     //System.out.println("Compressor Enabled: " + compressor.enabled());
	     //System.out.println("Compressor Pressure Gauge: " + compressor.getPressureSwitchValue());
	     if(activeController == 0){
		 fAxis *= -1;
		 sAxis *= -1;
	     }
	     
	     cameraServoUpDn.set(cameraUpDn);
	     cameraServoLR.set(cameraLR);
	     
	     System.out.println("CameraLR: " + cameraLR);
	     System.out.println("CameraUpDn: " + cameraUpDn);
	     
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

	     xboxController();
		
	     /**
                 * Do the image capture with the camera and apply the algorithm described above. This
                 * sample will either get images from the camera or from an image file stored in the top
                 * level directory in the flash memory on the cRIO. The file name in this case is "testImage.jpg"
                 * 
                 *//*
            try {
                
    
                ColorImage image = camera.getImage();     // comment if using stored images
               //ColorImage image;                           // next 2 lines read image from flash on cRIO
                //image = new RGBImage("/testImage.jpg");		// get the sample image from the cRIO flash
		BinaryImage thresholdImage = image.thresholdRGB(230, 255, 230, 255, 230, 255);   // keep only green objects
                thresholdImage.write("/threshold.bmp");
                BinaryImage filteredImage = thresholdImage.particleFilter(cc);           // filter out small particles
                filteredImage.write("/filteredImage.bmp");
                
                //iterate through each particle and score to see if it is a target
                Scores scores[] = new Scores[filteredImage.getNumberParticles()];
                horizontalTargetCount = verticalTargetCount = 0;                                                                                
		
		System.out.println("Filtered Image: " + filteredImage.getNumberParticles());
                
                if(filteredImage.getNumberParticles() > 0)
                {
			System.out.println("Made it inside filteredImage if-statement");
			for (int i = 0; i < MAX_PARTICLES && i < filteredImage.getNumberParticles(); i++) {
			    ParticleAnalysisReport report = filteredImage.getParticleAnalysisReport(i);
			    scores[i] = new Scores();
					
			    //Score each particle on rectangularity and aspect ratio
			    scores[i].rectangularity = scoreRectangularity(report);
			    scores[i].aspectRatioVertical = scoreAspectRatio(filteredImage, report, i, true);
			    scores[i].aspectRatioHorizontal = scoreAspectRatio(filteredImage, report, i, false);			
					
			    //Check if the particle is a horizontal target, if not, check if it's a vertical target
			    if(scoreCompare(scores[i], false))
			    {
				System.out.println("particle: " + i + "is a Horizontal Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
				horizontalTargets[horizontalTargetCount++] = i; //Add particle to target array and increment count
			    } else if (scoreCompare(scores[i], true)) {
				System.out.println("particle: " + i + "is a Vertical Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
				verticalTargets[verticalTargetCount++] = i;  //Add particle to target array and increment count
			    } else {
				System.out.println("particle: " + i + "is not a Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
			    }
				System.out.println("rect: " + scores[i].rectangularity + " ARHoriz: " + scores[i].aspectRatioHorizontal);
				System.out.println("ARVert: " + scores[i].aspectRatioVertical);	
			}

			//Zero out scores and set verticalIndex to first target in case there are no horizontal targets
			target.totalScore = target.leftScore = target.rightScore = target.tapeWidthScore = target.verticalScore = 0;
			target.verticalIndex = verticalTargets[0];
			for (int i = 0; i < verticalTargetCount; i++)
			{
				ParticleAnalysisReport verticalReport = filteredImage.getParticleAnalysisReport(verticalTargets[i]);
				for (int j = 0; j < horizontalTargetCount; j++)
				{
                                    ParticleAnalysisReport horizontalReport = filteredImage.getParticleAnalysisReport(horizontalTargets[j]);
                                    double horizWidth, horizHeight, vertWidth, leftScore, rightScore, tapeWidthScore, verticalScore, total;
	
                                    //Measure equivalent rectangle sides for use in score calculation
                                    horizWidth = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
                                    vertWidth = NIVision.MeasureParticle(filteredImage.image, verticalTargets[i], false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
                                    horizHeight = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
						
                                    //Determine if the horizontal target is in the expected location to the left of the vertical target
                                    leftScore = ratioToScore(1.2*(verticalReport.boundingRectLeft - horizontalReport.center_mass_x)/horizWidth);
                                    //Determine if the horizontal target is in the expected location to the right of the  vertical target
                                    rightScore = ratioToScore(1.2*(horizontalReport.center_mass_x - verticalReport.boundingRectLeft - verticalReport.boundingRectWidth)/horizWidth);
                                    //Determine if the width of the tape on the two targets appears to be the same
                                    tapeWidthScore = ratioToScore(vertWidth/horizHeight);
                                    //Determine if the vertical location of the horizontal target appears to be correct
                                    verticalScore = ratioToScore(1-(verticalReport.boundingRectTop - horizontalReport.center_mass_y)/(4*horizHeight));
                                    total = leftScore > rightScore ? leftScore:rightScore;
                                    total += tapeWidthScore + verticalScore;

                                    //If the target is the best detected so far store the information about it
                                    if(total > target.totalScore)
                                    {
                                            target.horizontalIndex = horizontalTargets[j];
                                            target.verticalIndex = verticalTargets[i];
                                            target.totalScore = total;
                                            target.leftScore = leftScore;
                                            target.rightScore = rightScore;
                                            target.tapeWidthScore = tapeWidthScore;
                                            target.verticalScore = verticalScore;
                                    }
                                }
                                //Determine if the best target is a Hot target
                                target.Hot = hotOrNot(target);
                            }

                            if(verticalTargetCount > 0)
                            {
                                    //Information about the target is contained in the "target" structure
                                    //To get measurement information such as sizes or locations use the
                                    //horizontal or vertical index to get the particle report as shown below
                                    ParticleAnalysisReport distanceReport = filteredImage.getParticleAnalysisReport(target.verticalIndex);
                                    double distance = computeDistance(filteredImage, distanceReport, target.verticalIndex);
                                    if(target.Hot)
                                    {
                                            System.out.println("Hot target located");
                                            System.out.println("Distance: " + distance);
					    smartDashboard.putString("Distance to Target: ", Double.toString(distance));
                                    } else {
                                            System.out.println("No hot target present");
                                            System.out.println("Distance: " + distance);
					    smartDashboard.putString("Distance to Target: ", Double.toString(distance));
                                    }
                            }
                }

                /**
                 * all images in Java must be freed after they are used since they are allocated out
                 * of C data structures. Not calling free() will cause the memory to accumulate over
                 * each pass of this loop.
                 *//*
    
                filteredImage.free();
                thresholdImage.free();
                image.free();
                
           } catch(AxisCameraException ex){
	       System.out.println("Axis Camera done goofed.");
	       ex.printStackTrace();
	   } catch (NIVisionException ex) {
	        System.out.println("NIVision done goofed.");
                ex.printStackTrace();
            }
	   */
	     
             Timer.delay(.02);
        }


        //turn off motors once teleoperated ends
        System.out.println("Done teleoperated...");
    }
   
    int lastSign = 0;
    boolean switchPressed = false;
   public void xboxController(){
       
       //SHOOTER: 0
       //COLLECTOR: 1
       if(xboxControllers[activeController].getRawButton(6)){ //PRESS RIGHT BUMPER TO SWITCH CONTROLS
	   //Activate Mayro Kratt: Double Dash
	   //Only active controller can switch controls
	   if(activeController == 0){
	       activeController = 1;
	       smartDashboard.putString("Current control: ", "Collector");
	   }
	   else{
	       activeController = 0;
	       smartDashboard.putString("Current control: ", "Shooter");
	   }
       }
       
       //fire winch (left bumper on shooter)
       if(xboxControllers[1].getRawButton(5)){
	       shifterSolenoid.set(DoubleSolenoid.Value.kForward);
       }
  
       //collector, mechanical control (triggers)(right trigger = 1, left trigger = -1)
	double intakeTrigger = xboxControllers[0].getRawAxis(3);
	double shooterTrigger = xboxControllers[1].getRawAxis(3);
	

	leftRoller.set(intakeTrigger);
	rightRoller.set(-intakeTrigger);

	winchCim1.set(shooterTrigger);
	winchCim2.set(shooterTrigger);
	if(!winchLimit.get() && (!switchPressed || (int)(shooterTrigger / Math.abs(shooterTrigger)) == lastSign))	{
	    winchCim1.set(0);
	    winchCim2.set(0);
	    if(!switchPressed) {
		lastSign = (int)(shooterTrigger / Math.abs(shooterTrigger));
	    }
	    switchPressed = true;
	}
	else	{
	    switchPressed = false;
	}
	System.out.println(winchLimit.get());
       
       //y button: engage collector, engage winch
	if(xboxControllers[0].getRawButton(4)){
	    intakeSolenoid.set(DoubleSolenoid.Value.kForward);
	    System.out.println("Engage Collector");
	}
	
	if(xboxControllers[1].getRawButton(4)){
	    shifterSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	//x button: bring back collector, pull back winch
	if(xboxControllers[0].getRawButton(3)){
	    intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}/*
	if(xboxControllers[0].getRawButton(3)){
	    while(winchLimit.get())	{
		winchCim1.set(-.5);
		winchCim2.set(-.5);
	    }
	}*/
	 
	//Increment catapult up (B)
	if(xboxControllers[0].getRawButton(2)){
	    winchCim1.set(.5);
	    winchCim2.set(.5);
	    Timer.delay(.5);
	    winchCim1.set(0);
	    winchCim2.set(0);
	}
	//Increment catapult down (A)
	if(xboxControllers[0].getRawButton(1)){
	    winchCim1.set(-.5);
	    winchCim2.set(-.5);
	    Timer.delay(.5);
	    winchCim1.set(0);
	    winchCim2.set(0);
	}
   }
       
      /* if(xboxControllers[activeController].getRawAxis(3) == -1){ //PRESS RIGHT TRIGGER TO SHOOT
	   if(activeController == 1){ //Only Shooter can shoot
		//Fire Ur Lazor
		//Shift ur Superz Poonewmatekally

	        //kReverse = Solenoid out, kForward = Solenoid in
		shifterSolenoid.set(DoubleSolenoid.Value.kReverse);
		System.out.println("kReverse");
		Timer.delay(3);
		shifterSolenoid.set(DoubleSolenoid.Value.kForward);
		System.out.println("kForward");
		Timer.delay(3);
		winchEncoder.reset();
		winchEncoder.start();
		winchCim1.set(0.5);
		winchCim2.set(0.5);
		Timer.delay(1);
		winchCim1.set(0);
		winchCim2.set(0);
		winchEncoder.stop();
		System.out.println(winchEncoder.getRaw());
		System.out.println(winchEncoder.get());
		
		
		//MOTOR MAKE CATAPULT GO VROOM VROOM;
	    }
       }
       */
       /*
       if(xboxControllers[activeController].getRawButton(4)){ //PRESS Y BUTTON TO COLLECT
	   if(activeController == 0){ //Only Driver can collect
	       //Activate motors to make collectors spin
	       //Two Planetary Gearboxes
	       leftRoller.set(-0.5);
	       rightRoller.set(0.5);
	   }
       }
       else{
	   leftRoller.set(0);
	   rightRoller.set(0);
       }
       */
       
   

     /**
     * Computes the estimated distance to a target using the height of the particle in the image. For more information and graphics
     * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
     * 
     * @param image The image to use for measuring the particle estimated rectangle
     * @param re port The Particle Analysis Report for the particle
     * @param outer True if the particle should be treated as an outer target, false to treat it as a center target
     * @return The estimated distance to the target in Inches.
     */
//    public double computeDistance (BinaryImage image, ParticleAnalysisReport report, int particleNumber) throws NIVisionException {
//            double rectLong, height;
//            int targetHeight;
//
//            rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
//            //using the smaller of the estimated rectangle long side and the bounding rectangle height results in better performance
//            //on skewed rectangles
//            height = Math.min(report.boundingRectHeight, rectLong);
//            targetHeight = 32;
//
//            return Y_IMAGE_RES * targetHeight / (height * 12 * 2 * Math.tan(VIEW_ANGLE*Math.PI/(180*2)));
//    }
    
    /**
     * Computes a score (0-100) comparing the aspect ratio to the ideal aspect ratio for the target. This method uses
     * the equivalent rectangle sides to determine aspect ratio as it performs better as the target gets skewed by moving
     * to the left or right. The equivalent rectangle is the rectangle with sides x and y where particle area= x*y
     * and particle perimeter= 2x+2y
     * 
     * @param image The image containing the particle to score, needed to perform additional measurements
     * @param report The Particle Analysis Report for the particle, used for the width, height, and particle number
     * @param outer	Indicates whether the particle aspect ratio should be compared to the ratio for the inner target or the outer
     * @return The aspect ratio score (0-100)
     */
//    public double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean vertical) throws NIVisionException
//    {
//        double rectLong, rectShort, aspectRatio, idealAspectRatio;
//
//        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
//        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
//        idealAspectRatio = vertical ? (4.0/32) : (23.5/4);	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" owide x 4" tall
//	
//        //Divide width by height to measure aspect ratio
//        if(report.boundingRectWidth > report.boundingRectHeight){
//            //particle is wider than it is tall, divide long by short
//            aspectRatio = ratioToScore((rectLong/rectShort)/idealAspectRatio);
//        } else {
//            //particle is taller than it is wide, divide short by long
//            aspectRatio = ratioToScore((rectShort/rectLong)/idealAspectRatio);
//        }
//	return aspectRatio;
//    }
    
    /**
     * Compares scores to defined limits and returns true if the particle appears to be a target
     * 
     * @param scores The structure containing the scores to compare
     * @param outer True if the particle should be treated as an outer target, false to treat it as a center target
     * 
     * @return True if the particle meets all limits, false otherwise
     */
//    boolean scoreCompare(Team3455Robot.Scores scores, boolean vertical){
//	boolean isTarget = true;
//
//	isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
//	if(vertical){
//            isTarget &= scores.aspectRatioVertical > ASPECT_RATIO_LIMIT;
//	} else {
//            isTarget &= scores.aspectRatioHorizontal > ASPECT_RATIO_LIMIT;
//	}
//
//	return isTarget;
//    }
//    
    /**
     * Computes a score (0-100) estimating how rectangular the particle is by comparing the area of the particle
     * to the area of the bounding box surrounding it. A perfect rectangle would cover the entire bounding box.
     * 
     * @param report The Particle Analysis Report for the particle to score
     * @return The rectangularity score (0-100)
     */
    double scoreRectangularity(ParticleAnalysisReport report){
            if(report.boundingRectWidth*report.boundingRectHeight !=0){
                    return 100*report.particleArea/(report.boundingRectWidth*report.boundingRectHeight);
            } else {
                    return 0;
            }	
    }
    
    	/**
	 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
	 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
	 */
	double ratioToScore(double ratio)
	{
		return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
	}
	
	/**
	 * Takes in a report on a target and compares the scores to the defined score limits to evaluate
	 * if the target is a hot target or not.
	 * 
	 * Returns True if the target is hot. False if it is not.
	 */
	boolean hotOrNot(Team3455Robot.TargetReport target)
	{
		boolean isHot = true;
		
		isHot &= target.tapeWidthScore >= TAPE_WIDTH_LIMIT;
		isHot &= target.verticalScore >= VERTICAL_SCORE_LIMIT;
		isHot &= (target.leftScore > LR_SCORE_LIMIT) | (target.rightScore > LR_SCORE_LIMIT);
		
		return isHot;
	}
	
    public void test() {
	System.out.println("Testing...");
	
    }

   
}
