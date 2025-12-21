public class SudokuGUI implements BoardObserver {

    private Controllable controller;
    private int[][] currentBoard;
    private Board board;

    public SudokuGUI(Controllable controller) {
        this.controller = controller;
        this.board = Board.getInstance();
        startApplication();
    }

    private void startApplication() {
        Catalog catalog = controller.getCatalog();

        try {
            if (catalog.isGameInProgress()) {
                // Load incomplete game
                Game incompleteGame = ((GameDriver) controller).loadIncompleteGame();
                if (incompleteGame != null) {
                    currentBoard = incompleteGame.getGrid();
                    System.out.println("Resuming unfinished game...");
                } else {
                    System.out.println("No incomplete game found.");
                }
            } else if (catalog.isAllModesExist()) {
                DifficultyEnum level = askDifficultyEnum();
                currentBoard = controller.getGame(level).getGrid();
            } else {
                int[][] solved = askSolvedBoard();
                Game solvedGame = new Game(solved, null);
                controller.driveGames(solvedGame);

                DifficultyEnum level = askDifficultyEnum();
                currentBoard = controller.getGame(level).getGrid();
            }

            displayBoard();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private DifficultyEnum askDifficultyEnum() {
        // Placeholder for user input
        System.out.println("Choose difficulty: E / M / H");
        char choice = 'E'; // Hardcoded for now
        return switch (choice) {
            case 'E' -> DifficultyEnum.EASY;
            case 'M' -> DifficultyEnum.MEDIUM;
            case 'H' -> DifficultyEnum.HARD;
            default -> DifficultyEnum.EASY;
        };
    }

    private int[][] askSolvedBoard() {
        board.readFromFile("solved.csv");
        return board.getGrid();
    }

    public void onVerifyClicked() {
        Game game = new Game(currentBoard, null);
        String result = controller.verifyGame(game);
        System.out.println("Verification result: " + result);
    }

    public void onSolveClicked() {
        try {
            Game game = new Game(currentBoard, null);
            int[] solution = controller.solveGame(game);
            applySolutionToBoard(solution, currentBoard);
            displayBoard();
        } catch (Exception e) {
            System.out.println("Solve failed: " + e.getMessage());
        }
    }

    private void applySolutionToBoard(int[] solution, int[][] board) {
        for (int i = 0; i < solution.length; i += 3) {
            int row = solution[i], col = solution[i + 1], value = solution[i + 2];
            board[row][col] = value;
        }
    }

    public void onUndo(UserAction action) {
        try {
            controller.logUserAction(action);
        } catch (Exception e) {
            System.out.println("Undo failed: " + e.getMessage());
        }
    }

    private void displayBoard() {
        System.out.println("Displaying Sudoku board:");
        for (int[] row : currentBoard) {
            for (int val : row) System.out.print(val + " ");
            System.out.println();
        }
    }

    @Override
    public void update(int[][] board) {
        System.out.println("Board updated:");
        for (int[] row : board) {
            for (int val : row) System.out.print(val + " ");
            System.out.println();
        }
    }
}
