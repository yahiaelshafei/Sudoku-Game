public class UserAction {
    
    private int row;
    private int col;
    private int newValue;
    private int previousValue;
    
        public UserAction(int row, int col, int newValue, int previousValue) {
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.previousValue = previousValue;
    }

    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getNewValue() {
        return newValue;
    }
    
    public int getPreviousValue() {
        return previousValue;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + "," + newValue + "," + previousValue + ")";
    }

    public static UserAction fromString(String actionString) {
 
        String clean = actionString.replace("(", "").replace(")", "").trim();
        String[] parts = clean.split(",");
        
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid action string format: " + actionString);
        }
        
        int row = Integer.parseInt(parts[0].trim());
        int col = Integer.parseInt(parts[1].trim());
        int newValue = Integer.parseInt(parts[2].trim());
        int previousValue = Integer.parseInt(parts[3].trim());
        
        return new UserAction(row, col, newValue, previousValue);
    }
}
