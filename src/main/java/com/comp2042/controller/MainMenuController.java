package com.comp2042.controller;

import com.comp2042.Main;
import com.comp2042.model.Game;
import com.comp2042.model.GameSession;
import com.comp2042.model.RivalLevel;
import com.comp2042.model.RivalMatch;
import com.comp2042.view.GameView;
import com.comp2042.view.RivalView;
import com.comp2042.view.ViewManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController {

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;

    @FXML
    private ComboBox<String> difficultyBox;
    @FXML
    private ComboBox<String> levelBox;

    private Scene gameScene;

    private ViewManager viewManager = Main.getViewManager();

    @FXML
    private void initialize() {
        difficultyBox.getItems().setAll("Easy", "Normal", "Hard");
        difficultyBox.getSelectionModel().select("Normal");
        levelBox.getItems().setAll("Classic", "Rival 1", "Rival 2");
        levelBox.getSelectionModel().select("Classic");
        levelBox.valueProperty().addListener((observable, oldValue, newValue) ->
                difficultyBox.setDisable(!"Classic".equals(newValue)));
    }

    public void handleStart() {
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        if ("Classic".equals(levelBox.getValue())) {
            launchSnake(gameStage);
        } else {
            launchRival(gameStage, levelForChoice(levelBox.getValue()));
        }
        gameStage.setScene(gameScene);
        viewManager.getPrimaryStage().close();
        gameStage.show();
    }

    private void launchSnake(Stage gameStage) {
        Game game = new Game(X_VALUE, Y_VALUE, POWER_UP, difficultyCode(difficultyBox.getValue()));
        GameSession session = new GameSession(game);

        GameView gameView = new GameView(game);
        GameController controller = new GameController(session, gameView, viewManager);

        StackPane gamePane = new StackPane();
        gameScene = new Scene(gamePane, game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier());

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(gameScene, session.snake(), session.coin());
        gamePane.getChildren().addAll(gameView.gridCanvas, gameView.gameOverLabel, gameView.gameWonLabel, gameView.scoreLabel);
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(gameView.gameOverLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.gameWonLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.scoreLabel, Pos.BOTTOM_CENTER);
    }

    private void launchRival(Stage gameStage, RivalLevel level) {
        RivalMatch match = new RivalMatch(level);
        RivalView rivalView = new RivalView(match);
        gameScene = rivalView.createScene();
        RivalController controller = new RivalController(match, rivalView, viewManager);
        gameScene.setOnKeyPressed(controller::handle);
        gameStage.setOnHidden(event -> controller.stop());
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    static char difficultyCode(String choice) {
        if (choice == null) {
            throw new IllegalArgumentException("Select a difficulty before starting");
        }
        return switch (choice) {
            case "Easy" -> 'E';
            case "Normal" -> 'N';
            case "Hard" -> 'H';
            default -> throw new IllegalArgumentException("Unsupported difficulty: " + choice);
        };
    }

    static RivalLevel levelForChoice(String choice) {
        return switch (choice) {
            case "Rival 1" -> RivalLevel.MEADOW;
            case "Rival 2" -> RivalLevel.CROSSROADS;
            default -> throw new IllegalArgumentException("Unsupported rival level: " + choice);
        };
    }

    public static int getxValue() {
        return X_VALUE;
    }

    public static int getyValue() {
        return Y_VALUE;
    }
}
