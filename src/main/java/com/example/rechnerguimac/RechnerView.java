package com.example.rechnerguimac;

//Frontend

import eu.hansolo.tilesfx.skins.RadialDistributionTileSkin;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Objects;

public class RechnerView extends Application {
    String expression = "";
    Label lExcpression = new Label("");
    boolean prevResult = false;

    @Override
    public void start(Stage stage) throws Exception {
        final boolean[] darkmode = {true};
        VBox layout = new VBox();
        ScrollPane scrollPane = new ScrollPane(lExcpression);
        scrollPane.getStyleClass().add("background-black");
        lExcpression.getStyleClass().addAll("background-black", "lable-expression");

        GridPane gridPane = new GridPane();
        //gridPane.getStyleClass().add("background-black");
        gridPane.setHgap(5);
        gridPane.setVgap(5);


        ArrayList<Button> buttons = createButtons();


        String os = System.getProperty("os.name").toLowerCase();
        String text = "\uF8FF";
        String moon = "\uD83C\uDF19";
        String sun = "☀\uFE0F";
        if (!os.contains("mac")) {
            text = sun;
        }
        Button changeTheme = new Button(text);
        changeTheme.getStyleClass().addAll("font-white", "button-operation", "button-form");
        changeTheme.setOnAction(e -> {
            if (darkmode[0]) {
                // Remove dark classes
                lExcpression.getStyleClass().removeAll("background-black", "color-white");
                gridPane.getStyleClass().removeAll("background-black");
                layout.getStyleClass().removeAll("background-black");
                scrollPane.getStyleClass().removeAll("background-black");

                // Add light classes
                lExcpression.getStyleClass().addAll("background-white", "color-black");
                gridPane.getStyleClass().add("background-white");
                layout.getStyleClass().add("background-white");
                scrollPane.getStyleClass().add("background-white");

                darkmode[0] = false;
                if (!os.contains("mac")) {
                    changeTheme.setText(moon);
                }
            } else {
                // Remove light classes
                lExcpression.getStyleClass().removeAll("background-white", "color-black");
                gridPane.getStyleClass().removeAll("background-white");
                layout.getStyleClass().removeAll("background-white");
                scrollPane.getStyleClass().removeAll("background-white");

                // Add dark classes
                lExcpression.getStyleClass().addAll("background-black", "color-white");
                gridPane.getStyleClass().add("background-black");
                layout.getStyleClass().add("background-black");
                scrollPane.getStyleClass().add("background-black");

                darkmode[0] = true;
                if (!os.contains("mac")) {
                    changeTheme.setText(sun);
                }
            }

        });


        Button stayOnTop = new Button("top");
        stayOnTop.getStyleClass().addAll("button-operation", "button-form", "font-white", "background-red");
        stage.setAlwaysOnTop(false);
        stayOnTop.setOnAction(e -> {
            if (stage.isAlwaysOnTop()) {
                stage.setAlwaysOnTop(false);
                stayOnTop.getStyleClass().remove("background-green");
                stayOnTop.getStyleClass().add("background-red");
            } else {
                stage.setAlwaysOnTop(true);
                stayOnTop.getStyleClass().remove("background-red");
                stayOnTop.getStyleClass().add("background-green");

            }
        });
        buttons.add(stayOnTop);
        buttons.add(changeTheme);

        int row = 0;
        int col = 0;

        for (Button button : buttons) {
            if (button.getText().equals("+") || button.getText().equals("-") || button.getText().equals("×")
                    || button.getText().equals("÷")) {
                gridPane.add(button, col, row);
                row++;
                col = 0;
            } else {
                gridPane.add(button, col, row);
                col++;
            }
        }


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        //scrollPane.setFitToWidth(true);
        layout.getChildren().addAll(scrollPane, gridPane);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getStyleClass().add("background-black");
        Scene scene = new Scene(layout);


        // adding real keys

        scene.setOnKeyPressed(event -> {
            String number = event.getText();
            if (number.matches("[0-9]")) {
                appendToExpression(number);
            }
            if (event.getCode() == KeyCode.ENTER) {
                RechnerControler rc = new RechnerControler();
                String tmpExpr = expression;
                expression = rc.getCalculation(tmpExpr);
                updateLabel();

                prevResult = true;
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(lExcpression.getText());
                clipboard.setContent(content);


            }
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (lExcpression.getText().equals("Error") || prevResult) {
                    expression = "";
                    updateLabel();
                    prevResult = false;
                } else {
                    char[] arr = lExcpression.getText().toCharArray();
                    expression = "";
                    for (int i = 0; i < arr.length - 1; i++) {
                        expression += arr[i];
                    }
                    updateLabel();
                }
            }
            if (event.getCode() == KeyCode.DELETE) {
                expression = "";
                updateLabel();
            }
            if (number.matches("[+-//*,.]")) {
                if (number.equals("/")) {
                    appendToExpression("÷");
                } else if (number.equals("*")) {
                    appendToExpression("×");
                } else if (number.equals(".")) {
                    appendToExpression(",");
                } else {
                    appendToExpression(number);
                }
            }

            if (event.isShiftDown() && event.isAltDown() && event.getCode() == KeyCode.UP) {
                if (stage.isAlwaysOnTop()) {
                    stage.setAlwaysOnTop(false);
                    stayOnTop.getStyleClass().remove("background-green");
                    stayOnTop.getStyleClass().add("background-red");
                } else {
                    stage.setAlwaysOnTop(true);
                    stayOnTop.getStyleClass().remove("background-red");
                    stayOnTop.getStyleClass().add("background-green");

                }
            }

            if (event.getCode() == KeyCode.T) {
                if (darkmode[0]) {
                    // Remove dark classes
                    lExcpression.getStyleClass().removeAll("background-black", "color-white");
                    gridPane.getStyleClass().removeAll("background-black");
                    layout.getStyleClass().removeAll("background-black");
                    scrollPane.getStyleClass().removeAll("background-black");

                    // Add light classes
                    lExcpression.getStyleClass().addAll("background-white", "color-black");
                    gridPane.getStyleClass().add("background-white");
                    layout.getStyleClass().add("background-white");
                    scrollPane.getStyleClass().add("background-white");

                    darkmode[0] = false;
                    if (!os.contains("mac")) {
                        changeTheme.setText(moon);
                    }
                } else {
                    // Remove light classes
                    lExcpression.getStyleClass().removeAll("background-white", "color-black");
                    gridPane.getStyleClass().removeAll("background-white");
                    layout.getStyleClass().removeAll("background-white");
                    scrollPane.getStyleClass().removeAll("background-white");

                    // Add dark classes
                    lExcpression.getStyleClass().addAll("background-black", "color-white");
                    gridPane.getStyleClass().add("background-black");
                    layout.getStyleClass().add("background-black");
                    scrollPane.getStyleClass().add("background-black");

                    darkmode[0] = true;
                    if (!os.contains("mac")) {
                        changeTheme.setText(sun);
                    }
                }
            }


        });


        String css = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Calculator");
        stage.setResizable(false);
        stage.setMinHeight(351);
        stage.setMinWidth(215);
//        stage.initStyle(StageStyle.UNIFIED);
        updateLabel();

    }

    private void updateLabel() {
        if (expression.equals("Error")) {
            lExcpression.getStyleClass().add("font-red");

        } else {
            lExcpression.getStyleClass().remove("font-red");

        }


        lExcpression.setText(expression);


    }

    public static void main(String[] args) {
        launch();
    }


    private ArrayList<Button> createButtons() {
        ArrayList<Button> buttons = new ArrayList<>();

        Button button00 = new Button("7");
        button00.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button00.setOnAction(event -> {
            appendToExpression(button00.getText());
        });
        buttons.add(button00);


        Button button01 = new Button("8");
        button01.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button01.setOnAction(event -> {
            appendToExpression(button01.getText());
        });
        buttons.add(button01);


        Button button02 = new Button("9");
        button02.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button02.setOnAction(event -> {
            appendToExpression(button02.getText());
        });
        buttons.add(button02);

        Button button03 = new Button("÷");
        button03.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button03.setOnAction(event -> {
            appendToExpression(button03.getText());
        });
        buttons.add(button03);

        Button button04 = new Button("4");
        button04.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button04.setOnAction(event -> {
            appendToExpression(button04.getText());
        });
        buttons.add(button04);

        Button button05 = new Button("5");
        button05.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button05.setOnAction(event -> {
            appendToExpression(button05.getText());
        });
        buttons.add(button05);

        Button button06 = new Button("6");
        button06.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button06.setOnAction(event -> {
            appendToExpression(button06.getText());
        });
        buttons.add(button06);

        Button button07 = new Button("×");
        button07.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button07.setOnAction(event -> {
            appendToExpression(button07.getText());
        });
        buttons.add(button07);

        Button button08 = new Button("1");
        button08.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button08.setOnAction(event -> {
            appendToExpression(button08.getText());
        });
        buttons.add(button08);

        Button button09 = new Button("2");
        button09.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button09.setOnAction(event -> {
            appendToExpression(button09.getText());
        });
        buttons.add(button09);

        Button button10 = new Button("3");
        button10.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button10.setOnAction(event -> {
            appendToExpression(button10.getText());
        });
        buttons.add(button10);

        Button button11 = new Button("-");
        button11.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button11.setOnAction(event -> {
            appendToExpression(button11.getText());
        });
        buttons.add(button11);

        Button button12 = new Button("=");
        button12.getStyleClass().addAll("font-white", "button-calc", "button-form");
        button12.setOnAction(event -> {
            RechnerControler rc = new RechnerControler();
            String tmpExpr = expression;
            expression = rc.getCalculation(tmpExpr);
            updateLabel();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(lExcpression.getText());
            clipboard.setContent(content);
            prevResult = true;
        });
        buttons.add(button12);

        Button button13 = new Button("0");
        button13.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button13.setOnAction(event -> {
            appendToExpression(button13.getText());
        });
        buttons.add(button13);

        Button button14 = new Button(",");
        button14.getStyleClass().addAll("font-white", "button-normal", "button-form");
        button14.setOnAction(event -> {
            appendToExpression(button14.getText());
        });
        buttons.add(button14);

        Button button15 = new Button("+");
        button15.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button15.setOnAction(event -> {
            appendToExpression(button15.getText());
        });
        buttons.add(button15);

        Button button16 = new Button("del");
        button16.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button16.setOnAction(event -> {
            if (lExcpression.getText().equals("Error") || prevResult) {
                expression = "";
                updateLabel();
                prevResult = false;
            } else {

                char[] arr = lExcpression.getText().toCharArray();
                expression = "";
                for (int i = 0; i < arr.length - 1; i++) {
                    expression += arr[i];
                }
                updateLabel();
            }
        });
        buttons.add(button16);

        Button button17 = new Button("AC");
        button17.getStyleClass().addAll("font-white", "button-operation", "button-form");
        button17.setOnAction(event -> {
            expression = "";
            updateLabel();
        });
        buttons.add(button17);

//        Button button18 = new Button("quit");
//        button18.getStyleClass().addAll("button-quit", "button-form");
//        button18.setOnAction(event -> {
//            System.exit(0);
//        });
//        buttons.add(button18);

        return buttons;
    }

    private void appendToExpression(String value) {
        if (prevResult) {
            expression = "";
            prevResult = false;
        }

        if (value.matches("[×÷+\\-,]")) {
            if (expression.isBlank()) {
                return;
            }
            String lastChar = String.valueOf(expression.charAt(expression.length() - 1));
            if (lastChar.matches("[×÷+\\-,]")) {
                return; // does not add a rechenoperator if t1he last character is one
            }
        }

        if (value.equals(",")) {
            char[] arr = expression.toCharArray();
            for (int i = arr.length - 1; i > -1; i--) {
                if (String.valueOf(arr[i]).matches("[×÷+\\-]")) {
                    break;
                }
                if (arr[i] == ',') {
                    return;
                }

            }
        }

        expression = expression + value;
        updateLabel();
//        System.out.println("Experssion: " + expression);
//        System.out.println("Label: " + lExcpression.getText());
//        System.out.println();
    }

}