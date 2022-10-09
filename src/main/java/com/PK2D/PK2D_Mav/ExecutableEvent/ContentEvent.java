package com.PK2D.PK2D_Mav.ExecutableEvent;

import com.PK2D.PK2D_Mav.World.WorldContent;

public interface ContentEvent {
    void onEventTriggered(WorldContent content);
}
