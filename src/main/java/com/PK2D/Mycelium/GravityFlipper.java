package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.Content.Entity.Entity;
import com.PK2D.PK2D_Mav.Content.Scenery.Scenery;
import com.PK2D.PK2D_Mav.Hitbox.Hitbox;
import com.PK2D.PK2D_Mav.Hitbox.RectHitbox;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GravityFlipper extends Scenery {

    public GravityFlipper(World world, Position position) {
        super(world, position);
    }

    @Override
    public boolean isRigid() {
        return false;
    }

    @Override
    protected int displayPriority() {
        return 5;
    }

    @Override
    protected boolean canInteract() {
        return true;
    }

    @Override
    protected Hitbox generateHitbox() {
        return new RectHitbox(this, 100, 1000);
    }

    @Override
    protected void onContentIntersect(WorldContent content, double duration) {
        if (duration > 2) return;
        if (content instanceof Entity) {
            Entity entity = ((Entity) content);
            double contentY = content.getPosition().getY();
            double bottomY = this.getPosition().getY() - this.getAvgHeight()/2;
            double maxHeight = this.getAvgHeight();
            entity.getMoveVelocity().accelerateY(20000 - 15000*Math.pow((contentY-bottomY)/maxHeight,8));
        }
        super.onContentIntersect(content, duration);
    }

    @Override
    protected Node generateDisplay() {
        Rectangle rectangle = new Rectangle(this.getAvgWidth(), this.getAvgHeight());
        rectangle.setFill(Color.ROYALBLUE);
        return rectangle;
    }
}
