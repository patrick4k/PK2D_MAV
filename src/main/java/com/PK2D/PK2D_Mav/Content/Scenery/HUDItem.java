package com.PK2D.PK2D_Mav.Content.Scenery;

import com.PK2D.PK2D_Mav.ExecutableEvent.ExecutableEvent;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.World.World;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;


public abstract class HUDItem {
    private final World world;
    private List<Node> displays = new ArrayList<>();
    protected List<ContentPosition> positions = new ArrayList<>();
    private final ExecutableEvent refreshEvent = new ExecutableEvent();

    public HUDItem(World world) {
        this.world = world;
        this.initiateDisplays(this.displays);
        this.world.getFrontHUD().getChildren().addAll(this.displays);
        for (Node display: this.displays) {
            this.positions.add(new ContentPosition(display));
        }
    }

    protected abstract void initiateDisplays(List<Node> displays);

    public void setRefreshTrigger(ExecutableEvent event) {
        event.addListener(this.refreshEvent::fireEvent);
    }

    public void setRefreshSequence(GameEvent event) {
        this.refreshEvent.setListener(event);
    }

    public World getWorld() {
        return world;
    }

    public void setPosition(Position position) {
        for (ContentPosition contentPosition: this.positions) {
            contentPosition.set(position);
        }
    }
}
