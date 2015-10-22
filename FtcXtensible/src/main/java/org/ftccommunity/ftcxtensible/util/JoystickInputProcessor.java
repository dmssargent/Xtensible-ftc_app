package org.ftccommunity.ftcxtensible.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An manager to handle running configuration questions to the OpMode
 *
 * @author David Sargent
 * @since 0.2.2
 */
// todo: finish class
public class JoystickInputProcessor {
    private static final String TAG = "JOYSTICK_INPUT_PRSCR::";
    private RobotContext context;
    private LinkedList<JoystickQuestion> questions;

    public JoystickInputProcessor(@NotNull RobotContext ctx) {
        context = ctx;
        questions = new LinkedList<>();
    }

    public void registerQuestion(@NotNull JoystickQuestion... questionsToAdd) {
        for (JoystickQuestion question : questionsToAdd) {
            checkNotNull(question);
        }

        questions.addAll(Arrays.asList(questionsToAdd));
    }

    public void loadFromSavedState() {
        checkNotNull(context.appContext());
        checkState(questions != null && questions.size() > 0, "Register at least one question, first");

        FileInputStream inputStream;
        try {
            inputStream = context.appContext().openFileInput("joystickInputState.json");
        } catch (FileNotFoundException e) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = (new GsonBuilder()).create();
        Map<String, String> myMap;
        try {
            myMap = gson.fromJson(reader, type);
            if (myMap == null) {
                return;
            }
        } catch (JsonIOException | JsonSyntaxException ex) {
            Log.i(TAG, ex.getLocalizedMessage());
            return;
        }

        ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<>(myMap);

        // todo: merge for eachs
        // load answers into questions
        HashMap<String, JoystickQuestion> currentQuestions = new HashMap<>();
        for (JoystickQuestion question : questions) {
            currentQuestions.put(question.getIdentifier(), question);
        }

        for (Map.Entry<String, String> setting : settings.entrySet()) {
            if (currentQuestions.containsKey(setting.getKey())) {
                currentQuestions.get(setting.getKey()).handleResponse(setting.getValue());
            }
        }
    }
}
