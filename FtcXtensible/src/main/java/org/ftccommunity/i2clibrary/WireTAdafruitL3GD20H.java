package org.ftccommunity.i2clibrary;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// TODO: 11/18/2016 STUB
public class WireTAdafruitL3GD20H extends Wire2T<WireTAdafruitL3GD20H.Register> {
    public static final I2cAddr I2C_ADDRESS = I2cAddr.zero();

    public WireTAdafruitL3GD20H(@NotNull I2cDevice device) {
        super(device, I2C_ADDRESS);
    }

    public static void main() {
        WireTAdafruitL3GD20H test = new WireTAdafruitL3GD20H(null);
        test.write(Register.WHO_AM_I, 1);
    }

    enum Register implements Wire2T.Register {
        /**
         * Default: 11010100
         * Mode: r
         */
        WHO_AM_I(0x0F, RegisterType.NORMAL),
        /**
         * Default: 00000111
         * Mode: r/w
         */
        CTRL_REG1(0x20, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG2(0x21, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG3(0x22, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG4(0x23, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        CTRL_REG5(0x24, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        REFERENCE(0x25, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_TEMP(0x26, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        STATUS_REG(0x27, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_X_L(0x28, RegisterType.LOW_HIGH),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_X_H(0x29, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Y_L(0x2A, RegisterType.LOW_HIGH),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Y_H(0x2B, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Z_L(0x2C, RegisterType.LOW_HIGH),
        /**
         * Default: N/A
         * Mode: r
         */
        OUT_Z_H(0x2D, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        FIFO_CTRL_REG(0x2E, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        FIFO_SRC_REG(0x2F, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        INT1_CFG(0x30, RegisterType.NORMAL),
        /**
         * Default: N/A
         * Mode: r
         */
        INT1_SRC(0x31, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_XH(0x32, RegisterType.HIGH_LOW),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_XL(0x33, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_YH(0x34, RegisterType.HIGH_LOW),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_YL(0x35, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_ZH(0x36, RegisterType.HIGH_LOW),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        TSH_ZL(0x37, RegisterType.NORMAL),
        /**
         * Default: 00000000
         * Mode: r/w
         */
        INT1_DURATION(0x38, RegisterType.NORMAL);

        int address;
        RegisterType type;

        Register(int value, RegisterType type) {
            address = value;
            this.type = type;
        }

        @Contract(pure = true)
        public byte address() {
            return (byte) address;
        }

        @Contract(pure = true)
        public RegisterType type() {
            return type;
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
