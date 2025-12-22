package controller;

public enum DifficultyEnum {
    HARD,
    MEDIUM,
    EASY;

    private static final String GAMES_DIRECTORY = "games";
    private static final String EASY_DIRECTORY = GAMES_DIRECTORY + "/easy";
    private static final String MEDIUM_DIRECTORY = GAMES_DIRECTORY + "/medium";
    private static final String HARD_DIRECTORY = GAMES_DIRECTORY + "/hard";

    public String getFolderPath() {
        switch (this) {
            case EASY:
                return EASY_DIRECTORY;
            case MEDIUM:
                return MEDIUM_DIRECTORY;
            case HARD:
                return HARD_DIRECTORY;
            default:
                return EASY_DIRECTORY;
        }
    }
}
