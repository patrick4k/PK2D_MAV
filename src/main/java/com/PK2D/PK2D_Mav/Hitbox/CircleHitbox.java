package com.PK2D.PK2D_Mav.Hitbox;

import com.PK2D.PK2D_Mav.Phys.Position.LinePosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class CircleHitbox extends Hitbox {
    private double radius;

    public CircleHitbox(WorldContent content, double radius) {
        super(content);
        this.radius = radius;
    }

    @Override
    protected boolean doesContainCirc(CircleHitbox hitbox) {
        return this.getPosition().distanceFrom(hitbox.getPosition())
                < (this.getRadius() + hitbox.getRadius());
    }

    @Override
    protected boolean doesContainRect(RectHitbox hitbox) {
        double thisXpos = this.getPosition().getX();
        double thisYpos = this.getPosition().getY();
        if ((thisXpos <= hitbox.getMaxX()) && (thisXpos >= hitbox.getMinX()) &&
                (thisYpos <= hitbox.getMaxY()) && (thisYpos >= hitbox.getMinY())) {
            return true;
        }
        for (Position corner: hitbox.getCorners()) {
            if (this.getPosition().distanceFrom(corner) < this.radius) return true;
        }
        for (LineHitbox lineHitbox:hitbox.getLineHitboxes()) {
            if (this.doesContainLine(lineHitbox)) return true;
        }
        return false;
    }

    @Override
    protected boolean doesContainLine(LineHitbox hitbox) {
        return (this.getWorldContent().getPosition().distanceFromLinePos(hitbox.linePosition) < this.radius);
    }

    @Override
    public boolean doesContainPoint(Position point) {
        return (this.getPosition().distanceFrom(point) <= this.radius);
    }

    @Override
    public Vector getNormalVecCirc(CircleHitbox hitbox) {
        return new Vector(hitbox.getPosition(),this.getPosition()).asUnitVector();
    }

    @Override
    public Vector getNormalVecRect(RectHitbox hitbox) {
        Vector hitToThis = new Vector(hitbox.getPosition(), this.getPosition());
        boolean inMiddleOfX = (this.getPosition().getX() >= hitbox.getMinX()) && (this.getPosition().getX() <= hitbox.getMaxX());
        boolean inMiddleOfY = (this.getPosition().getY() >= hitbox.getMinY()) && (this.getPosition().getY() <= hitbox.getMaxY());
        if (inMiddleOfX) {
            return new Vector(0, (hitToThis.getY() > 0)? 1:-1);
        }
        else if (inMiddleOfY) {
            return new Vector((hitToThis.getX() > 0)? 1:-1,0);
        }
        else {
            double smallestDistance = this.getPosition().distanceFrom(hitbox.getPosition());
            Position closestCorner = new Position();
            for (Position corner:hitbox.getCorners()) {
                if (this.getPosition().distanceFrom(corner) < smallestDistance) {
                    smallestDistance = this.getPosition().distanceFrom(corner);
                    closestCorner = corner;
                }
            }
            return new Vector(closestCorner, this.getPosition()).asUnitVector();
        }
    }

    @Override
    public Vector getNormalVecLine(LineHitbox hitbox) {
        Vector normalVec = hitbox.getLinePosition().asVector().getPerpVec().asUnitVector();
        LinePosition linePosition = hitbox.getLinePosition();

        Position pos1 = new Position(this.getWorldContent().getPosition());
        Position pos2 = new Position(this.getWorldContent().getPosition());

        pos1.moveInVec((new Vector(normalVec)).setMagnitude(this.radius));
        pos2.moveInVec((new Vector(normalVec)).setMagnitude(-this.radius));

        if (pos1.distanceFromLinePos(linePosition) > pos2.distanceFromLinePos(linePosition)) {
            return normalVec;
        }
        else {
            return normalVec.changeSign();
        }
    }

    @Override
    protected double distanceBetweenCirc(CircleHitbox hitbox) {
        return 0;
    }

    @Override
    protected double distanceBetweenRect(RectHitbox hitbox) {
        return 0;
    }

    @Override
    protected double distanceBetweenLine(LineHitbox hitbox) {
        return 0;
    }

    @Override
    protected Node generateHitboxDisplay() {
        Circle display = new Circle(this.radius);
        display.setFill(Color.TRANSPARENT);
        if (this.content.canInterfere()) {
            display.setStroke(this.content.isRigid()? Color.RED:Color.BLUE);
        }
        else {
            display.setStroke(Color.GREEN);
        }
        return display;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
