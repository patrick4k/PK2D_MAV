package com.PK2D.PK2D_Mav.Phys.Position;

import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Phys.Velocity;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import javafx.scene.shape.Line;

import java.io.Serializable;
import java.util.Objects;

public class LinePosition implements Serializable {
    private Line display;
    protected Position startPos, endPos;

    public LinePosition() {
        this.startPos = new Position(0,0);
        this.endPos = new Position(0,0);
    }

    public LinePosition(Line display) {
        this.startPos = new Position(0,0);
        this.endPos = new Position(0,0);
        this.display =  display;
        displayRefresh();
    }

    public LinePosition(Line display, Position startPos, Position endPos) {
        this.display = display;
        this.startPos = startPos;
        this.endPos = endPos;
        displayRefresh();
    }

    public LinePosition(Position startPos, Position endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
    
    public void setStartPos(Position pos) {
        this.startPos = pos;
    }

    public void setStartPos(double x, double y) {
        this.startPos = new Position(x, y);
    }

    public void setEndPos(Position pos) {
        this.endPos = pos;
    }

    public void setEndPos(double x, double y) {
        this.endPos = new Position(x, y);
    }

    public Position getStartPos() {
        return startPos;
    }

    public Position getEndPos() {
        return endPos;
    }

    public void displayRefresh() {
        if (Objects.isNull(this.display)) return;
        this.display.setStartX(this.startPos.getLayoutX());
        this.display.setStartY(this.startPos.getLayoutY());
        this.display.setEndX(this.endPos.getLayoutX());
        this.display.setEndY(this.endPos.getLayoutY());
    }

    public void moveInVec(Vector vector) {
        this.startPos.addX(vector.getX());
        this.startPos.addY(vector.getY());
        this.endPos.addX(vector.getX());
        this.endPos.addY(vector.getY());
        displayRefresh();
    }

    public void moveInVelocity(Velocity velocity) {
        this.startPos.addX(GameConstants.TICK_SECONDS*velocity.getX());
        this.startPos.addY(GameConstants.TICK_SECONDS*velocity.getY());
        this.endPos.addX(GameConstants.TICK_SECONDS*velocity.getX());
        this.endPos.addY(GameConstants.TICK_SECONDS*velocity.getY());
        displayRefresh();
    }

    public Vector asVector() {
        return new Vector(this.endPos.getX()-this.startPos.getX(),this.endPos.getY()-this.startPos.getY());
    }

    public double getLength() {
        return this.startPos.distanceFrom(this.endPos);
    }
}
