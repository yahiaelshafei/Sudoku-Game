package model.grid;

import model.Board;

public abstract class GridElement {
    enum Type {
        ROW,
        COL,
        BOX;
    }

    protected Board board;
    protected int[][] grid = new int[9][9];
    protected int rowNumber;
    protected int columnNumber;
    protected int boxNumber;
    private int elementNumber;
    protected Type type;
    protected boolean status;
    protected StringBuilder[] locations = new StringBuilder[10];

    public GridElement(int elementNumber, Type type) {
        board = Board.getInstance();
        this.grid = board.getGrid();
        this.type = type;
        for (int i = 0; i < 10; i++) {
            locations[i] = new StringBuilder();
        }
        this.elementNumber = elementNumber;
        this.rowNumber = elementNumber;
        this.columnNumber = elementNumber;
        this.boxNumber = elementNumber;
        this.status = true;
    }

    abstract public boolean scan();

    public void printError() {
        for (int i = 1; i <= 9; i++) {
            if (locations[i].length() > 1) {
                System.out.println(type.toString() + " " + Integer.toString(elementNumber + 1) + ", #"
                        + Integer.toString(i) + " [" + String.join(",", locations[i].toString().split("")) + "]");
            }
        }
    }

    public boolean getStatus() {
        return status;
    }
}