package com.PK2D.PK2D_Mav.Time;

import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEventBool;
import com.PK2D.PK2D_Mav.Util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Timer implements Serializable {
    private int originalTickDuration;
    private int tickDuration;
    private GameEventBool eventConditional;
    private final List<GameEvent> listeners = new ArrayList<>();
    private final List<Timer> children = new ArrayList<>();
    private Timer parent = null;
    private boolean isStopped = false;

    public Timer(double duration) {
        this(Util.timeToTicks(duration));
    }

    private Timer(int tickDuration) {
        this.originalTickDuration = tickDuration;
        this.tickDuration = this.originalTickDuration;
    }

    public Timer(double duration, GameEvent listener) {
        this(duration);
        this.listeners.add(listener);
    }

    public Timer addOnFinished(GameEvent listener) {
        this.listeners.add(listener);
        return this;
    }

    public Timer setOnFinished(GameEvent listener) {
        this.listeners.clear();
        this.listeners.add(listener);
        return this;
    }

    public Timer setEventConditional(GameEventBool eventConditional) {
        this.eventConditional = eventConditional;
        return this;
    }

    public void start() {
        timersAddedInTick.add(this);
    }

    public void startNew() {
        this.copy().start();
    }

    public void stop() {
        removeQueue.add(this);
    }

    public void stopAll() {
        this.stop();
        for (Timer timer: this.children) {
            timer.stop();
        }
    }

    public void stopChildren() {
        for (Timer timer: this.children) {
            timer.isStopped = true;
        }
        removeQueue.addAll(this.children);
        this.children.clear();
    }

    private void ding() {
        if (this.shouldDoEvent()) {
            for (GameEvent listener: this.listeners) {
                listener.onEventTriggered();
            }
        }
        removeQueue.add(this);
    }

    private void fire() {
        if (this.tickDuration > 0) {
            this.tickDuration -= 1;
        }
        else {
            this.ding();
        }
    }

    private void setParent(Timer timer) {
        this.parent = timer;
        timer.children.add(this);
    }

    private boolean shouldDoEvent() {
        if (removeQueue.contains(this)) return false;
        else if (Objects.nonNull(this.eventConditional)) return this.eventConditional.onEventTriggered();
        return true;
    }

    private void resetTick() {
        this.tickDuration = this.originalTickDuration;
    }

    public void restart() {
        this.stop();
        this.resetTick();
        this.start();
    }

    private Timer copy() {
        Timer timer = new Timer(this.tickDuration);
        for (GameEvent listener: this.listeners) {
            timer.addOnFinished(listener);
        }
        timer.setParent(this);
        return timer;
    }

    public void setTimeLeft(double duration) {
        this.tickDuration = Util.timeToTicks(duration);
    }

    public int getTickDuration() {
        return tickDuration;
    }

    private static final List<Timer> timersAddedInTick = new ArrayList<>();
    private static final List<Timer> activeTimers = new ArrayList<>();
    private static final List<Timer> removeQueue = new ArrayList<>();

    public static void staticStart(double duration, GameEvent listener) {
        Timer timer = new Timer(duration);
        timer.setOnFinished(listener);
        timer.start();
    }

    public static void checkTimers() {
        activeTimers.addAll(timersAddedInTick);
        timersAddedInTick.clear();
        for (Timer timer: activeTimers) {
            timer.fire();
        }
        activeTimers.removeIf(removeQueue::contains);
        for (Timer timer: removeQueue) {
            timer.resetTick();
        }
        removeQueue.clear();
    }
}
