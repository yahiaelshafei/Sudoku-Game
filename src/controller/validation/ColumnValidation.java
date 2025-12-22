package controller.validation;

import controller.grid.ColumnManager;
import model.*;

public class ColumnValidation implements ValidationStrategy {

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        ColumnManager manager = new ColumnManager();
        manager.run();
        return ColumnManager.getStatus();
    }
}