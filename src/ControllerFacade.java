import java.io.IOException;

public class ControllerFacade implements Controllable {

    private final Viewable controller;

    public ControllerFacade(Viewable controller) {
        this.controller = controller;
    }

    @Override
    public Catalog getCatalog() {
        return controller.getCatalog();
    }

    @Override
    public int[][] getGame(char level) throws NotFoundException {
        if (level == 'I') {
            Game g = ((GameDriver) controller).loadIncompleteGame();
            if (g == null) {
                throw new NotFoundException("No incomplete game found");
            }
            return g.getGrid();
        }

        DifficultyEnum d = switch (level) {
            case 'E' -> DifficultyEnum.EASY;
            case 'M' -> DifficultyEnum.MEDIUM;
            case 'H' -> DifficultyEnum.HARD;
            default -> throw new IllegalArgumentException("Invalid difficulty");
        };

        return controller.getGame(d).getGrid();
    }

    @Override
    public void driveGames(int[][] source) throws SolutionInvalidException {
        controller.driveGames(new Game(source));
    }

    @Override
    public boolean[][] verifyGame(int[][] game) {
        String result = controller.verifyGame(new Game(game));
        boolean[][] status = new boolean[9][9];

        boolean valid = result.equals("VALID") || result.equals("INCOMPLETE");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                status[i][j] = valid;
            }
        }
        return status;
    }

    @Override
    public int[][] solveGame(int[][] game) throws InvalidGameException {
        int[] solution = controller.solveGame(new Game(game));

        for (int i = 0; i < solution.length; i += 3) {
            int r = solution[i];
            int c = solution[i + 1];
            int v = solution[i + 2];
            game[r][c] = v;
        }
        return game;
    }

    @Override
    public void logUserAction(UserAction action) throws IOException {
        controller.logUserAction(action.toString());
    }
}
