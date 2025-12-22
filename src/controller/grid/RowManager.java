package controller.grid;

import java.util.*;

import model.grid.Row;

public class RowManager {
    private Row[] rows = new Row[9];
    private static boolean status;

    public RowManager() {
        status = true;
        for (int i = 0; i < 9; i++) {
            rows[i] = new Row(i);
        }
    }

    public void run() {
        for (int i = 0; i < 9; i++) {
            status &= rows[i].scan();
        }
    }

    public void run(Map<Integer, Integer> emptycells, int[] permutation) {
        for (int i = 0; i < 9; i++) {
            status &= rows[i].scan(emptycells, permutation);
        }
    }

    public void printError() {
        for (int i = 0; i < 9; i++) {
            rows[i].printError();
        }
    }

    public static synchronized boolean getStatus() {
        return status;
    }

    public static synchronized void setStatus(boolean status) {
        RowManager.status = status;
    }
}