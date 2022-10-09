package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.World.WorldBuilder;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.layout.Pane;

import java.util.List;

public class MyceliumWorldBuilder extends WorldBuilder {


    public MyceliumWorldBuilder(Pane worldPane, Pane backgroundHUD, Pane frontHUD) {
        super(worldPane, backgroundHUD, frontHUD);
    }

    @Override
    protected void initBackgroundPane(Pane backgroundHUD) {

    }

    @Override
    protected void initFrontPane(Pane frontHUD) {

    }

    @Override
    protected void addClassesToList(List<Class<? extends WorldContent>> contentClasses) {
        contentClasses.add(Grass.class);
        contentClasses.add(Dirt.class);
        contentClasses.add(BrickWall.class);
        contentClasses.add(GravityFlipper.class);
    }
}
