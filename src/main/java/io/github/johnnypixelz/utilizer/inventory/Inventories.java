package io.github.johnnypixelz.utilizer.inventory;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Inventories {

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static Item dropItem(Entity entity, ItemStack stack) {
        return entity.getWorld().dropItem(entity.getLocation(), stack);
    }

    public static Item dropItemNaturally(Entity entity, ItemStack stack) {
        return entity.getWorld().dropItemNaturally(entity.getLocation(), stack);
    }

    /**
     * Adds a list of items to the player's inventory and return the items that did not fit.
     *
     * @param player the player to give the items to.
     * @param items  the items to give.
     * @return the items that did not fit.
     */
    @Nonnull
    public static List<ItemStack> give(@Nonnull Player player, @Nullable ItemStack... items) {
        return give(player, true, items);
    }

    /**
     * Adds a list of items to the player's inventory and return the items that did not fit.
     *
     * @param player the player to give the items to.
     * @param items  the items to give.
     * @param split  same as {@link #addItems(Inventory, boolean, ItemStack...)}
     * @return the items that did not fit.
     */
    @Nonnull
    public static List<ItemStack> give(@Nonnull Player player, boolean split, @Nullable ItemStack... items) {
        if (items == null || items.length == 0) return new ArrayList<>();

        return addItems(player.getInventory(), split, items);
    }

    /**
     * Adds a list of items to the player's inventory and drop the items that did not fit.
     *
     * @param player the player to give the items to.
     * @param items  the items to give.
     * @return the items that did not fit and were dropped.
     */
    @Nonnull
    public static List<ItemStack> giveOrDrop(@Nonnull Player player, @Nullable ItemStack... items) {
        return giveOrDrop(player, true, items);
    }

    /**
     * Adds a list of items to the player's inventory and drop the items that did not fit.
     *
     * @param player the player to give the items to.
     * @param items  the items to give.
     * @param split  same as {@link #addItems(Inventory, boolean, ItemStack...)}
     * @return the items that did not fit and were dropped.
     */
    @Nonnull
    public static List<ItemStack> giveOrDrop(@Nonnull Player player, boolean split, @Nullable ItemStack... items) {
        if (items == null || items.length == 0) return new ArrayList<>();
        List<ItemStack> leftOvers = addItems(player.getInventory(), split, items);
        World world = player.getWorld();
        Location location = player.getLocation();

        for (ItemStack drop : leftOvers) world.dropItemNaturally(location, drop);
        return leftOvers;
    }

    public static List<ItemStack> addItems(@Nonnull Inventory inventory, boolean split, @Nonnull ItemStack... items) {
        return addItems(inventory, split, null, items);
    }

    /**
     * Optimized version of {@link Inventory#addItem(ItemStack...)}
     * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/inventory/CraftInventory.java
     *
     * @param inventory       the inventory to add the items to.
     * @param split           if it should check for the inventory stack size {@link Inventory#getMaxStackSize()} or
     *                        item's max stack size {@link ItemStack#getMaxStackSize()} when putting items. This is useful when
     *                        you're adding stacked tools such as swords that you'd like to split them to other slots.
     * @param modifiableSlots the slots that are allowed to be used for adding the items, otherwise null to allow all slots.
     * @param items           the items to add.
     * @return items that didn't fit in the inventory.
     */
    @Nonnull
    public static List<ItemStack> addItems(@Nonnull Inventory inventory, boolean split, @Nullable Predicate<Integer> modifiableSlots, @Nonnull ItemStack... items) {
        Objects.requireNonNull(inventory, "Cannot add items to null inventory");
        Objects.requireNonNull(items, "Cannot add null items to inventory");

        List<ItemStack> leftOvers = new ArrayList<>(items.length);

        // No other optimized way to access this using Bukkit API...
        // We could pass the length to individual methods, so they could also use getItem() which
        // skips parsing all the items in the inventory if not needed, but that's just too much.
        // Note: This is not the same as Inventory#getSize()
        int invSize = inventory.getStorageContents().length;
        int lastEmpty = 0;

        for (ItemStack item : items) {
            int lastPartial = 0;

            while (true) {
                // Check if there is a similar item that can be stacked before using free slots.
                int firstPartial = lastPartial >= invSize ? -1 : firstPartial(inventory, item, lastPartial, modifiableSlots);
                if (firstPartial == -1) {
                    // Start adding items to leftovers if there are no partial and empty slots
                    // -1 means that there are no empty slots left.
                    if (lastEmpty != -1) lastEmpty = firstEmpty(inventory, lastEmpty, modifiableSlots);
                    if (lastEmpty == -1) {
                        leftOvers.add(item);
                        break;
                    }

                    // Avoid firstPartial() for checking again for no reason, since if we're already checking
                    // for free slots, that means there are no partials even left.
                    lastPartial = invSize + 1;

                    int maxSize = split ? item.getMaxStackSize() : inventory.getMaxStackSize();
                    int amount = item.getAmount();
                    if (amount <= maxSize) {
                        inventory.setItem(lastEmpty, item);
                        break;
                    } else {
                        ItemStack copy = item.clone();
                        copy.setAmount(maxSize);
                        inventory.setItem(lastEmpty, copy);
                        item.setAmount(amount - maxSize);
                    }
                    if (++lastEmpty == invSize) lastEmpty = -1;
                } else {
                    ItemStack partialItem = inventory.getItem(firstPartial);
                    int maxAmount = split ? partialItem.getMaxStackSize() : inventory.getMaxStackSize();
                    int partialAmount = partialItem.getAmount();
                    int amount = item.getAmount();
                    int sum = amount + partialAmount;

                    if (sum <= maxAmount) {
                        partialItem.setAmount(sum);
                        inventory.setItem(firstPartial, partialItem);
                        break;
                    } else {
                        partialItem.setAmount(maxAmount);
                        inventory.setItem(firstPartial, partialItem);
                        item.setAmount(sum - maxAmount);
                    }
                    lastPartial = firstPartial + 1;
                }
            }
        }

        return leftOvers;
    }

    public static int firstPartial(@Nonnull Inventory inventory, @Nullable ItemStack item, int beginIndex) {
        return firstPartial(inventory, item, beginIndex, null);
    }

    /**
     * Gets the item slot in the inventory that matches the given item argument.
     * The matched item must be {@link ItemStack#isSimilar(ItemStack)} and has not
     * reached its {@link ItemStack#getMaxStackSize()} for the inventory.
     *
     * @param inventory       the inventory to match the item from.
     * @param item            the item to match.
     * @param beginIndex      the index which to start the search from in the inventory.
     * @param modifiableSlots the slots that can be used to share items.
     * @return the first matched item slot, otherwise -1
     * @throws IndexOutOfBoundsException if the beginning index is less than 0 or greater than the inventory storage size.
     */
    public static int firstPartial(@Nonnull Inventory inventory, @Nullable ItemStack item, int beginIndex, @Nullable Predicate<Integer> modifiableSlots) {
        if (item != null) {
            ItemStack[] items = inventory.getStorageContents();
            int invSize = items.length;
            if (beginIndex < 0 || beginIndex >= invSize)
                throw new IndexOutOfBoundsException("Begin Index: " + beginIndex + ", Inventory storage content size: " + invSize);

            for (; beginIndex < invSize; beginIndex++) {
                if (modifiableSlots != null && !modifiableSlots.test(beginIndex)) continue;
                ItemStack cItem = items[beginIndex];
                if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(item))
                    return beginIndex;
            }
        }
        return -1;
    }

    public static List<ItemStack> stack(@Nonnull Collection<ItemStack> items) {
        return stack(items, ItemStack::isSimilar);
    }

    /**
     * Stacks up the items in the given item collection that are pass the similarity check.
     * This means that if you have a collection that consists of separate items with the same material, you can reduce them using the following:
     * <pre>{@code
     *   List<ItemStack> items = Arrays.asList(XMaterial.STONE.parseItem(), XMaterial.STONE.parseItem(), XMaterial.AIR.parseItem());
     *   items = XItemStack.stack(items, (first, second) -> first.getType == second.getType());
     *   // items -> [STONE x2, AIR x1]
     * }</pre>
     *
     * @param items the items to stack.
     * @return stacked up items.
     */
    @Nonnull
    public static List<ItemStack> stack(@Nonnull Collection<ItemStack> items, @Nonnull BiPredicate<ItemStack, ItemStack> similarity) {
        Objects.requireNonNull(items, "Cannot stack null items");
        Objects.requireNonNull(similarity, "Similarity check cannot be null");
        List<ItemStack> stacked = new ArrayList<>(items.size());

        for (ItemStack item : items) {
            if (item == null) continue;

            boolean add = true;
            for (ItemStack stack : stacked) {
                if (similarity.test(item, stack)) {
                    stack.setAmount(stack.getAmount() + item.getAmount());
                    add = false;
                    break;
                }
            }

            if (add) stacked.add(item.clone());
        }
        return stacked;
    }

    public static int firstEmpty(@Nonnull Inventory inventory) {
        return firstEmpty(inventory, 0, null);
    }

    public static int firstEmpty(@Nonnull Inventory inventory, int beginIndex) {
        return firstEmpty(inventory, beginIndex, null);
    }

    /**
     * Gets the first item slot in the inventory that is empty or matches the given item argument.
     * The matched item must be {@link ItemStack#isSimilar(ItemStack)} and has not
     * reached its {@link ItemStack#getMaxStackSize()} for the inventory.
     *
     * @param inventory       the inventory to search from.
     * @param beginIndex      the item slot to start the search from in the inventory.
     * @param modifiableSlots the slots that can be used.
     * @return first empty item slot, otherwise -1
     * @throws IndexOutOfBoundsException if the beginning index is less than 0 or greater than the inventory storage size.
     */
    public static int firstEmpty(@Nonnull Inventory inventory, int beginIndex, @Nullable Predicate<Integer> modifiableSlots) {
        ItemStack[] items = inventory.getStorageContents();
        int invSize = items.length;
        if (beginIndex < 0 || beginIndex >= invSize)
            throw new IndexOutOfBoundsException("Begin Index: " + beginIndex + ", Inventory storage content size: " + invSize);

        for (; beginIndex < invSize; beginIndex++) {
            if (modifiableSlots != null && !modifiableSlots.test(beginIndex)) continue;
            if (items[beginIndex] == null) return beginIndex;
        }
        return -1;
    }

    /**
     * Gets the first empty slot or partial item in the inventory from an index.
     *
     * @param inventory  the inventory to search from.
     * @param beginIndex the item slot to start the search from in the inventory.
     * @return first empty or partial item slot, otherwise -1
     * @throws IndexOutOfBoundsException if the beginning index is less than 0 or greater than the inventory storage size.
     * @see #firstEmpty(Inventory, int)
     * @see #firstPartial(Inventory, ItemStack, int)
     */
    public static int firstPartialOrEmpty(@Nonnull Inventory inventory, @Nullable ItemStack item, int beginIndex) {
        if (item != null) {
            ItemStack[] items = inventory.getStorageContents();
            int len = items.length;
            if (beginIndex < 0 || beginIndex >= len)
                throw new IndexOutOfBoundsException("Begin Index: " + beginIndex + ", Size: " + len);

            for (; beginIndex < len; beginIndex++) {
                ItemStack cItem = items[beginIndex];
                if (cItem == null || (cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(item)))
                    return beginIndex;
            }
        }
        return -1;
    }

}
