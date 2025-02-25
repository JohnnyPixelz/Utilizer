package io.github.johnnypixelz.utilizer.evaluate;

import io.github.johnnypixelz.utilizer.config.evaluate.Evaluate;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class NumericEvaluationTest {

    @Test
    public void testBasicArithmetic() {
        assertEquals(0, Evaluate.toNumeric("3 + 5").compareTo(new BigDecimal("8")));
        assertEquals(0, Evaluate.toNumeric("15 - 5").compareTo(new BigDecimal("10")));
        assertEquals(0, Evaluate.toNumeric("4 * 5").compareTo(new BigDecimal("20")));
        assertEquals(0, Evaluate.toNumeric("25 / 5").compareTo(new BigDecimal("5")));
    }

    @Test
    public void testComplexExpressions() {
        assertEquals(0, Evaluate.toNumeric("2 + 3 * 4").compareTo(new BigDecimal("14")));
        assertEquals(0, Evaluate.toNumeric("(2 + 3)").compareTo(new BigDecimal("5")));
        assertEquals(0, Evaluate.toNumeric("(5 + 5) * 2 / 2").compareTo(new BigDecimal("10")));
        assertEquals(0, Evaluate.toNumeric("16 / (4 * 1)").compareTo(new BigDecimal("4")));
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals(0, Evaluate.toNumeric("-5").compareTo(new BigDecimal("-5")));
        assertEquals(0, Evaluate.toNumeric("3 - 5").compareTo(new BigDecimal("-2")));
        assertEquals(0, Evaluate.toNumeric("-3 * -2").compareTo(new BigDecimal("6")));
    }

    @Test
    public void testDecimalNumbers() {
        assertEquals(0, Evaluate.toNumeric("11 / 2").compareTo(new BigDecimal("5.5")));
        assertEquals(0, Evaluate.toNumeric("5.0 / 2").compareTo(new BigDecimal("2.5")));
        assertEquals(0, Evaluate.toNumeric("3 / 2").compareTo(new BigDecimal("1.5")));
    }

    @Test
    public void testNumericFunctions() {
        assertEquals(0, Evaluate.toNumeric("FLOOR(1.3)").compareTo(new BigDecimal("1")));
        assertEquals(0, Evaluate.toNumeric("floor(1.3)").compareTo(new BigDecimal("1")));
    }

    @Test
    public void testEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toNumeric("5 / 0"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toNumeric("()"));
        assertThrows(IllegalArgumentException.class, () -> Evaluate.toNumeric("invalid"));
    }

    @Test
    public void testWhitespaceHandling() {
        assertEquals(0, Evaluate.toNumeric("  3  +  4  ").compareTo(new BigDecimal("7")));
        assertEquals(0, Evaluate.toNumeric("  4 *   5 ").compareTo(new BigDecimal("20")));
    }

}
