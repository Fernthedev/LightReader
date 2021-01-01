package com.github.fernthedev.light;

import com.github.fernthedev.light.exceptions.NoPi4JLibsFoundException;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.NonNull;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LightManager {

    private static PinHandler pinHandler;

    private static final Logger logger = LoggerFactory.getLogger(LightManager.class);

    public static Logger getLogger() {
        return logger;
    }

    private static boolean init = false;

    @Synchronized
    private static boolean isInit() {
        return init;
    }

    @Synchronized
    public static void init(@NonNull PinHandler pinHandler) {
        if (!isInit()) {
            LightManager.pinHandler = pinHandler;
            try {

                init = true;
                logger.info("Loading pi4j java");


            } catch (UnsatisfiedLinkError | IllegalArgumentException e) {
                throw new NoPi4JLibsFoundException("Unable to find Pi4J Libraries", e);
            }
        }
        else throw new IllegalStateException("Already initialized");
    }

    private LightManager() {}

    @Synchronized
    public static GpioPinData getDataFromInt(int pinInt) {

        @NonNull Pin pin = getPinFromInt(pinInt);


        logger.debug(pin.toString());

        return pinHandler.getPin(pin);
    }

    /**
     * Gets pin from int
     * @param pin The pin int
     * @return The pin instance, null if none found (different raspberry pies have different amount of pins)
     */
    @Synchronized
    private static Pin getPinFromInt(int pin) {
        return RaspiPin.getPinByAddress(pin);
    }

    public static Map<Pin, GpioPinData> getPinDataMap() {
        return pinHandler.getPinDataMap();
    }
}
