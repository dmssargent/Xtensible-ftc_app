# Xtensible OpMode Library
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

### Bugs:
- Lack of unit testing
- Dashboard somewhat works, we are catching an IndexOutOfBounds for whatever reason
- @Alpha annotated classes need to be stabilized

### Core syntactical changes:
#### Getting references to robot hardware
Old Way:
```java
hardwareMap.dcMotor.get("motor_1");
```
New Way:
```java
ctx.hardwareMap().getDcMotors().get("motor_1");
```
#### Getting access to a gamepad's left joystick X
Old Way:
```java
gamepad1.left_joystick.X;
```
New Way:
```java
gamepad1().leftJoystick().X();
```

#### Logging
Old Way:
```java
RobotLog.i("Hello World!");
```
New Way:
```java
ctx.log().i("Hi", "Hello World!");
```

#### Networking
Old Way:
There was never an old way.

New Way:
```java
ctx.enableNetworking().startNetworking();
```


Modifying the server parameter:
```java
ctx.enableNetworking();
// The default web directory is "/sdcard/FIRST/web"
ctx.getServerSettings().setWebDirectory("/put/here/where/your/web/directory/is");
ctx.startNetworking();
```

### Camera (Alpha)
Old Way:

Not something easy.

New Way:
```java
// What direction, relative to the screen, does the camera face?
ctx.cameraManager().bindCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
// Set up the capture
ctx.cameraManager().prepareForCapture();
// Set the delay for image capture
ctx.cameraManager().getPreviewCallback().setDelay(20000);

// Get the latest image
ctx.cameraManager().getNextImage();

// Register a post-processing callback
CameraImageCallback cb = new MyCameraImageCallback(ctx);
ctx.cameraManager().setImageProcessingCallback(cb);
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
hexafraction - Image Processing
Swerve Robotics - Sensor Class, and Dashboard functionalities


