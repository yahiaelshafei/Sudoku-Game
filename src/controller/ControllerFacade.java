package controller;

import java.io.IOException;

import controller.exception.InvalidGameException;
import controller.exception.NotFoundException;
import controller.exception.SolutionInvalidException;
import model.Catalog;
import model.Game;
import model.UserAction;
import view.Controllable;

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
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        return controller.getGame(level);
    }

    public int[][] getGame(char level) throws NotFoundException {
        if (level == 'I') {
            Game g = loadIncompleteGame();
            if (g == null)
                throw new NotFoundException("No incomplete game found");
            return g.getGrid();
        }

        DifficultyEnum d = switch (level) {
            case 'E' -> DifficultyEnum.EASY;
            case 'M' -> DifficultyEnum.MEDIUM;
            case 'H' -> DifficultyEnum.HARD;
            default -> throw new IllegalArgumentException("Invalid difficulty: " + level);
        };

        return getGame(d).getGrid();
    }

    @Override
    public void driveGames(Game source) throws SolutionInvalidException {
        controller.driveGames(source);
    }

    @Override
    public String verifyGame(Game game) {
        return controller.verifyGame(game);
    }

    public boolean[][] verifyGameStatus(int[][] game) {
        String result = verifyGame(new Game(game));
        boolean valid = result.equals("VALID") || result.equals("INCOMPLETE");
        boolean[][] status = new boolean[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                status[i][j] = valid;
        return status;
    }

    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        return controller.solveGame(game);
    }

    public int[][] solveGame(int[][] game) throws InvalidGameException {
        int[] solution = solveGame(new Game(game));
        for (int i = 0; i < solution.length; i += 3) {
            int r = solution[i], c = solution[i + 1], v = solution[i + 2];
            game[r][c] = v;
        }
        return game;
    }

    @Override
    public void logUserAction(UserAction action) throws IOException {
        controller.logUserAction(action.toString());
    }

    @Override
    public Game loadIncompleteGame() {
        if (controller instanceof Controllable c) {
            return c.loadIncompleteGame();
        }
        return null;
    }
}
