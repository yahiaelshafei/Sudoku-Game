package controller;

import model.*;

import java.io.*;

import controller.exception.*;

public class ControllerFacade {
    private final GameDriver controller;
    
    public ControllerFacade(GameDriver controller) {
        this.controller = controller;
    }

    public Catalog getCatalog() {
        return controller.getCatalog();
    }

    public Game getGame(DifficultyEnum level) throws NotFoundException {
        return controller.getGame(level);
    }

    public int[][] getGame(char level) throws NotFoundException {
        if (level == 'I' || level == 'i') {
            Game g = loadIncompleteGame();
            if (g == null) throw new NotFoundException("No incomplete game found");
            return g.getGrid();
        }

        DifficultyEnum difficulty = charToDifficulty(level);
        return getGame(difficulty).getGrid();
    }
    
    public Game loadIncompleteGame() {
        return controller.loadIncompleteGame();
    }
    
    public void driveGames(Game source) throws SolutionInvalidException {
        controller.driveGames(source);
    }

    public void driveGames(String sourcePath) throws SolutionInvalidException {
        Board.getInstance().readFromFile(sourcePath);
        Game sourceGame = new Game(Board.getInstance().getGrid());
        controller.driveGames(sourceGame);
    }

    public String verifyGame(Game game) {
        return controller.verifyGame(game);
    }

    public boolean[][] verifyGame(int[][] grid) {
        String result = verifyGame(new Game(grid));
        boolean valid = result.equals("VALID") || result.equals("INCOMPLETE");
        
        boolean[][] status = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                status[i][j] = valid;
            }
        }
        return status;
    }
    

    public int[] solveGame(Game game) throws InvalidGameException {
        return controller.solveGame(game);
    }

    public int[][] solveGame(int[][] grid) throws InvalidGameException {
        int[] solution = solveGame(new Game(grid));
        
        for (int i = 0; i < solution.length; i += 3) {
            int r = solution[i];
            int c = solution[i + 1];
            int v = solution[i + 2];
            grid[r][c] = v;
        }
        
        return grid;
    }

    public void logUserAction(UserAction action) throws IOException {
        controller.logUserAction(action.toString());
    }
    
    public void logUserAction(String actionString) throws IOException {
        controller.logUserAction(actionString);
    }
    
    public UserAction getLastAction() {
        return controller.getLastAction();
    }

    public void undoLastAction() throws IOException {
        controller.undoLastAction();
    }

    public void clearLog() throws IOException {
        LogManager.getInstance().clearLog();
    }
    
    public void saveCurrentGameState(Game game) {
        controller.saveCurrentGameState(game);
    }

    public boolean handleCompletedGame(Game game) {
        return controller.handleCompletedGame(game);
    }

    private DifficultyEnum charToDifficulty(char level) {
        return switch (Character.toUpperCase(level)) {
            case 'E' -> DifficultyEnum.EASY;
            case 'M' -> DifficultyEnum.MEDIUM;
            case 'H' -> DifficultyEnum.HARD;
            default -> throw new IllegalArgumentException("Invalid difficulty: " + level);
        };
    }
}