package org.ftc.opmodes;

import android.widget.LinearLayout;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.ftccommunity.ftcxtensible.collections.ArrayQueue;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;

import java.util.concurrent.locks.Lock;

@Autonomous
public class TeamClutchAutoA extends LinearOpMode {
    private DcMotor right0;
    private DcMotor right1;
    private DcMotor left0;
    private DcMotor left1;

    private DcMotor armLift;
    private DcMotor armWrench;

    private int wrenchSpeed;
    private int armSpeed;

    private long time;

    @Override
    public void runOpMode() throws InterruptedException {
        right0 = hardwareMap.dcMotor.get("motor0");
        right1 = hardwareMap.dcMotor.get("motor1");
        left0 = hardwareMap.dcMotor.get("motor2");
        left1 = hardwareMap.dcMotor.get("motor3");

        armWrench = hardwareMap.dcMotor.get("armWrench");
        armLift = hardwareMap.dcMotor.get("armLift");

        right0.setDirection(DcMotor.Direction.REVERSE);
        right1.setDirection(DcMotor.Direction.REVERSE);

       //Wire wire = new Wire(hardwareMap, "colorS", 0x29);

        // Start up
        while (opModeIsActive()) {
            right0.setPower(.5);
            right1.setPower(.5);
            left0.setPower(.5);
            left1.setPower(.5);
            sleep(500);

            right0.setPower(.25);
            right1.setPower(.25);
            sleep(500);

            right0.setPower(.5);
            right1.setPower(.5);
            left0.setPower(.5);
            left1.setPower(.5);
            sleep(5000);

            right0.setPower(.25);
            right1.setPower(.25);
            sleep(500);

            right1.setPower(-.5);
            right0.setPower(-.5);
            sleep(500);

            right0.setPower(.6);
            right1.setPower(.6);
            sleep(1000);

            left1.setPower(0);
            left0.setPower(0);
            right1.setPower(0);
            right0.setPower(0);
        }
    }

    /**
     * Builds an array of the target position of the motors. The index of the target value array corresponds to
     * the motor that was passed in the vararg DcMotor parameter. So the first motor's target value
     * is in index 0, and the last motor is at n-1.
     *
     * @param inches the number of inches to move
     * @param motors the motors to create a target position for
     * @return the array of target values to each respective {@code DcMotor}
     * @throws IllegalStateException if a motor passed is not in a
     *  {@link com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode#READ_ONLY} mode
     */
    private int[] wait4Inches(int inches, DcMotor... motors) throws IllegalStateException {
        // 2 * pi * r = 2*3.14*2 = 12.57in per 360 deg
        // 360/12.57 = 28.65 deg per in
        // 1440 ticks per encoder revolution, 4 ticks per deg
        int encoderValue = (int) Math.round((float) inches * 28.65 * 4);      //Now in ticks

        int[] motorTargets = new int[motors.length];
        int i = 0;
        for (DcMotor motor : motors) {
            // check for in read mode...
            if (motor.getController().getMotorControllerDeviceMode()  != DcMotorController.DeviceMode.READ_ONLY) {
                throw new IllegalStateException("Invalid state on DcMotor " + motor.toString());
            }

            int motorTicks = motor.getCurrentPosition();
            if (motor.getDirection() == DcMotor.Direction.REVERSE) {
                motorTargets[i] = motorTicks - encoderValue;
            } else {
                motorTargets[i] = motorTicks + encoderValue;
            }
            i++;
        }

        return motorTargets;
        /*int LeftMotorTarget, RightMotorTarget;
        boolean LeftMotorDone, RightMotorDone
        /*if (motor0.getController().getMotorControllerDeviceMode() ==
                DcMotorController.DeviceMode.READ_ONLY &&
                motor1.getController().getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {
            int motor0EncoderTicks = motor0.getCurrentPosition();
            int motor1EncoderTicks = motor1.getCurrentPosition();
        }

        //int CurLeftMotorTick = nMotorEncoder[leftMotor];
        //int CurRightMotorTick = nMotorEncoder[rightMotor];
        if (bMotorReflected[leftMotor]) {
            nMotorEncoderTarget[leftMotor] = LeftMotorTarget = nMotorEncoder[leftMotor] - encoderValue;
        } else {
            nMotorEncoderTarget[leftMotor] = LeftMotorTarget = nMotorEncoder[leftMotor] + encoderValue;
        }
        if (bMotorReflected[rightMotor]) {
            nMotorEncoderTarget[rightMotor] = RightMotorTarget = nMotorEncoder[rightMotor] - encoderValue;
        } else {
            nMotorEncoderTarget[rightMotor] = RightMotorTarget = nMotorEncoder[rightMotor] + encoderValue;
        }

        // doesn't account for overflow of Encoder value

//              LeftMotorDone = abs(nMotorEncoder[leftMotor] - LeftMotorTarget) < encoderValue;
//              RightMotorDone = abs(nMotorEncoder[rightMotor] - RightMotorTarget) < encoderValue;
//              while(!LeftMotorDone && !RightMotorDone) {
//                      LeftMotorDone = abs(nMotorEncoder[leftMotor] - LeftMotorTarget) < encoderValue;
//                      RightMotorDone = abs(nMotorEncoder[rightMotor] - RightMotorTarget) < encoderValue;
//              }
        LeftMotorDone = true;
        RightMotorDone = false;
        if (LeftMotorDone) { // Left done, wait for Right
            if (bMotorReflected[rightMotor]) {
                RightMotorDone = (nMotorEncoder[rightMotor] - RightMotorTarget) <= 0;
            } else {
                RightMotorDone = (nMotorEncoder[rightMotor] - RightMotorTarget) >= 0;
            }
            while (!RightMotorDone) {
                CurLeftMotorTick = nMotorEncoder[leftMotor];
                CurRightMotorTick = nMotorEncoder[rightMotor];
                if (bMotorReflected[rightMotor]) {
                    RightMotorDone = (nMotorEncoder[rightMotor] - RightMotorTarget) <= 0;
                } else {
                    RightMotorDone = (nMotorEncoder[rightMotor] - RightMotorTarget) >= 0;
                }
            }
        } else { // Right done, wait for Left
            if (bMotorReflected[leftMotor]) {
                LeftMotorDone = (nMotorEncoder[leftMotor] - LeftMotorTarget) <= 0;
            } else {
                LeftMotorDone = (nMotorEncoder[leftMotor] - LeftMotorTarget) >= 0;
            }
            while (!LeftMotorDone) {
                if (bMotorReflected[leftMotor]) {
                    LeftMotorDone = (nMotorEncoder[leftMotor] - LeftMotorTarget) <= 0;
                } else {
                    LeftMotorDone = (nMotorEncoder[leftMotor] - LeftMotorTarget) >= 0;
                }
            }
        }*/
    }

    private boolean hasElapsed(long start, long seconds) {
        return System.nanoTime() > start + seconds * 1E9;
    }


    public class Wire implements I2cController.I2cPortReadyCallback {
// --------------------------------- CONSTANTS -------------------------------------------------

        // Cache buffer constant values
        static final byte
                READ_MODE       = 0x00 - 128,   // MSB = 1
                WRITE_MODE      = 0x00;         // MSB = 0

        // Cache buffer index values
        static final int
                CACHE_MODE      = 0,            // MSB = 1 when read mode is active
                DEV_ADDR        = 1,            // Device address
                REG_NUMBER      = 2,            // Register address
                REG_COUNT       = 3,            // Register count
                DATA_OFFSET     = 4,            // First byte of transferred data
                LAST_INDEX      = 29,           // Last index available for data
                ACTION_FLAG     = 31,           // 0 = idle, -1 = transfer is active
                CACHE_SIZE      = 32;           // dCache fixed size

        // --------------------------------- CLASS VARIABLES -------------------------------------------
        private ArrayQueue<Element> downQueue;  // Down stream buffer
        private ArrayQueue<Element> upQueue;    // Up stream buffer
        private I2cDevice wireDev;              // Generic I2C Device Object
        private byte wireDevAddr;               // Generic Device Address
        private byte[] rCache;                  // Read Cache
        private byte[] wCache;                  // Write Cache
        private Lock rLock;                     // Lock for Read Cache
        private Lock wLock;                     // Lock for Write Cache & request queue

        private byte[] dCache;                  // Buffer for down stream details
        private int dNext;                      // Next location for incoming bytes
        private byte[] uCache;                  // Buffer for up stream response
        private int uNext;                      // Next location for response bytes
        private int uLimit;                     // Last location for response bytes
        private long uMicros;                   // Time stamp, microseconds since start
        private long startTime;                 // Start time in nanoseconds
        private boolean isIdle;                 // Mechanism to control polling

// --------------------------------- CLASS INIT AND CLOSE ---------------------------------------

        public Wire(HardwareMap hardwareMap, String deviceName, int devAddr) {
            downQueue   = new ArrayQueue<>();
            upQueue     = new ArrayQueue<>();
            wireDev     = hardwareMap.i2cDevice.get(deviceName);
            wireDevAddr = (byte) devAddr;

            rCache      = wireDev.getI2cReadCache();
            wCache      = wireDev.getI2cWriteCache();
            rLock       = wireDev.getI2cReadCacheLock();
            wLock       = wireDev.getI2cWriteCacheLock();

            dCache      = new byte[CACHE_SIZE];
            dNext       = DATA_OFFSET;
            uCache      = new byte[CACHE_SIZE];
            uMicros     = 0L;
            startTime   = System.nanoTime();
            uNext       = DATA_OFFSET;
            uLimit      = uNext;
            isIdle      = true;

            wireDev.registerForI2cPortReadyCallback(this);
        }

        public void close() {
            wireDev.deregisterForPortReadyCallback();
            downQueue.close();
            upQueue.close();
            wireDev.close();
        }

        //------------------------------------------------- Public Methods -------------------------
        public void beginWrite(int regNumber) {
            dCache[CACHE_MODE]  = WRITE_MODE;
            dCache[DEV_ADDR]    = wireDevAddr;
            dCache[REG_NUMBER]  = (byte) regNumber;
            dNext               = DATA_OFFSET;
        }

        public void write(int value) {
            if (dNext >= LAST_INDEX) return;    // Max write size has been reached
            dCache[dNext++] = (byte) value;
        }

        public void write(int regNumber, int value) {
            beginWrite(regNumber);
            write(value);
            endWrite();
        }

        public void writeHL(int regNumber, int value) {
            beginWrite(regNumber);
            write((byte) (value >> 8));
            write((byte) (value));
            endWrite();
        }

        public void writeLH(int regNumber, int value) {
            beginWrite(regNumber);
            write((byte) (value));
            write((byte) (value >> 8));
            endWrite();
        }

        public void endWrite() {
            dCache[REG_COUNT]   = (byte) (dNext - DATA_OFFSET);
            addRequest();
        }

        public void requestFrom(int regNumber, int regCount) {
            dCache[CACHE_MODE]  = READ_MODE;
            dCache[DEV_ADDR]    = wireDevAddr;
            dCache[REG_NUMBER]  = (byte) regNumber;
            dCache[REG_COUNT]   = (byte) regCount;
            addRequest();
        }

        public int responseCount() {
            int count = 0;
            try {
                rLock.lock();
                count = upQueue.length();
            } finally {
                rLock.unlock();
            }
            return count;
        }

        public boolean getResponse() {
            boolean responseReceived = false;
            uNext = DATA_OFFSET;
            uLimit = uNext;
            try {
                rLock.lock();                   // Explicit protection with rLock
                if (!upQueue.isEmpty()) {
                    responseReceived    = true;
                    uMicros             = getFromQueue(uCache, upQueue);
                    uLimit              = uNext + uCache[REG_COUNT];
                }
            } finally {
                rLock.unlock();
            }
            return responseReceived;
        }

        public boolean isRead()     {
            return (uCache[CACHE_MODE] == READ_MODE);
        }

        public boolean isWrite()    {
            return (uCache[CACHE_MODE] == WRITE_MODE);
        }

        public int registerNumber() {
            return uCache[REG_NUMBER] & 0xff;
        }

        public int deviceAddress() {
            return uCache[DEV_ADDR] & 0xff;
        }

        public long micros() {
            return uMicros;
        }

        public int available() {
            return uLimit - uNext;
        }

        public int read() {
            if (uNext >= uLimit) return 0;
            return uCache[uNext++] & 0xff;
        }

        public int readHL() {
            int high    = read();
            int low     = read();
            return 256 * high + low;
        }

        public int readLH() {
            int low     = read();
            int high    = read();
            return 256 * high + low;
        }

//------------------------------------------------- Main routine: Device CallBack -------------

        public void portIsReady(int port) {
            boolean isValidReply = false;
            try {
                rLock.lock();
                if (
                        rCache[CACHE_MODE]  == wCache[CACHE_MODE] &&
                                rCache[DEV_ADDR]    == wCache[DEV_ADDR] &&
                                rCache[REG_NUMBER]  == wCache[REG_NUMBER] &&
                                rCache[REG_COUNT]   == wCache[REG_COUNT]) {
                    storeReceivedData();                        // Store read/write data
                    rCache[REG_COUNT]   = -1;                   // Mark the reply used
                    isValidReply = true;
                }
            } finally {
                rLock.unlock();
            }
            if (isValidReply) {
                executeCommands();                              // Start next transmission
            } else {
                boolean isPollingRequired = false;
                try {
                    wLock.lock();                               // Protect the testing
                    isPollingRequired = (wCache[DEV_ADDR] == wireDevAddr);
                } finally {
                    wLock.unlock();
                }
                if (isPollingRequired) {
                    wireDev.readI2cCacheFromController();       // Keep polling active
                }
            }
        }

// --------------------------------- Commands to DIM -------------------------------------------

        private void executeCommands() {
            try {
                wLock.lock();
                if (downQueue.isEmpty()) {
                    isIdle  = true;
                    wCache[DEV_ADDR]    = -1;           // No further polling is required
                } else {
                    getFromQueue(wCache, downQueue);    // Ignore timestamp
                    wCache[ACTION_FLAG] = -1;
                }
            } finally {
                wLock.unlock();
            }
            if (!isIdle) {
                wireDev.writeI2cCacheToController();
            }
        }

        private void addRequest() {
            boolean isStarting = false;
            try {
                wLock.lock();
                if (isIdle) {
                    int length = DATA_OFFSET + dCache[REG_COUNT];
                    for (int i = 0; i < length; i++) wCache[i] = dCache[i];
                    wCache[ACTION_FLAG] = -1;
                    isIdle = false;
                    isStarting = true;
                } else {
                    addToQueue(0L, dCache, downQueue);
                }
            } finally {
                wLock.unlock();
            }
            if (isStarting) {
                wireDev.writeI2cCacheToController();
            }
        }

// --------------------------------- PROCESSING OF RECEIVED DATA -------------------------------

        private void storeReceivedData() {
            // rCache has been locked
            long uMicros = (System.nanoTime() - startTime) / 1000L;
            addToQueue(uMicros, rCache, upQueue);
        }

//------------------------------------------------- Add and Remove from Queue ------------------

        private void addToQueue(long timeStamp, byte[] cache, ArrayQueue queue) {
            int length          = DATA_OFFSET + cache[REG_COUNT];
            Element element     = new Element();
            element.timeStamp   = timeStamp;
            element.cache       = new byte[length];
            for (int i = 0; i < length; i++) element.cache[i] = cache[i];
            queue.add(element);
        }

        private long getFromQueue(byte[] cache, ArrayQueue queue) {
            Element element     = (Element) queue.remove();
            if (element == null) return 0;
            int length          = element.cache.length;
            long timeStamp      = element.timeStamp;
            for (int i = 0; i < length; i++) cache[i] = element.cache[i];
            return timeStamp;
        }

        class Element {
            public long timeStamp;
            public byte[] cache;
        }
    }
}
