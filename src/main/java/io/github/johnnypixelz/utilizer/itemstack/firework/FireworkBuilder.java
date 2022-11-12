package io.github.johnnypixelz.utilizer.itemstack.firework;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

import java.util.concurrent.ThreadLocalRandom;

public class FireworkBuilder {
    public static FireworkBuilder builder() {
        return new FireworkBuilder();
    }

    boolean flicker = false;
    boolean trail = false;
    final ImmutableList.Builder<Color> colors = ImmutableList.builder();
    ImmutableList.Builder<Color> fadeColors = null;
    FireworkEffect.Type type = FireworkEffect.Type.BALL;

    FireworkBuilder() {}

    /**
     * Specify the type of the firework effect.
     *
     * @param type The effect type
     * @return This object, for chaining
     * @throws IllegalArgumentException If type is null
     */
    public FireworkBuilder withType(FireworkEffect.Type type) throws IllegalArgumentException {
        Validate.notNull(type, "Cannot have null type");
        this.type = type;
        return this;
    }

    /**
     * Add a flicker to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withFlicker() {
        flicker = true;
        return this;
    }

    /**
     * Set whether the firework effect should flicker.
     *
     * @param flicker true if it should flicker, false if not
     * @return This object, for chaining
     */
    public FireworkBuilder flicker(boolean flicker) {
        this.flicker = flicker;
        return this;
    }

    /**
     * Add a trail to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withTrail() {
        trail = true;
        return this;
    }

    /**
     * Set whether the firework effect should have a trail.
     *
     * @param trail true if it should have a trail, false for no trail
     * @return This object, for chaining
     */
    public FireworkBuilder trail(boolean trail) {
        this.trail = trail;
        return this;
    }

    /**
     * Add a primary color to the firework effect.
     *
     * @param color The color to add
     * @return This object, for chaining
     * @throws IllegalArgumentException If color is null
     */
    public FireworkBuilder withColor(Color color) throws IllegalArgumentException {
        Validate.notNull(color, "Cannot have null color");

        colors.add(color);

        return this;
    }

    /**
     * Add several primary colors to the firework effect.
     *
     * @param colors The colors to add
     * @return This object, for chaining
     * @throws IllegalArgumentException If colors is null
     * @throws IllegalArgumentException If any color is null (may be
     *                                  thrown after changes have occurred)
     */
    public FireworkBuilder withColor(Color... colors) throws IllegalArgumentException {
        Validate.notNull(colors, "Cannot have null colors");
        if (colors.length == 0) {
            return this;
        }

        ImmutableList.Builder<Color> list = this.colors;
        for (Color color : colors) {
            Validate.notNull(color, "Color cannot be null");
            list.add(color);
        }

        return this;
    }

    /**
     * Add several primary colors to the firework effect.
     *
     * @param colors An iterable object whose iterator yields the desired
     *               colors
     * @return This object, for chaining
     * @throws IllegalArgumentException If colors is null
     * @throws IllegalArgumentException If any color is null (may be
     *                                  thrown after changes have occurred)
     */
    public FireworkBuilder withColor(Iterable<?> colors) throws IllegalArgumentException {
        Validate.notNull(colors, "Cannot have null colors");

        ImmutableList.Builder<Color> list = this.colors;
        for (Object color : colors) {
            if (!(color instanceof Color)) {
                throw new IllegalArgumentException(color + " is not a Color in " + colors);
            }
            list.add((Color) color);
        }

        return this;
    }

    /**
     * Add a random color to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomColor() {
        Color color = Color.fromRGB(
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256)
        );

        withColor(color);

        return this;
    }

    /**
     * Add a random color to the firework effect.
     *
     * @param amount The amount of random colors to be added
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomColor(int amount) {
        for (int i = 0; i < amount; i++) {
            withRandomColor();
        }

        return this;
    }

    /**
     * Add a random bright color to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomBrightColor() {
        Color color = Color.fromRGB(
                ThreadLocalRandom.current().nextInt(128, 256),
                ThreadLocalRandom.current().nextInt(128, 256),
                ThreadLocalRandom.current().nextInt(128, 256)
        );

        withColor(color);

        return this;
    }

    /**
     * Add a random bright color to the firework effect.
     *
     * @param amount The amount of random colors to be added
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomBrightColor(int amount) {
        for (int i = 0; i < amount; i++) {
            withRandomBrightColor();
        }

        return this;
    }

    /**
     * Add a fade color to the firework effect.
     *
     * @param color The color to add
     * @return This object, for chaining
     * @throws IllegalArgumentException If colors is null
     * @throws IllegalArgumentException If any color is null (may be
     *                                  thrown after changes have occurred)
     */
    public FireworkBuilder withFade(Color color) throws IllegalArgumentException {
        Validate.notNull(color, "Cannot have null color");

        if (fadeColors == null) {
            fadeColors = ImmutableList.builder();
        }

        fadeColors.add(color);

        return this;
    }

    /**
     * Add several fade colors to the firework effect.
     *
     * @param colors The colors to add
     * @return This object, for chaining
     * @throws IllegalArgumentException If colors is null
     * @throws IllegalArgumentException If any color is null (may be
     *                                  thrown after changes have occurred)
     */
    public FireworkBuilder withFade(Color... colors) throws IllegalArgumentException {
        Validate.notNull(colors, "Cannot have null colors");
        if (colors.length == 0) {
            return this;
        }

        ImmutableList.Builder<Color> list = this.fadeColors;
        if (list == null) {
            list = this.fadeColors = ImmutableList.builder();
        }

        for (Color color : colors) {
            Validate.notNull(color, "Color cannot be null");
            list.add(color);
        }

        return this;
    }

    /**
     * Add several fade colors to the firework effect.
     *
     * @param colors An iterable object whose iterator yields the desired
     *               colors
     * @return This object, for chaining
     * @throws IllegalArgumentException If colors is null
     * @throws IllegalArgumentException If any color is null (may be
     *                                  thrown after changes have occurred)
     */
    public FireworkBuilder withFade(Iterable<?> colors) throws IllegalArgumentException {
        Validate.notNull(colors, "Cannot have null colors");

        ImmutableList.Builder<Color> list = this.fadeColors;
        if (list == null) {
            list = this.fadeColors = ImmutableList.builder();
        }

        for (Object color : colors) {
            if (!(color instanceof Color)) {
                throw new IllegalArgumentException(color + " is not a Color in " + colors);
            }
            list.add((Color) color);
        }

        return this;
    }

    /**
     * Add a random fade to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomFade() {
        Color color = Color.fromRGB(
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256)
        );

        withFade(color);

        return this;
    }

    /**
     * Add a random fade to the firework effect.
     *
     * @param amount The amount of random colors to be added
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomFade(int amount) {
        for (int i = 0; i < amount; i++) {
            withRandomFade();
        }

        return this;
    }

    /**
     * Add a random bright fade to the firework effect.
     *
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomBrightFade() {
        Color color = Color.fromRGB(
                ThreadLocalRandom.current().nextInt(128, 256),
                ThreadLocalRandom.current().nextInt(128, 256),
                ThreadLocalRandom.current().nextInt(128, 256)
        );

        withFade(color);

        return this;
    }

    /**
     * Add a random bright fade to the firework effect.
     *
     * @param amount The amount of random colors to be added
     * @return This object, for chaining
     */
    public FireworkBuilder withRandomBrightFade(int amount) {
        for (int i = 0; i < amount; i++) {
            withRandomBrightFade();
        }

        return this;
    }

    /**
     * Create a {@link FireworkEffect} from the current contents of this
     * builder.
     * <p>
     * To successfully build, you must have specified at least one color.
     *
     * @return The representative firework effect
     */
    public FireworkEffect build() {
        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .withColor(colors.build())
                .withFade(fadeColors == null ? ImmutableList.<Color>of() : fadeColors.build())
                .with(type)
                .build();
    }
}
