package com.PK2D.PK2D_Mav.Content.Scenery;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldContent;

public abstract class Scenery extends WorldContent {

    public Scenery(World world, Position position) {
        super(world, position);
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    public Scenery setRightOf(Scenery scenery) {
        this.position.set(scenery.getPosition().moveAsNewPos(this.getAvgWidth()/2 + scenery.getAvgWidth()/2,0));
        return this;
    }

    public Scenery setLeftOf(Scenery scenery) {
        this.position.set(scenery.getPosition().moveAsNewPos(-this.getAvgWidth()/2 - scenery.getAvgWidth()/2,0));
        return this;
    }

    public Scenery setTopOf(Scenery scenery) {
        this.position.set(scenery.getPosition().moveAsNewPos(0,this.getAvgHeight()/2 + scenery.getAvgHeight()/2));
        return this;
    }

    public Scenery setBottomOf(Scenery scenery) {
        this.position.set(scenery.getPosition().moveAsNewPos(0,-this.getAvgHeight()/2 - scenery.getAvgHeight()/2));
        return this;
    }

    public Position[] generateFourMidpoints() {
        Position[] positions = new Position[4];
        positions[0] = this.getPosition().moveAsNewPos(this.getAvgWidth()/2, 0);
        positions[1] = this.getPosition().moveAsNewPos(-this.getAvgWidth()/2, 0);
        positions[2] = this.getPosition().moveAsNewPos(0,this.getAvgHeight()/2);
        positions[3] = this.getPosition().moveAsNewPos(0,-this.getAvgHeight()/2);
        return positions;
    }


}
