package com.PK2D.PK2D_Mav.Input;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Time.Timer;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Input {
    public static boolean IGNORE = false;
    public static Scene SCENE;
    public static final List<KeyCode> pressedKeys = new ArrayList<>();
    public static Position dragStartPos, dragEndPos;
    private static final Timer mouseDragTimer = new Timer(GameConstants.MIN_MOUSE_DRAG_TIME, () -> mouseDragTimerOver = true);
    private static boolean mouseDragTimerOver = false;
    public static final List<GameKeyEvent> onKeyPressedListeners = new ArrayList<>();
    public static final List<List<KeyCode>> onKeyPressedListenersKeyBinds = new ArrayList<>(new ArrayList<>());
    public static final List<GameKeyEvent> onKeyReleasedListeners = new ArrayList<>();
    public static final List<KeyCode> onKeyReleasedListenersKeyBinds = new ArrayList<>();
    public static final List<GameMouseClickEvent> onMouseClickListeners = new ArrayList<>();
    public static final List<GameMouseDragEvent> onMouseDragListeners = new ArrayList<>();


    public static boolean isKeyPressed(KeyCode keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public static void keyPressed(KeyCode keyCode) {
        if (!isKeyPressed(keyCode)) pressedKeys.add(keyCode);
    }

    public static void keyReleased(KeyCode keyCode) {
        if (isKeyPressed(keyCode)) pressedKeys.remove(keyCode);
    }

    public static void onKeyPressed(KeyEvent event) {
        if (Input.IGNORE) return;
        if (!isKeyPressed(event.getCode())) pressedKeys.add(event.getCode());
        for (int i = 0; i < onKeyPressedListeners.size(); i++) {
            int correctKeyCounter = 0;
            for (KeyCode key: onKeyPressedListenersKeyBinds.get(i)) {
                if (isKeyPressed(key)) correctKeyCounter++;
            }
            if (correctKeyCounter == onKeyPressedListenersKeyBinds.get(i).size()) {
                onKeyPressedListeners.get(i).onEventTriggered(event);
            }
        }
    }
    public static void addOnKeyPressed(KeyCode keyBind, GameKeyEvent listener) {
        if (Input.IGNORE) return;
        onKeyPressedListeners.add(listener);
        onKeyPressedListenersKeyBinds.add(Collections.singletonList(keyBind));
    }
    public static void addOnKeyPressed(List<KeyCode> keyBind, GameKeyEvent listener) {
        if (Input.IGNORE) return;

        onKeyPressedListeners.add(listener);
        onKeyPressedListenersKeyBinds.add(keyBind);
    }

    public static void onKeyReleased(KeyEvent event) {
        if (Input.IGNORE) return;
        if (isKeyPressed(event.getCode())) pressedKeys.remove(event.getCode());
        for (int i = 0; i < onKeyReleasedListeners.size(); i++) {
            if (onKeyReleasedListenersKeyBinds.get(i) == event.getCode()) {
                onKeyReleasedListeners.get(i).onEventTriggered(event);
            }
        }
    }
    public static void addOnKeyReleased(KeyCode keyBind, GameKeyEvent listener) {
        onKeyReleasedListeners.add(listener);
        onKeyReleasedListenersKeyBinds.add(keyBind);
    }

    public static void onMouseDragEntered(MouseEvent event) {
        if (Input.IGNORE) return;
        dragStartPos = Position.mouseEventToScreenPos(event);
        mouseDragTimerOver = false;
        mouseDragTimer.restart();
    }

    public static void onMouseDragReleased(MouseEvent event) {
        if (Input.IGNORE) return;
        dragEndPos = Position.mouseEventToScreenPos(event);
        if ((dragStartPos.distanceFrom(dragEndPos) < GameConstants.MIN_MOUSE_DRAG_DISTANCE) ||
                (mouseDragTimerOver)){
            onMouseClick(event);
        }
        else {
            onMouseDragEnded(event);
        }
    }

    public static void onMouseClick(MouseEvent event) {
        if (Input.IGNORE) return;
        for (GameMouseClickEvent listener: onMouseClickListeners) {
            listener.onEventTriggered(event);
        }
    }

    public static void addOnMouseClick(GameMouseClickEvent listener) {
        onMouseClickListeners.add(listener);
    }

    public static void onMouseDragEnded(MouseEvent event) {
        if (Input.IGNORE) return;
        for (GameMouseDragEvent listener: onMouseDragListeners) {
            listener.onEventTriggered(event, dragStartPos, dragEndPos);
        }
        dragStartPos.clear();
        dragEndPos.clear();
    }

    public static void addOnMouseDrag(GameMouseDragEvent listener) {
        onMouseDragListeners.add(listener);
    }



    public static List<KeyCode> keyBind(KeyCode... keyCodes) {
        return Arrays.asList(keyCodes);
    }

}
