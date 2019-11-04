package com.github.fernthedev.light.exceptions;

import com.github.fernthedev.light.api.lines.ILightLine;

public class FileIsFolderException extends LightFileParseException {
    public FileIsFolderException(String message) {
        super(message);
    }

    public FileIsFolderException(ILightLine lightLine, String message) {
        super(lightLine, message);
    }

    public FileIsFolderException(ILightLine lightLine, Exception exception) {
        super(lightLine, exception);
    }

    public FileIsFolderException(ILightLine lightLine, String message, Exception exception) {
        super(lightLine, message, exception);
    }

    public FileIsFolderException(ILightLine lightLine) {
        super(lightLine);
    }
}
