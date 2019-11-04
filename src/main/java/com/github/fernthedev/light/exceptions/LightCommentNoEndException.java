package com.github.fernthedev.light.exceptions;

import com.github.fernthedev.light.api.lines.ILightLine;

public class LightCommentNoEndException extends LightFileParseException {
    public LightCommentNoEndException(ILightLine lightLine, String message) {
        super(lightLine, message);
    }

    public LightCommentNoEndException(ILightLine lightLine, Exception exception) {
        super(lightLine, exception);
    }

    public LightCommentNoEndException(ILightLine lightLine, String message, Exception exception) {
        super(lightLine, message, exception);
    }

    public LightCommentNoEndException(ILightLine lightLine) {
        super(lightLine);
    }
}
