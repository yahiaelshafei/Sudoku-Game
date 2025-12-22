import java.util.Map;

public class BoxManager{
    private Box[] boxes = new Box[9];
    private static boolean status;

    public BoxManager() {
        status = true;
        for (int i = 0; i < 9; i++) {
            boxes[i] = Box.getInstance(i);
        }
    }

    public void run() {
        for (int i = 0; i < 9; i++) {
            status &= boxes[i].scan();
        }
    }
    public void run(Map<Integer,Integer> emptycells, int[] permutation){
        for (int i = 0; i < 9; i++) {
            status &= boxes[i].scan(emptycells,permutation);
        }
    }

    public void printError() {
        for (int i = 0; i < 9; i++) {
            boxes[i].printError();
        }
    }

    public static synchronized boolean getStatus() {
        return status;
    }

    public static synchronized void setStatus(boolean status) {
        BoxManager.status = status;
    }
}