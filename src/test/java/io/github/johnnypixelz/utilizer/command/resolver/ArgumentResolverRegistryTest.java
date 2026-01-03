package io.github.johnnypixelz.utilizer.command.resolver;

import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolutionException;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolver;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverContext;
import io.github.johnnypixelz.utilizer.command.internal.resolver.ArgumentResolverRegistry;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.Assert.*;

public class ArgumentResolverRegistryTest {

    private ArgumentResolverRegistry registry;

    @Before
    public void setUp() {
        registry = new ArgumentResolverRegistry();
    }

    // Helper to create a context with just an argument
    private ArgumentResolverContext createContext(String argument, Class<?> type) throws Exception {
        // Get a parameter of the specified type using reflection
        Method method = TestMethods.class.getMethod("method", type);
        Parameter parameter = method.getParameters()[0];
        return new ArgumentResolverContext(null, parameter, argument);
    }

    // Test methods class to get Parameter objects
    public static class TestMethods {
        public static void method(int i) {}
        public static void method(Integer i) {}
        public static void method(long l) {}
        public static void method(Long l) {}
        public static void method(double d) {}
        public static void method(Double d) {}
        public static void method(float f) {}
        public static void method(Float f) {}
        public static void method(short s) {}
        public static void method(Short s) {}
        public static void method(boolean b) {}
        public static void method(Boolean b) {}
        public static void method(char c) {}
        public static void method(Character c) {}
        public static void method(String s) {}
        public static void method(String[] s) {}
        public static void method(TestEnum e) {}
    }

    public enum TestEnum {
        VALUE_ONE,
        VALUE_TWO,
        ANOTHER_VALUE
    }

    // ==================== Integer Tests ====================

    @Test
    public void testResolveInteger() throws Exception {
        ArgumentResolverContext context = createContext("42", int.class);
        ArgumentResolver<?> resolver = registry.getResolver(int.class);
        assertNotNull(resolver);
        assertEquals(42, resolver.resolve(context));
    }

    @Test
    public void testResolveIntegerBoxed() throws Exception {
        ArgumentResolverContext context = createContext("123", Integer.class);
        ArgumentResolver<?> resolver = registry.getResolver(Integer.class);
        assertNotNull(resolver);
        assertEquals(123, resolver.resolve(context));
    }

    @Test
    public void testResolveNegativeInteger() throws Exception {
        ArgumentResolverContext context = createContext("-500", int.class);
        ArgumentResolver<?> resolver = registry.getResolver(int.class);
        assertEquals(-500, resolver.resolve(context));
    }

    @Test(expected = ArgumentResolutionException.class)
    public void testResolveInvalidInteger() throws Exception {
        ArgumentResolverContext context = createContext("not_a_number", int.class);
        ArgumentResolver<?> resolver = registry.getResolver(int.class);
        resolver.resolve(context);
    }

    // ==================== Long Tests ====================

    @Test
    public void testResolveLong() throws Exception {
        ArgumentResolverContext context = createContext("9999999999", long.class);
        ArgumentResolver<?> resolver = registry.getResolver(long.class);
        assertEquals(9999999999L, resolver.resolve(context));
    }

    // ==================== Double Tests ====================

    @Test
    public void testResolveDouble() throws Exception {
        ArgumentResolverContext context = createContext("3.14159", double.class);
        ArgumentResolver<?> resolver = registry.getResolver(double.class);
        assertEquals(3.14159, (Double) resolver.resolve(context), 0.00001);
    }

    @Test
    public void testResolveDoubleNegative() throws Exception {
        ArgumentResolverContext context = createContext("-99.5", double.class);
        ArgumentResolver<?> resolver = registry.getResolver(double.class);
        assertEquals(-99.5, (Double) resolver.resolve(context), 0.00001);
    }

    // ==================== Float Tests ====================

    @Test
    public void testResolveFloat() throws Exception {
        ArgumentResolverContext context = createContext("2.5", float.class);
        ArgumentResolver<?> resolver = registry.getResolver(float.class);
        assertEquals(2.5f, (Float) resolver.resolve(context), 0.0001f);
    }

    // ==================== Short Tests ====================

    @Test
    public void testResolveShort() throws Exception {
        ArgumentResolverContext context = createContext("100", short.class);
        ArgumentResolver<?> resolver = registry.getResolver(short.class);
        assertEquals((short) 100, resolver.resolve(context));
    }

    // ==================== Boolean Tests ====================

    @Test
    public void testResolveBooleanTrue() throws Exception {
        ArgumentResolver<?> resolver = registry.getResolver(boolean.class);

        String[] trueValues = {"true", "yes", "on", "1", "y", "t"};
        for (String value : trueValues) {
            ArgumentResolverContext context = createContext(value, boolean.class);
            assertTrue("Expected true for: " + value, (Boolean) resolver.resolve(context));
        }
    }

    @Test
    public void testResolveBooleanFalse() throws Exception {
        ArgumentResolver<?> resolver = registry.getResolver(boolean.class);

        String[] falseValues = {"false", "no", "off", "0", "n", "f", "random"};
        for (String value : falseValues) {
            ArgumentResolverContext context = createContext(value, boolean.class);
            assertFalse("Expected false for: " + value, (Boolean) resolver.resolve(context));
        }
    }

    @Test
    public void testResolveBooleanCaseInsensitive() throws Exception {
        ArgumentResolver<?> resolver = registry.getResolver(boolean.class);

        ArgumentResolverContext context = createContext("TRUE", boolean.class);
        assertTrue((Boolean) resolver.resolve(context));

        context = createContext("Yes", boolean.class);
        assertTrue((Boolean) resolver.resolve(context));
    }

    // ==================== Character Tests ====================

    @Test
    public void testResolveCharacter() throws Exception {
        ArgumentResolverContext context = createContext("a", char.class);
        ArgumentResolver<?> resolver = registry.getResolver(char.class);
        assertEquals('a', resolver.resolve(context));
    }

    @Test(expected = ArgumentResolutionException.class)
    public void testResolveCharacterTooLong() throws Exception {
        ArgumentResolverContext context = createContext("abc", char.class);
        ArgumentResolver<?> resolver = registry.getResolver(char.class);
        resolver.resolve(context);
    }

    // ==================== String Tests ====================

    @Test
    public void testResolveString() throws Exception {
        ArgumentResolverContext context = createContext("hello world", String.class);
        ArgumentResolver<?> resolver = registry.getResolver(String.class);
        assertEquals("hello world", resolver.resolve(context));
    }

    @Test
    public void testResolveStringArray() throws Exception {
        ArgumentResolverContext context = createContext("one two three", String[].class);
        ArgumentResolver<?> resolver = registry.getResolver(String[].class);
        String[] result = (String[]) resolver.resolve(context);
        assertArrayEquals(new String[]{"one", "two", "three"}, result);
    }

    // ==================== Enum Tests ====================

    @Test
    public void testResolveEnum() throws Exception {
        ArgumentResolverContext context = createContext("VALUE_ONE", TestEnum.class);
        ArgumentResolver<?> resolver = registry.getResolver(TestEnum.class);
        assertEquals(TestEnum.VALUE_ONE, resolver.resolve(context));
    }

    @Test
    public void testResolveEnumCaseInsensitive() throws Exception {
        ArgumentResolverContext context = createContext("value_two", TestEnum.class);
        ArgumentResolver<?> resolver = registry.getResolver(TestEnum.class);
        assertEquals(TestEnum.VALUE_TWO, resolver.resolve(context));
    }

    @Test
    public void testResolveEnumMixedCase() throws Exception {
        ArgumentResolverContext context = createContext("Another_Value", TestEnum.class);
        ArgumentResolver<?> resolver = registry.getResolver(TestEnum.class);
        assertEquals(TestEnum.ANOTHER_VALUE, resolver.resolve(context));
    }

    @Test(expected = ArgumentResolutionException.class)
    public void testResolveEnumInvalid() throws Exception {
        ArgumentResolverContext context = createContext("NOT_A_VALUE", TestEnum.class);
        ArgumentResolver<?> resolver = registry.getResolver(TestEnum.class);
        resolver.resolve(context);
    }

    // ==================== Custom Resolver Tests ====================

    @Test
    public void testRegisterCustomResolver() throws Exception {
        registry.register(TestEnum.class, context -> TestEnum.VALUE_ONE);
        ArgumentResolverContext context = createContext("anything", TestEnum.class);
        assertEquals(TestEnum.VALUE_ONE, registry.getResolver(TestEnum.class).resolve(context));
    }

    @Test
    public void testHasResolver() {
        assertTrue(registry.hasResolver(int.class));
        assertTrue(registry.hasResolver(Integer.class));
        assertTrue(registry.hasResolver(String.class));
        assertTrue(registry.hasResolver(boolean.class));
        assertFalse(registry.hasResolver(Object.class));
    }

    // ==================== Primitive/Boxed Pairing Tests ====================

    @Test
    public void testPrimitiveBoxedPairing() {
        // Both primitive and boxed should have resolvers
        assertNotNull(registry.getResolver(int.class));
        assertNotNull(registry.getResolver(Integer.class));
        assertNotNull(registry.getResolver(boolean.class));
        assertNotNull(registry.getResolver(Boolean.class));
        assertNotNull(registry.getResolver(double.class));
        assertNotNull(registry.getResolver(Double.class));
    }

}
