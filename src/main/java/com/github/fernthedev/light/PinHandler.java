package com.github.fernthedev.light;

import com.pi4j.io.gpio.Pin;

import java.util.Map;

public interface PinHandler {
    /**
     * Gets the pin data
     * @param pinInt pin address
     * @return The pin registered, null if not found
     */
    GpioPinData getPin(Pin pinInt);

    /**
     * Gets the pin data and provisions if not found
     * @param pinInt pin address
     * @return The pin registered
     */
    GpioPinData getOrProvisionPin(Pin pinInt);

    /**
     * Unregisters the pin data
     * @param pinInt pin address
     * @return The pin registered,
     */
    void removePin(Pin pinInt);

    /**
     * Unregisters the pin data
     * @param pinData pin address
     * @return The pin registered,
     */
    void removePin(GpioPinData pinData);

    /**
     * Get pins with their associated data
     * @return
     */
    Map<Pin, GpioPinData> getPinDataMap();
}
