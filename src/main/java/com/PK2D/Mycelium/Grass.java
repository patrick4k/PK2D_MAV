package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.Content.Scenery.Scenery;
import com.PK2D.PK2D_Mav.Hitbox.Hitbox;
import com.PK2D.PK2D_Mav.Hitbox.RectHitbox;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Textures.Textures;
import com.PK2D.PK2D_Mav.World.World;
import javafx.scene.Node;

public class Grass extends Scenery {
    public Grass(World world, Position position) {
        super(world, position);
    }

    @Override
    protected int displayPriority() {
        return 1;
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
        return new RectHitbox(this, 400, 100);
    }

    @Override
    protected Node generateDisplay() {
        return Textures.getTexture("src/main/resources/com/PK2D/Mycelium/Terrain/grass1.png", this);
    }

}
