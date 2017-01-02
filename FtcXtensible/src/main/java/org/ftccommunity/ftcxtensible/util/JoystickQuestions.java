package org.ftccommunity.ftcxtensible.util;

import com.google.common.collect.ImmutableMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class JoystickQuestions {
    private static final String NO_RESPONSE_GIVEN = "no response";
    private final ExtensibleGamepad gamepad;
    private final LinkedList<Question> questions;
    private boolean povTrigger = false;
    private boolean buttonPress = false;
    private boolean started = false;
    private ImmutableMap<String, String> defensiveResponseMap;
    private ListIterator<Question> iterator;
    private Question currentQuestion;
    private Telemetry telemetry;

    public JoystickQuestions(RobotContext context) {
        this(context.gamepad1(), context.telemetry());
    }

    public JoystickQuestions(ExtensibleGamepad gamepad, Telemetry telemetry) {
        this.gamepad = gamepad;
        this.telemetry = telemetry;
        this.questions = new LinkedList<>();
    }

    public JoystickQuestions addQuestion(String tag, String question, String... responses) {
        checkState(!started, "You can't call this after loop has been called");
        checkArgument(!alreadyHas(tag), "Question tag is already in use");
        checkArgument(responses.length <= 4, "Too many responses given, current limit is 4");
        questions.add(new Question(tag, question, responses));

        return this;
    }

    public void loop() {
        checkState(!questions.isEmpty(), "You have either called stopRobot(), or you forgot to call addQuestion");
        if (iterator == null) {
            started = true;
            iterator = questions.listIterator();
            currentQuestion = questions.get(0);
        }

        if (!povTrigger) {
            if (gamepad.dpad.isLeftPressed() || gamepad.dpad.isRightPressed()) {
                povTrigger = true;

                if (gamepad.dpad.isRightPressed()) {
                    if (iterator.hasNext()) {
                        currentQuestion = iterator.next();
                    } else {
                        // Rewind
                        while (iterator.hasPrevious()) {
                            currentQuestion = iterator.previous();
                        }
                    }
                }

                // Else, dpad left is pressed
                if (iterator.hasPrevious()) {
                    currentQuestion = iterator.previous();
                } else {
                    // Rewind
                    while (iterator.hasNext()) {
                        currentQuestion = iterator.next();
                    }
                }
            }
        }

        if (gamepad.isAPressed() || gamepad.isBPressed() || gamepad.isXPressed() ||
                gamepad.isYPressed()) {
            if (!buttonPress) {
                buttonPress = true;

                if (gamepad.isAPressed())
                    currentQuestion.emulateAPress();
                else if (gamepad.isBPressed())
                    currentQuestion.emulateBPress();
                else if (gamepad.isXPressed())
                    currentQuestion.emulateXPress();
                else if (gamepad.isYPressed())
                    currentQuestion.emulateYPress();
            }
        } else {
            buttonPress = false;
        }

        // Update question display
        telemetry.addData("Q", currentQuestion.question);
        for (int i = 0; i < currentQuestion.responses.size(); i++) {
            telemetry.addData(Question.answerCaptions[i], currentQuestion.responses.get(i));
        }
        telemetry.addData("RESP", currentQuestion.currentResponse == null ? "None" : currentQuestion.currentResponse);
    }

    public void stop() {
        checkState(iterator != null, "You need to call loop at least once");
        HashMap<String, String> responseMap = new HashMap<>();
        iterator = null;
        currentQuestion = null;
        for (Question question : questions)
            responseMap.put(question.TAG, question.selectedResponse());

        questions.clear();
        defensiveResponseMap = ImmutableMap.copyOf(responseMap);
    }

    @Nullable
    public String responseTo(String tag) {
        return defensiveResponseMap.get(tag);
    }

    @NotNull
    public Map responseMap() {
        return defensiveResponseMap;
    }

    private boolean alreadyHas(String tag) {
        for (int i = 0, questionsSize = questions.size(); i < questionsSize; i++) {
            Question question = questions.get(i);
            if (tag.equals(question.TAG))
                return true;
        }

        return false;
    }


    private static class Question {
        private static String answerCaptions[] = {"A", "B", "X", "Y"};
        private final String TAG;
        private final String question;
        private final List<String> responses;
        private String currentResponse = JoystickQuestions.NO_RESPONSE_GIVEN;

        private Question(String tag, String question, String... responses) {
            this.TAG = tag;
            this.question = question;
            this.responses = Arrays.asList(responses);
        }

        void emulatePress(int index) {
            if (isValidIndex(index))
                currentResponse = responses.get(index);
        }

        void emulateAPress() {
            emulatePress(0);
        }

        void emulateBPress() {
            emulatePress(1);
        }

        void emulateXPress() {
            emulatePress(2);
        }

        void emulateYPress() {
            emulatePress(3);
        }

        String selectedResponse() {
            return currentResponse;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Question && TAG.equals(other);
        }

        @Override
        public int hashCode() {
            return TAG.hashCode();
        }

        private boolean isValidIndex(int responseIndex) {
            return (responseIndex >= 0 && responseIndex < responses.size());
        }
    }
}
