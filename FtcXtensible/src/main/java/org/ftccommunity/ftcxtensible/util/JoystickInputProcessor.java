///*
// * Copyright © 2016 David Sargent
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// * and associated documentation files (the "Software"), to deal in the Software without restriction,
// * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
// * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
// * the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or
// * substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
// * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */
//package org.ftccommunity.ftcxtensible.util;
//
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonIOException;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//
//import org.ftccommunity.ftcxtensible.robot.RobotContext;
//import org.jetbrains.annotations.NotNull;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static com.google.common.base.Preconditions.checkNotNull;
//import static com.google.common.base.Preconditions.checkState;
//
///**
// * An manager to handle running configuration questions to the OpMode
// *
// * @author David Sargent
// * @since 0.2.2
// */
//// todo: finish class
//public class JoystickInputProcessor {
//    private static final String TAG = "JOYSTICK_INPUT_PRSCR::";
//    private final String[] inputButtons = {
//            "A", "B", "X", "Y", "DPAD_UP", "DPAD_DOWN"
//    };
//    private RobotContext context;
//    private LinkedList<JoystickQuestion> questions;
//    private int index = -1;
//
//    public JoystickInputProcessor(@NotNull RobotContext ctx) {
//        context = ctx;
//        questions = new LinkedList<>();
//    }
//
//    public void registerQuestion(@NotNull JoystickQuestion... questionsToAdd) {
//        for (JoystickQuestion question : questionsToAdd) {
//            checkNotNull(question);
//        }
//
//        questions.addAll(Arrays.asList(questionsToAdd));
//    }
//
//    public void loadFromSavedState() {
//        checkNotNull(context.appContext());
//        checkState(questions != null && questions.size() > 0, "Register at least one question, first");
//
//        FileInputStream inputStream;
//        try {
//            inputStream = context.appContext().openFileInput("joystickInputState.json");
//        } catch (FileNotFoundException e) {
//            return;
//        }
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        Type type = new TypeToken<Map<String, String>>() {
//        }.getType();
//        Gson gson = (new GsonBuilder()).create();
//        Map<String, String> myMap;
//        try {
//            myMap = gson.fromJson(reader, type);
//            if (myMap == null) {
//                return;
//            }
//        } catch (JsonIOException | JsonSyntaxException ex) {
//            Log.i(TAG, ex.getLocalizedMessage());
//            return;
//        }
//
//        ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<>(myMap);
//
//        // todo: merge for eachs
//        // load answers into questions
//        HashMap<String, JoystickQuestion> currentQuestions = new HashMap<>();
//        for (JoystickQuestion question : questions) {
//            currentQuestions.put(question.getIdentifier(), question);
//        }
//
//        for (Map.Entry<String, String> setting : settings.entrySet()) {
//            if (currentQuestions.containsKey(setting.getKey())) {
//                currentQuestions.get(setting.getKey()).handleResponse(setting.getValue());
//            }
//        }
//    }
//
//    public void loop() {
//        checkState(questions.size() > 0);
//
//        if (index == -1) {
//            index = 0;
//        }
//
//        JoystickQuestion joystickQuestion = questions.get(index);
//        List<String> answers = joystickQuestion.getPossibleAnswers();
//        context.telemetry().data(joystickQuestion.getIdentifier(), joystickQuestion.getQuestion());
//        for (int i = 0; i < answers.size() && i < inputButtons.length; i++) {
//            context.telemetry().data(inputButtons[i], answers.get(i));
//        }
//
//        int index = -1;
//        if (context.gamepad1().isAPressed()) {
//            index = 0;
//        } else if (context.gamepad1().isBPressed()) {
//            index = 1;
//        } else if (context.gamepad1().isXPressed()) {
//            index = 2;
//        } else if (context.gamepad1().isYPressed()) {
//            index = 3;
//        } else if (context.gamepad1().dpad.isUpPressed()) {
//            index = 4;
//        } else if (context.gamepad1().dpad.isDownPressed()) {
//            index = 5;
//        }
//
//        if (index != -1) {
//            joystickQuestion.handleResponse(answers.get(index));
//        }
//
//        if (context.gamepad1().dpad.isLeftPressed()) {
//            this.index = --this.index % questions.size();
//        } else if (context.gamepad1().dpad.isRightPressed()) {
//            this.index %= ++this.index % questions.size();
//        }
//    }
//}
