package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.api.NullObject;
import lombok.NonNull;

public class LightLine extends ILightLine {
    public LightLine(@NonNull String line, int lineNumber) {
        super(line, lineNumber);
    }

    public LightLine(ILightLine lightLine) {
        super(lightLine);
    }

    @Override
    public String getArgumentName() {
        return null;
    }

    @Override
    public @NonNull ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        return lightLine;
    }

    public LightLine(NullObject nullObject) {
        super(nullObject);
    }

    @Override
    public void execute() {

    }
}
