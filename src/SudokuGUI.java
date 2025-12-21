public class SudokuGUI {

    private Controllable controller;
    private int[][] currentBoard;
    private Board board;

    public SudokuGUI(Controllable controller) {
        board = Board.getInstance();
        this.controller = controller;
        startApplication();
    }

    private void startApplication() {
        Catalog catalog = controller.getCatalog();

        try {
            if (catalog.isGameInProgress()) {
                System.out.println("Resuming unfinished game...");
                currentBoard = controller.getGame('I');
            }
            else if (catalog.isAllModesExist()) {
                char level = askDifficulty();
                currentBoard = controller.getGame(level);
            }
            else {
                int[][] solved = askSolvedBoard();
                controller.driveGames(solved);
                char level = askDifficulty();
                currentBoard = controller.getGame(level);
            }

            displayBoard();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private char askDifficulty() {
        System.out.println("Choose difficulty: E / M / H");
        return 'E'; // placeholder
    }

    private int[][] askSolvedBoard() {
        board.readFromFile("solved.csv");
        return board.getGrid();
    }

    public void onVerifyClicked() {
        boolean[][] result = controller.verifyGame(currentBoard);
        System.out.println("Verification requested.");
    }

    public void onSolveClicked() {
        try {
            currentBoard = controller.solveGame(currentBoard);
            displayBoard();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void onUndo(UserAction action) {
        try {
            controller.logUserAction(action);
        } catch (Exception e) {
            System.out.println("Undo failed.");
        }
    }

    private void displayBoard() {
        System.out.println("Displaying Sudoku board...");
    }
}
