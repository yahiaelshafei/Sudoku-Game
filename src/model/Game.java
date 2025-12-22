package model;

import java.util.HashMap;
import java.util.Map;

import controller.DifficultyEnum;

public class Game {
    private int[][] board;
    private DifficultyEnum level;

    public Game(int[][] grid) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = grid[i][j];
            }
        }
        this.level = null;
    }

    public Game(int[][] grid, DifficultyEnum difficulty) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = grid[i][j];
            }
        }
        this.level = difficulty;
    }

    public int[][] getGrid() {
        return board;
    }

    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < 9 && col >= 0 && col < 9) {
            board[row][col] = value;
        }
    }

    public int getCell(int row, int col) {
        if (row >= 0 && row < 9 && col >= 0 && col < 9) {
            return board[row][col];
        }
        return -1;
    }

    public DifficultyEnum getDifficulty() {
        return level;
    }

    public void setDifficulty(DifficultyEnum difficulty) {
        this.level = difficulty;
    }

    public int[][] toIntArray() {
        int[][] result = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                result[i][j] = board[i][j];
            }
        }
        return result;
    }

    public Game copy() {
        return new Game(this.board, this.level);
    }

    public boolean isComplete() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int countEmptyCells() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public Map<Integer, Integer> getEmptyCells() {
        Map<Integer, Integer> emptyCells = new HashMap<>();

        int idx = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    emptyCells.put(i * 10 + j, idx);
                    idx++;
                }
            }
        }
        return emptyCells;
    }

}