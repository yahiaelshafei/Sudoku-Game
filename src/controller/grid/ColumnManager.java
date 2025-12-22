package controller.grid;

import java.util.*;

import model.grid.Column;

public class ColumnManager {
    private Column[] columns = new Column[9];
    private static boolean status;

    public ColumnManager() {
        status = true;
        for (int i = 0; i < 9; i++) {
            columns[i] = new Column(i);
        }
    }

    public void run() {
        for (int i = 0; i < 9; i++) {
            status &= columns[i].scan();
        }
    }

    public void run(Map<Integer, Integer> emptycells, int[] permutation) {
        for (int i = 0; i < 9; i++) {
            status &= columns[i].scan(emptycells, permutation);
        }
    }

    public void printError() {
        for (int i = 0; i < 9; i++) {
            columns[i].printError();
        }
    }

    public static synchronized boolean getStatus() {
        return status;
    }

    public static synchronized void setStatus(boolean status) {
        ColumnManager.status = status;
    }
}