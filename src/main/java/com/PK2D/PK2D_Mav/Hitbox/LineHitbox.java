package com.PK2D.PK2D_Mav.Hitbox;

import com.PK2D.PK2D_Mav.Phys.Position.LinePosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Util.GameMath;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;

public class LineHitbox extends Hitbox {
    protected LinePosition linePosition;

    public LineHitbox(WorldContent content, Position startPos, Position endPos) {
        super(content);
        this.linePosition = new LinePosition(startPos, endPos);
    }
    @Override
    protected boolean doesContainCirc(CircleHitbox hitbox) {
        return false;
    }

    @Override
    protected boolean doesContainRect(RectHitbox hitbox) {
        return false;
    }

    @Override
    protected boolean doesContainLine(LineHitbox hitbox) {
        return GameMath.doLinesIntersect(this.linePosition, hitbox.getLinePosition());
    }

    @Override
    public boolean doesContainPoint(Position point) {
        return false;
    }

    @Override
    public Vector getNormalVecCirc(CircleHitbox hitbox) {
        return new Vector();
    }

    @Override
    public Vector getNormalVecRect(RectHitbox hitbox) {
        return new Vector();
    }

    @Override
    public Vector getNormalVecLine(LineHitbox hitbox) {
        return new Vector();
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
        return null;
    }

    public LinePosition getLinePosition() {
        return linePosition;
    }

    public void setLinePosition(LinePosition linePosition) {
        this.linePosition = linePosition;
    }
}
