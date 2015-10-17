package org.ftccommunity.ftcxtensible.util;

import java.util.List;

public interface JoystickQuestion {
    String getQuestion();

    String getIdentifier();

    List<String> getPossibleAnswers();

    void handleResponse(String response);

    String getUserResponse();
}
