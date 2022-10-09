package com.PK2D.PK2D_Mav.Textures.Animations;

import com.PK2D.PK2D_Mav.ExecutableEvent.EventCollection;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.Textures.AnimationConfig;
import com.PK2D.PK2D_Mav.Textures.Textures;
import com.PK2D.PK2D_Mav.Time.RepeatableAction;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.WorldContent;
import com.google.gson.Gson;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ContentAnimations {
    //*
    private int frameIndex;
    protected WorldContent content;
    private RepeatableAction animationRepeatable;
    private List<ImageView> animation;
    protected List<List<ImageView>> animations = new ArrayList<>();
    protected List<List<Image>> animationImages = new ArrayList<>();
    protected ArrayList<String> animationIdentifiers = new ArrayList<>();
    protected ArrayList<Double> frameDuration = new ArrayList<>();
    protected EventCollection events = new EventCollection();

    public ContentAnimations(WorldContent content) {
        this.content = content;
        this.registerAnimations();
        this.repeatableSetup();
    }

    protected abstract void registerAnimations();
    protected abstract void predicate(WorldContent content);

    public void predicate() {
        this.predicate(this.content);
    }

    protected void register(String ID, List<ImageView> frames, double frameDuration) {
        this.animations.add(frames);
        this.animationIdentifiers.add(ID);
        this.frameDuration.add(frameDuration);
        this.events.createEvent(ID + "Start");
        this.events.createEvent(ID + "Finish");
    }

    protected void register(String ID, String pathname, double width, double height, double frameDuration) {
        ArrayList<ImageView> frameList = Textures.getFrames(pathname,width,height);
        this.register(ID, frameList, frameDuration);
    }

    protected void register(String ID, String pathname, double frameDuration) {
        this.register(ID, pathname, this.content.getAvgWidth(), this.content.getAvgHeight(), frameDuration);
    }

    protected void register(String ID, String pathname, double frameSizeFactor, double frameDuration) {
        this.register(ID, pathname, frameSizeFactor*this.content.getAvgWidth(), frameSizeFactor*this.content.getAvgHeight(), frameDuration);
    }

    protected void register(AnimationConfig config) {
        this.register(config.getID(), config.getFrames(), config.getFrameDuration());
    }

    public void registerFromJSON(String jsonPath) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(jsonPath));
            Map<?, ?>[] maps = gson.fromJson(reader, Map[].class);
            for (Map<?, ?> map: maps) {
                this.register(new AnimationConfig(content, map));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected List<ImageView> getFrames(String ID) {
        return this.animations.get(this.getIndex(ID));
    }

    private void repeatableSetup() {
        this.animationRepeatable = new RepeatableAction(0,0);
        this.animationRepeatable.setOnStart(() -> {
            this.frameIndex = 0;
        });
        this.animationRepeatable.setOnAction(() -> {
            if (this.frameIndex >= this.animation.size()) this.frameIndex = 0;
            this.content.changeDisplay(this.animation.get(this.frameIndex));
            this.frameIndex++;
        });
    }

    private int getIndex(String ID) {
        return this.animationIdentifiers.indexOf(ID);
    }

    public void tryStart(String ID) {
        if (!this.isPlaying(ID)) this.forceStart(ID);
    }

    public void forceStart(String ID) {
        this.forceStart(ID, 1);
    }

    public void forceStart(String ID, int cycleCount) {
        if (!this.animationIdentifiers.contains(ID)) {
            Util.log("Animation: \"" + ID + "\" not found");
            return;
        }
        Util.log("Animation: " + ID, LogPreference.ANIMATIONS);
        if (this.isPlaying()) this.animationRepeatable.stop();
        this.frameIndex = 0;
        this.animation = this.animations.get(this.getIndex(ID));
        this.animationRepeatable.setDuration(this.frameDuration.get(this.getIndex(ID)));
        this.animationRepeatable.setMaxRepeats(cycleCount * this.animation.size());
        this.events.getEvent(ID + "Start").fireEvent();
        this.animationRepeatable.forceStart();
        this.animationRepeatable.setOnFinished(this.events.getEvent(ID + "Finish")::fireEvent);
    }

    public void stop() {
        this.animationRepeatable.stop();
    }

    public void play() {
        this.predicate();
    }

    public boolean isPlaying() {
        return this.animationRepeatable.isPlaying();
    }

    public boolean isPlaying(String ID) {
        return (this.isPlaying() && (this.animations.indexOf(this.animation) == this.getIndex(ID)));
    }

    public void addOnStart(String ID, GameEvent event) {
        this.events.getEvent(ID + "Start").addListener(event);
    }

    public void addOnFinish(String ID, GameEvent event) {
        this.events.getEvent(ID + "Finish").addListener(event);
    }

     /*/

    //*
    private int frameIndex;
    protected WorldContent content;
    private RepeatableAction animationRepeatable;
    private ArrayList<ImageView> animation;
    protected ArrayList<ArrayList<ImageView>> animations = new ArrayList<>();
    protected ArrayList<String> animationIdentifiers = new ArrayList<>();
    protected ArrayList<Double> frameDuration = new ArrayList<>();
    protected EventCollection events = new EventCollection();

    public ContentAnimations(WorldContent content) {
        this.content = content;
        this.registerAnimations();
        this.repeatableSetup();
    }

    public ContentAnimations(WorldContent content, String animationJSON) {
        this.content = content;
        this.parseAnimationFile(animationJSON);
        this.registerAnimations();
        this.repeatableSetup();
    }

    protected void parseAnimationFile(String animationJSON) {



    }

    protected void registerAnimations() {

    }

    protected abstract void predicate(WorldContent content);

    public void predicate() {
        this.predicate(this.content);
    }

    protected void register(String ID, ArrayList<ImageView> frames, double frameDuration) {
        this.animations.add(frames);
        this.animationIdentifiers.add(ID);
        this.frameDuration.add(frameDuration);
        this.events.createEvent(ID + "Start");
        this.events.createEvent(ID + "Finish");
    }

    protected void register(String ID, String pathname, double width, double height, double frameDuration) {
        ArrayList<ImageView> frameList = Textures.getFrames(pathname,width,height);
        this.register(ID, frameList, frameDuration);
    }

    protected void register(String ID, String pathname, double frameDuration) {
        this.register(ID, pathname, this.content.getAvgWidth(), this.content.getAvgHeight(), frameDuration);
    }

    protected void register(String ID, String pathname, double frameSizeFactor, double frameDuration) {
        this.register(ID, pathname, frameSizeFactor*this.content.getAvgWidth(), frameSizeFactor*this.content.getAvgHeight(), frameDuration);
    }

    protected ArrayList<ImageView> getFrames(String ID) {
        return this.animations.get(this.getIndex(ID));
    }

    private void repeatableSetup() {
        this.animationRepeatable = new RepeatableAction(0,0);
        this.animationRepeatable.setOnStart(() -> {
            this.frameIndex = 0;
        });
        this.animationRepeatable.setOnAction(() -> {
            if (this.frameIndex >= this.animation.size()) this.frameIndex = 0;
            this.content.changeDisplay(this.animation.get(this.frameIndex));
            this.frameIndex++;
        });
    }

    private int getIndex(String ID) {
        return this.animationIdentifiers.indexOf(ID);
    }

    public void tryStart(String ID) {
        if (!this.isPlaying(ID)) this.forceStart(ID);
    }

    public void forceStart(String ID) {
        this.forceStart(ID, 1);
    }

    public void forceStart(String ID, int cycleCount) {
        if (!this.animationIdentifiers.contains(ID)) {
            Util.log("Animation: \"" + ID + "\" not found");
            return;
        }
        Util.log("Animation: " + ID, LogPreference.ANIMATIONS);
        if (this.isPlaying()) this.animationRepeatable.stop();
        this.frameIndex = 0;
        this.animation = this.animations.get(this.getIndex(ID));
        this.animationRepeatable.setDuration(this.frameDuration.get(this.getIndex(ID)));
        this.animationRepeatable.setMaxRepeats(cycleCount * this.animation.size());
        this.events.getEvent(ID + "Start").fireEvent();
        this.animationRepeatable.forceStart();
        this.animationRepeatable.setOnFinished(this.events.getEvent(ID + "Finish")::fireEvent);
    }

    public void stop() {
        this.animationRepeatable.stop();
    }

    public void play() {
        this.predicate();
    }

    public boolean isPlaying() {
        return this.animationRepeatable.isPlaying();
    }

    public boolean isPlaying(String ID) {
        return (this.isPlaying() && (this.animations.indexOf(this.animation) == this.getIndex(ID)));
    }

    public void addOnStart(String ID, GameEvent event) {
        this.events.getEvent(ID + "Start").addListener(event);
    }

    public void addOnFinish(String ID, GameEvent event) {
        this.events.getEvent(ID + "Finish").addListener(event);
    }
     //*/

}
