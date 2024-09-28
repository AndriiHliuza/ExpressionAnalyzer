package org.pzks.utils;

public class SyntaxUnitErrorMessageBuilder {
    private int errorPosition;
    private String message;
    private String errorDetails;

    public SyntaxUnitErrorMessageBuilder(int errorPosition, String message, String errorDetails) {
        this.errorPosition = errorPosition;
        this.message = message;
        this.errorDetails = errorDetails;
    }

    public SyntaxUnitErrorMessageBuilder(int errorPosition, String message) {
        this.errorPosition = errorPosition;
        this.message = message;
    }

    public int getErrorPosition() {
        return errorPosition;
    }

    public void setErrorPosition(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
        String basicMessage = Color.RED.getAnsiValue() +
                " ".repeat(5) +
                "- Error: " +
                Color.DEFAULT.getAnsiValue() +
                "[Position: " + errorPosition + "] " +
                message;
        String extraDetails = "\n";
        if (errorDetails != null && !errorDetails.isBlank()) {
            extraDetails = Color.RED.getAnsiValue() +
                    "\n" +
                    " ".repeat(7) +
                    "Error details: " +
                    Color.DEFAULT.getAnsiValue() +
                    errorDetails +
                    "\n";
        }

        return basicMessage + extraDetails;
    }
}
