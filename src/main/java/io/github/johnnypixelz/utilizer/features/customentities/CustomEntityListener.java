package io.github.johnnypixelz.utilizer.features.customentities;

import io.github.johnnypixelz.utilizer.event.BiStatefulEventEmitter;
import io.github.johnnypixelz.utilizer.serialize.world.ChunkPosition;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Optional;

public class CustomEntityListener<T extends CustomEntity<E>, E extends Entity> implements Listener {
    private final CustomEntityManager<T, E> manager;

    private boolean damageableByPlayers; // Default: false
    private boolean damageableByEntities; // Default: false
    private boolean damageableByBlocks; // Default: false
    private boolean damageable; // Default: false

    private boolean interactable; // Default: false
    private boolean explodable; // Default: false

    private final BiStatefulEventEmitter<PlayerInteractEntityEvent, T> rightClickInteractEventEmitter;
    private final BiStatefulEventEmitter<EntityExplodeEvent, T> explodeEventEmitter;

    private final BiStatefulEventEmitter<EntityDamageByEntityEvent, T> damageByEntityEventEmitter;
    private final BiStatefulEventEmitter<EntityDamageByEntityEvent, T> damageByPlayerEventEmitter;
    private final BiStatefulEventEmitter<EntityDamageByBlockEvent, T> damageByBlockEventEmitter;
    private final BiStatefulEventEmitter<EntityDamageEvent, T> damageEventEmitter;

    public CustomEntityListener(CustomEntityManager<T, E> manager) {
        this.manager = manager;

        this.damageableByPlayers = false;
        this.damageableByEntities = false;
        this.damageableByBlocks = false;
        this.damageable = false;

        this.interactable = false;
        this.explodable = false;

        this.rightClickInteractEventEmitter = new BiStatefulEventEmitter<>();
        this.explodeEventEmitter = new BiStatefulEventEmitter<>();

        this.damageByEntityEventEmitter = new BiStatefulEventEmitter<>();
        this.damageByPlayerEventEmitter = new BiStatefulEventEmitter<>();
        this.damageByBlockEventEmitter = new BiStatefulEventEmitter<>();
        this.damageEventEmitter = new BiStatefulEventEmitter<>();
    }

    public CustomEntityManager<T, E> getManager() {
        return manager;
    }

    public BiStatefulEventEmitter<PlayerInteractEntityEvent, T> getRightClickInteractEventEmitter() {
        return rightClickInteractEventEmitter;
    }

    public BiStatefulEventEmitter<EntityExplodeEvent, T> getExplodeEventEmitter() {
        return explodeEventEmitter;
    }

    public BiStatefulEventEmitter<EntityDamageByEntityEvent, T> getDamageByEntityEventEmitter() {
        return damageByEntityEventEmitter;
    }

    public BiStatefulEventEmitter<EntityDamageByEntityEvent, T> getDamageByPlayerEventEmitter() {
        return damageByPlayerEventEmitter;
    }

    public BiStatefulEventEmitter<EntityDamageByBlockEvent, T> getDamageByBlockEventEmitter() {
        return damageByBlockEventEmitter;
    }

    public BiStatefulEventEmitter<EntityDamageEvent, T> getDamageEventEmitter() {
        return damageEventEmitter;
    }

    public boolean isDamageableByPlayers() {
        return damageableByPlayers;
    }

    public void setDamageableByPlayers(boolean damageableByPlayers) {
        this.damageableByPlayers = damageableByPlayers;
    }

    public boolean isDamageableByEntities() {
        return damageableByEntities;
    }

    public void setDamageableByEntities(boolean damageableByEntities) {
        this.damageableByEntities = damageableByEntities;
    }

    public boolean isDamageableByBlocks() {
        return damageableByBlocks;
    }

    public void setDamageableByBlocks(boolean damageableByBlocks) {
        this.damageableByBlocks = damageableByBlocks;
    }

    public boolean isDamageable() {
        return damageable;
    }

    public void setDamageable(boolean damageable) {
        this.damageable = damageable;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public boolean isExplodable() {
        return explodable;
    }

    public void setExplodable(boolean explodable) {
        this.explodable = explodable;
    }

    private Optional<T> getMatchingCustomEntity(Entity entity) {
        for (T customEntity : manager.getCustomEntities()) {
            final Optional<E> optionalEntity = customEntity.getEntity();
            if (optionalEntity.isEmpty()) continue;

            if (optionalEntity.get() == entity) {
                return Optional.of(customEntity);
            }
        }

        return Optional.empty();
    }

    @EventHandler
    private void onInteractEvent(PlayerInteractEntityEvent event) {
        final Entity rightClicked = event.getRightClicked();

        getMatchingCustomEntity(rightClicked).ifPresent(customEntity -> {
            try {
                rightClickInteractEventEmitter.emit(event, customEntity);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (!interactable) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity damaged = event.getEntity();

        getMatchingCustomEntity(damaged).ifPresent(customEntity -> {
            final boolean damagerIsPlayer = damager instanceof Player;

            try {
                if (damagerIsPlayer) {
                    damageByPlayerEventEmitter.emit(event, customEntity);
                } else {
                    damageByEntityEventEmitter.emit(event, customEntity);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if ((damagerIsPlayer && !damageableByPlayers) || (!damagerIsPlayer && !damageableByEntities)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    private void onDamageEvent(EntityDamageEvent event) {
        final Entity entity = event.getEntity();

        getMatchingCustomEntity(entity).ifPresent(customEntity -> {
            try {
                damageEventEmitter.emit(event, customEntity);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (!damageable) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    private void onBlockDamageEvent(EntityDamageByBlockEvent event) {
        final Entity entity = event.getEntity();

        getMatchingCustomEntity(entity).ifPresent(customEntity -> {
            try {
                damageByBlockEventEmitter.emit(event, customEntity);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (!damageableByBlocks) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    private void onExplodeEvent(EntityExplodeEvent event) {
        final Entity entity = event.getEntity();

        getMatchingCustomEntity(entity).ifPresent(customEntity -> {
            try {
                explodeEventEmitter.emit(event, customEntity);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (!explodable) {
                event.setCancelled(true);
            }
        });
    }

//    @EventHandler
//    private void onWorldLoad(WorldLoadEvent event) {
//        if (!manager.isAutoLoad()) return;
//
//        for (T customEntity : manager.getCustomEntities()) {
//            if (customEntity.isLoaded()) continue;
//            if (!customEntity.getPosition().getPoint().getWorld().equals(event.getWorld().getName())) continue;
//
//            customEntity.load(manager);
//        }
//    }
//
//    @EventHandler
//    private void onWorldUnload(WorldUnloadEvent event) {
//        for (T customEntity : manager.getCustomEntities()) {
//            if (!customEntity.isLoaded()) continue;
//            if (!customEntity.getPosition().getPoint().getWorld().equals(event.getWorld().getName())) continue;
//
//            customEntity.unload();
//        }
//    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        if (!manager.isAutoLoad()) return;

        for (T customEntity : manager.getCustomEntities()) {
            if (customEntity.isLoaded()) continue;
            if (!customEntity.getPosition().getPoint().getWorld().equals(event.getWorld().getName())) continue;
            if (ChunkPosition.of(customEntity.getPosition().getPoint().getX() >> 4, customEntity.getPosition().getPoint().getZ() >> 4, event.getWorld()))

            customEntity.load(manager);
        }
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        for (T customEntity : manager.getCustomEntities()) {
            if (!customEntity.isLoaded()) continue;
            if (!customEntity.getPosition().getPoint().getWorld().equals(event.getWorld().getName())) continue;

            customEntity.unload();
        }
    }

}
