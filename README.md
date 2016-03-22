# Xtensible Library
### Alpha Release
##### Code quality may be below usual standards
This is the FTC SDK that can be used to create an FTC Robot Controller app, with custom op modes.
The FTC Robot Controller app is designed to work in conjunction with the FTC Driver Station app.
The FTC Driver Station app is available through Google Play.

To use this SDK, download/clone the entire project to your local computer.
Use Android Studio to import the folder ("Import project (Eclipse ADT, Gradle, etc.)").

# Xtensible OpMode
This is the main "OpMode" class for this library. You can extend this class for use
within the FTC SDK. It also bootstraps our library for use.

### Known Issues:
- Lack of unit testing

### Core syntactical changes:
#### Getting references to robot hardware
Old Way:
```java
hardwareMap.dcMotor.get("motor_1");
```
New Way:
```java
hardwareMap().getDcMotors().get("motor_1");
```
#### Getting access to a gamepad's left joystick X
Old Way:
```java
gamepad1.left_joystick.X;
```
New Way:
```java
gamepad1.leftJoystick().X();
```

#### Logging
Old Way:
```java
RobotLog.i("Hello World!");
```
New Way:
```java
RobotLogger.i("Hi", "Hello World!");
```

#### Networking
Old Way:
There was never an old way.


**************************************************************************************

Release 16.03.09

 * Changes made to make the FTC SDK synchronous (significant change!)
    - waitOneFullHardwareCycle() and waitForNextHardwareCycle() are no longer needed and have been deprecated.
    - runOpMode() (for a LinearOpMode) is now decoupled from the system's hardware read/write thread.
    - loop() (for an OpMode) is now decoupled from the system's hardware read/write thread.
    - Methods are synchronous.
    - For example, if you call setMode(DcMotorController.RunMode.RESET_ENCODERS) for a motor, the encoder is guaranteed to be reset when the method call is complete.
    - For legacy module (NXT compatible), user no longer has to toggle between read and write modes when reading from or writing to a legacy device.
 * Changes made to enhance reliability/robustness during ESD event.
 * Changes made to make code thread safe.
 * Debug keystore added so that user-generated robot controller APKs will all use the same signed key (to avoid conflicts if a team has multiple developer laptops for example).
 * Firmware version information for Modern Robotics modules are now logged.
 * Changes made to improve USB comm reliability and robustness.
 * Added support for voltage indicator for legacy (NXT-compatible) motor controllers.
 * Changes made to provide auto stop capabilities for op modes.
    - A LinearOpMode class will stop when the statements in runOpMode() are complete.  User does not have to push the stop button on the driver station.
    - If an op mode is stopped by the driver station, but there is a run away/uninterruptible thread persisting, the app will log an error message then force itself to crash to stop the runaway thread.
 * Driver Station UI modified to display lowest measured voltage below current voltage (12V battery).
 * Driver Station UI modified to have color background for current voltage (green=good, yellow=caution, red=danger, extremely low voltage).
 * javadoc improved (edits and additional classes).
 * Added app build time to About activity for driver station and robot controller apps.
 * Display local IP addresses on Driver Station About activity.
 * Added I2cDeviceSynchImpl.
 * Added I2cDeviceSync interface.
 * Added seconds() and milliseconds() to ElapsedTime for clarity.
 * Added getCallbackCount() to I2cDevice.
 * Added missing clearI2cPortActionFlag.
 * Added code to create log messages while waiting for LinearOpMode shutdown.
 * Fix so Wifi Direct Config activity will no longer launch multiple times.
 * Added the ability to specify an alternate i2c address in software for the Modern Robotics gyro.

**************************************************************************************

Release 16.02.09

 * Improved battery checker feature so that voltage values get refreshed regularly (every 250 msec) on Driver Station (DS) user interface.
 * Improved software so that Robot Controller (RC) is much more resilient and “self-healing” to USB disconnects:
    - If user attempts to start/restart RC with one or more module missing, it will display a warning but still start up.
    - When running an op mode, if one or more modules gets disconnected, the RC & DS will display warnings,and robot will keep on working in spite of the missing module(s).
    - If a disconnected module gets physically reconnected the RC will auto detect the module and the user will regain control of the recently connected module.
    - Warning messages are more helpful (identifies the type of module that’s missing plus its USB serial number).
 * Code changes to fix the null gamepad reference when users try to reference the gamepads in the init() portion of their op mode.
 * NXT light sensor output is now properly scaled.  Note that teams might have to readjust their light threshold values in their op modes.
 * On DS user interface, gamepad icon for a driver will disappear if the matching gamepad is disconnected or if that gamepad gets designated as a different driver.
 * Robot Protocol (ROBOCOL) version number info is displayed in About screen on RC and DS apps.
 * Incorporated a display filter on pairing screen to filter out devices that don’t use the “<TEAM NUMBER>-“ format. This filter can be turned off to show all WiFi Direct devices.
 * Updated text in License file.
 * Fixed formatting error in OpticalDistanceSensor.toString().
 * Fixed issue on with a blank (“”) device name that would disrupt WiFi Direct Pairing.
 * Made a change so that the WiFi info and battery info can be displayed more quickly on the DS upon connecting to RC.
 * Improved javadoc generation.
 * Modified code to make it easier to support language localization in the future.

**************************************************************************************

New Way:
```java
enableNetworking().startNetworking();
```


Modifying the server parameter:
```java
enableNetworking();
// The default web directory is "/sdcard/FIRST/web"
getServerSettings().setWebDirectory("/put/here/where/your/web/directory/is");
startNetworking();
```

### Camera (Alpha)
Old Way:

Not something easy.

New Way:
```java
// What direction, relative to the screen, does the camera face?
cameraManager().bindCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
// Set up the capture
cameraManager().prepareForCapture();
// Set the delay for image capture
cameraManager().getPreviewCallback().setDelay(20000);

// Get the latest image
cameraManager().getNextImage();

// Register a post-processing callback
CameraImageCallback cb = new MyCameraImageCallback(ctx);
cameraManager().setImageProcessingCallback(cb);
```

### Controller Bindings
#### Under development, for internal use (for now)

Binds core values and objects present in a module for use in any other module without requiring
a dependency on that module. This is for use when there is no other sane, readable, or maintable way
to do something.


### Structure
 * FtcRobotController
     - doc - Documentation for the FTC SDK are included with this repository.
        - "apk" - contains the .apk files for the FTC Driver Station and FTC Robot Controller apps.
        - "javadoc" - contains the JavaDoc user documentation for the FTC SDK.
        - "tutorial" - contains PDF files that help teach the basics of using the FTC SDK.
     - src - contains the source code for the FTC SDK user-editable code portions
        - "opmodes" - provides user-defined OpModes
 * OpModeLibrary - This module is where you add your OpMode code (note that you must not have
    dependencies on the FtcRobotController module, but you may depend on its libraries)
 * FtcXtesible - This module contains our code to help you out with your programming

### Upstream Changelog
 * New user interfaces for FTC Driver Station and FTC Robot Controller apps.
 * An init() method is added to the OpMode class.
   - For this release, init() is triggered right before the start() method.
   - Eventually, the init() method will be triggered when the user presses an "INIT" button on driver station.
   - The init() and loop() methods are now required (i.e., need to be overridden in the user's op mode).
   - The start() and stop() methods are optional.
 * A new LinearOpMode class is introduced.
   - Teams can use the LinearOpMode mode to create a linear (not event driven) program model.
   - Teams can use blocking statements like Thread.sleep() within a linear op mode.
 * The API for the Legacy Module and Core Device Interface Module have been updated.
   - Support for encoders with the Legacy Module is now working.
 * The hardware loop has been updated for better performance.

### Authors
David Sargent, T. Eng, Jonathan Berling

### Credits
- hexafraction - Image Processing
- Swerve Robotics - Sensor Classes, I2C functionality, and Dashboard functionalities
- Ollie - Generic I2C Functionality


