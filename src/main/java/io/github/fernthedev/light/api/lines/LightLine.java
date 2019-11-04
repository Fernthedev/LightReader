package io.github.fernthedev.light.api.lines;

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

    @Override
    public void execute() {

    }
}
