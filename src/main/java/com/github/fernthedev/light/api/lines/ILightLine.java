package com.github.fernthedev.light.api.lines;

import com.github.fernthedev.light.api.LightParser;
import com.github.fernthedev.light.exceptions.LightFileParseException;
import com.github.fernthedev.light.api.NullObject;
import com.github.fernthedev.light.api.annotations.LineData;
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

    /**
     * The current method of constructing LightLines is by calling {@link #constructEmptyLightLine(ILightLine)} and then using reflection to instantiate the variables in the order they are in the class.
     * @deprecated New method uses reflection for instantiating. Only use if using classes or specific ways of instantiating the variables and handling any other part of the construction that requires the variables.
     * @param lightLine The light line for getting the position and arguments
     * @param args The arguments provided in the command
     * @return The fully instantiated light line
     */
    @NonNull
    @Deprecated
    public ILightLine constructLightLine(ILightLine lightLine, String[] args) {throw new IllegalStateException("This method is deprecated. Override this method and override requireManualArgumentHandling() to return true");}

    @NonNull
    public abstract ILightLine constructEmptyLightLine(ILightLine lightLine);

    public abstract void execute();

    protected void throwException(String message) {
        throw new LightFileParseException(this, message);
    }

    protected void throwException(String message, Throwable e) {
        throw new LightFileParseException(this, message, e);
    }

    public String getArguments() {
        return LightParser.formatLightLineToString(this);
    }

    public String getArguments(Class<? extends ILightLine> aClass) {
        return LightParser.formatLightLineToString(aClass);
    }

    /**
     * Called if you have a {@link com.github.fernthedev.light.api.annotations.LineRestArguments} annotation on a String[] field
     * Use this to validate and parse the rest manually
     */
    public void validateRestArguments() {}

    /**
     * Return true if you wish to handle the arguments in {@link #constructLightLine(ILightLine, String[])}
     * @return
     */
    public boolean requireManualArgumentHandling() {
        return false;
    }

}
