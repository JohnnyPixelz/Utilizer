package io.github.johnnypixelz.utilizer.enchantments;

import org.bukkit.enchantments.Enchantment;

public class Enchantments {

    public static String getName(Enchantment ench) {
        switch (ench.getName()) {
            case "DAMAGE_ALL":
                return "Sharpness";

            case "DAMAGE_ARTHROPODS":
                return "Bane Of Arthropods";

            case "DAMAGE_UNDEAD":
                return "Smite";

            case "DIG_SPEED":
                return "Efficiency";

            case "DURABILITY":
                return "Unbreaking";

            case "FIRE_ASPECT":
                return "Fire Aspect";

            case "KNOCKBACK":
                return "Knockback";

            case "LOOT_BONUS_BLOCKS":
                return "Fortune";

            case "LOOT_BONUS_MOBS":
                return "Looting";

            case "OXYGEN":
                return "Respiration";

            case "PROTECTION_ENVIRONMENTAL":
                return "Protection";

            case "PROTECTION_EXPLOSIONS":
                return "Blast Protection";

            case "PROTECTION_FALL":
                return "Feather Falling";

            case "PROTECTION_FIRE":
                return "Fire Protection";

            case "PROTECTION_PROJECTILE":
                return "Projectile Protection";

            case "SILK_TOUCH":
                return "Silk Touch";

            case "WATER_WORKER":
                return "Aqua Affinity";

            case "ARROW_FIRE":
                return "Flame";

            case "ARROW_DAMAGE":
                return "Power";

            case "ARROW_KNOCKBACK":
                return "Punch";

            case "ARROW_INFINITE":
                return "Infinity";

            case "LUCK":
                return "Luck of the Sea";

            case "LURE":
                return "Lure";

            case "THORNS":
                return "Thorns";

            case "DEPTH_STRIDER":
                return "Depth Strider";

        }
        throw new RuntimeException("Unknown Enchantment " + ench.getName());
    }
}
