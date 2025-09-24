package com.example.rechnerguimac;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;


public class RechnerView extends Application {
    String expression = "";
    Label lExcpression = new Label("");
    private Stage stage;
    private Scene scene;
    private boolean darkmode = true;
    private ScrollPane scrolPane;
    private GridPane gp;
    public ArrayList<String> history = new ArrayList<>();
    Stage historyStage;
    Scene historyScene;

    public void addToHistrory(String expr){
        history.add(expr);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;


        scrolPane = new ScrollPane(lExcpression);
        scrolPane.getStyleClass().add("background-black");
        scrolPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrolPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrolPane.setFitToHeight(true);
        scrolPane.setPannable(true);

        lExcpression.getStyleClass().addAll("background-black", "lable-expression");
        VBox root = new VBox(scrolPane, createButtons());


        this.scene = new Scene(root);
        addKeyboardShortCuts();
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon-1.png")));
        stage.getIcons().add(icon);
        String css = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Calculator");
        stage.setResizable(false);
        stage.setMinHeight(301);
        stage.setMinWidth(215);

    }

    private boolean isValid(String newValue){
        char[] arrayExpression = expression.toCharArray(); //expression als char Array
        String lastExpression = "";
        if(!expression.isBlank()){
            lastExpression = String.valueOf(arrayExpression[arrayExpression.length - 1]); //letztes zeichen der expression als String
        }

        //wenn buchstaben außer E in der Expression sind
        if(newValue.matches(".*[A-DF-Za-z].*")){
            return false;
        }

        //wenn newValue mit division, multiplikation, komma oder geschlossener klammer beginnt
        if(newValue.matches("[×÷/).,]") && expression.isBlank()){
            return false;
        }

        //wenn . oder , in der expression ist
        if(newValue.matches("[.,]")){
            //überprüfen ob das letzte elemnt in der expression eine zahl ist, wenn nicht fasle returnen
            if (!lastExpression.matches("[0-9]")) {
                return false;
            }
            //expression von hinten durchgehen und prüfen ob es noch ein komma gibt
            for(int i = arrayExpression.length-1; i >= 0; i--){
                String tmp =  String.valueOf(arrayExpression[i]);
                //false returnen wenn ein ., in der Zahl ist
                if(tmp.matches("[.,]")){
                    return false;
                }
                //true returnen wenn die zahl endet
                if(tmp.matches("[×÷+\\-/()]")){
                    return true;
                }
            }
            return true;
        }

        //wenn newValue ein rechenoperator ist und wenn letztes element auch ein rechenoperator ist
        if(newValue.matches("[×÷+\\-/]") && lastExpression.matches("[×÷+\\-/]")){
                return false;
        }

        if((newValue.equals("(") && lastExpression.equals("(")) ||
                (newValue.equals(")") && lastExpression.equals(")"))){
            return false;
        }


        //nach ( darf kein .,/* sein
        if(!lastExpression.isBlank() && lastExpression.equals("(") && newValue.matches("[×÷/.,]")){
            return false;
        }
        //nach ) darf kein ., sein
        if(!lastExpression.isBlank() && lastExpression.equals(")") && newValue.matches("[.,]")){
            return false;
        }
        return true;
    }

    private void updateLabel() {
        if (expression.contains("Error") || expression.contains("Infinity")) {
            lExcpression.getStyleClass().add("font-red");
        } else {
            lExcpression.getStyleClass().remove("font-red");
        }
        lExcpression.setText(expression);

        //System.out.println(lExcpression.getText());
    }

    private void appendToExpression(String value) {
        if(expression.contains("Error") || expression.contains("Infinity")) {
            expression = "";
            lExcpression.getStyleClass().remove("font-red");
        }
        if(isValid(value)){
            expression = expression + value;
            updateLabel();
        }
    }


    private GridPane createButtons() {
        String[] buttonsText = {"7", "4", "1", "=", "del", "8", "5", "2", "0", "()", "9", "6", "3", ".", "top", "÷", "×", "-", "+", "t", "AC", "paste", "History"};
        gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        Set<String> specials = Set.of("t", "()", "AC", "del", "top", "paste", "History");
        int row = 0;
        int col = 0;
        int maxRows = 5; //wie viele zeilen es geben soll
        //buttons eventlistener und css classen hinzufügen
        for (int i = 0; i < buttonsText.length; i++) {
            Button button = new Button(buttonsText[i]);
            //wenn special character
            if (specials.contains(buttonsText[i])) {
                button.getStyleClass().addAll("font-white", "button-operation", "button-form");// css class for special buttons
                //top button
                if (buttonsText[i].equals("t")) {
                    button.setOnAction(event -> {
                        if (darkmode) {
                            // Remove dark classes
                            lExcpression.getStyleClass().removeAll("background-black", "color-white");
                            gp.getStyleClass().removeAll("background-black");
                            //layout.getStyleClass().removeAll("background-black");
                            scrolPane.getStyleClass().removeAll("background-black");

                            // Add light classes
                            lExcpression.getStyleClass().addAll("background-white", "color-black");
                            gp.getStyleClass().add("background-white");
                            //layout.getStyleClass().add("background-white");
                            scrolPane.getStyleClass().add("background-white");

                            darkmode = false;

                        } else {
                            // Remove light classes
                            lExcpression.getStyleClass().removeAll("background-white", "color-black");
                            gp.getStyleClass().removeAll("background-white");
                            //layout.getStyleClass().removeAll("background-white");
                            scrolPane.getStyleClass().removeAll("background-white");

                            // Add dark classes
                            lExcpression.getStyleClass().addAll("background-black", "color-white");
                            gp.getStyleClass().add("background-black");
                            //layout.getStyleClass().add("background-black");
                            scrolPane.getStyleClass().add("background-black");

                            darkmode = true;

                        }
                    });
                }

                //paste button
                if (buttonsText[i].equals("paste")) {
                    button.setOnAction(event -> {
                        pasteFromClipboard();
                    });
                }

                //history
                if(buttonsText[i].equals("History")){
                    button.setOnAction(event -> {
                        showHistory();
                    });
                }

                //del button
                if (buttonsText[i].equals("del")) {
                    button.setOnAction(event -> {
                        if (lExcpression.getText().contains("Error")) {
                            expression = "";
                            updateLabel();
                        } else {

                            char[] arr = lExcpression.getText().toCharArray();
                            expression = "";
                            for (int j = 0; j < arr.length - 1; j++) {
                                expression += arr[j];
                            }
                            updateLabel();
                        }
                    });
                }

                //() button
                if (buttonsText[i].equals("()")) {
                    button.setOnAction(event -> {
                        long openCount = expression.chars().filter(c -> c == '(').count();
                        long closeCount = expression.chars().filter(c -> c == ')').count();

                        if (openCount > closeCount) {
                            appendToExpression(")");
                        } else {
                            appendToExpression("(");
                        }
                    });
                }

                //AC Button
                if (buttonsText[i].equals("AC")) {
                    button.setOnAction(event -> {
                        expression = "";
                        updateLabel();
                    });
                }

                if (buttonsText[i].equals("top")) {
                    button.getStyleClass().addAll("button-operation", "button-form", "font-white", "background-red");
                    button.setOnAction(event -> {
                        if (stage.isAlwaysOnTop()) {
                            stage.setAlwaysOnTop(false);
                            button.getStyleClass().remove("background-green");
                            button.getStyleClass().add("background-red");
                        } else {
                            stage.setAlwaysOnTop(true);
                            button.getStyleClass().remove("background-red");
                            button.getStyleClass().add("background-green");
                        }
                    });
                }
            } else {
                if (buttonsText[i].equals("=")) {
                    button.getStyleClass().addAll("font-white", "button-calc", "button-form");
                    button.setOnAction(event -> {
                        RechnerControler rc = new RechnerControler();

                        String tmpExpr = expression;
                        expression = rc.getCalculation(tmpExpr);
                        addToHistrory(tmpExpr + " = " + expression);
                        updateLabel();
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(lExcpression.getText());
                        clipboard.setContent(content);
                    });
                } else {
                    if (buttonsText[i].matches("[×÷+\\-,.]")) {
                        button.getStyleClass().addAll("font-white", "button-operation", "button-form");
                    } else {
                        button.getStyleClass().addAll("font-white", "button-normal", "button-form");
                    }
                    button.setOnAction(event -> {
                        appendToExpression(button.getText());
                    });
                }
            }

            gp.add(button, col, row);
            row++;
            if (row >= maxRows) {
                col++;
                row = 0;
            }

        }
        gp.getStyleClass().add("background-black");
        return gp;
    }

    private void addKeyboardShortCuts() {
        scene.setOnKeyPressed(event -> {
            String key = event.getText();
            switch (key) {
                case "*" -> appendToExpression("×");
                case "/" -> appendToExpression("÷");
                case "(" -> appenBrackets();
                case ")" -> appenBrackets();
                case "." -> appendToExpression(".");
                case "," -> appendToExpression(".");

            }

            if(!event.isShiftDown() && (key.equals("7") || key.equals("8") || key.equals("9") || key.equals("+") )) {
                appendToExpression(key);
            }

            if (event.isShiftDown() && event.isAltDown() && event.getCode() == KeyCode.UP) {

                for (Node node : gp.getChildren()) {
                    if (node instanceof Button button && "top".equals(button.getText())) {
                        if (stage.isAlwaysOnTop()) {
                            stage.setAlwaysOnTop(false);
                            button.getStyleClass().remove("background-green");
                            button.getStyleClass().add("background-red");
                        } else {
                            stage.setAlwaysOnTop(true);
                            button.getStyleClass().remove("background-red");
                            button.getStyleClass().add("background-green");
                        }
                    }
                }
                return; // verhindert dass unten der switch greift
            }
            if (event.isShiftDown() && event.getCode() == KeyCode.DIGIT7) {
                appendToExpression("÷");
                return;
            }
            switch (event.getCode()) {
                case DIGIT0, NUMPAD0 -> appendToExpression("0");
                case DIGIT1, NUMPAD1 -> appendToExpression("1");
                case DIGIT2, NUMPAD2 -> appendToExpression("2");
                case DIGIT3, NUMPAD3 -> appendToExpression("3");
                case DIGIT4, NUMPAD4 -> appendToExpression("4");
                case DIGIT5, NUMPAD5 -> appendToExpression("5");
                case DIGIT6, NUMPAD6 -> appendToExpression("6");
                case NUMPAD7 -> appendToExpression("7");
                case  NUMPAD8 -> appendToExpression("8");
                case  NUMPAD9 -> appendToExpression("9");
                case V -> pasteFromClipboard();
                case PLUS -> appendToExpression("+");
                case MINUS -> appendToExpression("-");
                case MULTIPLY -> appendToExpression("*");
                case DIVIDE -> appendToExpression("/");
                case ENTER, EQUALS -> {
                    RechnerControler rc = new RechnerControler();
                    String tmpExpr = expression;
                    expression = rc.getCalculation(tmpExpr);
                    addToHistrory(tmpExpr + " = " + expression);
                    updateLabel();
                }
                case BACK_SPACE -> {
                    if (lExcpression.getText().contains("Error")) {
                        expression = "";
                        updateLabel();
                    } else {

                        char[] arr = lExcpression.getText().toCharArray();
                        expression = "";
                        for (int j = 0; j < arr.length - 1; j++) {
                            expression += arr[j];
                        }
                        updateLabel();
                    }
                }
                case T -> {
                    if (darkmode) {
                        // Remove dark classes
                        lExcpression.getStyleClass().removeAll("background-black", "color-white");
                        gp.getStyleClass().removeAll("background-black");
                        //layout.getStyleClass().removeAll("background-black");
                        scrolPane.getStyleClass().removeAll("background-black");

                        // Add light classes
                        lExcpression.getStyleClass().addAll("background-white", "color-black");
                        gp.getStyleClass().add("background-white");
                        //layout.getStyleClass().add("background-white");
                        scrolPane.getStyleClass().add("background-white");

                        darkmode = false;

                    } else {
                        // Remove light classes
                        lExcpression.getStyleClass().removeAll("background-white", "color-black");
                        gp.getStyleClass().removeAll("background-white");
                        //layout.getStyleClass().removeAll("background-white");
                        scrolPane.getStyleClass().removeAll("background-white");

                        // Add dark classes
                        lExcpression.getStyleClass().addAll("background-black", "color-white");
                        gp.getStyleClass().add("background-black");
                        //layout.getStyleClass().add("background-black");
                        scrolPane.getStyleClass().add("background-black");

                        darkmode = true;

                    }
                }
                case DELETE -> {
                    expression = "";
                    updateLabel();
                }
            }
        });

    }

    private void appenBrackets() {
        long openCount = expression.chars().filter(c -> c == '(').count();
        long closeCount = expression.chars().filter(c -> c == ')').count();

        if (openCount > closeCount) {
            appendToExpression(")");
        } else {
            appendToExpression("(");
        }
    }

    private void pasteFromClipboard() {
        try {
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                // Text auslesen
                String text = (String) clipboard.getData(DataFlavor.stringFlavor);
                if (!isStringValid(text)) {
                    expression = "Error: can't paste text because it contains letters illegal characters";
                    updateLabel();
                    return;
                } else {
                    appendToExpression(text);
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isStringValid(String text) {
        if (text.matches(".*[a-zA-Z].*")) {
            return false;
        }
        if (text.contains("[×÷+\\-,.]")) {
            if (expression.isBlank()) {
                return false;
            }
            String lastChar = String.valueOf(expression.charAt(expression.length() - 1));
            if (lastChar.matches("[×÷+\\-,.]")) {
                return false; // does not add a rechenoperator if the last character is a rechenoperator
            }
        }
        return true;
    }

    private void showHistory(){
        historyStage = new Stage();
        historyStage.setTitle("History");
        VBox container = new  VBox();
        for (String tmp : history){
            Label tmpExpr = new Label(tmp);
            Button tmpBtn = new Button("Copy");
            Button copyResult = new  Button("Copy Result");
            tmpBtn.setOnAction(e -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(tmp);
                clipboard.setContent(content);
            });
            copyResult.setOnAction(e ->{
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                String[] put = tmp.split("=");
                content.putString(put[1].trim());
                clipboard.setContent(content);
            });
            HBox hBox = new HBox(20, tmpExpr, tmpBtn, copyResult);
            container.getChildren().add(hBox);
        }
        Button close = new  Button("Close");
        close.setOnAction(e -> {
            historyStage.close();
        });
        container.getChildren().add(close);
        historyScene = new Scene(container);
        historyStage.setScene(historyScene);
        //historyStage.setResizable(false);
        historyStage.setMinHeight(301);
        historyStage.setMinWidth(215);
        historyStage.show();
    }
}
