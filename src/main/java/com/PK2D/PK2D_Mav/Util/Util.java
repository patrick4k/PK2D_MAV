package com.PK2D.PK2D_Mav.Util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

public class Util {
    public static Stage primaryStage;
    public static void TERMINATE() {
        primaryStage.close();
    }
    public static Circle generateCircle(double Radius, Color color) {
        Circle circle = new Circle();
        circle.setRadius(Radius* GameConstants.WORLD_PER_PIXEL_RATIO);
        circle.setFill(color);
        return circle;
    }
    public static int timeToCycleCount(double timeSec) {
        return (int) (Math.round(timeSec*GameConstants.TICK_PER_SECONDS));
    }
    private static final ArrayList<String> previousLogList = new ArrayList<>();
    private static final ArrayList<String> logList = new ArrayList<>();
    public static double LOG_TIME = 0;
    public static int RUNTIME_MAX_COUNT = 100;
    public static void log(String message) {
        System.out.println(getLogTime() + message);
        logList.add("[Log Time: " + LOG_TIME + "]: " + message);
    }
    public static void log(double num) {
        System.out.println(getLogTime() + num);
        logList.add("[Log Time: " + LOG_TIME + "]: " + num);
    }
    public static void log(String message, boolean shouldLog) {
        if (shouldLog) log(message);
    }
    public static void log(double num, boolean shouldLog) {
        if (shouldLog) log(num);
    }
    private static String getLogTime() {
        return (LogPreference.SHOW_LOG_TIME)? "[Log Time: " + LOG_TIME + "]: ": "";
    }
    public static void startLog() {
        Util.log("Start Request");
        if (LogPreference.SHOW_LOG_TIME) {
            Timeline logTimeline = new Timeline(new KeyFrame(Duration.seconds(GameConstants.TICK_SECONDS), event -> {
                LOG_TIME += GameConstants.TICK_SECONDS;
            }));
            logTimeline.setCycleCount(-1);
            logTimeline.playFromStart();
        }
        if ((!GameConstants.SHOULD_SAVE_LOG) || GameConstants.SHOULD_OVERRIDE_LOG) return;
        try {
            Scanner scanner = new Scanner(new File("log.txt"));
            while (scanner.hasNext()) {
                previousLogList.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            log("Error in log start");
        }

    }
    public static void returnLog() {
        Util.log("Close Request");
        if (!GameConstants.SHOULD_SAVE_LOG) return;
        try {
            FileWriter fileWriter = new FileWriter("log.txt");
            for (String txt: previousLogList) {
                fileWriter.write(txt + "\n");
            }
            for (String txt: logList) {
                fileWriter.write(txt + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            log("Error in log return");
        }
    }
    public static final String SYS_PATH_TO_SRC = new File("").getAbsolutePath() + "/src/";
    public static String realtivePath(String path1, String path2) {
        // Two absolute paths
        File absolutePath1 = new File(path1);
        System.out.println("Absolute Path1: " + absolutePath1);
        File absolutePath2 = new File(path2);
        System.out.println("Absolute Path2: " + absolutePath2);

        // convert the absolute path to URI
        URI uri1 = absolutePath1.toURI();
        URI uri2 = absolutePath2.toURI();

        // create a relative path from the two paths
        URI relativePath = uri2.relativize(uri1);

        // convert the URI to string
        String path = relativePath.getPath();

        System.out.println("Relative Path: " + path);
        return path;
    }

    public static int timeToTicks(double time) {
        return ((int) Math.round(GameConstants.TICK_PER_SECONDS * time));
    }
    public static double tickToTime(int tick) {
        return tick*GameConstants.TICK_SECONDS;
    }

    public static Object[] toArray(Object... objs) {
        return objs;
    }
}
