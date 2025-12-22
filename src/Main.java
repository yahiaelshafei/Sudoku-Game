import javax.swing.*;

import controller.ControllerFacade;
import controller.GameDriver;
import view.Controllable;
import view.SudokuGui;

public class Main {
    public static void main(String[] args) {
        Controllable controller = new ControllerFacade(new GameDriver());

        SwingUtilities.invokeLater(() -> {
            new SudokuGui(controller);
        });
    }
}
