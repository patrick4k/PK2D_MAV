package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.Textures.Animations.ContentAnimations;
import com.PK2D.PK2D_Mav.Content.Entity.Entity;
import com.PK2D.PK2D_Mav.Hitbox.CircleHitbox;
import com.PK2D.PK2D_Mav.Hitbox.Hitbox;
import com.PK2D.PK2D_Mav.Input.Input;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Textures.Textures;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class MushMan extends Entity {
    protected boolean isCrouched = false;

    public MushMan(World world, Position position) {
        super(world, position);
        Input.addOnKeyReleased(KeyCode.S, event -> {
            this.setCrouched(false);
        });
    }

    @Override
    protected double generateTerminalVelocityX() {
        return 750;
    }

    @Override
    protected double generateTerminalVelocityY() {
        return 2000;
    }

    @Override
    protected double generateDragAccX() {
        return 5000;
    }

    @Override
    protected double generateDragAccY() {
        return 5000;
    }

    @Override
    public void moveOnTick() {
        this.moveVelocity.accelerateY(-10000);
    }

    @Override
    public boolean isRigid() {
        return true;
    }

    @Override
    protected boolean canInteract() {
        return false;
    }

    @Override
    protected Hitbox generateHitbox() {
        return new CircleHitbox(this, 50);
    }

    @Override
    protected Node generateDisplay() {
        return Textures.getTexture("src/main/resources/com/PK2D/Mycelium/Entity/MushMan/Crouch/5.png",this);
    }

    @Override
    protected ContentAnimations generateAnimations() {
        return new ContentAnimations(this) {

            @Override
            protected void registerAnimations() {
                /*
                this.register("Idle", "com/PK2D/Mycelium/Entity/MushMan/MushManIdle", 1.2,0.4);
                this.register("WalkRight", "com/PK2D/Mycelium/Entity/MushMan/MushManWalkRight", 0.15);
                this.register("WalkLeft", Textures.reflectedFramesX(this.getFrames("WalkRight")), 0.15);
                this.register("Crouch", "com/PK2D/Mycelium/Entity/MushMan/Crouch", 0.3);
                 */
                this.registerFromJSON("src/main/resources/com/PK2D/Mycelium/Entity/MushMan/MushMan.json");
                this.addOnFinish("Crouch", () -> {
                    ((MushMan) this.content).setCrouched(true);
                });
            }

            @Override
            protected void predicate(WorldContent content) {
                if ((Math.abs(((Entity) this.content).getMoveVelocity().getX()) >
                        (((Entity) this.content).getMoveVelocity().getTermVelX()/5))) {
                    if (((Entity) content).getMoveVelocity().getX() > 0) {
                        this.tryStart("WalkRight");
                    } else {
                        this.tryStart("WalkLeft");
                    }
                }
                else if (Input.isKeyPressed(KeyCode.S)) {
                    if (!((MushMan) this.content).isCrouched) {
                        this.tryStart("Crouch");
                    }
                }
                else {
                    this.tryStart("Idle");
                }
            }
        };
    }

    public boolean isCrouched() {
        return isCrouched;
    }

    public void setCrouched(boolean crouched) {
        isCrouched = crouched;
    }
}
