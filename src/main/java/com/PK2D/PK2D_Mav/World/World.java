package com.PK2D.PK2D_Mav.World;

import com.PK2D.Mycelium.MyceliumWorld;
import com.PK2D.Mycelium.MyceliumWorldBuilder;
import com.PK2D.PK2D_Mav.ExecutableEvent.EventCollection;
import com.PK2D.PK2D_Mav.ExecutableEvent.ExecutableEvent;
import com.PK2D.PK2D_Mav.GUI.PositionLabel;
import com.PK2D.PK2D_Mav.Input.Input;
import com.PK2D.PK2D_Mav.Input.Keybinds;
import com.PK2D.PK2D_Mav.PK2D_app;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Time.Timer;
import com.PK2D.PK2D_Mav.Util.GameConstants;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class World implements Serializable {

    private int tick = 0;
    protected final Pane worldPane;
    protected Pane backgroundHUD, frontHUD;
    protected Timeline worldTimeline;
    protected List<WorldContent> worldContents = new ArrayList<>();
    protected List<WorldContent> tickingContents = new ArrayList<>();
    protected List<WorldContent> bigTickingContents = new ArrayList<>();
    protected List<WorldContent> postTickingContents = new ArrayList<>();
    protected List<WorldContent> interferingContent = new ArrayList<>();
    protected List<WorldBundle> worldBundles = new ArrayList<>();
    protected List<WorldContent> removeQueue = new ArrayList<>();
    private boolean isShowingHitboxes = false, shouldPause = false;
    protected EventCollection worldEvents = new EventCollection();

    public World(Pane worldPane, Pane backgroundHUD, Pane frontHUD) {
        this.worldPane = worldPane;
        this.backgroundHUD = backgroundHUD;
        this.frontHUD = frontHUD;
        if (GameConstants.USES_DEBUG_CONTROL) PositionLabel.addToPane(frontHUD);
        this.initTimeline();
        this.worldEventSetup(this.worldEvents);
        this.worldSetUp();
        this.initBackgroundPane(this.backgroundHUD);
        this.initFrontPane(this.frontHUD);
        this.setKeyBinds();

    }

    public static void main(String[] args) {
        Class<? extends World> worldClass;
        worldClass = MyceliumWorld.class;
//        worldClass = MyceliumWorldBuilder.class;
        PK2D_app.launchWorld(worldClass);
    }

    private void initTimeline() {
        this.worldTimeline = new Timeline(new KeyFrame(Duration.seconds(GameConstants.TICK_SECONDS), event -> {
            this.tick();
            if (this.tick%GameConstants.TICKS_PER_BIG_TICK == 0) this.bigTick();
            this.postTickUpdate();
        }));
        this.worldTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    protected void pausePlayTimeline() {
        if (this.worldTimeline.getStatus() == Animation.Status.RUNNING) {
            this.shouldPause = true;
        }
        else if (this.worldTimeline.getStatus() == Animation.Status.PAUSED) {
            this.worldTimeline.play();
        }
        else if (this.worldTimeline.getStatus() == Animation.Status.STOPPED) {
            this.worldTimeline.play();
        }
        Util.log("Set timeline status: " + this.worldTimeline.getStatus(), LogPreference.WORLD_VIEW_EDITS);
    }

    protected void worldEventSetup(EventCollection worldEvents) {
        worldEvents.createEvent("onStart").addListener(this::onWorldStart);
    }

    protected void onWorldStart() {
    }

    protected abstract void initBackgroundPane(Pane backgroundHUD);
    protected abstract void initFrontPane(Pane frontHUD);
    protected abstract void worldSetUp();

    public void startWorld() {
        this.worldTimeline.play();
    }

    public void add(WorldContent content) {
        if (Objects.isNull(content)) {
            Util.log("Content is null, could not be added to world", LogPreference.NULL_CATCH);
            return;
        }
        this.addContentDisplayToPane(content);
        if (content.isTicking()) this.tickingContents.add(content);
        if (content.isBigTicking()) this.bigTickingContents.add(content);
        if (content.doesPostTick()) this.postTickingContents.add(content);
        if (content.canInterfere()) this.interferingContent.add(content);
        this.worldContents.add(content);
        if (this.isShowingHitboxes) this.showHitboxes();
        Util.log("Added " + content.getContentCode() + " to world", LogPreference.WORLD_ADDITIONS);
    }

    public void add(WorldBundle bundle) {
        if (Objects.isNull(bundle)) {
            Util.log("Bundle is null, could not be added to world", LogPreference.NULL_CATCH);
            return;
        }
        Util.log("Bundle Added", LogPreference.WORLD_ADDITIONS);
        for (WorldContent content:bundle.getContents()) {
            this.add(content);
        }
    }

    private void addContentDisplayToPane(WorldContent content) {
        // TODO very computationally heavy, look for better fix
        WorldContent closestHigherPriority = null, lastCommonPriority = null;
        for (Node display: this.worldPane.getChildren()) { // Back to front
            WorldContent contentFromPane = this.matchDisplayToContent(display);
            if (Objects.nonNull(contentFromPane)) {
                if (contentFromPane.displayPriority() == content.displayPriority()) {
                    lastCommonPriority = contentFromPane;
                }
                else if ((contentFromPane.displayPriority() < content.displayPriority()) && Objects.isNull(closestHigherPriority)) {
                    closestHigherPriority = contentFromPane;
                }
            }
        }
        if (Objects.nonNull(lastCommonPriority)) {
            int index = this.worldPane.getChildren().indexOf(lastCommonPriority.getDisplay());
            this.worldPane.getChildren().add(index, content.getDisplay());
        }
        else if (Objects.nonNull(closestHigherPriority)) {
            int index = this.worldPane.getChildren().indexOf(closestHigherPriority.getDisplay());
            this.worldPane.getChildren().add(index, content.getDisplay());
        }
        else {
            this.worldPane.getChildren().add(content.getDisplay());
        }
    }

    private WorldContent matchDisplayToContent(Node display) {
        for (WorldContent content: this.worldContents) {
            if (Objects.equals(content.getDisplay(), display)) return content;
        }
        return null;
    }

    public void remove(WorldContent content) {
        if (Objects.nonNull(content.getAnimations())) content.getAnimations().stop();
        if (content.getHitbox().isShowing()) content.getHitbox().hideHitbox();
        this.worldPane.getChildren().remove(content.getDisplay());
        this.worldContents.remove(content);
        if (content.canInterfere()) this.interferingContent.remove(content);
        if (content.isBigTicking()) this.bigTickingContents.remove(content);
        if (content.doesPostTick()) this.postTickingContents.remove(content);
        if (content.isTicking()) this.tickingContents.remove(content);
        Util.log("Removed " + content.getContentCode() + " from world", LogPreference.WORLD_REMOVALS);
    }

    private void tick() {
        this.worldTick();
        for (WorldContent content:this.tickingContents) {
            if (!this.removeQueue.contains(content)) content.tick();
        }
        Timer.checkTimers();
    }

    protected void worldTick() {
        if (GameConstants.USES_DEBUG_CONTROL) PositionLabel.set(String.valueOf(Math.round(this.getTime()*1000)/1000.0));
    }

    private void bigTick() {
        for (WorldContent content:this.bigTickingContents) {
            content.bigTick();
        }
        this.worldBigTick();
    }

    protected void worldBigTick() {
    }

    protected void postTickUpdate() {
        this.checkRemoveQueue();
        for (WorldContent content:this.postTickingContents) {
            content.postTickUpdate();
        }
        if (this.shouldPause) {
            this.worldTimeline.pause();
            this.shouldPause = false;
        }
        this.tick++;
    }

    protected void setKeyBinds() {
        if (GameConstants.USES_DEBUG_CONTROL) debuggerControls();
    }

    private void debuggerControls() {
        Input.addOnKeyPressed(KeyCode.F8, event -> {
            this.pausePlayTimeline();
        });

        Input.addOnKeyPressed(KeyCode.F9, event -> {
            this.worldTimeline.setRate(2*this.worldTimeline.getCurrentRate());
            Util.log("Rate adjusted to " + this.worldTimeline.getCurrentRate(), LogPreference.WORLD_VIEW_EDITS);
        });
        Input.addOnKeyPressed(KeyCode.F7, event -> {
            this.worldTimeline.setRate(this.worldTimeline.getCurrentRate()/2);
            Util.log("Rate adjusted to " + this.worldTimeline.getCurrentRate(), LogPreference.WORLD_VIEW_EDITS);
        });

        Input.addOnKeyPressed(KeyCode.MINUS, event -> {
            if (Input.isKeyPressed(KeyCode.SHIFT)) return;
            this.worldPane.setScaleX(0.5*this.worldPane.getScaleX());
            this.worldPane.setScaleY(0.5*this.worldPane.getScaleY());
        });
        Input.addOnKeyPressed(KeyCode.EQUALS, event -> {
            if (Input.isKeyPressed(KeyCode.SHIFT)) return;
            this.worldPane.setScaleX(2*this.worldPane.getScaleX());
            this.worldPane.setScaleY(2*this.worldPane.getScaleY());
        });

        Input.addOnKeyPressed(KeyCode.CLOSE_BRACKET, event -> this.showHitboxes());
        Input.addOnKeyPressed(KeyCode.OPEN_BRACKET, event -> this.hideHitboxes());
        Input.addOnKeyPressed(KeyCode.UP, event -> this.moveWorldPaneY(100));
        Input.addOnKeyPressed(KeyCode.DOWN, event -> this.moveWorldPaneY(-100));
        Input.addOnKeyPressed(KeyCode.RIGHT, event -> this.moveWorldPaneX(100));
        Input.addOnKeyPressed(KeyCode.LEFT, event -> this.moveWorldPaneX(-100));

        Input.addOnMouseDrag((event, dragStartPos, dragEndPos) -> {
            this.moveWorldPaneX(dragStartPos.getX() - dragEndPos.getX());
            this.moveWorldPaneY(dragStartPos.getY() - dragEndPos.getY());
        });

        Input.addOnKeyPressed(Keybinds.RESET_WORLD_POS, event -> {
            this.worldPane.setTranslateX(0);
            this.worldPane.setTranslateY(0);
        });
    }

    public void killContent(WorldContent content) {
        if (this.worldTimeline.getStatus() == Animation.Status.PAUSED) {
            this.remove(content);
        }
        else {
            this.removeQueue.add(content);
        }
    }

    public void killContent(WorldBundle bundle) {
        for (WorldContent content:bundle.getContents()) {
            this.killContent(content);
        }
    }

    public void checkRemoveQueue() {
        for (WorldContent content:this.removeQueue) {
            this.remove(content);
        }
        this.removeQueue.clear();
    }

    public void killAllContent() {
        List<WorldContent> killList = new ArrayList<>(this.worldContents);
        for (WorldContent content: killList) {
            content.kill();
        }
    }

    public WorldContent[] getAllOfType(Class<? extends WorldContent> contentClass) {
        List<WorldContent> contentsOfClass = new ArrayList<>();
        for (WorldContent content: this.worldContents) {
            if (contentClass.isInstance(content)) contentsOfClass.add(content);
        }
        return contentsOfClass.toArray(new WorldContent[0]);
    }

    protected List<WorldContent> getAllContentAtPos(Position position) {
        List<WorldContent> contentAtClick = new ArrayList<>();
        for (WorldContent content: this.worldContents) {
            if (content.getHitbox().doesContainPoint(position)) contentAtClick.add(content);
        }
        if (contentAtClick.size() == 0) Util.log("No content at " + position.asString(), LogPreference.NULL_CATCH);
        return contentAtClick;
    }

    protected WorldContent getTopContentAtPos(Position position) {
        for (int i = this.worldPane.getChildren().size()-1; i > 0; i--) {
            WorldContent content = this.matchDisplayToContent(this.worldPane.getChildren().get(i));
            if ((Objects.nonNull(content)) &&(content.getHitbox().doesContainPoint(position))) return content;
        }
        Util.log("No content at " + position.asString(), LogPreference.NULL_CATCH);
        return null;
    }

    public boolean isInWorld(WorldContent content) {
        return this.worldContents.contains(content);
    }

    public boolean isInWorld(WorldBundle bundle) {
        return this.worldBundles.contains(bundle);
    }

    public int getTick() {
        return this.tick;
    }

    public double getTime() {
        return Util.tickToTime(this.tick);
    }

    public boolean onTimeInterval(double sec) {
        return (this.tick % ((int) Math.round(sec * GameConstants.TICK_PER_SECONDS)) == 0);
    }

    public boolean onTime(double sec) {
        return this.getTime() == sec;
    }

    public Pane getWorldPane() {
        return worldPane;
    }

    public Pane getBackgroundHUD() {
        return backgroundHUD;
    }

    public Pane getFrontHUD() {
        return frontHUD;
    }

    public void moveWorldPaneX(double dx) {
        this.worldPane.setTranslateX(this.worldPane.getTranslateX()-dx);
    }

    public void moveWorldPaneY(double dy) {
        this.worldPane.setTranslateY(this.worldPane.getTranslateY()+dy);
    }

    public void showHitboxes() {
        this.isShowingHitboxes = true;
        for (WorldContent content:this.worldContents) {
            if (!content.getHitbox().isShowing()) content.getHitbox().showHitbox();
            else content.getHitbox().refreshNode();
        }
    }

    public void hideHitboxes() {
        this.isShowingHitboxes = false;
        for (WorldContent content:this.worldContents) {
            if (content.getHitbox().isShowing()) content.getHitbox().hideHitbox();
        }
    }

    public List<WorldContent> getWorldContents() {
        return worldContents;
    }

    public List<WorldContent> getTickingContents() {
        return tickingContents;
    }

    public List<WorldContent> getBigTickingContents() {
        return bigTickingContents;
    }

    public List<WorldContent> getInterferingContent() {
        return interferingContent;
    }

    public List<WorldBundle> getWorldBundles() {
        return worldBundles;
    }

    public List<WorldContent> getRemoveQueue() {
        return removeQueue;
    }

    public boolean isShowingHitboxes() {
        return isShowingHitboxes;
    }

    public Enum<Animation.Status> getAnimationStatus() {
        return this.worldTimeline.getStatus();
    }

    public ExecutableEvent getOnStartEvent() {
        return this.worldEvents.getEvent("onStart");
    }

    public void setCenter(Position position) {
        this.moveWorldPaneX(-this.worldPane.getTranslateX() + position.getX());
        this.moveWorldPaneY(this.worldPane.getTranslateY() + position.getY());
    }

    public Position worldPositionToScreenPosition(Position worldPostion) {
        return worldPostion.moveAsNewPos(this.worldPane.getTranslateX(), -this.worldPane.getTranslateY());
    }

    public Position screenPositionToWorldPosition(Position screenPosition) {
        return screenPosition.moveAsNewPos(-this.worldPane.getTranslateX(), this.worldPane.getTranslateY());
    }

    public Position mouseEventToWorldPos(MouseEvent event) {
        return screenPositionToWorldPosition(Position.mouseEventToScreenPos(event));
    }
}
