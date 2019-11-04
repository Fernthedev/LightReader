package io.github.fernthedev.light.api.lines;

import io.github.fernthedev.light.api.NullObject;
import io.github.fernthedev.light.api.annotations.LineArgument;
import io.github.fernthedev.light.api.annotations.LineData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@LineData(name = LightSleepLine.ARGUMENT_NAME)
public class LightSleepLine extends ILightLine {

    protected static final String ARGUMENT_NAME = "sleep";

    @LineArgument(name = "sleepTimeMS")
    private double sleepDouble;

    public LightSleepLine(@NonNull String line, int lineNumber, double sleepDouble) {
        super(line, lineNumber);
        this.sleepDouble = sleepDouble;
    }

    public LightSleepLine(ILightLine lightLine, double sleepDouble) {
        super(lightLine);
        this.sleepDouble = sleepDouble;
    }

    public LightSleepLine(NullObject nullObject) {
        super(nullObject);
        this.sleepDouble = -1;
    }

    public static String formatString(double sleep) {
        return ARGUMENT_NAME + " " + sleep;
    }

    @Override
    public ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        if (args.length > 0) {
            String amount = args[0];
            if (amount.replaceAll("\\.", "").replace(",","").matches("[0-9]+")) {
                double time = Double.parseDouble(amount);
                return new LightSleepLine(lightLine, time);
            } else {
                throwException("The amount provided to sleep is not a number. It can only contain numbers, commands and decimals");
            }
        } else {
            throwException("No time provided to sleep. (it is in milliseconds)");
        }
        return null;
    }

    @Override
    public void execute() {
        try {
            Thread.sleep((long) sleepDouble);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
