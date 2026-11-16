package com.comp2042.controller;

import com.comp2042.Main;
import com.comp2042.model.Game;
import com.comp2042.model.GameSession;
import com.comp2042.model.Level;
import com.comp2042.model.LevelCatalog;
import com.comp2042.view.GameView;
import com.comp2042.view.ViewManager;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;

/**
 * Reads the selected level and difficulty from the menu and starts a game round.
 */
public class MainMenuController {
    /** Creates the controller loaded by the menu FXML. */
    public MainMenuController() {
    }

    private static final int X_VALUE = 20;
    private static final int Y_VALUE = 20;
    private static final boolean POWER_UP = true;

    @FXML
    private ComboBox<String> difficultyBox;
    @FXML
    private ComboBox<String> levelBox;
    private Map<String, Level> levels;

    private Scene gameScene;

    private ViewManager viewManager = Main.getViewManager();

    @FXML
    private void initialize() {
        difficultyBox.getItems().setAll("Easy", "Normal", "Hard");
        difficultyBox.getSelectionModel().select("Normal");
        levels = LevelCatalog.load();
        levelBox.getItems().setAll(levels.keySet());
        levelBox.getSelectionModel().selectFirst();
    }

    /**
     * Creates the selected round and displays its game window.
     */
    public void handleStart() {
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        launchSnake(gameStage);
        gameStage.setScene(gameScene);
        viewManager.getPrimaryStage().close();
        gameStage.show();
    }

    private void launchSnake(Stage gameStage) {
        Level level = levels.get(levelBox.getValue());
        if (level == null) throw new IllegalStateException("Select a level before starting");
        Game game = new Game(level.columns(), level.rows(), POWER_UP,
                difficultyCode(difficultyBox.getValue()), level.target(), level.speedMillis());
        GameSession session = new GameSession(game, level);

        GameView gameView = new GameView(game, level);
        GameController controller = new GameController(session, gameView, viewManager);

        double boardWidth = game.getWidth() * game.getMultiplier();
        double boardHeight = game.getHeight() * game.getMultiplier();
        VBox gamePane = new VBox();
        gameScene = new Scene(gamePane, boardWidth, boardHeight + 32);

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(session.snake(), session.coin(),
                session.poisonCoin(), session.obstacle());
        StackPane boardPane = new StackPane(gameView.gridCanvas, gameView.gameOverLabel,
                gameView.gameWonLabel, gameView.effectLabel);
        boardPane.prefWidthProperty().bind(gameView.gridCanvas.widthProperty());
        boardPane.prefHeightProperty().bind(gameView.gridCanvas.heightProperty());
        boardPane.maxWidthProperty().bind(gameView.gridCanvas.widthProperty());
        boardPane.maxHeightProperty().bind(gameView.gridCanvas.heightProperty());
        StackPane.setAlignment(gameView.gameOverLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(gameView.gameWonLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(gameView.effectLabel, Pos.TOP_LEFT);
        gameView.scoreLabel.prefWidthProperty().bind(gameView.gridCanvas.widthProperty());
        gamePane.setAlignment(Pos.CENTER);
        gamePane.getChildren().addAll(boardPane, gameView.scoreLabel);
    }

    /**
     * Exposes the window manager used by the menu.
     *
     * @return the current view manager
     */
    public ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * Maps the selected menu label to its difficulty code.
     *
     * @param choice the selected difficulty label
     * @return E, N, or H
     * @throws IllegalArgumentException if the choice is null or unsupported
     */
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

    /**
     * Returns the legacy default board width.
     *
     * @return the default number of columns
     */
    public static int getxValue() {
        return X_VALUE;
    }

    /**
     * Returns the legacy default board height.
     *
     * @return the default number of rows
     */
    public static int getyValue() {
        return Y_VALUE;
    }
}
