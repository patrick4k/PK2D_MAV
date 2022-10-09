package com.PK2D.PK2D_Mav.Input;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import javafx.scene.input.MouseEvent;

public interface GameMouseDragEvent {
    void onEventTriggered(MouseEvent event, Position dragStartPos, Position dragEndPos);
}
