import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SudokuGUI extends JFrame {

    private Controllable controller;
    private Game currentGame;
    private JTable boardTable;
    private DefaultTableModel tableModel;
    private UserAction lastAction;

    public SudokuGUI(Controllable controller) {
        this.controller = controller;
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);

        handleStartup();

        setVisible(true);
    }

    private void handleStartup() {
        Catalog catalog = controller.getCatalog();

        try {
            if (catalog.isGameInProgress()) {
                // Load incomplete game
                currentGame = controller.loadIncompleteGame();
                if (currentGame == null) {
                    JOptionPane.showMessageDialog(this, "Error: no incomplete game found.");
                    askForSolvedFile();
                } else {
                    showBoardScreen();
                }
            } else if (catalog.isAllModesExist()) {
                // Ask for difficulty
                DifficultyEnum level = askDifficulty();
                currentGame = new Game(controller.getGame(level).getGrid(), level);
                showBoardScreen();
            } else {
                // Ask for solved Sudoku file
                askForSolvedFile();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Startup error: " + e.getMessage());
        }
    }

    private void askForSolvedFile() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a solved Sudoku CSV file");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            Board.getInstance().readFromFile(file.getPath());
            int[][] solvedGrid = Board.getInstance().getGrid();
            Game solvedGame = new Game(solvedGrid);
            controller.driveGames(solvedGame); // generate easy/medium/hard
            DifficultyEnum level = askDifficulty();
            currentGame = new Game(controller.getGame(level).getGrid(), level);
            showBoardScreen();
        } else {
            JOptionPane.showMessageDialog(this, "No file selected. Exiting.");
            System.exit(0);
        }
    }

    private DifficultyEnum askDifficulty() {
        String[] options = {"EASY", "MEDIUM", "HARD"};
        int choice = JOptionPane.showOptionDialog(this, "Select difficulty",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        return switch (choice) {
            case 0 -> DifficultyEnum.EASY;
            case 1 -> DifficultyEnum.MEDIUM;
            case 2 -> DifficultyEnum.HARD;
            default -> DifficultyEnum.EASY;
        };
    }

    private void showBoardScreen() {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(9, 9);
        boardTable = new JTable(tableModel);
        boardTable.setRowHeight(50);
        boardTable.setFont(new Font("Arial", Font.BOLD, 20));
        refreshGrid();

        JScrollPane scrollPane = new JScrollPane(boardTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton verifyBtn = new JButton("Verify");
        JButton solveBtn = new JButton("Solve");
        JButton undoBtn = new JButton("Undo");

        verifyBtn.addActionListener(e -> onVerifyClicked());
        solveBtn.addActionListener(e -> onSolveClicked());
        undoBtn.addActionListener(e -> onUndoClicked());

        buttonPanel.add(verifyBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(undoBtn);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        boardTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = boardTable.getSelectedRow();
                int col = boardTable.getSelectedColumn();
                String input = JOptionPane.showInputDialog("Enter value (1-9):");
                if (input != null && !input.isEmpty()) {
                    try {
                        int val = Integer.parseInt(input);
                        if (val >= 1 && val <= 9) {
                            int prev = currentGame.getCell(row, col);
                            currentGame.setCell(row, col, val);
                            lastAction = new UserAction(row, col, val, prev);
                            refreshGrid();
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        revalidate();
        repaint();
    }

    private void refreshGrid() {
        int[][] grid = currentGame.getGrid();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                tableModel.setValueAt(grid[i][j] == 0 ? "" : grid[i][j], i, j);
    }

    private void onVerifyClicked() {
        String result = controller.verifyGame(currentGame);
        JOptionPane.showMessageDialog(this, "Verification result: " + result);
    }

    private void onSolveClicked() {
        try {
            int[] solution = controller.solveGame(currentGame);
            for (int i = 0; i < solution.length; i += 3) {
                int r = solution[i], c = solution[i + 1], v = solution[i + 2];
                currentGame.setCell(r, c, v);
            }
            refreshGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Solve failed: " + e.getMessage());
        }
    }

    private void onUndoClicked() {
        if (lastAction != null) {
            currentGame.setCell(lastAction.getRow(), lastAction.getCol(), lastAction.getPreviousValue());
            refreshGrid();
            lastAction = null;
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SudokuGUI(new ControllerFacade(new GameDriver())));
    }
}
