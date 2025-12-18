public class Catalog {
    private boolean gameInProgress;
    private boolean allModesExist;

    public Catalog(boolean gameInProgress, boolean allModesExist) {
        this.gameInProgress = gameInProgress;
        this.allModesExist = allModesExist;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public boolean isAllModesExist() {
        return allModesExist;
    }

    public void setAllModesExist(boolean allModesExist) {
        this.allModesExist = allModesExist;
    }
    
}