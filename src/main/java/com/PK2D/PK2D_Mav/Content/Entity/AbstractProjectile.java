package com.PK2D.PK2D_Mav.Content.Entity;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.World.World;

public abstract class AbstractProjectile extends Entity {

    public AbstractProjectile(World world, Position pos) {
        super(world, pos);
    }

    public void launch(Vector direction) {
        direction = direction.setMagnitude(this.getTerminalVelocity());
        this.moveVelocity.set(direction);
    }

    public void launch(double x, double y) {
        Vector direction = new Vector(x, y).asUnitVector().setMagnitude(this.getTerminalVelocity());
        this.moveVelocity.set(direction);
    }
}
