package io.github.johnnypixelz.utilizer.depend.dependencies.worldedit;

// Commented out due to worldedit using a java version > 8
public class WorldEditWrapper {

//    public Optional<Region> getSelection(Player player) {
//        final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
//        try {
//            LocalSession session = ((WorldEditPlugin) worldEdit).getSession(player);
//
//            final com.sk89q.worldedit.regions.Region selection = session.getSelection();
//            if (!(selection instanceof CuboidRegion)) {
//                return Optional.empty();
//            }
//
//            CuboidRegion cuboidRegion = (CuboidRegion) selection;
//
//            final BlockVector3 pos1 = cuboidRegion.getPos1();
//            final BlockVector3 pos2 = cuboidRegion.getPos2();
//
//            final World world = Bukkit.getWorld(cuboidRegion.getWorld().getName());
//            Region region = Region.of(
//                    Point.of(pos1.getX(), pos1.getY(), pos1.getZ(), world),
//                    Point.of(pos2.getX(), pos2.getY(), pos2.getZ(), world)
//            );
//
//            return Optional.of(region);
//        } catch (IncompleteRegionException | NullPointerException ignored) {
//            return Optional.empty();
//        }
//    }
//
//    public void getSelectionOrElse(Player player, Consumer<Region> onSuccess, Consumer<SelectionError> onError) {
//        final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
//        try {
//            LocalSession session = ((WorldEditPlugin) worldEdit).getSession(player);
//
//            final com.sk89q.worldedit.regions.Region selection = session.getSelection();
//            if (!(selection instanceof CuboidRegion)) {
//                onError.accept(SelectionError.NOT_CUBOID);
//                return;
//            }
//
//            CuboidRegion cuboidRegion = (CuboidRegion) selection;
//
//            final BlockVector3 pos1 = cuboidRegion.getPos1();
//            final BlockVector3 pos2 = cuboidRegion.getPos2();
//
//            final World world = Bukkit.getWorld(cuboidRegion.getWorld().getName());
//            Region region = Region.of(
//                    Point.of(pos1.getX(), pos1.getY(), pos1.getZ(), world),
//                    Point.of(pos2.getX(), pos2.getY(), pos2.getZ(), world)
//            );
//
//            onSuccess.accept(region);
//        } catch (IncompleteRegionException | NullPointerException exception) {
//            onError.accept(SelectionError.INCOMPLETE);
//        }
//    }
//
//    public enum SelectionError {
//        INCOMPLETE("&cCurrent selection is incomplete."),
//        NOT_CUBOID("&cCurrent selection is not in cuboid form.");
//
//        private final String defaultMessage;
//
//        SelectionError(String defaultMessage) {
//            this.defaultMessage = defaultMessage;
//        }
//
//        public String getDefaultMessage() {
//            return defaultMessage;
//        }
//
//        public void sendDefaultMessage(Player player) {
//            player.sendMessage(Colors.color(defaultMessage));
//        }
//
//    }

}
