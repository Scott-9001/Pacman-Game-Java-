package pacman.model.engine;

public class GhostCounter {
    private static GhostCounter instance;
    private int ghostCount;

    private GhostCounter() {
        ghostCount = 0;
    }

    public static GhostCounter getInstance() {
        if (instance == null) {
            instance = new GhostCounter();
        }
        return instance;
    }

    public void resetCount() {
        ghostCount = 0;
    }

    public void incrementCount() {
        ghostCount++;
    }

    public int getGhostCount() {
        return ghostCount;
    }
}

