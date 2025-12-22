package view;

import controller.*;
import controller.exception.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SudokuGui extends JFrame {

    private ControllerFacade controller;
    private Game currentGame;
    private int[][] originalGrid;
    private boolean[][] editableCells;
    private JTextField[][] cells = new JTextField[9][9];
    private JButton solveButton;
    private StorageManager storage;

    public SudokuGui(ControllerFacade controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        
        this.controller = controller;
        this.storage = StorageManager.getInstance();
        setTitle("Sudoku Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 750);
        setLocationRelativeTo(null);

        handleStartup();
    }

    private void handleStartup() {
        try {
            Catalog catalog = controller.getCatalog();

            if (catalog.isGameInProgress()) {
                currentGame = controller.loadIncompleteGame();
                if (currentGame != null) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "You have an unfinished game. Continue?",
                            "Resume Game",
                            JOptionPane.YES_NO_OPTION);

                    if (option != JOptionPane.YES_OPTION) {
                        currentGame = null;
                    }
                }
            }

            if (currentGame == null) {
                if (catalog.isAllModesExist()) {
                    DifficultyEnum difficulty = askDifficultyWithAvailability();
                    currentGame = controller.getGame(difficulty);
                    currentGame.setDifficulty(difficulty);
                } else {
                    askForSolvedFileAndGenerate();

                    DifficultyEnum difficulty = askDifficultyWithAvailability();
                    currentGame = controller.getGame(difficulty);
                    currentGame.setDifficulty(difficulty);
                }
            }

            if (currentGame != null) {
                initializeGameState();
                showBoardScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading game: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeGameState() {
        originalGrid = currentGame.toIntArray();
        editableCells = new boolean[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                editableCells[i][j] = (originalGrid[i][j] == 0);
            }
        }
    }

    private DifficultyEnum askDifficultyWithAvailability() {
        boolean hasEasy = storage.hasEasyGame();
        boolean hasMedium = storage.hasMediumGame();
        boolean hasHard = storage.hasHardGame();

        if (!hasEasy && !hasMedium && !hasHard) {
            JOptionPane.showMessageDialog(this,
                    "No games available in any difficulty!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Choose difficulty level:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        JButton easyBtn = new JButton("Easy");
        JButton mediumBtn = new JButton("Medium");
        JButton hardBtn = new JButton("Hard");

        // Disable buttons if no games available and show count
        int easyCount = storage.getGameCount(storage.getEasyDirectory());
        int mediumCount = storage.getGameCount(storage.getMediumDirectory());
        int hardCount = storage.getGameCount(storage.getHardDirectory());

        easyBtn.setEnabled(hasEasy);
        easyBtn.setText("Easy (" + easyCount + ")");
        
        mediumBtn.setEnabled(hasMedium);
        mediumBtn.setText("Medium (" + mediumCount + ")");
        
        hardBtn.setEnabled(hasHard);
        hardBtn.setText("Hard (" + hardCount + ")");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        easyBtn.setFont(buttonFont);
        mediumBtn.setFont(buttonFont);
        hardBtn.setFont(buttonFont);

        buttonPanel.add(easyBtn);
        buttonPanel.add(mediumBtn);
        buttonPanel.add(hardBtn);
        
        panel.add(buttonPanel);

        // Use a final array to store the result
        final DifficultyEnum[] result = new DifficultyEnum[1];
        final JDialog dialog = new JDialog(this, "Difficulty Selection", true);

        easyBtn.addActionListener(e -> {
            result[0] = DifficultyEnum.EASY;
            dialog.dispose();
        });

        mediumBtn.addActionListener(e -> {
            result[0] = DifficultyEnum.MEDIUM;
            dialog.dispose();
        });

        hardBtn.addActionListener(e -> {
            result[0] = DifficultyEnum.HARD;
            dialog.dispose();
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        if (result[0] == null) {
            if (hasEasy) return DifficultyEnum.EASY;
            if (hasMedium) return DifficultyEnum.MEDIUM;
            if (hasHard) return DifficultyEnum.HARD;
        }

        return result[0] != null ? result[0] : DifficultyEnum.EASY;
    }

    private void askForSolvedFileAndGenerate() {
        JOptionPane.showMessageDialog(this,
                "No games available. Please provide a solved Sudoku CSV file.",
                "Generate Games",
                JOptionPane.INFORMATION_MESSAGE);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Solved Sudoku CSV");
        fileChooser.setCurrentDirectory(new File("."));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                Board.getInstance().readFromFile(file.getAbsolutePath());
                Game sourceGame = new Game(Board.getInstance().getGrid());

                controller.driveGames(sourceGame);

                JOptionPane.showMessageDialog(this,
                        "Successfully generated Easy, Medium, and Hard games!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SolutionInvalidException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid solution provided: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No file selected. Exiting.",
                    "Exit",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    private void showBoardScreen() {
        getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel gridPanel = createGridPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        updateSolveButtonState();
        revalidate();
        repaint();
        setVisible(true);
    }

    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 1, 1));
        gridPanel.setBackground(Color.BLACK);
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        Font cellFont = new Font("Arial", Font.BOLD, 24);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(cellFont);

                int value = currentGame.getCell(i, j);
                cell.setText(value == 0 ? "" : String.valueOf(value));

                if (!editableCells[i][j]) {
                    cell.setEditable(false);
                    cell.setBackground(new Color(230, 230, 230));
                    cell.setForeground(Color.BLACK);
                } else {
                    cell.setEditable(true);
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(new Color(0, 100, 200));
                }

                int top = (i % 3 == 0) ? 2 : 0;
                int left = (j % 3 == 0) ? 2 : 0;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, 0, 0, Color.BLACK));

                if (editableCells[i][j]) {
                    int row = i, col = j;
                    cell.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            handleCellInput(e, cell, row, col);
                        }
                    });
                }

                cells[i][j] = cell;
                gridPanel.add(cell);
            }
        }

        return gridPanel;
    }

    private void handleCellInput(KeyEvent e, JTextField cell, int row, int col) {
        char c = e.getKeyChar();

        if (c >= '1' && c <= '9') {
            int previousValue = currentGame.getCell(row, col);
            int newValue = Character.getNumericValue(c);

            currentGame.setCell(row, col, newValue);
            cell.setText(String.valueOf(newValue));

            try {
                UserAction action = new UserAction(row, col, newValue, previousValue);
                controller.logUserAction(action);
            } catch (Exception ex) {
                System.err.println("Error logging action: " + ex.getMessage());
            }

            controller.saveCurrentGameState(currentGame);
            updateSolveButtonState();

            if (currentGame.isComplete()) {
                checkCompletedGame();
            }

            e.consume();

        } else if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            int previousValue = currentGame.getCell(row, col);

            if (previousValue != 0) {
                currentGame.setCell(row, col, 0);
                cell.setText("");

                try {
                    UserAction action = new UserAction(row, col, 0, previousValue);
                    controller.logUserAction(action);
                } catch (Exception ex) {
                    System.err.println("Error logging action: " + ex.getMessage());
                }

                controller.saveCurrentGameState(currentGame);
                updateSolveButtonState();
            }

            e.consume();
        } else {
            e.consume();
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));

        JButton verifyBtn = new JButton("Verify");
        solveButton = new JButton("Solve");
        JButton undoBtn = new JButton("Undo");
        JButton resetBtn = new JButton("Reset");
        JButton exitBtn = new JButton("Exit");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton btn : new JButton[] { verifyBtn, solveButton, undoBtn, resetBtn, exitBtn }) {
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
        }

        verifyBtn.addActionListener(e -> onVerify());
        solveButton.addActionListener(e -> onSolve());
        undoBtn.addActionListener(e -> onUndo());
        resetBtn.addActionListener(e -> onReset());
        exitBtn.addActionListener(e -> onExit());

        panel.add(verifyBtn);
        panel.add(solveButton);
        panel.add(undoBtn);
        panel.add(resetBtn);
        panel.add(exitBtn);

        return panel;
    }

    private void updateSolveButtonState() {
        if (solveButton != null) {
            int emptyCount = currentGame.countEmptyCells();
            
            // Enable solve for 1-9 empty cells
            solveButton.setEnabled(emptyCount > 0 && emptyCount < 10);

            if (emptyCount == 0) {
                solveButton.setToolTipText("Board is already complete");
            } else if (emptyCount >= 10) {
                solveButton.setToolTipText("Too many empty cells (max 9). Currently: " + emptyCount);
            } else {
                solveButton.setToolTipText("Click to solve remaining " + emptyCount + " cells");
            }
        }
    }

    private void onVerify() {
        String result = controller.verifyGame(currentGame);

        String message = switch (result) {
            case "VALID" -> "Perfect! All filled cells are correct!";
            case "INCOMPLETE" -> "Board is incomplete. Keep solving!";
            case "INVALID" -> "Board contains errors. Check your entries!";
            default -> "Verification result: " + result;
        };

        JOptionPane.showMessageDialog(this,
                message,
                "Verification Result",
                result.equals("VALID") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void onSolve() {
        try {
            int emptyCount = currentGame.countEmptyCells();
            
            if (emptyCount == 0) {
                JOptionPane.showMessageDialog(this,
                        "Board is already complete!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (emptyCount >= 10) {
                JOptionPane.showMessageDialog(this,
                        "Too many empty cells to solve (" + emptyCount + "). Maximum is 9.",
                        "Cannot Solve",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create a progress dialog for larger puzzles
            JDialog progressDialog = null;
            if (emptyCount > 5) {
                progressDialog = createProgressDialog(emptyCount);
                final JDialog finalDialog = progressDialog;
                
                // Show dialog in a separate thread
                SwingUtilities.invokeLater(() -> finalDialog.setVisible(true));
            }

            final JDialog dialogToClose = progressDialog;
            
            // Solve in background thread to keep UI responsive
            SwingWorker<int[], Void> worker = new SwingWorker<int[], Void>() {
                @Override
                protected int[] doInBackground() throws Exception {
                    return controller.solveGame(currentGame);
                }

                @Override
                protected void done() {
                    try {
                        if (dialogToClose != null) {
                            dialogToClose.dispose();
                        }

                        int[] solution = get();

                        // Apply solution
                        for (int i = 0; i < solution.length; i += 3) {
                            int r = solution[i];
                            int c = solution[i + 1];
                            int v = solution[i + 2];

                            currentGame.setCell(r, c, v);
                            cells[r][c].setText(String.valueOf(v));
                            cells[r][c].setForeground(new Color(0, 150, 0));
                        }

                        controller.saveCurrentGameState(currentGame);

                        JOptionPane.showMessageDialog(SudokuGui.this,
                                "Successfully solved! " + (solution.length / 3) + " cells filled.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        if (currentGame.isComplete()) {
                            checkCompletedGame();
                        }

                    } catch (Exception e) {
                        if (dialogToClose != null) {
                            dialogToClose.dispose();
                        }
                        JOptionPane.showMessageDialog(SudokuGui.this,
                                "Solve failed: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Solve failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JDialog createProgressDialog(int emptyCount) {
        JDialog dialog = new JDialog(this, "Solving...", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("<html><center>Solving " + emptyCount + " cells...<br>" +
                                        "Please wait</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        return dialog;
    }

    private void onUndo() {
        try {
            UserAction lastAction = controller.getLastAction();

            if (lastAction == null) {
                JOptionPane.showMessageDialog(this,
                        "Nothing to undo.",
                        "Undo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int row = lastAction.getRow();
            int col = lastAction.getCol();
            int previousValue = lastAction.getPreviousValue();

            currentGame.setCell(row, col, previousValue);
            cells[row][col].setText(previousValue == 0 ? "" : String.valueOf(previousValue));

            controller.undoLastAction();
            controller.saveCurrentGameState(currentGame);

            updateSolveButtonState();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Undo failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onReset() {
        int option = JOptionPane.showConfirmDialog(this,
                "Reset board to original state?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (editableCells[i][j]) {
                        currentGame.setCell(i, j, 0);
                        cells[i][j].setText("");
                    }
                }
            }

            try {
                controller.clearLog();
            } catch (Exception e) {
                System.err.println("Error clearing log: " + e.getMessage());
            }

            controller.saveCurrentGameState(currentGame);
            updateSolveButtonState();
        }
    }

    private void onExit() {
        int option = JOptionPane.showConfirmDialog(this,
                "Exit game? Progress will be saved.",
                "Exit",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            controller.saveCurrentGameState(currentGame);
            System.exit(0);
        }
    }

    private void checkCompletedGame() {
        String result = controller.verifyGame(currentGame);

        if (result.equals("VALID")) {
            JOptionPane.showMessageDialog(this,
                    "Congratulations! You solved the puzzle correctly!",
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);

            controller.handleCompletedGame(currentGame);

            int option = JOptionPane.showConfirmDialog(this,
                    "Play another game?",
                    "Continue?",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new SudokuGui(controller));
            } else {
                System.exit(0);
            }
        } else if (result.equals("INVALID")) {
            JOptionPane.showMessageDialog(this,
                    "Board is full but contains errors. Keep trying!",
                    "Invalid Solution",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}