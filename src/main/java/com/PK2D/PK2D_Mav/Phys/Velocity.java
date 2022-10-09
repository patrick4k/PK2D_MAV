package com.PK2D.PK2D_Mav.Phys;


import com.PK2D.PK2D_Mav.Util.GameConstants;

import java.util.ArrayList;

public class Velocity extends Vector {
    private double termVelX, termVelY;
    private double dragAccX, dragAccY;
    private double dragCoeff;
    public Velocity() {
    }

    public Velocity(double xComp, double yComp) {
        super(xComp, yComp);
    }

    public Vector getTickPositionVec() {
        return new Vector(this.xComp*GameConstants.TICK_SECONDS,this.yComp*GameConstants.TICK_SECONDS);
    }

    public void setTerminalVelocity(double terminalVelocity) {
        this.termVelX = terminalVelocity;
        this.termVelY = terminalVelocity;
    }

    public void setTerminalVelocity(double termVelX, double termVelY) {
        this.termVelX = termVelX;
        this.termVelY = termVelY;
    }

    public void setDragCoeff(double dragCoeff) {
        this.dragCoeff = dragCoeff;
    }

    public void setDragAccleration(double dragAcc) {
        this.dragAccX = dragAcc;
        this.dragAccY = dragAcc;
    }

    public void setDragAccleration(double dragAccX, double dragAccY) {
        this.dragAccX = dragAccX;
        this.dragAccY = dragAccY;
    }

    public void accelerateX(double ax) {
        if (Math.abs(this.xComp+ax*GameConstants.TICK_SECONDS) <= this.termVelX) {
            this.xComp += ax*GameConstants.TICK_SECONDS;
        }
        else {
            this.xComp = (this.xComp+ax*GameConstants.TICK_SECONDS > 0)? termVelX :-termVelX;
        }
    }

    public void accelerateY(double ay) {
        if (Math.abs(this.yComp+ay*GameConstants.TICK_SECONDS) <= this.termVelY) {
            this.yComp += ay*GameConstants.TICK_SECONDS;
        }
        else {
            this.yComp = (this.yComp+ay*GameConstants.TICK_SECONDS > 0)? termVelY :-termVelY;
        }
    }

    public void checkSetTerminalVelocity() {
        if (Math.abs(this.xComp) > this.termVelX) {
            this.xComp = (this.xComp>0)? this.termVelX:-this.termVelX;
        }
        if (Math.abs(this.yComp) > this.termVelY) {
            this.yComp = (this.yComp>0)? this.termVelY:-this.termVelY;
        }
    }

    public void dragX() {
        if (this.xComp != 0) {
            boolean isPositive = this.xComp > 0;
            this.accelerateX(isPositive? -this.dragAccX:this.dragAccX);
            if ((this.xComp > 0) != isPositive) {
                this.xComp = 0;
            }
        }
    }

    public void dragY() {
        if (this.yComp != 0) {
            boolean isPositive = this.yComp > 0;
            this.accelerateY(isPositive? -this.dragAccY:this.dragAccY);
            if ((this.yComp > 0) != isPositive) {
                this.yComp = 0;
            }
        }
    }

    protected void dragVelocity() {
        double velocity = this.getMagnitude();
        
    }

    public void drag() {
        dragX();
        dragY();
    }

    @Override
    public void bounce(ArrayList<Vector> normalVectors, double bounceFactor) {
        super.bounce(normalVectors, bounceFactor);
        this.checkSetTerminalVelocity();
    }

    public double getTerminalVelocity() {
        return Math.sqrt(this.termVelX*this.termVelX + this.termVelY*this.termVelY);
    }

    public double getTermVelX() {
        return termVelX;
    }

    public double getTermVelY() {
        return termVelY;
    }
}
