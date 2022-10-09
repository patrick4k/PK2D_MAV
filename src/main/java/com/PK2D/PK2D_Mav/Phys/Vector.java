package com.PK2D.PK2D_Mav.Phys;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Util.GameMath;

import java.io.Serializable;
import java.util.ArrayList;

public class Vector implements Serializable {
    protected double xComp = 0;
    protected double yComp = 0;

    public Vector() {
    }

    public Vector(double xComp, double yComp) {
        this.setXY(xComp, yComp);
    }

    public Vector(Position pos1, Position pos2) {
        this.setXY(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY());
    }

    public Vector(Vector vector) {
        this.set(vector);
    }

    public void set(Vector vector) {
        this.xComp = vector.getX();
        this.yComp = vector.getY();
    }

    public void setXY(double xComp, double yComp) {
        this.xComp = xComp;
        this.yComp = yComp;
    }

    public Vector setMagnitudeAndAngle(double magnitude, double theta) {
        this.xComp = magnitude*GameMath.cosd(theta);
        this.yComp = magnitude*GameMath.sind(theta);
        return this;
    }

    public Vector setMagnitude(double magnitude) {
        this.xComp = magnitude*this.xComp/this.getMagnitude();
        this.yComp = magnitude*this.yComp/this.getMagnitude();
        return this;
    }

    public Vector setandGetMagnitude(double magnitude) {
        return (new Vector(this.asUnitVector())).scale(magnitude);
    }

    public Vector scale(double factor) {
        this.xComp *= factor;
        this.yComp *= factor;
        return this;
    }

    public Vector scaleAndGetNew(double factor) {
        return new Vector(factor*this.getX(), factor*this.getY());
    }

    public Vector changeSign() {
        this.xComp*=-1;
        this.yComp*=-1;
        return this;
    }

    public void setDot(Vector vector) {
        this.setMagnitudeAndAngle(this.dot(vector), vector.getAngle());
    }

    public void setRestrictions(ArrayList<Vector> normalVectors) {
        for (Vector vector:normalVectors) {
            if (this.dot(vector) < 0) {
                Vector restrictedVector = vector.setandGetMagnitude(this.dot(vector));
                this.subtract(restrictedVector);
            }
        }
    }

    public void bounce(ArrayList<Vector> normalVectors, double bounceFactor) {
        for (Vector vector:normalVectors) {
            if (this.dot(vector) < 0) {
                Vector bounceVector = vector.setandGetMagnitude(-bounceFactor*this.dot(vector));
                this.add(bounceVector);
            }
        }
    }

    public double dot(Vector vector) {
        return this.xComp*vector.getX() + this.yComp*vector.getY();
    }

    public Vector getPerpVec() {
        return new Vector(-this.yComp, this.xComp);
    }

    public double angleBetween(Vector vector) {
        return Math.toDegrees(Math.acos(this.dot(vector)/(this.getMagnitude()*vector.getMagnitude())));
    }

    public void setAngle(double angle) {
        this.setMagnitudeAndAngle(this.getMagnitude(), angle);
    }

    public void rotateCCW(double dTheta) {
        this.setAngle(this.getAngle() + dTheta);
    }

    public void rotateCW(double dTheta) {
        this.rotateCCW(-dTheta);
    }

    public void add(Vector vector) {
        this.xComp += vector.getX();
        this.yComp += vector.getY();
    }

    public void subtract(Vector vector) {
        this.xComp -= vector.getX();
        this.yComp -= vector.getY();
    }

    public double getX() {
        return xComp;
    }

    public double getY() {
        return yComp;
    }

    public double getAbsX() {
        return Math.abs(xComp);
    }

    public double getAbsY() {
        return Math.abs(yComp);
    }

    public double getSignOfX() {
        return xComp/Math.abs(xComp);
    }

    public double getSignOfY() {
        return yComp/Math.abs(yComp);
    }

    public void addX(double dx) {
        this.setXY(this.getX()+dx, this.getY());
    }

    public void addY(double dy) {
        this.setXY(this.getX(), this.getY()+dy);
    }

    public void addXY(double dx, double dy) {
        this.setXY(this.getX()+dx, this.getY()+dy);
    }

    public Vector asUnitVector() {
        return new Vector(this.xComp/this.getMagnitude(),this.yComp/this.getMagnitude());
    }

    public double getMagnitude() {
        return Math.sqrt(xComp*xComp+yComp*yComp);
    }

    public double getAngle() {
        return GameMath.atand(this.yComp/this.xComp);
    }

    public boolean isEmpty() {
        return ((this.xComp == 0) && (this.yComp == 0));
    }

    public String asString() {
        return "[" + this.xComp + ", " + this.yComp + "]";
    }

    public static Vector average(Vector... vectors) {
        Vector returnVec = new Vector();
        for (Vector vector:vectors) {
            returnVec.add(vector);
        }
        returnVec.scale(1.0/vectors.length);
        return returnVec;
    }

    public static Vector average(ArrayList<Vector> vectors) {
        Vector returnVec = new Vector();
        for (Vector vector:vectors) {
            returnVec.add(vector);
        }
        returnVec.scale(1.0/vectors.size());
        return returnVec;
    }

    public static Vector unitFromPositions(Position startPos, Position endPos) {
        return (new Vector(endPos.getX()-startPos.getX(),endPos.getY()-startPos.getY())).asUnitVector();
    }

    public static final Vector rightVec = new Vector(1,0);
    public static final Vector rightUpVec = new Vector(1,1).asUnitVector();
    public static final Vector upVec = new Vector(0,1);
    public static final Vector leftUpVec = new Vector(-1, 1).asUnitVector();
    public static final Vector leftVec = new Vector(-1,0);
    public static final Vector leftDownVec = new Vector(-1, -1).asUnitVector();
    public static final Vector downVec = new Vector(0,-1);
    public static final Vector rightDownVec = new Vector(1,-1).asUnitVector();
}
