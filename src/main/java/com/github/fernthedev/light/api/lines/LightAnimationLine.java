package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.animations.AnimationRunnable;
import com.github.fernthedev.light.animations.Animations;
import com.github.fernthedev.light.animations.LedStrip;
import com.github.fernthedev.light.api.NullObject;
import com.github.fernthedev.light.api.annotations.LineArgument;
import com.github.fernthedev.light.api.annotations.LineData;
import com.github.fernthedev.light.api.annotations.LineRestArguments;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
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

    @LineRestArguments
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
     * Called if you have a {@link LineRestArguments} annotation on a String[] field
     * Use this to validate and parse the rest manually
     */
    @Override
    public void validateRestArguments() {
        try {
            if (restArguments.length >= 2) {
                int amountOfLED = Integer.parseInt(restArguments[0]);

                float brightness = Float.parseFloat(restArguments[1]);


                ledStrip = new LedStrip(amountOfLED, brightness);

                List<String> methodArgs = new ArrayList<>();

                for (int i = 2; i < restArguments.length; i++) {
                    methodArgs.add(restArguments[i]);
                }

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
            animationRunnable.runAsync();
        } else {
            animationRunnable.run();
        }
    }
}
