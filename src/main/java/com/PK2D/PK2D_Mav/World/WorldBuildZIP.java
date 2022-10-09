package com.PK2D.PK2D_Mav.World;

import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldBuildZIP implements Serializable {
    private static final String fileEnding = ".bundle";
    private final List<String> contentClasses = new ArrayList<>();
    private final List<Double> contentXPos = new ArrayList<>(), contentYPos = new ArrayList<>();
    public WorldBuildZIP(WorldBuilder builder) {
        for (WorldContent content: builder.worldContents) {
            contentClasses.add(content.getClass().getName());
            contentXPos.add(content.getPosition().getX());
            contentYPos.add(content.getPosition().getY());
        }
    }
    private WorldBundle getAsWorldBundle(World world) {
        WorldBundle returnBundle = new WorldBundle(world);
        for (int i = 0; i < contentClasses.size(); i++) {
            WorldContent contentToAdd = null;
            try {
                for (Constructor<?> constructor: Class.forName(contentClasses.get(i)).getConstructors()) {
                    try {
                        contentToAdd = (WorldContent) constructor.newInstance(world,
                                new Position(this.contentXPos.get(i), this.contentYPos.get(i)));
                    } catch (IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        Util.log("Content constructor mismatched", LogPreference.NULL_CATCH);
                    }
                }
            } catch (ClassNotFoundException e) {
                Util.log("Could not locate class: " + contentClasses.get(i));
            }
            returnBundle.add(contentToAdd);
        }
        return returnBundle;
    }

    public static void exportBuild(WorldBuilder worldBuilder) {
        Util.log("Starting save build...", LogPreference.IO_UPDATES);
        WorldBuildZIP exportFile = new WorldBuildZIP(worldBuilder);
        try {
            File file = new File(worldBuilder.getClass().getSimpleName() + fileEnding);
            FileOutputStream saveFile = new FileOutputStream(file.getName());
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(exportFile);
            save.close();
            Util.log("Build successfully saved as " + worldBuilder.getClass().getSimpleName() + fileEnding, LogPreference.IO_UPDATES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WorldBundle importBuild(World world, Class<? extends WorldBuilder> worldBuilderClass) {
        return importBuild(world, worldBuilderClass.getSimpleName() + fileEnding);
    }

    public static WorldBundle importBuild(World world, String filename) {
        Util.log("Starting " + filename + " import...", LogPreference.IO_UPDATES);
        WorldBuildZIP importFile = null;
        try {
            FileInputStream openFile = new FileInputStream(filename);
            ObjectInputStream open = new ObjectInputStream(openFile);
            importFile = (WorldBuildZIP) open.readObject();
            open.close();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        if (Objects.isNull(importFile)) {
            Util.log("Import bundle = null", LogPreference.NULL_CATCH);
            return null;
        }
        Util.log("Imported " + filename + " successfully", LogPreference.IO_UPDATES);
        return importFile.getAsWorldBundle(world);
    }


}
