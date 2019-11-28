package com.github.fernthedev.light.exceptions;

import com.github.fernthedev.light.api.lines.ILightLine;
import lombok.Getter;

public class LightFileParseException extends RuntimeException {

    @Getter
    protected ILightLine lightLine;


    protected LightFileParseException(String message) {
        super(message);
    }

    public LightFileParseException(ILightLine lightLine, String message) {
        super(formatErrorMessage(lightLine,message));
        handleLightLine(lightLine);
    }


    public LightFileParseException(ILightLine lightLine, Exception exception) {
        super(formatErrorMessage(lightLine),exception);
        handleLightLine(lightLine);
    }

    public LightFileParseException(ILightLine lightLine, String message, Throwable exception) {
        super(formatErrorMessage(lightLine,message), exception);
        handleLightLine(lightLine);
    }

    public LightFileParseException(ILightLine lightLine) {
        super(formatErrorMessage(lightLine));
        handleLightLine(lightLine);
    }

    protected void handleLightLine(ILightLine lightLine) {
        this.lightLine = lightLine;
    }

    protected static String formatErrorMessage(ILightLine lightLine) {
        return "Error at {" + lightLine.getLine() + "}:" + lightLine.getLineNumber();
    }

    protected static String formatErrorMessage(ILightLine lightLine, String message) {
        return formatErrorMessage(lightLine) +
                "\nCause of error: " + message;
    }

}
