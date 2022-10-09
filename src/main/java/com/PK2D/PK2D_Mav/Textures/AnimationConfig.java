package com.PK2D.PK2D_Mav.Textures;

import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.*;

public class AnimationConfig {
    private List<ImageView> frames = new ArrayList<>();
    private WorldContent content;
    private String directory, ID;
    private double frameDuration, frameSizeFactor, offsetX, offsetY;
    private boolean reflectX, reflectY;

    public AnimationConfig(WorldContent content, Map<?, ?> map) {
        this.content = content;
        for (Map.Entry<?, ?> entry: map.entrySet()) {
            if (entryEquals(entry, "ID")) {
                this.ID = (String) entry.getValue();
            }
            if (entryEquals(entry, "directory")) {
                this.directory = (String) entry.getValue();
            }
            if (entryEquals(entry, "frameDuration")) {
                this.frameDuration = (double) entry.getValue();
            }
            if (entryEquals(entry, "frameSizeFactor")) {
                this.frameSizeFactor = (double) entry.getValue();
            }
            if (entryEquals(entry, "offsetX")) {
                this.offsetX = (double) entry.getValue();
            }
            if (entryEquals(entry, "offsetY")) {
                this.offsetY = (double) entry.getValue();
            }
            if (entryEquals(entry, "reflectX")) {
                this.reflectX = (boolean) entry.getValue();
            }
            if (entryEquals(entry, "reflectY")) {
                this.reflectY = (boolean) entry.getValue();
            }
        }

        this.generateFrames();
    }

    private boolean entryEquals(Map.Entry<?, ?> entry, String key) {
        return Objects.equals(entry.getKey().toString(), key);
    }

    private void generateFrames() {
        File folder = new File(this.directory);
        if (Objects.isNull(folder.list())) {
            Util.log("Animation returns 0 frames: " + this.directory);
            return;
        }
        List<String> filenames = Arrays.asList(Objects.requireNonNull(folder.list()));
        filenames.sort(String.CASE_INSENSITIVE_ORDER);
        for (String filename:filenames) {
            frames.add(Textures.getTexture(directory + "/" + filename,
                    this.frameSizeFactor*content.getAvgWidth(), this.frameSizeFactor*content.getAvgWidth()));
        }
        if (this.reflectX || this.reflectY || (this.offsetX > 0) || (this.offsetY > 0)) {
            this.modifyFrames();
        }
    }

    private void modifyFrames() {
        ContentPosition position;
        for (ImageView image: this.frames) {
            position = new ContentPosition(image);
            position.moveInVec(new Vector(offsetX, offsetY));
        }
        if (reflectX) this.frames = Textures.reflectedFramesX(this.frames);
        if (reflectY) this.frames = Textures.reflectedFramesY(this.frames);
    }

    public List<ImageView> getFrames() {
        return frames;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public double getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(double frameDuration) {
        this.frameDuration = frameDuration;
    }

    public double getFrameSizeFactor() {
        return frameSizeFactor;
    }

    public void setFrameSizeFactor(double frameSizeFactor) {
        this.frameSizeFactor = frameSizeFactor;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public boolean getReflectX() {
        return reflectX;
    }

    public void setReflectX(boolean reflectX) {
        this.reflectX = reflectX;
    }

    public boolean getReflectY() {
        return reflectY;
    }

    public void setReflectY(boolean reflectY) {
        this.reflectY = reflectY;
    }
}
