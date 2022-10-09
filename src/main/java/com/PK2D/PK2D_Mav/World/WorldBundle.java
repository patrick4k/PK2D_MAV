package com.PK2D.PK2D_Mav.World;

import com.PK2D.PK2D_Mav.ExecutableEvent.ContentEvent;
import com.PK2D.PK2D_Mav.Util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldBundle implements Serializable {
    private static final List<WorldBundle> ALL_WORLD_BUNDLES = new ArrayList<>();

    protected World world;
    private final List<WorldContent> contents = new ArrayList<>();

    public WorldBundle(World world) {
        this.world = world;
        ALL_WORLD_BUNDLES.add(this);
        this.setUp(this.contents);
        for (WorldContent content:this.contents) {
            content.setContentCode("BNDL" + (ALL_WORLD_BUNDLES.indexOf(this) + 1) + "." + content.getContentCode());
        }
    }

    public void add(WorldContent content) {
        if (contents.contains(content)) {
            Util.log("Cannot add duplicates in bundle");
            return;
        }
        this.contents.add(content);
    }

    public void addAll(WorldContent... worldContents) {
        this.addAll(Arrays.asList(worldContents));
    }

    public void addAll(List<WorldContent> worldContents) {
        for (WorldContent content: this.contents) {
            if (worldContents.contains(content)) {
                Util.log("Duplicate found and removed");
                worldContents.removeIf(content::equals);
            }
        }
        this.contents.addAll(worldContents);
    }

    public void forAllContent(ContentEvent contentEvent) {
        for (WorldContent content: this.contents) {
            contentEvent.onEventTriggered(content);
        }
    }

    protected void setUp(List<WorldContent> contents){
    }

    public List<WorldContent> getContents() {
        return contents;
    }
}
