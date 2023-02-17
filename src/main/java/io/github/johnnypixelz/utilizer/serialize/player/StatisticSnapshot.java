package io.github.johnnypixelz.utilizer.serialize.player;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StatisticSnapshot {
    private final Map<Statistic, Integer> untypedStatistics;
    private final Map<Statistic, Map<Material, Integer>> itemStatistics;
    private final Map<Statistic, Map<EntityType, Integer>> entityStatistics;
    private final Map<Statistic, Map<Material, Integer>> blockStatistics;

    public StatisticSnapshot(Player player) {
        this.untypedStatistics = new HashMap<>();
        this.itemStatistics = new HashMap<>();
        this.entityStatistics = new HashMap<>();
        this.blockStatistics = new HashMap<>();

        for (Statistic statistic : Statistic.values()) {
            try {
                switch (statistic.getType()) {
                    case UNTYPED -> untypedStatistics.put(statistic, player.getStatistic(statistic));
                    case ITEM -> {
                        final HashMap<Material, Integer> map = new HashMap<>();
                        Arrays.stream(Material.values()).forEach(material -> {
                            int stat = player.getStatistic(statistic, material);
                            if (stat == 0) return;

                            map.put(material, stat);
                        });
                        itemStatistics.put(statistic, map);
                    }
                    case ENTITY -> {
                        final HashMap<EntityType, Integer> map = new HashMap<>();
                        Arrays.stream(EntityType.values()).forEach(entity -> {
                            int stat = player.getStatistic(statistic, entity);
                            if (stat == 0) return;

                            map.put(entity, stat);
                        });
                        entityStatistics.put(statistic, map);
                    }
                    case BLOCK -> {
                        final HashMap<Material, Integer> map = new HashMap<>();
                        Arrays.stream(Material.values()).forEach(material -> {
                            int stat = player.getStatistic(statistic, material);
                            if (stat == 0) return;

                            map.put(material, stat);
                        });
                        blockStatistics.put(statistic, map);
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void apply(Player player) {
        untypedStatistics.forEach(player::setStatistic);
        itemStatistics.forEach((statistic, materialIntegerMap) -> {
            materialIntegerMap.forEach((material, integer) -> {
                player.setStatistic(statistic, material, integer);
            });
        });
        entityStatistics.forEach((statistic, entityTypeIntegerMap) -> {
            entityTypeIntegerMap.forEach((entityType, integer) -> {
                player.setStatistic(statistic, entityType, integer);
            });
        });
        blockStatistics.forEach((statistic, materialIntegerMap) -> {
            materialIntegerMap.forEach((material, integer) -> {
                player.setStatistic(statistic, material, integer);
            });
        });
    }

}
