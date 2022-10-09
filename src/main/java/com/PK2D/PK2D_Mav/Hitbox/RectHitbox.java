package com.PK2D.PK2D_Mav.Hitbox;

import com.PK2D.PK2D_Mav.Phys.Position.LinePosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class RectHitbox extends Hitbox {
    private double width, height;

    public RectHitbox(double width, double height) {

    }

    public RectHitbox(WorldContent content) {
        this(content, content.getAvgWidth(), content.getAvgHeight());
    }

    public RectHitbox(WorldContent content, double width, double height) {
        super(content);
        this.width = width;
        this.height = height;
    }

    public LineHitbox[] getLineHitboxes() {
        LineHitbox[] lineHitboxes = new LineHitbox[4];
        lineHitboxes[0] = this.getBottomLine();
        lineHitboxes[1] = this.getRightLine();
        lineHitboxes[2] = this.getTopLine();
        lineHitboxes[3] = this.getLeftLine();
        return lineHitboxes;
    }
    
    public LineHitbox getBottomLine() {
        return new LineHitbox(this.content, this.getBottomLeft(), this.getBottomRight());
    }
    
    public LineHitbox getRightLine() {
        return new LineHitbox(this.content, this.getBottomRight(), this.getTopRight());
    }

    public LineHitbox getTopLine() {
        return new LineHitbox(this.content, this.getTopRight(), this.getTopLeft());
    }

    public LineHitbox getLeftLine() {
        return new LineHitbox(this.content, this.getTopLeft(), this.getBottomLeft());
    }

    @Override
    protected boolean doesContainCirc(CircleHitbox hitbox) {
        return hitbox.doesContainRect(this);
    }

    @Override
    protected boolean doesContainRect(RectHitbox hitbox) {
        for (Position corner:hitbox.getCorners()) {
            if (this.doesContainPoint(corner)) return true;
        }
        for (Position corner:this.getCorners()) {
            if (hitbox.doesContainPoint(corner)) return true;
        }
        return false;
    }

    @Override
    protected boolean doesContainLine(LineHitbox hitbox) {
        for (LineHitbox lineHitbox:this.getLineHitboxes()) {
            if (lineHitbox.doesContainLine(hitbox)) return true;
        }
        return false;
    }

    @Override
    public boolean doesContainPoint(Position point) {
        return ((point.getX() >= this.getMinX()) && (point.getX() <= this.getMaxX()) &&
                (point.getY() >= this.getMinY()) && (point.getY() <= this.getMaxY()));
    }

    @Override
    public Vector getNormalVecCirc(CircleHitbox hitbox) {
        return hitbox.getNormalVecRect(this).changeSign();
    }

    @Override
    public Vector getNormalVecRect(RectHitbox hitbox) {
        boolean inMiddleOfX = ((this.getMinX() < hitbox.getMaxX()) || (this.getMaxX() > hitbox.getMinX()));
        boolean inMiddleOfY = ((this.getMinY() < hitbox.getMaxY()) || (this.getMaxY() > hitbox.getMinY()));
        Vector hitToThis = new Vector(hitbox.getPosition(), this.getPosition());
        if (inMiddleOfX && inMiddleOfY) {
            double minX = Math.max(this.getMinX()-hitbox.getMaxX(),hitbox.getMinX()-this.getMaxX());
            double minY = Math.max(this.getMinY()-hitbox.getMaxY(),hitbox.getMinY()-this.getMaxY());
            if (minX > minY) inMiddleOfX = false;
            else inMiddleOfY = false;
        }
        if (inMiddleOfX) {
            return new Vector(0, (hitToThis.getY() > 0)? 1:-1);
        }
        else if (inMiddleOfY) {
            return new Vector((hitToThis.getX() > 0)? 1:-1,0);
        }
        else return new Vector((hitToThis.getX() > 0)? 1:-1, (hitToThis.getY() > 0)? 1:-1).asUnitVector();
    }

    @Override
    public Vector getNormalVecLine(LineHitbox hitbox) {
        Vector normalVec = hitbox.getLinePosition().asVector().getPerpVec().asUnitVector();
        LinePosition linePosition = hitbox.getLinePosition();

        Position pos1 = new Position(this.getWorldContent().getPosition());
        Position pos2 = new Position(this.getWorldContent().getPosition());

        pos1.moveInVec((new Vector(normalVec)).setMagnitude(Math.max(this.height, this.width)));
        pos2.moveInVec((new Vector(normalVec)).setMagnitude(-Math.max(this.height, this.width)));

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
        Vector normalVec = this.getNormalVecRect(hitbox);
        if (Math.abs(normalVec.getX()) > Math.abs(normalVec.getY())) {
            return (Math.abs(this.getPosition().getX() - hitbox.getPosition().getX()) - (this.width + hitbox.width)/2);
        }
        else if (Math.abs(normalVec.getY()) > Math.abs(normalVec.getX())) {
            return (Math.abs(this.getPosition().getY() - hitbox.getPosition().getY()) - (this.height + hitbox.height)/2);
        }
        else {
            double thisDiagonal, hitboxDiagonal;
            thisDiagonal = Math.sqrt(this.width*this.width + this.height*this.height);
            hitboxDiagonal = Math.sqrt(hitbox.width*hitbox.width + hitbox.height*hitbox.height);
            return (this.getPosition().distanceFrom(hitbox.getPosition()) - (thisDiagonal + hitboxDiagonal)/2);
        }
    }

    @Override
    protected double distanceBetweenLine(LineHitbox hitbox) {
        return 0;
    }

    @Override
    protected Node generateHitboxDisplay() {
        Rectangle display = new Rectangle(this.width, this.height);
        display.setFill(Color.TRANSPARENT);
        if (this.content.canInterfere()) {
            display.setStroke(this.content.isRigid()? Color.RED:Color.BLUE);
        }
        else {
            display.setStroke(Color.GREEN);
        }
        return display;
    }

    public Position getBottomLeft() {
        return new Position(this.getMinX(),this.getMinY());
    }

    public Position getBottomRight() {
        return new Position(this.getMaxX(),this.getMinY());
    }

    public Position getTopLeft() {
        return new Position(this.getMinX(),this.getMaxY());
    }

    public Position getTopRight() {
        return new Position(this.getMaxX(),this.getMaxY());
    }

    public Position[] getCorners() {
        Position[] corners = new Position[4];
        corners[0] = this.getBottomRight();
        corners[1] = this.getBottomLeft();
        corners[2] = this.getTopRight();
        corners[3] = this.getTopLeft();
        return corners;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getMaxX() {
        return (this.getPosition().getX() + this.getWidth()/2);
    }

    public double getMinX() {
        return (this.getPosition().getX() - this.getWidth()/2);
    }

    public double getMaxY() {
        return (this.getPosition().getY() + this.getHeight()/2);
    }

    public double getMinY() {
        return (this.getPosition().getY() - this.getHeight()/2);
    }
}
