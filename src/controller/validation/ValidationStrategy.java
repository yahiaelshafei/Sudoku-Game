package controller.validation;

import model.Game;

public interface ValidationStrategy {
    boolean validate(Game game);
}
