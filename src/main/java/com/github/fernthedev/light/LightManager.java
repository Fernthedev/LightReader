package com.github.fernthedev.light;

import com.github.fernthedev.light.exceptions.NoPi4JLibsFoundException;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.system.SystemInfo;
import lombok.NonNull;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LightManager {

    private static GpioController gpio;

    @Synchronized
    public static LightManager getInstance() {
        if (!init) throw new IllegalStateException("init() was not called");
        return instance;
    }

    private static LightManager instance;

    private static final Map<@NonNull Pin,@NonNull GpioPinData> pinDataMap = new HashMap<>();


    private static Pin[] pins;

    @Synchronized
    public static Map<Pin, GpioPinData> getPinDataMap() {
        return pinDataMap;
    }

    @Synchronized
    public static Pin[] getPins() {
        return pins;
    }

    private static final Logger logger = LoggerFactory.getLogger(LightManager.class);

    @Synchronized
    public static Logger getLogger() {
        return logger;
    }

    private static boolean init = false;

    @Synchronized
    private static boolean isInit() {
        return init;
    }

    @Synchronized
    public static void init() {
        if (!isInit()) {

            try {

                init = true;
                instance = new LightManager();
                logger.info("Loading pi4j java");

                gpio = GpioFactory.getInstance();
                try {
                    pins = RaspiPin.allPins(SystemInfo.getBoardType());
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }

                try {
                    if (pins != null) {
                        for (Pin pin : pins) {
                            pinDataMap.put(pin, new GpioPinData(gpio.provisionDigitalOutputPin(pin, "LightManager" + pin.getName(), PinState.HIGH), pin, pin.getAddress()));
                        }
                    }
                } catch (GpioPinExistsException e) {
                    logger.error("Unable to check {}", e.getMessage());
                }
            } catch (UnsatisfiedLinkError | IllegalArgumentException e) {
                throw new NoPi4JLibsFoundException("Unable to find Pi4J Libraries", e);
            }
        }
    }

    private LightManager() {}

    @Synchronized
    public static GpioPinData getDataFromInt(int pinInt) {

        @NonNull Pin pin = getPinFromInt(pinInt);


        logger.debug(pin.toString());

        if(getPinDataMap().get(pin) == null) {
            getPinDataMap().put(pin, new GpioPinData(gpio.provisionDigitalOutputPin(pin, "GPIOData" + pinInt, PinState.HIGH), pin, pinInt));
        }


        // logger.info("CHecked " + lightManager.getPinDataMap().get(pin));
        //  logger.info("List: " + lightManager.getPinDataMap().keySet().toString());

        return getPinDataMap().get(pin);
    }

    /**
     * Gets pin from int
     * @param pin The pin int
     * @return The pin instance, null if none found (different raspberry pies have different amount of pins)
     */
    @Synchronized
    private static Pin getPinFromInt(int pin) {
        return RaspiPin.getPinByAddress(pin);
        /*
        for(int i =0; i < pins.length;i++) {
            if(pin == i) {
                return pins[i];
            }
        }*/

        //return null;
    }

}
