package com.PK2D.PK2D_Mav.Time;

import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEventBool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RepeatableAction implements Serializable {
    private boolean isPlaying = false;
    private GameEvent onStart, onAction, onInterrupt, onFinish;
    private GameEventBool startBool;
    private double duration, cooldownDuration;
    private int maxRepeats;
    private final List<Timer> timers = new ArrayList<>();

    public RepeatableAction(double duration, int maxRepeats) {
        this.duration = duration;
        this.maxRepeats = maxRepeats;
        InitTimers();
    }

    private void InitTimers() {
        timers.clear();
        for (int i = 0; i < maxRepeats; i++) {
            timers.add(new Timer(i*this.duration).setOnFinished(this::actionEventTrigger));
        }
        timers.add(new Timer(maxRepeats*this.duration).setOnFinished(this::finishEventTrigger));
    }

    public RepeatableAction setStartConditional(GameEventBool startBool) {
        this.startBool = startBool;
        return this;
    }

    public RepeatableAction setOnStart(GameEvent onStart) {
        this.onStart = onStart;
        return this;
    }

    public RepeatableAction setOnAction(GameEvent onAction) {
        this.onAction = onAction;
        return this;
    }

    public RepeatableAction setOnInterrupt(GameEvent onInterrupt) {
        this.onInterrupt = onInterrupt;
        return this;
    }

    public RepeatableAction setOnFinished(GameEvent onFinished) {
        this.onFinish = onFinished;
        return this;
    }

    public void setCooldownDuration(double cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public void tryStart() {
        if (this.isPlaying) return;
        this.forceStart();
    }

    public void forceStart() {
        if (!this.getStartConditional()) return;
        if (this.isPlaying) this.stop();
        else this.isPlaying = true;
        this.startEventTrigger();
        for (Timer timer: this.timers) {
            timer.start();
        }
    }

    public void stop() {
        for (Timer timer: this.timers) {
            timer.stop();
        }
        Timer.staticStart(this.cooldownDuration, () -> this.isPlaying = false);
        this.interruptEventTrigger();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private boolean getStartConditional() {
        if (Objects.nonNull(this.startBool)) return (startBool.onEventTriggered());
        else return true;
    }

    private void startEventTrigger() {
        if (Objects.nonNull(onStart)) onStart.onEventTriggered();
    }

    private void actionEventTrigger() {
        if (Objects.nonNull(onAction)) onAction.onEventTriggered();
    }

    private void interruptEventTrigger() {
        if (Objects.nonNull(this.onInterrupt)) this.onInterrupt.onEventTriggered();
    }

    private void finishEventTrigger() {
        Timer.staticStart(this.cooldownDuration, () -> this.isPlaying = false);
        if (Objects.nonNull(onFinish)) this.onFinish.onEventTriggered();
    }

    public void setDuration(double duration) {
        this.duration = duration;
        this.InitTimers();
    }

    public void setMaxRepeats(int maxRepeats) {
        this.maxRepeats = maxRepeats;
        this.InitTimers();
    }

    public GameEvent getOnStart() {
        return onStart;
    }

    public GameEvent getOnAction() {
        return onAction;
    }

    public GameEvent getOnFinish() {
        return onFinish;
    }
}
