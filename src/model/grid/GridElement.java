package model.grid;
// array[y][x] ... for anyone reading this, to not get confused

// we start the grid at 0,0
// but in the locations[] array, the first index is skipped ,i.e. we use locations[1-9]

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
    // locations is an array that, for all numbers, would have a string of all the
    // indices of its occurrence
    // Since the indices are 1-9 (i.e. one digit):
    // we could use the .length() to get the number of occurrences
    // we could use the .split() when printing, to eliminate other iterations of the
    // row

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