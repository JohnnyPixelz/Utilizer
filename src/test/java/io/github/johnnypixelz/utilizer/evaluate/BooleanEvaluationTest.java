package io.github.johnnypixelz.utilizer.evaluate;

import io.github.johnnypixelz.utilizer.config.evaluate.Evaluate;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanEvaluationTest {

    @Test
    public void testBasicOperations() {
        assertTrue(Evaluate.toBoolean("true && yes"));
        assertTrue(Evaluate.toBoolean("true = yes"));
        assertTrue(Evaluate.toBoolean("true == yes"));
        assertTrue(Evaluate.toBoolean("true != no"));
        assertFalse(Evaluate.toBoolean("yes && no"));
        assertTrue(Evaluate.toBoolean("true || no"));
    }

    @Test
    public void testParenthesesAndNegation() {
        assertTrue(Evaluate.toBoolean("!(yes && no)"));
        assertTrue(Evaluate.toBoolean("(true || false)"));
        assertTrue(Evaluate.toBoolean("true && (false || yes)"));
        assertTrue(Evaluate.toBoolean("!false && (yes == true)"));
        assertFalse(Evaluate.toBoolean("!(true && (no || yes))"));
    }

    @Test
    public void testComplexBooleanExpressions() {
        assertFalse(Evaluate.toBoolean("(true && false) || (no && yes)"));
        assertTrue(Evaluate.toBoolean("!(false || no)"));
        assertFalse(Evaluate.toBoolean("no || (false && yes)"));
        assertTrue(Evaluate.toBoolean("!(false && true) && (yes || no)"));
        assertTrue(Evaluate.toBoolean("true && (!false || no)"));
    }

    @Test
    public void testNumbers() {
        assertTrue(Evaluate.toBoolean("true && 1"));
        assertTrue(Evaluate.toBoolean("2 && yes"));
        assertTrue(Evaluate.toBoolean("0 || 1"));
        assertFalse(Evaluate.toBoolean("true && 0"));
    }

    @Test
    public void testBooleanEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("true &&"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("&& true"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("yes && (no"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("(yes && no))"));
    }

    @Test
    public void testStringEquality() {
        assertTrue(Evaluate.toBoolean("\"weather\" == \"weather\""));
        assertTrue(Evaluate.toBoolean("weather == \"weather\""));
        assertTrue(Evaluate.toBoolean("\"weather\" == weather"));
        assertTrue(Evaluate.toBoolean("weather == weather"));
        assertFalse(Evaluate.toBoolean("\"hello\" == \"world\""));
        assertTrue(Evaluate.toBoolean("'abc' == 'abc'"));
        assertFalse(Evaluate.toBoolean("'abc' == 'ABC'")); // Case-sensitive?
    }

    @Test
    public void testStringInequality() {
        assertTrue(Evaluate.toBoolean("hello != hell0")); // Case-sensitive?
        assertTrue(Evaluate.toBoolean("java != python"));
        assertFalse(Evaluate.toBoolean("code != code"));
    }

    @Test
    public void testNumericComparisons() {
        assertTrue(Evaluate.toBoolean("3 + 5 == 8"));
        assertFalse(Evaluate.toBoolean("10 - 3 == 2"));
        assertTrue(Evaluate.toBoolean("4 * 2 == 8"));
        assertTrue(Evaluate.toBoolean("9 / 3 == 3"));
        assertFalse(Evaluate.toBoolean("5 * 2 == 15"));
    }

    @Test
    public void testComplexExpressions() {
        assertTrue(Evaluate.toBoolean("(3 + 5) == (2 * 4)"));
        assertFalse(Evaluate.toBoolean("5 + 5 == 11"));
        assertTrue(Evaluate.toBoolean("2 * 3 != 7"));
        assertTrue(Evaluate.toBoolean("(hello == hello) && (5 + 5 == 10)"));
        assertFalse(Evaluate.toBoolean("!(3 * 3 == 9)"));
    }

    @Test
    public void testArithmeticEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("hello === hellO"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toBoolean("5 / 0 == 0"));
    }

}
