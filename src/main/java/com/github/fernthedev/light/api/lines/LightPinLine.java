package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.GpioPinData;
import com.github.fernthedev.light.LightManager;
import com.github.fernthedev.light.exceptions.LightFileParseException;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.github.fernthedev.light.api.NullObject;
import com.github.fernthedev.light.api.annotations.LineArgument;
import com.github.fernthedev.light.api.annotations.LineData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@LineData(name = "pin")
public class LightPinLine extends ILightLine {


    @LineArgument(classTypes = {int.class, boolean.class})
    private Object pin;

    @LineArgument(name = "mode")
    private boolean toggle;

    public LightPinLine(@NonNull String line, int lineNumber, int pin, boolean toggle) {
        super(line, lineNumber);
        this.pin = pin;
        this.toggle = toggle;
    }

    public LightPinLine(int lineNumber,int pin, boolean toggle) {
        this(formatString(pin,toggle),lineNumber,pin,toggle);
    }

    public LightPinLine(int lineNumber,boolean allPins, boolean toggle) {
        this(formatString(toggle),lineNumber,allPins,toggle);
    }

    public LightPinLine(ILightLine lightLine, int pin, boolean toggle) {
        super(lightLine);
        this.pin = pin;
        this.toggle = toggle;
    }

    public LightPinLine(ILightLine lightLine, boolean allPins, boolean toggle) {
        super(lightLine);
        if(!allPins) throw new IllegalArgumentException("If not all pins specify pins");
        handlePins(allPins,toggle);
    }

    public LightPinLine(String line,int lineNumber, boolean allPins,boolean toggle) {
        super(line,lineNumber);
    }

    public LightPinLine(NullObject nullObject) {
        super(nullObject);
        this.pin = -1;
        this.toggle = false;
    }


    private void handlePins(boolean allPins, boolean toggle) {
        if(!allPins) throw new IllegalArgumentException("If not all pins specify pins");
        this.pin = true;
        this.toggle = toggle;
    }

    public static String formatString(int pin,boolean mode) {
        String mod;
        if(mode) mod = "on"; else mod = "off";
        return "pin " + pin + " "  + mod;
    }

    public static String formatString(boolean mode) {
        String mod;
        if(mode) mod = "on"; else mod = "off";
        return "pin all " + mod;
    }

    @Override
    public @NonNull ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("all")) {


                String newPar = args[1];

                boolean mode;

                if (newPar.equalsIgnoreCase("on")) {
                    mode = true;
                } else if (newPar.equalsIgnoreCase("off")) {
                    mode = false;
                } else {
                    throw new LightFileParseException(lightLine, "Could not find parameter " + newPar);
                }

                return new LightPinLine(lightLine, true, mode);


            } else if (args[0].matches("[0-9]+")) {
                int pinInt = Integer.parseInt(args[0]);


                String newPar = args[1];
                boolean mode;

                if (newPar.equalsIgnoreCase("on")) {
                    mode = true;
                } else if (newPar.equalsIgnoreCase("off")) {
                    mode = false;
                } else {
                    throw new LightFileParseException(lightLine, "Could not parse parameter " + newPar);
                }

                return new LightPinLine(lightLine, pinInt, mode);


            } else {
                throw new LightFileParseException(lightLine, "Argument " + args[0] + " can only be numerical.");
            }
        } else {
            throwException("Not enough arguments defined. \nFound: " + getArguments() + "\nRequired: " + getArguments(getClass()));
        }
        return null;
    }

    @Override
    public @NonNull ILightLine constructEmptyLightLine(ILightLine lightLine) {
        return new LightPinLine(lightLine, 0, false);
    }

    @Override
    public void execute() {
        GpioPinDigitalOutput output;
        if (pin instanceof Boolean) {
            for (GpioPinData pin : LightManager.getPinDataMap().values()) {

                output = pin.getOutput();

                if (isToggle()) {
                    output.high();
                } else {
                    output.low();
                }
            }
        }else if (pin instanceof Integer) {
            output = LightManager.getDataFromInt((int) pin).getOutput();

            if (isToggle()) {
                output.high();
            } else {
                output.low();
            }
        } else {
            LightManager.getLogger().error("The pin provided is not supported. Pin Object: {} type: {}", pin, pin.getClass());
        }
    }
}
