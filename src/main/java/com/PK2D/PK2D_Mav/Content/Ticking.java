package com.PK2D.PK2D_Mav.Content;

public interface Ticking {
    void tick();
    default void bigTick() {
    }
}
