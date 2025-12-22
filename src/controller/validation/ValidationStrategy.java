package controller.validation;

import model.*;

public interface ValidationStrategy {
    boolean validate(Game game);
}