package com.PK2D.PK2D_Mav.Textures;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class Display {
    private Position position;
    private final List<Node> displays = new ArrayList<>();
    private final List<Vector> offsets = new ArrayList<>();

    public void addWithOffset(Node node, Vector r) {
        this.position.moveAsNewPos(r.getX(), r.getY()).setNodeToPos(node);
        this.displays.add(node);
        this.offsets.add(r);
    }

    public void add(Node... nodes) {
        for (Node node: nodes) {
            this.position.setNodeToPos(node);
        }
        this.displays.addAll(List.of(nodes));
        this.offsets.add(new Vector());
    }

    public void set(Node... nodes) {
        this.displays.clear();
        this.add(nodes);
    }

    public List<Node> getDisplays() {
        return displays;
    }

    public void moveDisplay(Vector r) {
        for (Node node: this.displays) {
            node.setLayoutX(node.getLayoutX() + r.getX());
            node.setLayoutY(node.getLayoutY() + r.getY());
        }
    }
}
