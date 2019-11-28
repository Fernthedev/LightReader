package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.LightManager;
import com.github.fernthedev.light.api.NullObject;
import com.github.fernthedev.light.api.annotations.LineArgument;
import com.github.fernthedev.light.api.annotations.LineData;
import com.github.fernthedev.light.api.annotations.LineRestArguments;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@LineData(name = "print")
public class LightPrintLine extends ILightLine {

    @LineArgument(name = "message")
    @LineRestArguments
    private String[] print;

    public LightPrintLine(@NonNull String line, int lineNumber, @NonNull String print) {
        super(line, lineNumber);
        this.print = new String[] {print};
    }

    public LightPrintLine(ILightLine lightLine, @NonNull String print) {
        super(lightLine);
        this.print = new String[] {print};
    }

    public LightPrintLine(NullObject nullObject) {
        super(nullObject);
        this.print = new String[] {""};
    }

    @Override
    public @NonNull ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        if(args.length > 0) {
            StringBuilder st = new StringBuilder();
            int t = 0;
            for (String se : args) {
                if (t > 0) st.append(" ");
                st.append(se);

                t++;
            }

            return new LightPrintLine(lightLine, st.toString());
        } else {
            throwException("Not enough arguments. \nFound: " + getArguments() + "\nRequired:" + getArguments(getClass()));
        }
        return null;
    }

    /**
     * Return true if you wish to handle the arguments in {@link #constructLightLine(ILightLine, String[])}
     *
     * @return
     */
    @Override
    public boolean requireManualArgumentHandling() {
        return false;
    }

    @Override
    public @NonNull ILightLine constructEmptyLightLine(ILightLine lightLine) {
        return new LightPrintLine(lightLine, "");
    }

    @Override
    public void execute() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : print) {
            stringBuilder.append(s).append(" ");
        }

        LightManager.getLogger().info(stringBuilder.toString());
    }
}
