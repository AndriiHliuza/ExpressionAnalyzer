package org.pzks;

import org.junit.jupiter.api.Test;
import org.pzks.utils.BasicExpressionUnitRecognizer;

import static org.assertj.core.api.Assertions.assertThat;

class BasicExpressionUnitRecognizerTest {

    private final BasicExpressionUnitRecognizer basicExpressionUnitRecognizer = new BasicExpressionUnitRecognizer();

    @Test
    public void shouldMatchIntegers() {
        boolean actual = basicExpressionUnitRecognizer.isIntegerNumber("563332");
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchVariableOrFunctionName() {
        boolean actualWithCharsAndNumbers = basicExpressionUnitRecognizer.isValidAlphaNumericNaming("abc123");
        assertThat(actualWithCharsAndNumbers).isTrue();

        boolean actualWithCharsAndNumbersAndUnderScore = basicExpressionUnitRecognizer.isValidAlphaNumericNaming("abc12_3");
        assertThat(actualWithCharsAndNumbersAndUnderScore).isTrue();
    }

    @Test
    public void shouldFailOnMatchForInvalidVariableOrFunctionName() {
        boolean actualStartingWithNumber = basicExpressionUnitRecognizer.isValidAlphaNumericNaming("1abc");
        assertThat(actualStartingWithNumber).isFalse();

        boolean actualWithSpecialCharacter = basicExpressionUnitRecognizer.isValidAlphaNumericNaming("a#bc");
        assertThat(actualWithSpecialCharacter).isFalse();

        boolean a = basicExpressionUnitRecognizer.isValidAlphaNumericNaming("#");
        assertThat(a).isFalse();
    }

    @Test
    public void shouldMatchSpecialCharactersAndSpaces() {
        boolean actualWithOpeningParenthesis = basicExpressionUnitRecognizer.isSpecialCharacters("(");
        assertThat(actualWithOpeningParenthesis).isTrue();

        boolean actualWithClosingParenthesis = basicExpressionUnitRecognizer.isSpecialCharacters(")");
        assertThat(actualWithClosingParenthesis).isTrue();

        boolean actualWithDot = basicExpressionUnitRecognizer.isSpecialCharacters(".");
        assertThat(actualWithDot).isTrue();

        boolean actualWithSpecialCharactersAndSpaces = basicExpressionUnitRecognizer.isSpecialCharacters("#  ?    *^.");
        assertThat(actualWithSpecialCharactersAndSpaces).isTrue();
    }



}