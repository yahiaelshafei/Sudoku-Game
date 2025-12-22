package controller.solver;

import java.util.*;

public class BoardVerifier {

    private StringBuilder[] rowLocations;
    private StringBuilder[] colLocations;
    private StringBuilder[] boxLocations;

    public BoardVerifier() {
        rowLocations = new StringBuilder[10];
        colLocations = new StringBuilder[10];
        boxLocations = new StringBuilder[10];

        for (int i = 0; i < 10; i++) {
            rowLocations[i] = new StringBuilder();
            colLocations[i] = new StringBuilder();
            boxLocations[i] = new StringBuilder();
        }
    }

    public boolean verify(int[][] board, Map<Integer, Integer> emptyCells, int[] permutation) {
        resetLocations();

        boolean valid = true;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board[row][col];

                if (value == 0) {
                    Integer idx = emptyCells.get(row * 10 + col);
                    if (idx != null) {
                        value = permutation[idx];
                    }
                }

                rowLocations[value].append('1');
                valid &= rowLocations[value].length() == 1;
            }

            if (!valid)
                return false;
            resetRow(rowLocations);
        }

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                int value = board[row][col];

                if (value == 0) {
                    Integer idx = emptyCells.get(row * 10 + col);
                    if (idx != null) {
                        value = permutation[idx];
                    }
                }

                colLocations[value].append('1');
                valid &= colLocations[value].length() == 1;
            }

            if (!valid)
                return false;
            resetRow(colLocations);
        }

        for (int boxIdx = 0; boxIdx < 9; boxIdx++) {
            int startRow = (boxIdx / 3) * 3;
            int startCol = (boxIdx % 3) * 3;

            for (int i = 0; i < 9; i++) {
                int row = startRow + (i / 3);
                int col = startCol + (i % 3);
                int value = board[row][col];

                if (value == 0) {
                    Integer idx = emptyCells.get(row * 10 + col);
                    if (idx != null) {
                        value = permutation[idx];
                    }
                }

                boxLocations[value].append('1');
                valid &= boxLocations[value].length() == 1;
            }

            if (!valid)
                return false;
            resetRow(boxLocations);
        }

        return valid;
    }

    private void resetLocations() {
        resetRow(rowLocations);
        resetRow(colLocations);
        resetRow(boxLocations);
    }

    private void resetRow(StringBuilder[] locations) {
        for (int i = 0; i < 10; i++) {
            locations[i].setLength(0);
        }
    }

    public boolean verifyComplete(int[][] board) {
        resetLocations();
        boolean valid = true;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board[row][col];
                if (value == 0)
                    return false;

                rowLocations[value].append('1');
                valid &= rowLocations[value].length() == 1;
            }
            if (!valid)
                return false;
            resetRow(rowLocations);
        }

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                int value = board[row][col];
                colLocations[value].append('1');
                valid &= colLocations[value].length() == 1;
            }
            if (!valid)
                return false;
            resetRow(colLocations);
        }

        for (int boxIdx = 0; boxIdx < 9; boxIdx++) {
            int startRow = (boxIdx / 3) * 3;
            int startCol = (boxIdx % 3) * 3;

            for (int i = 0; i < 9; i++) {
                int row = startRow + (i / 3);
                int col = startCol + (i % 3);
                int value = board[row][col];

                boxLocations[value].append('1');
                valid &= boxLocations[value].length() == 1;
            }
            if (!valid)
                return false;
            resetRow(boxLocations);
        }

        return valid;
    }
}