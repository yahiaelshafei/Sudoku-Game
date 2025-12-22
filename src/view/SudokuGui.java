package view;

import javax.swing.*;

import controller.DifficultyEnum;
import model.Catalog;
import model.Game;
import model.UserAction;
import model.grid.Board;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

public class SudokuGui extends JFrame {

    private Controllable controller;
    private Game currentGame;
    private int[][] originalGrid;
    private boolean[][] editableCells;
    private JTextField[][] cells = new JTextField[9][9];
    private Stack<UserAction> undoStack = new Stack<>();

    public SudokuGui(Controllable controller) {
        this.controller = controller;
        setTitle("Sudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        handleStartup();
    }

    private void handleStartup() {
        Catalog catalog = controller.getCatalog();

        try {
            // 1. Load incomplete game if exists
            if (catalog.isGameInProgress()) {
                currentGame = controller.loadIncompleteGame();
                if (currentGame != null) {
                    JOptionPane.showMessageDialog(this, "Resuming unfinished game...");
                }
            }

            // 2. If no incomplete game, check all difficulties
            if (currentGame == null) {
                if (catalog.isAllModesExist()) {
                    DifficultyEnum diff = askDifficulty();
                    currentGame = controller.getGame(diff);
                } else {
                    // 3. Neither incomplete nor full set exists → ask for solved file
                    askForSolvedFile();
                    DifficultyEnum diff = askDifficulty();
                    currentGame = controller.getGame(diff);
                }
            }

            // Initialize editable cells
            if (currentGame != null) {
                originalGrid = currentGame.toIntArray();
                editableCells = new boolean[9][9];
                for (int i = 0; i < 9; i++)
                    for (int j = 0; j < 9; j++)
                        editableCells[i][j] = originalGrid[i][j] == 0;

                showBoardScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage());
        }
    }

    private DifficultyEnum askDifficulty() {
        String[] options = { "Easy", "Medium", "Hard" };
        int choice = JOptionPane.showOptionDialog(this,
                "Choose difficulty",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        return switch (choice) {
            case 0 -> DifficultyEnum.EASY;
            case 1 -> DifficultyEnum.MEDIUM;
            case 2 -> DifficultyEnum.HARD;
            default -> DifficultyEnum.EASY;
        };
    }

    private void askForSolvedFile() {
        JOptionPane.showMessageDialog(this,
                "No incomplete game or full set found. Please select a solved Sudoku CSV file.");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select solved Sudoku CSV");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Board.getInstance().readFromFile(file.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "No file selected. Exiting.");
            System.exit(0);
        }
    }

    private void showBoardScreen() {
        getContentPane().removeAll();
        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        Font font = new Font("SansSerif", Font.BOLD, 20);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField tf = new JTextField();
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(font);
                tf.setText(Integer.toString(currentGame.getCell(i, j)));
                tf.setEditable(editableCells[i][j]);
                int row = i, col = j;

                if (editableCells[i][j]) {
                    tf.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyReleased(KeyEvent e) {
                            String text = tf.getText();
                            int prev = currentGame.getCell(row, col);
                            int val = 0;
                            try {
                                val = Integer.parseInt(text);
                                if (val < 1 || val > 9)
                                    throw new NumberFormatException();
                            } catch (NumberFormatException ex) {
                                tf.setText(prev == 0 ? "" : Integer.toString(prev));
                                return;
                            }
                            currentGame.setCell(row, col, val);
                            undoStack.push(new UserAction(row, col, val, prev));
                        }
                    });
                }

                cells[i][j] = tf;
                gridPanel.add(tf);
            }
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        JButton verifyBtn = new JButton("Verify");
        JButton solveBtn = new JButton("Solve");
        JButton undoBtn = new JButton("Undo");
        JButton resetBtn = new JButton("Reset");
        JButton exitBtn = new JButton("Exit");

        verifyBtn.addActionListener(e -> onVerify());
        solveBtn.addActionListener(e -> onSolve());
        undoBtn.addActionListener(e -> onUndo());
        resetBtn.addActionListener(e -> onReset());
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(verifyBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(undoBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(exitBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
        setVisible(true);
    }

    private void onVerify() {
        String result = controller.verifyGame(currentGame);
        JOptionPane.showMessageDialog(this, "Verification result: " + result);
    }

    private void onSolve() {
        try {
            int[] solution = controller.solveGame(currentGame);
            for (int i = 0; i < solution.length; i += 3) {
                int r = solution[i], c = solution[i + 1], v = solution[i + 2];
                currentGame.setCell(r, c, v);
                cells[r][c].setText(Integer.toString(v));
            }
            JOptionPane.showMessageDialog(this, "Solved successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Solve failed: " + e.getMessage());
        }
    }

    private void onUndo() {
        if (!undoStack.isEmpty()) {
            UserAction action = undoStack.pop();
            currentGame.setCell(action.getRow(), action.getCol(), action.getPreviousValue());
            cells[action.getRow()][action.getCol()]
                    .setText(action.getPreviousValue() == 0 ? "" : Integer.toString(action.getPreviousValue()));
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
        }
    }

    private void onReset() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (editableCells[i][j]) {
                    currentGame.setCell(i, j, 0);
                    cells[i][j].setText("");
                }
        undoStack.clear();
    }
}
