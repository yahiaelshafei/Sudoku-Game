package controller.validation;

import model.Game;
import model.grid.Board;

public class BoxValidation implements ValidationStrategy {

    @Override
    public boolean validate(Game game) {
        Board.setGrid(game.getGrid());
        BoxManager manager = new BoxManager();
        manager.run();
        return BoxManager.getStatus();
    }
}
