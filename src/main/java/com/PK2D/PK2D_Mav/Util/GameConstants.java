package com.PK2D.PK2D_Mav.Util;


import javafx.stage.Screen;

public class GameConstants {
    private static final double PANE_SCREEN_RATIO = 0.8;
    public static final boolean SHOULD_FULLSCREEN = false;
    public static final double SCREEN_WIDTH = PANE_SCREEN_RATIO*(Screen.getPrimary().getBounds().getWidth());
    public static final double SCREEN_HEIGHT = PANE_SCREEN_RATIO*(Screen.getPrimary().getBounds().getHeight());
    public static final double TICK_PER_SECONDS = 100;
    public static final double TICKS_PER_BIG_TICK = 200;
    public static final double TICK_SECONDS = 1/TICK_PER_SECONDS;
    public static final double PIXEL_PER_WORLD_RATIO = 1;
    public static final double WORLD_PER_PIXEL_RATIO = 1/PIXEL_PER_WORLD_RATIO;
    public static final double MIN_MOUSE_DRAG_DISTANCE = 2;
    public static final double MIN_MOUSE_DRAG_TIME = 2;
    public static final boolean SHOULD_SAVE_LOG = true;
    public static final boolean SHOULD_OVERRIDE_LOG = true;
    public static final boolean USES_DEBUG_CONTROL = true;
}

