package model.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Board {
    private static int[][] grid;
    private static Board instance = null;

    private Board() {
    }

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    public void readFromFile(String fileName) {
        grid = new int[9][9];
        Path p = Paths.get(fileName);
        if (!Files.exists(p))
            return;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                for (int j = 0; j < 9; j++) {
                    grid[i][j] = Integer.parseInt(split[j]);
                }
                i++;
            }
        } catch (IOException e) {
            System.err.println("Error reading " + fileName + ": " + e.getMessage());
        }
        return;
    }

    public int[][] getGrid() {
        return grid;
    }

    // added
    public static void setGrid(int[][] newGrid) {
        grid = newGrid;
    }
}