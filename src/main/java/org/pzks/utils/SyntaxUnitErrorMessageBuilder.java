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

    @Override
    public String toString() {
        String basicMessage = Color.RED.getAnsiValue() +
                " ".repeat(5) +
                "- Error: " +
                Color.DEFAULT.getAnsiValue() +
                "[Position: " + errorPosition + "] " +
                message;
        String extraDetails = "";
        if (errorDetails != null && !errorDetails.isBlank()) {
            extraDetails += Color.RED.getAnsiValue() +
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
