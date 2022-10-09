package com.PK2D.PK2D_Mav.Content.Entity;

import com.PK2D.PK2D_Mav.ExecutableEvent.EventCollection;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Phys.Velocity;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldContent;

import java.util.ArrayList;

public abstract class Entity extends WorldContent {
    protected ContentPosition previousPosition;
    protected Velocity moveVelocity;
    protected ArrayList<Vector> normalVectors = new ArrayList<>();

    protected boolean inIntersectWithRigid = false;
    protected boolean isOnGround;

    public Entity(World world, Position position) {
        super(world, position);
    }

    @Override
    protected void contentSetup() {
        super.contentSetup();
        this.moveVelocity = new Velocity();
        this.moveVelocity.setTerminalVelocity(this.generateTerminalVelocityX(), this.generateTerminalVelocityY());
        this.moveVelocity.setDragAccleration(this.generateDragAccX(), this.generateDragAccY());
        this.previousPosition = new ContentPosition(this, this.position);
    }

    @Override
    protected void contentEventSetup(EventCollection contentEvents) {
        contentEvents.createEvent("onMove");
        super.contentEventSetup(contentEvents);
    }

    protected abstract double generateTerminalVelocityX();
    protected abstract double generateTerminalVelocityY();
    protected abstract double generateDragAccX();
    protected abstract double generateDragAccY();

    @Override
    public void tick() {
        this.tryMove();
        super.tick();
    }

    @Override
    public void postTickUpdate() {
        this.hitbox.refreshNode();
        super.postTickUpdate();
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public boolean isTicking() {
        return true;
    }

    public abstract void moveOnTick();

    private void tryMove() {
        this.moveOnTick();
        this.moveVelocity.drag();
        if (this.moveVelocity.getMagnitude() == 0) return;
        this.previousPosition.set(this.position);
        this.position.moveInVelocity(this.moveVelocity, false);
        this.checkAllIntersect();
        if (this.inIntersectWithRigid) {
            this.position.set(previousPosition);
            this.moveVelocity.setRestrictions(this.normalVectors);
            if (this.getBounceFactor() > 0) {
                this.moveVelocity.bounce(this.normalVectors, this.getBounceFactor());
                this.moveVelocity.setRestrictions(this.normalVectors);
            }
            this.position.moveInVelocity(this.moveVelocity);
            this.checkIfOnGround();
            this.normalVectors.clear();
        }
        this.position.refresh();
        this.contentEvents.getEvent("onMove").fireEvent();
    }

    public void addOnEntityMove(GameEvent listener) {
        this.contentEvents.getEvent("onMove").addListener(listener);
    }

    private void checkIfOnGround() {
        this.isOnGround = false;
        for (Vector vector: this.normalVectors) {
            if (vector.getY() > vector.getAbsX()) this.isOnGround = true;
        }
    }

    public void checkRigidity(WorldContent content) {
        if (this.isRigid(content) && content.isRigid(this)) {
            this.inIntersectWithRigid = true;
            Vector normalVector = this.getHitbox().getNormalVector(content.getHitbox());
            this.normalVectors.add(normalVector);
        }
    }

    public ContentPosition getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(ContentPosition previousPosition) {
        this.previousPosition = previousPosition;
    }

    public Velocity getMoveVelocity() {
        return moveVelocity;
    }

    public void setMoveVelocity(Velocity moveVelocity) {
        this.moveVelocity = moveVelocity;
    }

    public double getTerminalVelocity() {
        return this.moveVelocity.getTerminalVelocity();
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean isMoving() {
        return (this.moveVelocity.getMagnitude() > 0);
    }

    public ArrayList<Vector> getNormalVectors() {
        return normalVectors;
    }

    public double getBounceFactor() {
        return 0;
    }
}
