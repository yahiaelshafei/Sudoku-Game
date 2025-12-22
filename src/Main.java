import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Controllable controller = new ControllerFacade(new GameDriver());

        SwingUtilities.invokeLater(() -> {
            new SudokuGui(controller);
        });
    }
}
