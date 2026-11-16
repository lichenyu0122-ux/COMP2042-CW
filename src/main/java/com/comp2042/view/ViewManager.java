package com.comp2042.view;

import com.comp2042.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Creates the menu and game windows and switches between them.
 */
public class ViewManager {

    private Stage primaryStage = new Stage();
    private AnchorPane root;

    private Stage gameStage;

    private Image snakeLogo = new Image("resources/views/res/SnakeLogoWhite.png");

    /**
     * Creates a window manager with a menu stage.
     */
    public ViewManager() {
    }

    /**
     * Loads the FXML menu and displays its window.
     */
    public void initalizeMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/resources/views/MainMenu.fxml"));
            root = loader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Scene mainScene = new Scene(root);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(snakeLogo);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
    }

    /**
     * The menu window to show again when the player leaves a round.
     *
     * @return the menu stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Creates a new game window with the snake icon.
     */
    public void initGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Snake Game");
        gameStage.getIcons().add(snakeLogo);
        gameStage.setResizable(false);
    }

    /**
     * The current game window, which is created when a round starts.
     *
     * @return the game stage, or null before initialization
     */
    public Stage getGameStage() {
        return gameStage;
    }

    /**
     * Shows the menu window and closes the game window.
     */
    public void showMainMenu() {
        primaryStage.show();
        gameStage.close();
    }
}
