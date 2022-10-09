package com.PK2D.Mycelium;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.World.World;
import com.PK2D.PK2D_Mav.World.WorldBuildZIP;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MyceliumWorld extends World {
    public MyceliumWorld(Pane worldPane, Pane backgroundHUD, Pane frontHUD) {
        super(worldPane, backgroundHUD, frontHUD);
    }

    @Override
    protected void initBackgroundPane(Pane backgroundHUD) {
        backgroundHUD.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    protected void initFrontPane(Pane frontHUD) {

    }

    @Override
    protected void worldSetUp() {
        this.add(WorldBuildZIP.importBuild(this, MyceliumWorldBuilder.class));
        this.add(new MushManControl(this, Position.MIDDLE_OF_SCREEN));
    }
}
