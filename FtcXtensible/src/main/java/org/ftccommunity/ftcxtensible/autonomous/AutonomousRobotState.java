/*
 * Copyright © 2015 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 *  BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.autonomous;

import org.ftccommunity.ftcxtensible.autonomous.hardware.BasicSensor;
import org.ftccommunity.ftcxtensible.autonomous.hardware.Motor;
import org.ftccommunity.ftcxtensible.autonomous.hardware.Servo;
import org.ftccommunity.ftcxtensible.internal.Alpha;
import org.ftccommunity.ftcxtensible.internal.NotDocumentedWell;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Robot State object designed for managing the state of Robots in a Autonomous routine
 *
 * @author David Sargent
 * @since 0.2.0
 */
@Alpha
@NotDocumentedWell
public class AutonomousRobotState {
    private HashMap<String, Motor> motors;
    private HashMap<String, Servo> servos;
    private HashMap<String, BasicSensor> sensors;

    private RobotContext context;
    private Autonomous auto;
    private Thread autoThread;
    private AutoMode exec;

    /**
     * Recommended way for creating an autonomous RobotState
     *
     * @param ctx      main Robot Context
     * @param executor AutoMode handling the execution of the autonomous routine
     * @see org.ftccommunity.ftcxtensible.robot.RobotStatus
     * @see AutoMode
     */
    public AutonomousRobotState(@NotNull RobotContext ctx, @NotNull AutoMode executor) {
        auto = new Autonomous(null, ctx);
        autoThread = new Thread(auto);
        context = ctx;
        exec = executor;
    }

    /**
     * Backwards compatiblabilty, please migrate to the new constructor, this method will be removed
     *
     * @param robot_motors A motor array representing the motors to use
     * @param robot_servos A Servo Array representing the available servos
     */
    @Deprecated
    public AutonomousRobotState(Motor[] robot_motors, Servo[] robot_servos) {

        motors = new HashMap<>(robot_motors.length * 2);
        for (Motor motor : robot_motors) {
            motors.put(motor.GetName(), motor);
        }

        servos = new HashMap<>(robot_servos.length * 2);
        for (Servo servo : robot_servos) {
            servos.put(servo.GetName(), servo);
        }

        //Set sensors capacity to twice that of motors.length
        sensors = new HashMap<>(robot_motors.length * 2);
    }

    /**
     * Gets the main Robot Context
     *
     * @return the Robot Context
     */
    @NotNull
    public RobotContext context() {
        return context;
    }

    /**
     * Gets the Autonomous executioner
     *
     * @return the Autonomous executor
     */
    @NotNull
    public Autonomous autonomous() {
        return auto;
    }

    /**
     * Gets the parent OpMode running Autonomous
     *
     * @return parent OpMode to this Auto Routine
     */
    @NotNull
    public AutoMode parentMode() {
        return exec;
    }

    /**
     * Starts the RunLevel system
     */
    public void startAutonomous() {
        autoThread.start();
    }

    /**
     * Notifies all threads waiting on this object to an event
     */
    public void notifyAutonomous() {
        this.notifyAll();
    }

    /**
     * Returns if the Autonoums is still running
     *
     * @return whether or not, the executor is running
     */
    public boolean isInGoodState() {
        return autoThread.isAlive() && !auto.isDone();
    }

    /**
     * Performs the init of the executor by calling the Init RunLevel
     *
     * @see RunLevel
     */
    public void init() {
        auto.runInit();
    }

    /**
     * Gentlely stops the auto executor
     */
    public void gentleStop() {
        auto.runFinishLevel();
        auto.close();
    }

    /**
     * Notifies all threads waiting on this object, then aborts
     */
    public void stopNow() {
        autoThread.notifyAll();
        autoThread.interrupt();
    }

    /**
     * Add a motor for use by routine
     * @param motor the motor to use
     */
    @Deprecated
    public void addMotor(Motor motor) {
        motors.put(motor.GetName(), motor);
    }

    /**
     * Gets a motor for the routine
     *
     * @param name name of the motor
     * @return the motor associated with the given name, or null, if the name is not valid
     */
    @Deprecated
    public Motor getMotor(String name) {
        return motors.get(name);
    }

    @Deprecated
    public void addServo(Servo servo) {
        servos.put(servo.GetName(), servo);
    }

    @Deprecated
    public Servo getServo(String name) {
        return servos.get(name);
    }

    @Deprecated
    public void addIrSeeker(IrSensor sensor) {
        sensors.put(sensor.getName(), sensor);
    }

    @Deprecated
    public IrSensor getIrSeeker(String name) {
        return (IrSensor) sensors.get(name);
    }

    @Deprecated
    public synchronized void syncState() {
        for (HashMap.Entry<String, BasicSensor> entry : sensors.entrySet()) {
            entry.getValue().writeToHW();
        }

        for (HashMap.Entry<String, Motor> entry : motors.entrySet()) {
            entry.getValue().SetSpeedHW();
        }

        for (HashMap.Entry<String, Servo> entry : servos.entrySet()) {
            entry.getValue().SetPositionHW();
        }

        for (HashMap.Entry<String, BasicSensor> entry : sensors.entrySet()) {
            entry.getValue().readFromHW();
        }
    }
}
