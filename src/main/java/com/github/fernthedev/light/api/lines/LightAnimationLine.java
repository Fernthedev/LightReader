package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.animations.AnimationRunnable;
import com.github.fernthedev.light.animations.Animations;
import com.github.fernthedev.light.animations.LedStrip;
import com.github.fernthedev.light.api.NullObject;
import com.github.fernthedev.light.api.annotations.LineArgument;
import com.github.fernthedev.light.api.annotations.LineData;
import com.github.fernthedev.light.api.annotations.LineUnparsedArguments;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@LineData(name = "animation")
@ToString
public class LightAnimationLine extends ILightLine {

    private AnimationRunnable animationRunnable;

    private enum RUN_MODE {
        RUN,
        ASYNC
    }

    @LineArgument(name = "run_mode")
    private RUN_MODE runMode;

    @LineArgument(name = "animation_name")
    private String animationName;

    @LineUnparsedArguments
    @LineArgument
    private String[] restArguments;

    private LedStrip ledStrip;

    public LightAnimationLine(ILightLine lightLine) {
        super(lightLine);
    }

    public LightAnimationLine(NullObject nullObject) {
        super(nullObject);
    }

    public LightAnimationLine(@NonNull String line, int lineNumber) {
        super(line, lineNumber);
    }

    /**
     *
     * @param lightLine The light line for getting the position and arguments
     * @param args The arguments provided in the command
     * @deprecated New method uses reflection for instantiating. Only use if using classes or specific ways of instantiating the variables and handling any other part of the construction that requires the variables.
     * @return
     */
    @Override
    @Deprecated
    public @NonNull ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        return null;
    }

    @Override
    public @NonNull ILightLine constructEmptyLightLine(ILightLine lightLine) {
        return new LightAnimationLine(lightLine);
    }

    /**
     * Called if you have a {@link LineUnparsedArguments} annotation on a String[] field
     * Use this to validate and parse the rest manually
     */
    @Override
    public void validateUnparsedArguments() {
        try {
            if (restArguments.length >= 2) {
                int amountOfLED = Integer.parseInt(restArguments[0]);

                float brightness = Float.parseFloat(restArguments[1]);

                ledStrip = new LedStrip(amountOfLED, brightness);

                List<String> methodArgs = new ArrayList<>(Arrays.asList(restArguments).subList(2, restArguments.length));

                animationRunnable = Animations.runAnimation(animationName, ledStrip, methodArgs.toArray(new String[0]));


            } else {
                throwException("Not enough arguments provided. Required: [{amountOfLed (string)}, {brightness (float)}, {rest of arguments for animation type}");
            }
        } catch (Exception e) {
            throwException("Unable to manually parse rest of the arguments", e);
//            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        if (runMode == RUN_MODE.ASYNC) {
            animationRunnable.runAsync(ledStrip);
        } else {
            animationRunnable.run(ledStrip);
        }
    }
}
