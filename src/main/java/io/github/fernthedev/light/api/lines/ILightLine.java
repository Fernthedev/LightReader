package io.github.fernthedev.light.api.lines;

import io.github.fernthedev.light.api.LightParser;
import io.github.fernthedev.light.api.NullObject;
import io.github.fernthedev.light.api.annotations.LineData;
import io.github.fernthedev.light.exceptions.LightFileParseException;
import lombok.*;

@Data
@EqualsAndHashCode()
public abstract class ILightLine {

    @NonNull
    private final String line;

    private final int lineNumber;


    @Getter(AccessLevel.NONE)
    private final String argumentName;

    public ILightLine(ILightLine lightLine) {
        this(lightLine.getLine(), lightLine.getLineNumber());
    }

    public ILightLine(NullObject nullObject) {
        this("", 0);
    }

    public ILightLine(@NonNull String line, int lineNumber) {
        this.line = line;
        this.lineNumber = lineNumber;

        argumentName = getArgumentName();
    }


    @NonNull
    public String getArgumentName() {
        if(argumentName != null) return argumentName;

        if(!getClass().isAnnotationPresent(LineData.class)) throw new IllegalStateException("Class requires LineData annotation");

        return getClass().getAnnotation(LineData.class).name();
    }

    @NonNull
    public abstract ILightLine constructLightLine(ILightLine lightLine, String[] args);

    public abstract void execute();

    protected void throwException(String message) {
        throw new LightFileParseException(this, message);
    }

    public String getArguments() {
        return LightParser.formatLightLineToString(this);
    }

    public String getArguments(Class<? extends ILightLine> aClass) {
        return LightParser.formatLightLineToString(aClass);
    }

}
