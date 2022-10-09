package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Textures.Textures;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldBundle;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.Node;

import java.util.List;

public class TerrainPack extends WorldBundle {
    public TerrainPack(World world) {
        super(world);
    }

    @Override
    protected void setUp(List<WorldContent> contents) {
        BrickWall wall = new BrickWall(this.world, new Position(0, 250));
        contents.add(wall);
         wall = new BrickWall(this.world, wall.getPosition()) {
            @Override
            public boolean isRigid() {
                return false;
            }
        };
        for (int i = 0; i < 8; i++) {
            contents.add(wall.setLeftOf(wall));
            wall = new BrickWall(this.world, wall.getPosition()) {
                @Override
                public boolean isRigid() {
                    return false;
                }
            };
        }

        Grass grassBlock = new Grass(this.world, new Position());
        contents.add(grassBlock.setBottomOf(wall));
        for (int i = 0; i < 8; i++) {
            grassBlock = (Grass) new Grass(this.world, grassBlock.getPosition()).setRightOf(grassBlock);
            contents.add(grassBlock);
        }

        Grass dirt = (Grass) new Grass(this.world, grassBlock.getPosition()){
            @Override
            public boolean isRigid() {
                return false;
            }
            @Override
            protected Node generateDisplay() {
                return Textures.getTexture("resources/FedExTheGame/Terrain/dirt.png", this);
            }
        }.setBottomOf(grassBlock);
        contents.add(dirt);

        for (int i = 0; i < 8; i++) {
            dirt = (Grass) new Grass(this.world, grassBlock.getPosition()){
                @Override
                public boolean isRigid() {
                    return false;
                }
                @Override
                protected Node generateDisplay() {
                    return Textures.getTexture("resources/FedExTheGame/Terrain/dirt.png", this);
                }
            }.setLeftOf(dirt);
            contents.add(dirt);
        }




    }
}
