package com.PK2D.Mycelium;


import com.PK2D.PK2D_Mav.Input.Input;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import com.PK2D.PK2D_Mav.World.World;
import javafx.scene.input.KeyCode;

public class MushManControl extends MushMan {
    public MushManControl(World world, Position position) {
        super(world, position);
        /*
        Spotlight spotlight = new Spotlight(this.world);
        spotlight.setRefreshTrigger(this.getContentEvents().getEvent("onMove"));
        spotlight.setRefreshSequence(() -> {
            spotlight.setPosition(this.world.worldPositionToScreenPosition(this.getPosition()).
                    moveAsNewPos(-GameConstants.SCREEN_WIDTH/2, GameConstants.SCREEN_HEIGHT/2));
        });
         */
    }

    @Override
    public void moveOnTick() {
        if (Input.isKeyPressed(KeyCode.D)) this.moveVelocity.accelerateX(10000);
        if (Input.isKeyPressed(KeyCode.A)) this.moveVelocity.accelerateX(-10000);
        if (Input.isKeyPressed(KeyCode.W)) this.moveVelocity.accelerateY(20000);
        if (Input.isKeyPressed(KeyCode.S)) this.moveVelocity.accelerateY(-10000);
        super.moveOnTick();
    }

    public void shiftViewingPane() {
        double worldPaneX = this.getWorld().getWorldPane().getTranslateX();
        double worldPaneY = - this.getWorld().getWorldPane().getTranslateY();
        if (((this.getPosition().getX() + worldPaneX >= (2.0/3.0)* GameConstants.SCREEN_WIDTH) ||
                (this.getPosition().getX() + worldPaneX <= (1.0/3.0)*GameConstants.SCREEN_WIDTH))) {
            this.getWorld().moveWorldPaneX(this.getPosition().getX()-this.previousPosition.getX());
        }
        // todo fix y shifting
        if (((this.getPosition().getY() + worldPaneY >= (2.5/3.0)* GameConstants.SCREEN_HEIGHT) ||
                (this.getPosition().getY() + worldPaneY <= (0.5/3.0)*GameConstants.SCREEN_HEIGHT))) {
            this.getWorld().moveWorldPaneY(this.getPosition().getY()-this.previousPosition.getY());
        }
    }

    @Override
    public void postTickUpdate() {
        this.shiftViewingPane();
        super.postTickUpdate();
    }
}
