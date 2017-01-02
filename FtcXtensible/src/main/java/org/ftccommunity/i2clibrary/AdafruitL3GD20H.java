package org.ftccommunity.i2clibrary;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;


public class AdafruitL3GD20H extends I2cDeviceSynchDevice<I2cDeviceSynch> {
    private final static byte L3GD20H_ID = (byte) 0xD7;
    private SensorRange sensorRange = SensorRange.RANGE_250DPS;

    protected AdafruitL3GD20H(I2cDeviceSynch deviceClient, boolean isOwned) {
        super(deviceClient, isOwned);

    }

    public void init() {


    }

    @Override
    protected boolean doInitialize() {
        deviceClient.setI2cAddress(new I2cAddr(0xD6));
        if (deviceClient.read8(Register.WHO_AM_I.bVal) !=  L3GD20H_ID)
            return false;
        deviceClient.write8(Register.CTRL_REG1.bVal, 0x0F);
        deviceClient.write8(Register.CTRL_REG4.bVal, sensorRange.range);

        deviceClient.setReadWindow(new I2cDeviceSynch.ReadWindow(Register.OUT_X_L.bVal, Register.OUT_Z_H.bVal, I2cDeviceSynch.ReadMode.REPEAT));

        return true;
    }

    public int readX() {
        return readAxis(Register.OUT_X_L, Register.OUT_X_H);
    }

    public double readY() {
        return readAxis(Register.OUT_Y_L, Register.OUT_Y_H);
    }

    public double readZ() {
        return readAxis(Register.OUT_Z_L, Register.OUT_Z_H);
    }

    private int readAxis(Register low, Register high) {
        return (int) (sensorRange.sensitivity * (deviceClient.read8(low.bVal) | (deviceClient.read8(high.bVal) << 8)));
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Adafruit;
    }

    @Override
    public String getDeviceName() {
        return "Adafruit L3GD20H";
    }



    enum Register {
        /**
         * Default: 11010100
         * Mode: r
         */
        WHO_AM_I(0x0F),
        /**
         * Default: 00000111
         * Mode: r/w
         */
        CTRL_REG1(0x20),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG2(0x21),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG3(0x22),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG4(0x23),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG5(0x24),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        REFERENCE(0x25),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_TEMP(0x26),
        /**
         * Default: N/A
         * Mode: r
         */
        STATUS_REG(0x27),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_X_L(0x28),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_X_H(0x29),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Y_L(0x2A),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Y_H(0x2B),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Z_L(0x2C),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Z_H(0x2D),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        FIFO_CTRL_REG(0x2E),
        /**
         * Default: N/A
         * Mode: r
         */
        FIFO_SRC_REG(0x2F),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        INT1_CFG(0x30),
        /** Default: N/A
         * Mode: r
         */
        INT1_SRC(0x31),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_XH(0x32),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_XL(0x33),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_YH(0x34),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_YL(0x35),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_ZH(0x36),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_ZL(0x37),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        INT1_DURATION(0x38);


        //public static final int CTRL_REG1 = ;
        int bVal;

        Register(int value) {
            bVal = value;
        }
    }

    enum SensorRange {
        RANGE_250DPS(0x00, 0.00875),
        RANGE_500DPS(0x10, 0.0175),
        RANGE_2000DPS(0x20, 0.070);

        public double sensitivity;
        int range;

        SensorRange(int val, double sensitivity) {
            range = val;
            this.sensitivity = sensitivity;
        }
    }
}
