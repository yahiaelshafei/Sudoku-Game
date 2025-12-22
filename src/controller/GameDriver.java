package controller;

import model.*;
import java.io.*;
import java.util.*;
import controller.exception.*;
import controller.solver.*;
import controller.validation.*;

public class GameDriver implements Viewable {
    private final StorageManager storage;
    private final LogManager logger;
    private final SudokuSolver solver;

    public GameDriver() {
        this.storage = StorageManager.getInstance();
        this.logger = LogManager.getInstance();
        this.solver = new SudokuSolver();
    }

    @Override
    public Catalog getCatalog() {
        boolean hasIncomplete = storage.hasIncompleteGame();
        boolean allExist = storage.hasEasyGame() && storage.hasMediumGame() && storage.hasHardGame();

        return new Catalog(hasIncomplete, allExist);
    }

    @Override
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        String folderPath = getFolderPath(level);
        return storage.loadRandomGame(folderPath);
    }

    @Override
    public void driveGames(Game source) throws SolutionInvalidException {
        String verification = verifyGame(source);

        if (!verification.equals("VALID")) {
            throw new SolutionInvalidException(
                    "Source solution is " + verification + ". Must be VALID.");
        }

        Game easyGame = source.copy();
        Game mediumGame = source.copy();
        Game hardGame = source.copy();

        RandomPairs randomPairs = new RandomPairs();

        removeCellsFromGame(easyGame, 10, randomPairs);
        easyGame.setDifficulty(DifficultyEnum.EASY);

        removeCellsFromGame(mediumGame, 20, randomPairs);
        mediumGame.setDifficulty(DifficultyEnum.MEDIUM);

        removeCellsFromGame(hardGame, 25, randomPairs);
        hardGame.setDifficulty(DifficultyEnum.HARD);

        storage.saveGame(easyGame, storage.getEasyDirectory());
        storage.saveGame(mediumGame, storage.getMediumDirectory());
        storage.saveGame(hardGame, storage.getHardDirectory());

        System.out.println("Successfully generated 3 difficulty levels!");
    }

    @Override
    public String verifyGame(Game game) {
        int[][] grid = game.getGrid();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return "INCOMPLETE";
                }
            }
        }

        List<ValidationStrategy> strategies = List.of(
                new RowValidation(),
                new ColumnValidation(),
                new BoxValidation());

        for (ValidationStrategy strategy : strategies) {
            if (!strategy.validate(game)) {
                return "INVALID";
            }
        }

        return "VALID";
    }

    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        return solver.solve(game);
    }

    @Override
    public void logUserAction(String userAction) throws IOException {
        logger.logAction(userAction);
    }

    public Game loadIncompleteGame() {
        return storage.loadIncompleteGame();
    }

    public void saveCurrentGameState(Game game) {
        if (game != null) {
            storage.saveCurrentGame(game.getGrid());
        }
    }

    public boolean handleCompletedGame(Game game) {
        String status = verifyGame(game);

        if (status.equals("VALID")) {
            storage.deleteIncompleteGame();
            System.out.println("Congratulations! Puzzle solved correctly!");
            return true;
        } else if (status.equals("INVALID")) {
            System.out.println("Board is full but contains errors.");
            return false;
        }

        return false;
    }

    public UserAction getLastAction() {
        return logger.getLastAction();
    }

    public void undoLastAction() throws IOException {
        logger.removeLastAction();
    }

    private String getFolderPath(DifficultyEnum level) {
        return switch (level) {
            case EASY -> storage.getEasyDirectory();
            case MEDIUM -> storage.getMediumDirectory();
            case HARD -> storage.getHardDirectory();
        };
    }

    private void removeCellsFromGame(Game game, int count, RandomPairs randomPairs) {
        List<int[]> positions = randomPairs.generateDistinctPairs(count);
        for (int[] pos : positions) {
            game.setCell(pos[0], pos[1], 0);
        }
    }
}