package com.PK2D.PK2D_Mav.Input;

import javafx.scene.input.KeyCode;

import java.util.Arrays;
import java.util.List;

public class Keybinds {
    private static List<KeyCode> keyBind(KeyCode... keyCodes) {
        return Arrays.asList(keyCodes);
    }
    public static KeyCode cmdCtrl() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return KeyCode.CONTROL;
        }
        else return KeyCode.COMMAND;
    }
    public static List<KeyCode> RESET_WORLD_POS = keyBind(KeyCode.SHIFT, cmdCtrl(), KeyCode.R);
    public static List<KeyCode> KILL_ALL_IN_WORLD = keyBind(KeyCode.SHIFT, cmdCtrl(), KeyCode.X);
}
