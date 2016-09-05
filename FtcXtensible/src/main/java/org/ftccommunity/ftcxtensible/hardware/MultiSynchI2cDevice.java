//package org.ftccommunity.ftcxtensible.hardware;
//
//import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
//import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
//import com.qualcomm.robotcore.hardware.I2cController;
//import com.qualcomm.robotcore.hardware.I2cDevice;
//import com.qualcomm.robotcore.hardware.I2cDeviceImpl;
//import com.qualcomm.robotcore.util.SerialNumber;
//
//import java.util.Locale;
//import java.util.concurrent.locks.Lock;
//
//public class MultiSynchI2cDevice extends ForwardingI2cDevice {
//
//    @Override
//    protected I2cDevice delegate() {
//        return null;
//    }
//
//    public I2cDevice createCopy() {
//        return new I2cDeviceImpl(delegate().getI2cController(), delegate().getPort());
//    }
//
//    public class ExtensibleI2cController extends ForwardingHardwareDevice<I2cController> implements I2cController {
//        private final I2cController delegate;
//        protected final int numberOfI2cPorts = 127;
//        protected final I2cController.I2cPortReadyBeginEndNotifications[]
//                i2cPortReadyBeginEndCallbacks = new I2cController.I2cPortReadyBeginEndNotifications[numberOfI2cPorts];
//        private boolean notificationsActive;
//
//
//        public ExtensibleI2cController(I2cController controller) {
//            delegate = controller;
//            notificationsActive = false;
//        }
//
//        private void throwIfI2cPortIsInvalid(int port) {
//            if(port < 0 || port >= this.numberOfI2cPorts) {
//                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "port %d is invalid; valid ports are %d..%d", port, 0, this.numberOfI2cPorts - 1));
//            }
//        }
//
//        @Override
//        public SerialNumber getSerialNumber() {
//            return null;
//        }
//
//        @Override
//        public void enableI2cReadMode(int i, int i1, int i2, int i3) {
//
//        }
//
//        @Override
//        public void enableI2cWriteMode(int i, int i1, int i2, int i3) {
//
//        }
//
//        @Override
//        public byte[] getCopyOfReadBuffer(int i) {
//            return new byte[0];
//        }
//
//        @Override
//        public byte[] getCopyOfWriteBuffer(int i) {
//            return new byte[0];
//        }
//
//        @Override
//        public void copyBufferIntoWriteBuffer(int i, byte[] bytes) {
//
//        }
//
//        @Override
//        public void setI2cPortActionFlag(int i) {
//
//        }
//
//        @Override
//        public void clearI2cPortActionFlag(int i) {
//
//        }
//
//        @Override
//        public boolean isI2cPortActionFlagSet(int i) {
//            return false;
//        }
//
//        @Override
//        public void readI2cCacheFromController(int i) {
//
//        }
//
//        @Override
//        public void writeI2cCacheToController(int i) {
//
//        }
//
//        @Override
//        public void writeI2cPortFlagOnlyToController(int i) {
//
//        }
//
//        @Override
//        public boolean isI2cPortInReadMode(int i) {
//            return false;
//        }
//
//        @Override
//        public boolean isI2cPortInWriteMode(int i) {
//            return false;
//        }
//
//        @Override
//        public boolean isI2cPortReady(int i) {
//            return false;
//        }
//
//        @Override
//        public Lock getI2cReadCacheLock(int i) {
//            return null;
//        }
//
//        @Override
//        public Lock getI2cWriteCacheLock(int i) {
//            return null;
//        }
//
//        @Override
//        public byte[] getI2cReadCache(int i) {
//            return new byte[0];
//        }
//
//        @Override
//        public byte[] getI2cWriteCache(int i) {
//            return new byte[0];
//        }
//
//        @Override
//        public void registerForI2cPortReadyCallback(I2cPortReadyCallback i2cPortReadyCallback, int i) {
//
//        }
//
//        @Override
//        public I2cPortReadyCallback getI2cPortReadyCallback(int i) {
//            return null;
//        }
//
//        @Override
//        public void deregisterForPortReadyCallback(int i) {
//
//        }
//
//        @Override
//        public synchronized void registerForPortReadyBeginEndCallback(I2cController.I2cPortReadyBeginEndNotifications callback, int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            if(callback == null) {
//                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "illegal null: registerForI2cNotificationsCallback(null,%d)", port));
//            } else {
//                this.deregisterForPortReadyBeginEndCallback(port);
//                this.i2cPortReadyBeginEndCallbacks[port] = callback;
//                if(this.notificationsActive) {
//                    try {
//                        callback.onPortIsReadyCallbacksBegin(port);
//                    } catch (InterruptedException var4) {
//                        Thread.currentThread().interrupt();
//                    }
//                }
//
//            }
//        }
//
//        @Override
//        public synchronized void deregisterForPortReadyBeginEndCallback(int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            if(this.i2cPortReadyBeginEndCallbacks[port] != null) {
//                try {
//                    this.i2cPortReadyBeginEndCallbacks[port].onPortIsReadyCallbacksEnd(port);
//                } catch (InterruptedException var3) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//
//            this.i2cPortReadyBeginEndCallbacks[port] = null;
//        }
//
//        @Override
//        public boolean isArmed() {
//            return false;
//        }
//
//        /**
//         * @param i
//         * @deprecated
//         */
//        @Override
//        public void readI2cCacheFromModule(int i) {
//
//        }
//
//        /**
//         * @param i
//         * @deprecated
//         */
//        @Override
//        public void writeI2cCacheToModule(int i) {
//
//        }
//
//        /**
//         * @param i
//         * @deprecated
//         */
//        @Override
//        public void writeI2cPortFlagOnlyToModule(int i) {
//
//        }
//
//        @Override
//        public synchronized I2cController.I2cPortReadyBeginEndNotifications getPortReadyBeginEndCallback(int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            return this.i2cPortReadyBeginEndCallbacks[port];
//        }
//
//        public void startupComplete() throws InterruptedException {
//            this.notificationsActive = true;
//            for(int var1 = 0; var1 < this.numberOfI2cPorts; ++var1) {
//                I2cController.I2cPortReadyBeginEndNotifications var2 = this.i2cPortReadyBeginEndCallbacks[var1];
//                if(var2 != null) {
//                    var2.onPortIsReadyCallbacksBegin(var1);
//                }
//            }
//        }
//
//        public void shutdownComplete() throws InterruptedException {
//            for(int var1 = 0; var1 < this.numberOfI2cPorts; ++var1) {
//                I2cController.I2cPortReadyBeginEndNotifications var2 = this.i2cPortReadyBeginEndCallbacks[var1];
//                if(var2 != null) {
//                    var2.onPortIsReadyCallbacksEnd(var1);
//                }
//            }
//
//            this.notificationsActive = false;
//        }
//
//        @Override
//        protected I2cController delegate() {
//            return delegate;
//        }
//    }
//
//    public class ExtensibleDeviceInterfaceModule extends
//            ExtensibleForwardingUsbI2cController implements
//            DeviceInterfaceModule {
//        private final DeviceInterfaceModule delegate;
//        private final int numberOfDevices =
//                ModernRoboticsUsbDeviceInterfaceModule.MAX_NEW_I2C_ADDRESS - ModernRoboticsUsbDeviceInterfaceModule.MIN_NEW_I2C_ADDRESS + 1;
//        private final I2cPortReadyCallback[] callbacks = new I2cPortReadyCallback[numberOfDevices];
//
//        public ExtensibleDeviceInterfaceModule(DeviceInterfaceModule delegate) {
//            this.delegate = delegate;
//        }
//
//        @Override
//        public I2cController delegate() {
//            return delegate;
//        }
//
//        public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback, int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            this.callbacks[port] = callback;
//        }
//
//        public I2cPortReadyCallback getI2cPortReadyCallback(int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            return this.callbacks[port];
//        }
//
//        public void deregisterForPortReadyCallback(int port) {
//            this.throwIfI2cPortIsInvalid(port);
//            this.callbacks[port] = null;
//        }
//
////        public void readComplete() throws InterruptedException {
////            if(this.e != null) {
////                byte var1 = this.read(3);
////
////                for(int var2 = 0; var2 < 6; ++var2) {
////                    if(this.e[var2] != null && this.a(var2, var1)) {
////                        this.e[var2].portIsReady(var2);
////                    }
////                }
////
////            }
////            delegate.readComplete()
////        }
//
//        protected void throwIfI2cPortIsInvalid(int port) {
//            if(port < 0 || port >= this.numberOfDevices) {
//                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "port %d is invalid; valid ports are %d..%d", port, 0, this.numberOfDevices - 1));
//            }
//        }
//
//        @Override
//        public int getDigitalInputStateByte() {
//            return delegate.getDigitalInputStateByte();
//        }
//
//        @Override
//        public void setDigitalIOControlByte(byte b) {
//            delegate.setDigitalIOControlByte(b);
//        }
//
//        @Override
//        public byte getDigitalIOControlByte() {
//            return delegate.getDigitalIOControlByte();
//        }
//
//        @Override
//        public void setDigitalOutputByte(byte b) {
//            delegate.setDigitalOutputByte(b);
//        }
//
//        @Override
//        public byte getDigitalOutputStateByte() {
//            return delegate.getDigitalOutputStateByte();
//        }
//
//        @Override
//        public boolean getLEDState(int i) {
//            return delegate.getLEDState(i);
//        }
//
//        @Override
//        public void setLED(int i, boolean b) {
//            delegate.setLED(i, b);
//        }
//
//        @Override
//        public int getAnalogInputValue(int i) {
//            return delegate.getAnalogInputValue(i);
//        }
//
//        @Override
//        public void setAnalogOutputVoltage(int i, int i1) {
//            delegate.setAnalogOutputVoltage(i, i1);
//        }
//
//        @Override
//        public void setAnalogOutputFrequency(int i, int i1) {
//            delegate.setAnalogOutputFrequency(i, i1);
//        }
//
//        @Override
//        public void setAnalogOutputMode(int i, byte b) {
//            delegate.setAnalogOutputMode(i, b);
//        }
//
//        @Override
//        public Mode getDigitalChannelMode(int i) {
//            return delegate.getDigitalChannelMode(i);
//        }
//
//        @Override
//        public void setDigitalChannelMode(int i, Mode mode) {
//            delegate.setDigitalChannelMode(i, mode);
//        }
//
//        @Override
//        public boolean getDigitalChannelState(int i) {
//            return delegate.getDigitalChannelState(i);
//        }
//
//        @Override
//        public void setDigitalChannelState(int i, boolean b) {
//            delegate.setDigitalChannelState(i, b);
//        }
//
//        @Override
//        public void setPulseWidthOutputTime(int i, int i1) {
//            delegate.setPulseWidthOutputTime(i, i1);
//        }
//
//        @Override
//        public void setPulseWidthPeriod(int i, int i1) {
//            delegate.setPulseWidthPeriod(i, i1);
//        }
//
//        @Override
//        public int getPulseWidthOutputTime(int i) {
//            return delegate.getPulseWidthOutputTime(i);
//        }
//
//        @Override
//        public int getPulseWidthPeriod(int i) {
//            return delegate.getPulseWidthOutputTime(i);
//        }
//    }
//
//    public abstract class ExtensibleForwardingUsbI2cController extends ForwardingHardwareDevice<I2cController> implements I2cController {
//        @Override
//        protected abstract I2cController delegate();
//
//        @Override
//        public SerialNumber getSerialNumber() {
//            return delegate().getSerialNumber();
//        }
//
//        @Override
//        public void enableI2cReadMode(int i, int i1, int i2, int i3) {
//            delegate().enableI2cReadMode(i, i1, i2, i3);
//        }
//
//        @Override
//        public void enableI2cWriteMode(int i, int i1, int i2, int i3) {
//            delegate().enableI2cWriteMode(i, i1, i2, i3);
//        }
//
//        @Override
//        public byte[] getCopyOfReadBuffer(int i) {
//            return delegate().getCopyOfReadBuffer(i);
//        }
//
//        @Override
//        public byte[] getCopyOfWriteBuffer(int i) {
//            return delegate().getCopyOfWriteBuffer(i);
//        }
//
//        @Override
//        public void copyBufferIntoWriteBuffer(int i, byte[] bytes) {
//            delegate().copyBufferIntoWriteBuffer(i, bytes);
//        }
//
//        @Override
//        public void setI2cPortActionFlag(int i) {
//delegate().setI2cPortActionFlag(i);
//        }
//
//        @Override
//        public void clearI2cPortActionFlag(int i) {
//            delegate().clearI2cPortActionFlag(i);
//        }
//
//        @Override
//        public boolean isI2cPortActionFlagSet(int i) {
//            return delegate().isI2cPortActionFlagSet(i);
//        }
//
//        @Override
//        public void readI2cCacheFromController(int i) {
//            delegate().readI2cCacheFromController(i);
//        }
//
//        @Override
//        public void writeI2cCacheToController(int i) {
//            delegate().writeI2cCacheToController(i);
//        }
//
//        @Override
//        public void writeI2cPortFlagOnlyToController(int i) {
//            delegate().writeI2cPortFlagOnlyToController(i);
//        }
//
//        @Override
//        public boolean isI2cPortInReadMode(int i) {
//            return delegate().isI2cPortInReadMode(i);
//        }
//
//        @Override
//        public boolean isI2cPortInWriteMode(int i) {
//            return delegate().isI2cPortInWriteMode(i);
//        }
//
//        @Override
//        public boolean isI2cPortReady(int i) {
//            return delegate().isI2cPortReady(i);
//        }
//
//        @Override
//        public Lock getI2cReadCacheLock(int i) {
//            return delegate().getI2cReadCacheLock(i);
//        }
//
//        @Override
//        public Lock getI2cWriteCacheLock(int i) {
//            return delegate().getI2cWriteCacheLock(i);
//        }
//
//        @Override
//        public byte[] getI2cReadCache(int i) {
//            return delegate().getI2cReadCache(i);
//        }
//
//        @Override
//        public byte[] getI2cWriteCache(int i) {
//            return delegate().getI2cWriteCache(i);
//        }
//
//        @Override
//        public void registerForI2cPortReadyCallback(I2cPortReadyCallback i2cPortReadyCallback, int i) {
//            delegate().registerForI2cPortReadyCallback(i2cPortReadyCallback, i);
//        }
//
//        @Override
//        public I2cPortReadyCallback getI2cPortReadyCallback(int i) {
//            return delegate().getI2cPortReadyCallback(i);
//        }
//
//        @Override
//        public void deregisterForPortReadyCallback(int i) {
//            delegate().deregisterForPortReadyCallback(i);
//        }
//
//        @Override
//        public void registerForPortReadyBeginEndCallback(I2cPortReadyBeginEndNotifications i2cPortReadyBeginEndNotifications, int i) {
//            delegate().registerForPortReadyBeginEndCallback(i2cPortReadyBeginEndNotifications, i);
//        }
//
//        @Override
//        public I2cPortReadyBeginEndNotifications getPortReadyBeginEndCallback(int i) {
//            return delegate().getPortReadyBeginEndCallback(i);
//        }
//
//        @Override
//        public void deregisterForPortReadyBeginEndCallback(int i) {
//            delegate().deregisterForPortReadyBeginEndCallback(i);
//        }
//
//        @Override
//        public boolean isArmed() {
//            return delegate().isArmed();
//        }
//
//        @Deprecated
//        @Override
//        public void readI2cCacheFromModule(int i) {
//            //noinspection deprecation
//            delegate().readI2cCacheFromModule(i);
//        }
//
//        @Deprecated
//        @Override
//        public void writeI2cCacheToModule(int i) {
//            //noinspection deprecation
//            delegate().writeI2cCacheToModule(i);
//        }
//
//        @Deprecated
//        @Override
//        public void writeI2cPortFlagOnlyToModule(int i) {
//            //noinspection deprecation
//            delegate().writeI2cPortFlagOnlyToModule(i);
//        }
//    }
//}
