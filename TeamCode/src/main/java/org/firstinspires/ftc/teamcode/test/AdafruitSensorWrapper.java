package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class AdafruitSensorWrapper {
    private final ColorSensor sensor;
    private Color rawBlue;
    private Color rawRed;
    private Color rawGreen;
    private double redBlueWhiteDelta = 0;
    private double redGreenWhiteDelta = 0;
    private List<ColorSample> colorSamples;
    private int sampleSize;
    private ColorSample sampleAverage;
    private int sampleHash;

    private boolean led;
    private int ledPort;
    private DeviceInterfaceModule dim;


    public AdafruitSensorWrapper(ColorSensor sensor, DeviceInterfaceModule dim, int ledPort, boolean enabled) {
        this.sensor = sensor;
        rawBlue = new Color();
        rawGreen = new Color();
        rawRed = new Color();

        if (dim != null) {
            this.dim = dim;
            this.ledPort = ledPort;
            led = enabled;
            dim.setDigitalChannelMode(0, DigitalChannelController.Mode.OUTPUT);
            dim.setDigitalChannelState(0, enabled);
        }
        update();
    }

    public void update() {
        rawRed.update(sensor.red(), 0);
        rawGreen.update(sensor.green(), (int) redGreenWhiteDelta);
        rawBlue.update(sensor.blue(), (int) redBlueWhiteDelta);
        normalize();
        updateLed();
        if (isSampling()) {
            colorSamples.add(new ColorSample(rawRed.raw, rawGreen.raw, rawBlue.raw));
        }
    }

    private void updateLed() {
        if (dim != null) {
            dim.setDigitalChannelState(ledPort, led);
        }
    }

    public void enableLed(boolean enabled) {
        if (dim == null) {
            throw new IllegalStateException("Not binded to a Device Interface Module");
        }
        if (ledPort < 0) {
            throw new IllegalStateException("The LED is not configured");
        }
        led = enabled;
        rawRed.resetLowHigh();
        rawBlue.resetLowHigh();
        rawGreen.resetLowHigh();
    }

    private void normalize() {
        // temp things
        int low = Math.min(Math.min(rawRed.raw, rawGreen.raw), rawBlue.raw);
        int high = Math.max(Math.max(rawRed.raw, rawGreen.raw), rawBlue.raw);

        if (low - high != 0) {
            int nRed = 0;
            if (rawRed.low != rawRed.high) {
                nRed = (int) Range.scale(rawRed.raw, rawRed.low, rawRed.high, low, high);
            }

            int nGreen = 0;
            if (rawGreen.low != rawGreen.high) {
                nGreen = (int) Range.scale(rawGreen.raw, rawGreen.low, rawGreen.high, low, high);
            }

            int nBlue = 0;
            if (rawBlue.low != rawBlue.high) {
                nBlue = (int) Range.scale(rawBlue.raw, rawBlue.low, rawBlue.high, low, high);
            }

            rawRed.normal = (int) Range.scale(nRed, low, high, 0, 255);
            rawGreen.normal = (int) Range.scale(nGreen, low, high, 0, 255);
            rawBlue.normal = (int) Range.scale(nBlue, low, high, 0, 255);
        }
    }

    public void defineColorAsNeutral(int red, int green, int blue) {
        redBlueWhiteDelta = red - blue;
        redGreenWhiteDelta = red - green;
    }


    public void beginColorSample(int size) {
        sampleSize = size;
        colorSamples = new ArrayList<>(size);
    }

    public boolean isSampling() {
        return colorSamples != null && !isSampleDone();
    }

    public boolean isSampleDone() {
        return sampleSize == colorSamples.size();
    }

    private void stopSampling() {
        sampleSize = colorSamples.size();
    }

    public int red() {
        return rawRed.normal;
    }

    public int rawRed() {
        return rawRed.raw;
    }

    public int blue() {
        return rawBlue.normal;
    }

    public int rawBlue() {
        return rawBlue.raw;
    }

    public int green() {
        return rawGreen.normal;
    }

    public int averageRed() {
        prepareAverageCache();
        return sampleAverage.red;
    }

    public int averageGreen() {
        prepareAverageCache();
        return sampleAverage.green;
    }

    public int averageBlue() {
        prepareAverageCache();
        return sampleAverage.blue;
    }

    private void prepareAverageCache() {
        if (colorSamples == null) {
            throw new IllegalStateException("No sample to use");
        }
        if (sampleAverage == null || sampleHash != colorSamples.hashCode()) {
            sampleAverage = getAverageColorForSample();
            sampleHash = colorSamples.hashCode();
        }
    }

    private ColorSample getAverageColorForSample() {
        if (colorSamples == null) {
            return null;
        }

        long red = 0, green = 0, blue = 0;
        for (ColorSample sample : colorSamples) {
            red += sample.red;
            green += sample.green;
            blue += sample.blue;
        }
        final int size = colorSamples.size();
        return new ColorSample((int) (red / size), (int) (green / size), (int) (blue / size));
    }

    public boolean isLedEnabled() {
        return led;
    }

    public int low(int raw) {
        return raw & 255;
    }

    public double getRedBlueWhiteDelta() {
        return redBlueWhiteDelta;
    }

    public double getRedGreenWhiteDelta() {
        return redGreenWhiteDelta;
    }

    public Colors redOrBlue() {
        if (led) return red() > blue() ? Colors.RED : red() == blue() ? Colors.NONE : Colors.BLUE;
        final double aRed = rawRed();
        if (Math.abs(aRed - rawBlue()) < 3) return Colors.NONE;
        return aRed > rawBlue() ? Colors.RED : (aRed) == rawBlue() ? Colors.NONE : Colors.BLUE;

    }

    public int rawGreen() {
        return rawGreen.raw;
    }


    public enum Colors {
        RED, GREEN, BLUE, NONE
    }

    private class Color {
        int raw;
        int normal;
        int low;
        int high;

        private void update(int color, int delta) {
            raw = color + delta;
            low = Math.min(low, raw);
            high = Math.max(high, raw);
        }

        public void resetLowHigh() {
            low = 0;
            high = 0;
        }
    }

    private class ColorSample {
        private final int red;
        private final int green;
        private final int blue;


        private ColorSample(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
