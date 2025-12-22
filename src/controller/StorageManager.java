package controller;

import model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import controller.exception.*;

public class StorageManager {
    private static StorageManager instance;

    private static final String GAMES_DIRECTORY = "games";
    private static final String EASY_DIRECTORY = GAMES_DIRECTORY + "/easy";
    private static final String MEDIUM_DIRECTORY = GAMES_DIRECTORY + "/medium";
    private static final String HARD_DIRECTORY = GAMES_DIRECTORY + "/hard";
    private static final String INCOMPLETE_DIRECTORY = GAMES_DIRECTORY + "/incomplete";
    private static final String CURRENT_GAME_FILE = "current_game.csv";

    private String currentGameSourceFolder = null;
    private String currentGameSourceFile = null;

    private StorageManager() {
        createFolderStructure();
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    private void createFolderStructure() {
        try {
            Files.createDirectories(Paths.get(INCOMPLETE_DIRECTORY));
            Files.createDirectories(Paths.get(EASY_DIRECTORY));
            Files.createDirectories(Paths.get(MEDIUM_DIRECTORY));
            Files.createDirectories(Paths.get(HARD_DIRECTORY));
        } catch (IOException e) {
            System.err.println("Error creating folder structure: " + e.getMessage());
        }
    }

    public boolean hasIncompleteGame() {
        return hasGameInFolder(INCOMPLETE_DIRECTORY);
    }

    public boolean hasEasyGame() {
        return hasGameInFolder(EASY_DIRECTORY);
    }

    public boolean hasMediumGame() {
        return hasGameInFolder(MEDIUM_DIRECTORY);
    }

    public boolean hasHardGame() {
        return hasGameInFolder(HARD_DIRECTORY);
    }

    private boolean hasGameInFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
            return files != null && files.length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Game loadRandomGame(String folderPath) throws NotFoundException {
        File gameFolder = new File(folderPath);
        File[] availableGames = gameFolder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (availableGames == null || availableGames.length == 0) {
            throw new NotFoundException("No game found in: " + folderPath);
        }

        int randomIndex = new Random().nextInt(availableGames.length);
        File selectedFile = availableGames[randomIndex];
        String filePath = selectedFile.getPath();
        int[][] gameGrid = loadGameFromFile(filePath);

        currentGameSourceFolder = folderPath;
        currentGameSourceFile = selectedFile.getName();

        saveGameToFolder(INCOMPLETE_DIRECTORY, gameGrid, CURRENT_GAME_FILE);

        return new Game(gameGrid);
    }

    public Game loadIncompleteGame() {
        try {
            File incompleteDir = new File(INCOMPLETE_DIRECTORY);
            File[] files = incompleteDir.listFiles((dir, name) -> name.endsWith(".csv"));

            if (files != null && files.length > 0) {
                return new Game(loadGameFromFile(files[0].getPath()));
            }
        } catch (Exception e) {
            System.err.println("Error loading incomplete game: " + e.getMessage());
        }
        return null;
    }

    public int[][] loadGameFromFile(String filePath) {
        int[][] grid = new int[9][9];

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < 9) {
                String[] values = line.split(",");
                for (int col = 0; col < 9 && col < values.length; col++) {
                    grid[row][col] = Integer.parseInt(values[col].trim());
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }

        return grid;
    }

    public void saveGameToFolder(String folderPath, int[][] grid, String fileName) {
        try {
            Path filePath = Paths.get(folderPath, fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        writer.write(String.valueOf(grid[i][j]));
                        if (j < 8)
                            writer.write(",");
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving game to " + folderPath + ": " + e.getMessage());
        }
    }

    public void saveGame(Game game, String folderPath) {
        String fileName = generateUniqueFileName(folderPath);
        saveGameToFolder(folderPath, game.getGrid(), fileName);
        System.out.println("Saved game to: " + folderPath + "/" + fileName);
    }

    public void saveCurrentGame(int[][] grid) {
        saveGameToFolder(INCOMPLETE_DIRECTORY, grid, CURRENT_GAME_FILE);
    }

    private String generateUniqueFileName(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        int maxNumber = 0;
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                String numberPart = name.replace("game", "").replace(".csv", "");
                try {
                    int num = Integer.parseInt(numberPart);
                    if (num > maxNumber)
                        maxNumber = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return "game" + (maxNumber + 1) + ".csv";
    }

    public void deleteGameFromFolder(String folderPath, String fileName) {
        Path filePath = Paths.get(folderPath, fileName);
        try {
            Files.deleteIfExists(filePath);
            System.out.println("Deleted: " + filePath);
        } catch (IOException e) {
            System.err.println("Error deleting game: " + e.getMessage());
        }
    }

    public void deleteIncompleteGame() {
        try {
            File incompleteDir = new File(INCOMPLETE_DIRECTORY);
            File[] files = incompleteDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    Files.deleteIfExists(file.toPath());
                    System.out.println("Deleted incomplete: " + file.getName());
                }
            }

            if (currentGameSourceFolder != null && currentGameSourceFile != null) {
                deleteGameFromFolder(currentGameSourceFolder, currentGameSourceFile);
                System.out.println("Deleted original game: " + currentGameSourceFile +
                        " from " + currentGameSourceFolder);

                currentGameSourceFolder = null;
                currentGameSourceFile = null;
            }
        } catch (IOException e) {
            System.err.println("Error deleting incomplete game: " + e.getMessage());
        }
    }

    public void setCurrentGameSource(String folderPath, String fileName) {
        this.currentGameSourceFolder = folderPath;
        this.currentGameSourceFile = fileName;
    }

    public String getCurrentGameSourceFolder() {
        return currentGameSourceFolder;
    }

    public String getCurrentGameSourceFile() {
        return currentGameSourceFile;
    }

    public String getEasyDirectory() {
        return EASY_DIRECTORY;
    }

    public String getMediumDirectory() {
        return MEDIUM_DIRECTORY;
    }

    public String getHardDirectory() {
        return HARD_DIRECTORY;
    }

    public String getIncompleteDirectory() {
        return INCOMPLETE_DIRECTORY;
    }

    public int getGameCount(String folderPath) {
        try {
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
            return files != null ? files.length : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}