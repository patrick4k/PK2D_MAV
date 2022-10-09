package com.PK2D.PK2D_Mav.Phys.Position;

import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Phys.Velocity;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Objects;

public class ContentPosition extends Position {
    private WorldContent content;
    private Node display;

    public ContentPosition(WorldContent content) {
        super(0,0);
        this.content = content;
        refresh();
    }

    public ContentPosition(Node display) {
        super(0,0);
        this.display = display;
        refresh();
    }
    
    public ContentPosition(WorldContent content, double x, double y) {
        super(x,y);
        this.content = content;
        refresh();
    }

    public ContentPosition(WorldContent content, Position position) {
        super(position.getX(),position.getY());
        this.content = content;
        refresh();
    }

    public ContentPosition(Node display, Position position) {
        super(position.getX(),position.getY());
        this.display = display;
        refresh();
    }

    public void set(double x, double y) {
        this.xWorldLocation = x;
        this.yWorldLocation = y;
        refresh();
    }

    public void set(Position pos) {
        this.set(pos.getX(),pos.getY());
    }

    @Override
    public void addX(double dx) {
        super.addX(dx);
        refresh();
    }

    @Override
    public void addY(double dy) {
        super.addY(dy);
        refresh();
    }

    @Override
    public void moveInVec(Vector vector) {
        super.moveInVec(vector);
        refresh();
    }

    @Override
    public void moveInVelocity(Velocity velocity) {
        super.moveInVelocity(velocity);
        refresh();
    }

    public void moveInVelocity(Velocity velocity, boolean shouldRefresh) {
        super.moveInVelocity(velocity);
        if (shouldRefresh) this.refresh();
    }

    public void set(ContentPosition pos) {
        this.xWorldLocation = pos.getX();
        this.yWorldLocation = pos.getY();
        refresh();
    }

    public void setAverage(Position... positionList) {
        double sumX = 0, sumY = 0;
        int count = 0;
        for (Position position:positionList) {
            count++;
            sumX += position.getX();
            sumY += position.getY();
        }
        this.set(sumX/count,sumY/count);
    }
    
    public void refresh() {
        if (Objects.nonNull(this.content)) this.displayRefreshContent();
        if (Objects.nonNull(this.display)) this.displayRefreshNode();
    }

    public void displayRefreshContent() {
        if (this.content.getDisplay() instanceof Circle) {
            this.content.getDisplay().setLayoutX(this.getLayoutX());
            this.content.getDisplay().setLayoutY(this.getLayoutY());
        }
        else if (this.content.getDisplay() instanceof Rectangle) {
            this.content.getDisplay().setLayoutX(this.getLayoutX() - ((Rectangle) this.content.getDisplay()).getWidth()/2);
            this.content.getDisplay().setLayoutY(this.getLayoutY() - ((Rectangle) this.content.getDisplay()).getHeight()/2);
        }
        else if (this.content.getDisplay() instanceof ImageView) {
            this.content.getDisplay().setLayoutX(this.getLayoutX() - ((ImageView) this.content.getDisplay()).getFitWidth()/2);
            this.content.getDisplay().setLayoutY(this.getLayoutY() - ((ImageView) this.content.getDisplay()).getFitHeight()/2);
        }
        else Util.log(this.content.getContentCode() + " display refresh not completed", this.content.isInWorld());
    }

    public void displayRefreshNode() {
        if (this.display instanceof Rectangle) {
            this.display.setLayoutX(this.getLayoutX() - ((Rectangle) this.display).getWidth()/2);
            this.display.setLayoutY(this.getLayoutY() - ((Rectangle) this.display).getHeight()/2);
        }
        else if (this.display instanceof Shape) {
            this.display.setLayoutX(this.getLayoutX());
            this.display.setLayoutY(this.getLayoutY());
        }
        else if (this.display instanceof ImageView) {
            this.display.setLayoutX(this.getLayoutX() - ((ImageView) this.display).getFitWidth()/2);
            this.display.setLayoutY(this.getLayoutY() - ((ImageView) this.display).getFitHeight()/2);
        }
        else Util.log("Node display refresh not completed", this.content.isInWorld());
    }
}
