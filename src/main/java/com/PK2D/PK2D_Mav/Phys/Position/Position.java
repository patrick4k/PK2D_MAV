package com.PK2D.PK2D_Mav.Phys.Position;

import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Phys.Velocity;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import com.PK2D.PK2D_Mav.Util.GameMath;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.io.Serializable;

public class Position implements Serializable {
    protected double xWorldLocation = 0;
    protected double yWorldLocation = 0;

    public Position() {
    }

    public Position(double x, double y) {
        this.xWorldLocation = x;
        this.yWorldLocation = y;
    }

    public Position(Position pos) {
        this.xWorldLocation = pos.getX();
        this.yWorldLocation = pos.getY();
    }

    public void setPosition(double x, double y) {
        this.xWorldLocation = x;
        this.yWorldLocation = y;
    }

    public void setPosition(Position position) {
        this.setPosition(position.xWorldLocation, position.yWorldLocation);
    }

    public void addX(double dx) {
        xWorldLocation += dx;
    }

    public void addY(double dy) {
        yWorldLocation += dy;
    }

    public Position addXYandGet(double dx, double dy) {
        this.addX(dx);
        this.addY(dy);
        return this;
    }

    public void moveInVec(Vector vector) {
        this.addX(vector.getX());
        this.addY(vector.getY());
    }
    public void moveInVelocity(Velocity velocity) {
        this.addX(GameConstants.TICK_SECONDS*velocity.getX());
        this.addY(GameConstants.TICK_SECONDS*velocity.getY());
    }

    public double distanceFrom(Position pos) {
        return (Math.sqrt(Math.pow((this.getX()-pos.getX()),2) + Math.pow((this.getY()-pos.getY()),2)));
    }

    public double getX() {
        return xWorldLocation;
    }

    public double getY() {
        return yWorldLocation;
    }

    public double getLayoutX() {
        return GameConstants.WORLD_PER_PIXEL_RATIO*this.xWorldLocation;
    }

    public double getLayoutY() {
        return GameConstants.SCREEN_HEIGHT - GameConstants.WORLD_PER_PIXEL_RATIO *this.yWorldLocation;
    }

    public Position moveAsNewPos(double dx, double dy) {
        return (new Position(this)).addXYandGet(dx, dy);
    }

    public String asString() {
        return "(" + Math.round(this.xWorldLocation) + ", " + Math.round(this.yWorldLocation) + ")";
    }

    public void clear() {
        this.xWorldLocation = Position.MIDDLE_OF_SCREEN.xWorldLocation;
        this.yWorldLocation = Position.MIDDLE_OF_SCREEN.yWorldLocation;
    }

    public double distanceFromLinePos(LinePosition wallPosition) {
        double x, x1, x2, y, y1, y2;
        x = this.getX();
        y = this.getY();
        x1 = wallPosition.getStartPos().getX();
        y1 = wallPosition.getStartPos().getY();
        x2 = wallPosition.getEndPos().getX();
        y2 = wallPosition.getEndPos().getY();
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;
        double xx, yy;
        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        double dx = x - xx;
        double dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Position layoutToPos(double x, double y) {
        return new Position((x/GameConstants.WORLD_PER_PIXEL_RATIO),
                ((GameConstants.SCREEN_HEIGHT - y)/GameConstants.WORLD_PER_PIXEL_RATIO));
    }

    public static Position average(Position... positionList) {
        double sumX = 0, sumY = 0;
        int count = 0;
        for (Position position:positionList) {
            count++;
            sumX += position.getX();
            sumY += position.getY();
        }
        return new Position(sumX/count,sumY/count);
    }

    public void setNodeToPos(Node node) {
        node.setLayoutX(this.getLayoutX());
        node.setLayoutY(this.getLayoutY());
    }

    public static Position mouseEventToScreenPos(MouseEvent event) {
        return layoutToPos(event.getX(), event.getY());
    }
    public static Position NULL_POSITION = new Position(0,0);
    public static Position BOTTOM_LEFT_POS = new Position(0,0);
    public static Position TOP_LEFT_POS = new Position(0,GameConstants.SCREEN_HEIGHT);
    public static Position BOTTOM_RIGHT_POS = new Position(GameConstants.SCREEN_WIDTH,0);
    public static Position TOP_RIGHT_POS = new Position(GameConstants.SCREEN_WIDTH,GameConstants.SCREEN_HEIGHT);
    public static Position MIDDLE_OF_SCREEN = new Position(GameConstants.SCREEN_WIDTH/2,GameConstants.SCREEN_HEIGHT/2);
    public static Position random_on_screen() {
        return new Position(GameMath.randomBetween(0, GameConstants.SCREEN_WIDTH), GameMath.randomBetween(0, GameConstants.SCREEN_HEIGHT));
    }
    public static Position at(double x, double y) {
        return new Position(x,y);
    }

}
