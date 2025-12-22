import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameDriver implements Viewable {
    private static final String GAMES_DIRECTORY = "games";
    private static final String EASY_DIRECTORY = GAMES_DIRECTORY + "/easy";
    private static final String MEDIUM_DIRECTORY = GAMES_DIRECTORY + "/medium";
    private static final String HARD_DIRECTORY = GAMES_DIRECTORY + "/hard";
    private static final String INCOMPLETE_DIRECTORY = GAMES_DIRECTORY + "/incomplete";

    private RowManager rowManager;
    private ColumnManager columnManager;
    private BoxManager boxManager;
    private Board board;

    public GameDriver() {
        board = Board.getInstance();
        rowManager = new RowManager();
        columnManager = new ColumnManager();
        boxManager = new BoxManager();
        create_Folder_Structure();
    }

    private void create_Folder_Structure() {
        try {
            Files.createDirectories(Paths.get(INCOMPLETE_DIRECTORY));
            Files.createDirectories(Paths.get(EASY_DIRECTORY));
            Files.createDirectories(Paths.get(MEDIUM_DIRECTORY));
            Files.createDirectories(Paths.get(HARD_DIRECTORY));
        } catch (IOException d) {
            System.err.println("Error creating folder structure: " + d.getMessage());
        }
    }

    @Override
    public Catalog getCatalog() {
        return new Catalog(hasIncompleteGame(), hasEasyGame() && hasMediumGame() && hasHardGame());
    }

    private boolean hasIncompleteGame() {
        try {
            File incompleteDir = new File(INCOMPLETE_DIRECTORY);
            File[] files = incompleteDir.listFiles((dir, name) -> name.endsWith(".csv"));
            return files != null && files.length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasHardGame() {
        return hasGameInFolder(HARD_DIRECTORY);
    }

    private boolean hasMediumGame() {
        return hasGameInFolder(MEDIUM_DIRECTORY);
    }

    private boolean hasEasyGame() {
        return hasGameInFolder(EASY_DIRECTORY);
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

    @Override
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        File gameFolder = new File(level.getFolderPath());
        File[] availableGames = gameFolder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (availableGames == null || availableGames.length == 0) {
            throw new NotFoundException("No game found for difficulty: " + level);
        }

        int randomIndex = new Random().nextInt(availableGames.length);
        String filePath = availableGames[randomIndex].getPath();
        int[][] gameGrid = loadGameFromFile(filePath);

        saveGameToFolder(INCOMPLETE_DIRECTORY, gameGrid, "current_game.csv");

        return new Game(gameGrid, level);
    }

    private int[][] loadGameFromFile(String filePath) {
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

    private void saveGameToFolder(String folderPath, int[][] grid, String fileName) {
        try {
            Path filePath = Paths.get(folderPath, fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        writer.write(String.valueOf(grid[i][j]));
                        if (j < 8) writer.write(",");
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving game to " + folderPath + ": " + e.getMessage());
        }
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
                    if (num > maxNumber) maxNumber = num;
                } catch (NumberFormatException e) {
                }
            }
        }

        return "game" + (maxNumber + 1) + ".csv";
    }

    @Override
    public String verifyGame(Game game) {
        int[][] grid = game.getGrid();

        // Check if the game is incomplete
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return "INCOMPLETE";
                }
            }
        }

        // Strategy-based validation
        List<ValidationStrategy> strategies = List.of(
                new RowValidation(),
                new ColumnValidation(),
                new BoxValidation()
        );

        for (ValidationStrategy strategy : strategies) {
            if (!strategy.validate(game)) return "INVALID";
        }

        return "VALID";
    }

    @Override
    public void driveGames(Game source) throws SolutionInvalidException {
        String verificationResult = verifyGame(source);

        if (!verificationResult.equals("VALID")) {
            throw new SolutionInvalidException(
                    "Source solution is " + verificationResult + ". Must be VALID."
            );
        }

        Game easyGame = source.copy();
        Game mediumGame = source.copy();
        Game hardGame = source.copy();

        RandomPairs randomPairs = new RandomPairs();

        removeCellsFromGame(easyGame, 10, randomPairs);
        easyGame.setDifficulty(DifficultyEnum.EASY);

        removeCellsFromGame(mediumGame, 25, randomPairs);
        mediumGame.setDifficulty(DifficultyEnum.MEDIUM);

        removeCellsFromGame(hardGame, 20, randomPairs);
        hardGame.setDifficulty(DifficultyEnum.HARD);

        saveGame(easyGame, EASY_DIRECTORY);
        saveGame(mediumGame, MEDIUM_DIRECTORY);
        saveGame(hardGame, HARD_DIRECTORY);

        System.out.println("Successfully generated 3 difficulty levels!");
    }

    private void removeCellsFromGame(Game game, int count, RandomPairs randomPairs) {
        List<int[]> positions = randomPairs.generateDistinctPairs(count);
        for (int[] pos : positions) {
            game.setCell(pos[0], pos[1], 0);
        }
    }

    private void saveGame(Game game, String folderPath) {
        String fileName = generateUniqueFileName(folderPath);
        saveGameToFolder(folderPath, game.getGrid(), fileName);
        System.out.println("Saved " + game.getDifficulty() + " game: " + folderPath + "/" + fileName);
    }

    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        int emptyCount = game.countEmptyCells();

        if (emptyCount != 5) {
            throw new InvalidGameException(
                    "Solver only works with exactly 5 empty cells. Found: " + emptyCount
            );
        }

        Map<Integer, Integer> emptyCells = game.getEmptyCells();
        int[] permutations = new int[5];
        Iterator permutator = new PermuationGenerator();
        boolean isValid = false;

        while (permutator.hasNext()) {
            permutations = (int[]) permutator.next();
            rowManager.run(emptyCells, permutations);
            columnManager.run(emptyCells, permutations);
            boxManager.run(emptyCells, permutations);

            isValid = RowManager.getStatus() &&
                    ColumnManager.getStatus() &&
                    BoxManager.getStatus();
            if (isValid) break;
        }

        int[] ret = new int[game.countEmptyCells() * 3];
        if (isValid) {
            int i = 0;
            while (i != 5) {
                for (Map.Entry<Integer, Integer> mapElement : emptyCells.entrySet()) {
                    int key = mapElement.getKey();
                    int value = mapElement.getValue();

                    if (value == i) {
                        ret[i * 3] = key / 10;
                        ret[i * 3 + 1] = key % 10;
                        ret[i * 3 + 2] = permutations[value];
                        i++;
                    }
                }
            }
            return ret;
        }

        throw new UnsupportedOperationException("Unsolvable grid");
    }

    @Override
    public void logUserAction(String userAction) throws IOException {
        Path logPath = Paths.get(INCOMPLETE_DIRECTORY, "log.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(logPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(userAction);
            writer.newLine();
        }
    }

    public void deleteGameFromFolder(DifficultyEnum difficulty, String fileName) {
        String folderPath = difficulty.getFolderPath();
        Path filePath = Paths.get(folderPath, fileName);

        try {
            Files.deleteIfExists(filePath);
            System.out.println("Deleted completed game: " + filePath);
        } catch (IOException e) {
            System.err.println("Error deleting game: " + e.getMessage());
        }
    }

    public void deleteCurrentGame() {
        try {
            File incompleteDir = new File(INCOMPLETE_DIRECTORY);
            File[] files = incompleteDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    Files.deleteIfExists(file.toPath());
                    System.out.println("Deleted: " + file.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error deleting current game: " + e.getMessage());
        }
    }

    public boolean handleCompletedGame(Game game, String originalFileName) {
        String gameStatus = verifyGame(game);

        return switch (gameStatus) {
            case "VALID" -> {
                deleteGameFromFolder(game.getDifficulty(), originalFileName);
                deleteCurrentGame();
                System.out.println("Congratulations! Puzzle solved correctly!");
                yield true;
            }
            case "INVALID" -> {
                System.out.println("Board is full but contains errors. Keep trying!");
                yield false;
            }
            default -> {
                System.out.println("Board is not complete yet.");
                yield false;
            }
        };
    }

    public void saveCurrentGameState(Game game) {
        if (game != null) {
            saveGameToFolder(INCOMPLETE_DIRECTORY, game.getGrid(), "current_game.csv");
        }
    }

    public Game loadIncompleteGame() {
        try {
            File incompleteDir = new File(INCOMPLETE_DIRECTORY);
            File[] files = incompleteDir.listFiles((dir, name) -> name.endsWith(".csv"));

            boolean filesExist = files != null && files.length > 0;
            return filesExist ? new Game(loadGameFromFile(files[0].getPath())) : null;

        } catch (Exception e) {
            System.err.println("Error loading incomplete game: " + e.getMessage());
            return null;
        }
    }
}
