package fr.mathilde.realyEasyVanish.bukkit.scheduler;

public final class FoliaSupport {

    private static final boolean FOLIA = detect();

    private FoliaSupport() {
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    private static boolean detect() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
