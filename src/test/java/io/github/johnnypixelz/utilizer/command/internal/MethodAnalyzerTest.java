package io.github.johnnypixelz.utilizer.command.internal;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.Assert.*;

public class MethodAnalyzerTest {

    // Test methods with various signatures
    public static class TestCommands {
        public void noParams() {}
        public void senderOnly(CommandSender sender) {}
        public void playerOnly(Player player) {}
        public void consoleOnly(ConsoleCommandSender console) {}
        public void senderWithArgs(CommandSender sender, String name, int count) {}
        public void playerWithArgs(Player player, String message) {}
        public void noSenderWithArgs(String name, int count) {}
        public void stringOnly(String text) {}
        public void multipleArgs(String a, int b, boolean c, double d) {}
    }

    private Method getMethod(String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return TestCommands.class.getMethod(name, paramTypes);
    }

    // ==================== detectSenderType Tests ====================

    @Test
    public void testDetectSenderType_NoParams() throws Exception {
        Method method = getMethod("noParams");
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertNull(senderType);
    }

    @Test
    public void testDetectSenderType_CommandSender() throws Exception {
        Method method = getMethod("senderOnly", CommandSender.class);
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertEquals(CommandSender.class, senderType);
    }

    @Test
    public void testDetectSenderType_Player() throws Exception {
        Method method = getMethod("playerOnly", Player.class);
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertEquals(Player.class, senderType);
    }

    @Test
    public void testDetectSenderType_Console() throws Exception {
        Method method = getMethod("consoleOnly", ConsoleCommandSender.class);
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertEquals(ConsoleCommandSender.class, senderType);
    }

    @Test
    public void testDetectSenderType_StringFirst() throws Exception {
        Method method = getMethod("stringOnly", String.class);
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertNull(senderType);
    }

    @Test
    public void testDetectSenderType_NoSenderWithArgs() throws Exception {
        Method method = getMethod("noSenderWithArgs", String.class, int.class);
        Class<?> senderType = MethodAnalyzer.detectSenderType(method.getParameters());
        assertNull(senderType);
    }

    // ==================== extractCommandParameters Tests ====================

    @Test
    public void testExtractCommandParameters_NoParams() throws Exception {
        Method method = getMethod("noParams");
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(0, params.length);
    }

    @Test
    public void testExtractCommandParameters_SenderOnly() throws Exception {
        Method method = getMethod("senderOnly", CommandSender.class);
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(0, params.length);
    }

    @Test
    public void testExtractCommandParameters_SenderWithArgs() throws Exception {
        Method method = getMethod("senderWithArgs", CommandSender.class, String.class, int.class);
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(2, params.length);
        assertEquals(String.class, params[0].getType());
        assertEquals(int.class, params[1].getType());
    }

    @Test
    public void testExtractCommandParameters_PlayerWithArgs() throws Exception {
        Method method = getMethod("playerWithArgs", Player.class, String.class);
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(1, params.length);
        assertEquals(String.class, params[0].getType());
    }

    @Test
    public void testExtractCommandParameters_NoSenderWithArgs() throws Exception {
        Method method = getMethod("noSenderWithArgs", String.class, int.class);
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(2, params.length);
        assertEquals(String.class, params[0].getType());
        assertEquals(int.class, params[1].getType());
    }

    @Test
    public void testExtractCommandParameters_MultipleArgs() throws Exception {
        Method method = getMethod("multipleArgs", String.class, int.class, boolean.class, double.class);
        Parameter[] params = MethodAnalyzer.extractCommandParameters(method);
        assertEquals(4, params.length);
        assertEquals(String.class, params[0].getType());
        assertEquals(int.class, params[1].getType());
        assertEquals(boolean.class, params[2].getType());
        assertEquals(double.class, params[3].getType());
    }

    // ==================== extractCommandParameters with hasSender flag Tests ====================

    @Test
    public void testExtractCommandParameters_WithHasSenderTrue() throws Exception {
        Method method = getMethod("senderWithArgs", CommandSender.class, String.class, int.class);
        Parameter[] allParams = method.getParameters();
        Parameter[] params = MethodAnalyzer.extractCommandParameters(allParams, true);
        assertEquals(2, params.length);
    }

    @Test
    public void testExtractCommandParameters_WithHasSenderFalse() throws Exception {
        Method method = getMethod("noSenderWithArgs", String.class, int.class);
        Parameter[] allParams = method.getParameters();
        Parameter[] params = MethodAnalyzer.extractCommandParameters(allParams, false);
        assertEquals(2, params.length);
    }

    @Test
    public void testExtractCommandParameters_EmptyWithHasSenderTrue() throws Exception {
        Parameter[] params = MethodAnalyzer.extractCommandParameters(new Parameter[0], true);
        assertEquals(0, params.length);
    }

}
