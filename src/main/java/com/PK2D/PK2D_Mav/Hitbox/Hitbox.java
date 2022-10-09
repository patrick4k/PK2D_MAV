package com.PK2D.PK2D_Mav.Hitbox;

import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;

import java.io.Serializable;
import java.util.Objects;

public abstract class Hitbox implements Serializable {
    protected WorldContent content;
    protected Node hitboxDisplay;
    protected ContentPosition displayPosition;
    private boolean isShowing = false;

    public Hitbox() {
    }

    public Hitbox(WorldContent content) {
        this.content = content;
    }

    public boolean doesContain(Hitbox hitbox) {
        if (hitbox instanceof CircleHitbox) {
            return doesContainCirc((CircleHitbox) hitbox);
        }
        else if (hitbox instanceof RectHitbox) {
            return doesContainRect((RectHitbox) hitbox);
        }
        else if (hitbox instanceof LineHitbox) {
            return doesContainLine((LineHitbox) hitbox);
        }
        Util.log("If " + this.getWorldContent().getContentCode() + " does contain " +
                hitbox.getWorldContent().getContentCode() + " not calculated, returned false");
        return false;
    }
    protected abstract boolean doesContainCirc(CircleHitbox hitbox);
    protected abstract boolean doesContainRect(RectHitbox hitbox);
    protected abstract boolean doesContainLine(LineHitbox hitbox);
    public abstract boolean doesContainPoint(Position point);


    public Vector getNormalVector(Hitbox hitbox) {
        if (hitbox instanceof CircleHitbox) {
            return getNormalVecCirc((CircleHitbox) hitbox);
        }
        else if (hitbox instanceof RectHitbox) {
            return getNormalVecRect((RectHitbox) hitbox);
        }
        else if (hitbox instanceof LineHitbox) {
            return getNormalVecLine((LineHitbox) hitbox);
        }
        Util.log("Normal Vector from " + this.getWorldContent().getContentCode() + " to " +
                hitbox.getWorldContent().getContentCode() + " not calculated, returned <0,0>");
        return new Vector();
    }
    protected abstract Vector getNormalVecCirc(CircleHitbox hitbox);
    protected abstract Vector getNormalVecRect(RectHitbox hitbox);
    protected abstract Vector getNormalVecLine(LineHitbox hitbox);


    public double distanceBetween(Hitbox hitbox) {
        // TODO needs work, calculations not final
        if (hitbox instanceof CircleHitbox) {
            return distanceBetweenCirc((CircleHitbox) hitbox);
        }
        else if (hitbox instanceof RectHitbox) {
            return distanceBetweenRect((RectHitbox) hitbox);
        }
        else if (hitbox instanceof LineHitbox) {
            return distanceBetweenLine((LineHitbox) hitbox);
        }
        Util.log("Distance between " + this.getWorldContent().getContentCode() + " and " +
                hitbox.getWorldContent().getContentCode() + " not calculated, returned 0");
        return 0;
    }
    protected abstract double distanceBetweenCirc(CircleHitbox hitbox);
    protected abstract double distanceBetweenRect(RectHitbox hitbox);
    protected abstract double distanceBetweenLine(LineHitbox hitbox);


    public WorldContent getWorldContent() {
        return content;
    }

    public ContentPosition getPosition() {
        return this.content.getPosition();
    }

    protected abstract Node generateHitboxDisplay();

    private void setHitboxDisplay() {
        this.hitboxDisplay = this.generateHitboxDisplay();
        this.displayPosition = new ContentPosition(this.hitboxDisplay);
    }

    public void showHitbox() {
        if (Objects.isNull(this.hitboxDisplay)) this.setHitboxDisplay();
        this.displayPosition.set(content.getPosition());
        if (!this.content.getWorld().getWorldPane().getChildren().contains(this.hitboxDisplay)) {
            this.content.getWorld().getWorldPane().getChildren().add(this.hitboxDisplay);
            this.isShowing = true;
        }
    }

    public void hideHitbox() {
        this.content.getWorld().getWorldPane().getChildren().remove(this.hitboxDisplay);
        this.isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }
    public void refreshNode() {
        if (this.isShowing) {
            this.displayPosition.set(this.content.getPosition());
            this.hitboxDisplay.toFront();
        }
    }

    public Node getHitboxDisplay() {
        return hitboxDisplay;
    }
}
