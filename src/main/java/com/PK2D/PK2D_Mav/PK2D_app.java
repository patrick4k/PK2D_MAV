package com.PK2D.PK2D_Mav;

import com.PK2D.Mycelium.MyceliumWorld;
import com.PK2D.Mycelium.MyceliumWorldBuilder;
import com.PK2D.PK2D_Mav.Input.Input;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.World;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PK2D_app extends Application {
    /*
     * TODO
     *  Show class name selected when in worldBuilder
     *  Come up with better solution to world content code
     *  Rework hitbox distance between methods
     *  Add rotatable rectangle hitbox
     *  Rename LogPreferences to Log / UtilLog / SystemLog and move log commands to log class
     * **/

    private static Class<? extends World> worldClass = World.class;
    private final BorderPane borderPane;
    private final Pane mainPane, backgroundHUDPane, frontHUDPane;
    private World world;

    public PK2D_app() {
        Util.startLog();
        borderPane = new BorderPane();
        mainPane = new Pane();
        backgroundHUDPane = new Pane();
        frontHUDPane = new Pane();

        for (Constructor<?> constructor:worldClass.getConstructors()) {
            World newWorld = null;
            try {
                newWorld = ((World) constructor.newInstance(mainPane, backgroundHUDPane, frontHUDPane));
            } catch (IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Util.log("World constructor mismatched", LogPreference.NULL_CATCH);
            } finally {
                if
                (Objects.nonNull(newWorld)) this.world = newWorld;
            }
        }
        this.world.startWorld();
    }

    public static void launchWorld(Class<? extends World> worldClass) {
        PK2D_app.worldClass = worldClass;
        PK2D_app.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(borderPane, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        scene.setOnKeyPressed(Input::onKeyPressed);
        scene.setOnKeyReleased(Input::onKeyReleased);
        scene.setOnMousePressed(Input::onMouseDragEntered);
        scene.setOnMouseReleased(Input::onMouseDragReleased);
        Input.SCENE = scene;
        borderPane.setCenter(backgroundHUDPane);
        borderPane.getChildren().add(mainPane);
        borderPane.getChildren().add(frontHUDPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PK2D");
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(GameConstants.SHOULD_FULLSCREEN);
        primaryStage.setOnCloseRequest(event -> Util.returnLog());
        Util.primaryStage = primaryStage;
        this.world.getOnStartEvent().fireEvent();
    }
}
