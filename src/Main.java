import controller.*;
import view.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        ControllerFacade facade = new ControllerFacade(gameDriver);
        
        SwingUtilities.invokeLater(() -> {
            new SudokuGui(facade);
        });
    }
}