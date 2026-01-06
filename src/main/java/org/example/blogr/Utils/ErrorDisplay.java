package org.example.blogr.Utils;

import org.controlsfx.validation.ValidationMessage;


import java.util.List;

public interface ErrorDisplay {

    void showValidationErrors(List<ValidationMessage> errors);

    void showServerError(String message);

    default void clear(){};
}

