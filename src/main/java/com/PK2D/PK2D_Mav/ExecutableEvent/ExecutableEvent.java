package com.PK2D.PK2D_Mav.ExecutableEvent;

import java.util.ArrayList;
import java.util.List;

public class ExecutableEvent {
    public static List<ExecutableEvent> ALL_EVENTS = new ArrayList<>();

    private final List<GameEvent> gameEventsListener = new ArrayList<>();

    public ExecutableEvent() {
        ALL_EVENTS.add(this);
    }

    public void addListener(GameEvent listener) {
        this.gameEventsListener.add(listener);
    }

    public void setListener(GameEvent listener) {
        this.clearListeners();
        this.gameEventsListener.add(listener);
    }

    public void removeListener(GameEvent removeListener) {
       this.gameEventsListener.removeIf(removeListener::equals);
    }

    public void clearListeners() {
        this.gameEventsListener.clear();
    }

    public void fireEvent() {
        for (GameEvent listener: this.gameEventsListener) {
            listener.onEventTriggered();
        }
    }

    public List<GameEvent> getGameEventsListener() {
        return gameEventsListener;
    }
}
