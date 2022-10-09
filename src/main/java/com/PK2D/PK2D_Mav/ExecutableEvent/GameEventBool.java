package com.PK2D.PK2D_Mav.ExecutableEvent;

public interface GameEventBool {
    boolean onEventTriggered();

    public static GameEventBool FALSE = () -> false;
    public static GameEventBool TRUE = () -> true;
}
