package io.github.johnnypixelz.utilizer.itemstack.firework;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Fireworks {

    public static Builder builder() {
        return new Builder();
    }

    public static Firework spawn(Consumer<FireworkBuilder> builder, Location location) {
        FireworkBuilder effectBuilder = FireworkBuilder.builder();
        builder.accept(effectBuilder);

        return spawn(effectBuilder.build(), location);
    }

    public static Firework spawn(FireworkEffect effect, Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(effect);
        firework.setFireworkMeta(fireworkMeta);

        return firework;
    }

    public static Firework spawn(Location location) {
        return location.getWorld().spawn(location, Firework.class);
    }

    public static class Builder {
        private final List<FireworkEffect> effects = new ArrayList<>();
        private boolean instant = false;
        private int power = -1;

        private Builder() {}

        public Builder addEffect(FireworkEffect effect) {
            effects.add(effect);
            return this;
        }

        public Builder addEffect(Consumer<FireworkBuilder> effectBuilder) {
            FireworkBuilder builder = FireworkBuilder.builder();
            effectBuilder.accept(builder);
            return addEffect(builder.build());
        }

        public Builder instant() {
            this.instant = true;
            return this;
        }

        public Builder setInstant(boolean instant) {
            this.instant = instant;
            return this;
        }

        public Builder power(int power) {
            this.power = power;
            return this;
        }

        public Firework spawn(Location location) {
            Firework firework = location.getWorld().spawn(location, Firework.class);

            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffects(effects);

            if (power >= 1) {
                fireworkMeta.setPower(power);
            }

            firework.setFireworkMeta(fireworkMeta);

            if (instant) {
                firework.detonate();
            }

            return firework;
        }

    }
}
