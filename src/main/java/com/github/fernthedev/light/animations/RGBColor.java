package com.github.fernthedev.light.animations;

import lombok.Setter;

/**
 * {@link LedStrip} RGBLed private class
 */
@Setter
public class RGBColor {

    private int red;
    private int green;
    private int blue;

    /**
     * Initiate a single led in a led strip.
     *
     * @param red        value between 0 and 255 for the red led
     * @param green      value between 0 and 255 for the green led
     * @param blue       value between 0 and 255 for the blue led
     */
    public RGBColor(final int red, final int green, final int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * @return the value for the green led (between 0 and 255)
     */
    public int getGreen() {
        return green;
    }

    /**
     * @return the value for the blue led (between 0 and 255)
     */
    public int getBlue() {
        return blue;
    }

    /**
     * @return the value for the red led (between 0 and 255)
     */
    public int getRed() {
        return red;
    }

    public void setColor(int red, int green, int blue) {
        if(!isRange(red) || !isRange(green) || !isRange(blue)) {
            throw new IllegalArgumentException("The RGB color values cannot be less than 0 or greater than 255. " +
                    "\nRed: " + red + " IsRange:" + isRange(red) +
                    "\nGreen: " + green + " IsRange:" + isRange(green) +
                    "\nBlue: " + blue + " IsRange:" + isRange(blue));
        }

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    private boolean isRange(int color) {
        return color <= 255 && color >= 0;
    }
}
