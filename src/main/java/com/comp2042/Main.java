package com.comp2042;

import com.comp2042.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and exposes the view manager used by the FXML menu controller.
 */
public class Main extends Application {
    /** Creates the JavaFX application entry point. */
    public Main() {
    }

    private static ViewManager viewManager;

    /**
     * Initializes the menu when JavaFX starts the application.
     *
     * @param primaryStage the JavaFX stage, unused because ViewManager creates the menu window
     */
    @Override
    public void start(Stage primaryStage) {
        viewManager = new ViewManager();
        viewManager.initalizeMainMenu();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Provides the manager initialized during application startup.
     *
     * @return the current view manager, or null before startup
     */
    public static ViewManager getViewManager() {
        return viewManager;
    }
}
