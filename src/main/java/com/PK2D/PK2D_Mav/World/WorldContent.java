package com.PK2D.PK2D_Mav.World;

import com.PK2D.PK2D_Mav.Textures.Animations.ContentAnimations;
import com.PK2D.PK2D_Mav.Content.Entity.Entity;
import com.PK2D.PK2D_Mav.ExecutableEvent.EventCollection;
import com.PK2D.PK2D_Mav.ExecutableEvent.ExecutableEvent;
import com.PK2D.PK2D_Mav.Hitbox.CircleHitbox;
import com.PK2D.PK2D_Mav.Hitbox.Hitbox;
import com.PK2D.PK2D_Mav.Hitbox.RectHitbox;
import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class WorldContent implements Serializable {
    private static final List<String> ALL_CONTENT_CODE = new ArrayList<>();
    public static boolean equals(WorldContent content1, WorldContent content2) {
        return Objects.equals(content1.getContentCode(),content2.getContentCode());
    }

    private String contentCode;
    protected World world;
    protected Node display;
    protected ContentAnimations animations;
    protected List<Class<? extends WorldContent>> rigidityExceptions = new ArrayList<>();

    private final List<WorldContent> tickIntersect = new ArrayList<>();
    private final List<WorldContent> currentIntersects = new ArrayList<>();
    private final List<Integer> currentIntersectTickTime = new ArrayList<>();
    private final List<Integer> initialIntersectTickTime = new ArrayList<>();


    protected Hitbox hitbox;
    protected ContentPosition position = new ContentPosition(this);

    protected final EventCollection contentEvents = new EventCollection();

    public WorldContent(World world, Position position) {
        this.world = world;
        this.generateContentCode();
        this.contentSetup();
        this.contentEventSetup(this.contentEvents);
        this.position.set(position);
    }

    public boolean isInWorld() {
        return Objects.nonNull(this.world);
    }
    public abstract boolean isTicking();
    public boolean doesPostTick() {
        return this.isTicking() || this.canInteract();
    }

    public void tick() {
        if (Objects.nonNull(this.animations)) this.animations.predicate();
    }

    public void postTickUpdate() {
        this.checkInterceptList();
    }

    public boolean isBigTicking() {return false;}
    public void bigTick() {}
    public abstract boolean canMove();
    public abstract boolean isRigid();
    protected abstract boolean canInteract();
    public boolean canInterfere() {
        if (!this.isRigid()) return canInteract();
        else return true;
    }

    protected abstract Hitbox generateHitbox();
    protected abstract Node generateDisplay();
    protected ContentAnimations generateAnimations() {
        return null;
    }
    protected void addToRigidityExceptions(List<Class<? extends WorldContent>> rigidityExceptions) {}
    protected int displayPriority() {
        return 0;
    }

    protected void contentSetup() {
        this.hitbox = this.generateHitbox();
        this.display = this.generateDisplay();
        this.animations = this.generateAnimations();
        this.addToRigidityExceptions(this.rigidityExceptions);
    }

    protected void contentEventSetup(EventCollection contentEvents) {
        contentEvents.createEvent("onKill");
    }

    private void generateContentCode() {
        String instanceTag;
        if (Objects.equals(this.getClass().getSimpleName() , "")) instanceTag = this.getClass().getName() + "(";
        else instanceTag = this.getClass().getSimpleName() + "(";
        int code = 1;
        while (WorldContent.ALL_CONTENT_CODE.contains(instanceTag + code + ")")) {
            code++;
        }
        this.contentCode = instanceTag + code + ")";
        ALL_CONTENT_CODE.add(this.contentCode);
    }

    protected void kill() {
        this.getOnKillEvent().fireEvent();
        this.world.killContent(this);
    }

    protected void forceKill() {
        this.world.killContent(this);
    }

    public void changeDisplay(Node newDisplay) {
        int index = this.world.getWorldPane().getChildren().indexOf(this.display);
        if (this.isInWorld()) this.getWorld().getWorldPane().getChildren().remove(this.display);
        this.display = newDisplay;
        this.getPosition().refresh();
        if (this.isInWorld()) this.getWorld().getWorldPane().getChildren().add(index, this.display);
    }

    public void changeDisplay(Image image) {
        if (this.display instanceof ImageView) {
            ((ImageView) this.display).setImage(image);
        }
        else Util.log(this.contentCode + " cannot set image since display !instance of ImageView", LogPreference.TEXTURES);
    }

    public double distanceFrom(WorldContent content) {
        return this.position.distanceFrom(content.getPosition());
    }

    public boolean isRigid(WorldContent content) {
        for (Class<? extends WorldContent> contentClass: this.rigidityExceptions) {
            if (contentClass.isInstance(content)) return !this.isRigid();
        }
        return this.isRigid();
    }

    protected void checkAllIntersect() {
        double contentSearchRadius;
        double searchRadius = 1.25*this.getMaxDistanceFromCenter();
        for (WorldContent content:this.getWorld().getInterferingContent()) {
            contentSearchRadius = 1.25*content.getMaxDistanceFromCenter();
            if (!WorldContent.equals(this,content) && (this.distanceFrom(content) <= (searchRadius+contentSearchRadius))) {
                if (this.hitbox.doesContain(content.getHitbox())) {
                    if (this instanceof Entity) ((Entity) this).checkRigidity(content);
                    Util.log(this.getContentCode() + " intersect with " + content.getContentCode(), LogPreference.ENTITY_INTERSECTS);
                    this.tickIntersect.add(content);
                    content.tickIntersect.add(this);
                    this.onContentIntersect(content, this.getTimeOfIntersect(content));
                    content.onContentIntersect(this, content.getTimeOfIntersect(this));
                }
            }
        }
    }

    private void checkInterceptList() {
        List<WorldContent> removeContent = new ArrayList<>();
        for (WorldContent content: this.tickIntersect) {
            if (!this.currentIntersects.contains(content)) {
                this.currentIntersects.add(content);
                this.currentIntersectTickTime.add(this.world.getTick());
                this.initialIntersectTickTime.add(this.world.getTick());
            }
        }
        for (WorldContent content: this.currentIntersects) {
            if (this.tickIntersect.contains(content)) {
                int index = this.currentIntersects.indexOf(content);
                this.currentIntersectTickTime.set(index, this.world.getTick());
            }
            else {
                removeContent.add(content);
            }
        }
        for (WorldContent content: removeContent) {
            int index = this.currentIntersects.indexOf(content);
            this.currentIntersects.remove(index);
            this.currentIntersectTickTime.remove(index);
            this.initialIntersectTickTime.remove(index);
        }
        this.tickIntersect.clear();
    }

    private double getTimeOfIntersect(WorldContent content) {
        //TODO NOT RETURNING RIGHT TIME
        if (this.currentIntersects.contains(content)) {
            int index = this.currentIntersects.indexOf(content);
            return Util.tickToTime(this.world.getTick() - this.initialIntersectTickTime.get(index));
        }
        else {
            return 0;
        }
    }

    protected void onContentIntersect(WorldContent content, double duration) {
    }

    public void setContentCode(String contentCode) {
        this.contentCode = contentCode;
    }

    public String getContentCode() {
        return contentCode;
    }

    public World getWorld() {
        return world;
    }

    public Node getDisplay() {
        return display;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public ContentPosition getPosition() {
        return position;
    }

    public ContentAnimations getAnimations() {
        return animations;
    }

    public double getAvgHeight() {
        if (this.hitbox instanceof CircleHitbox) return 2*((CircleHitbox) this.hitbox).getRadius();
        else if (this.hitbox instanceof RectHitbox) return ((RectHitbox) this.hitbox).getHeight();
        else {
            Util.log(this.getContentCode() + ": getAvgHeight() = 0", this.isInWorld());
            return 0;
        }
    }

    public double getAvgWidth() {
        if (this.hitbox instanceof CircleHitbox) return 2*((CircleHitbox) this.hitbox).getRadius();
        else if (this.hitbox instanceof RectHitbox) return ((RectHitbox) this.hitbox).getWidth();
        else {
            Util.log(this.getContentCode() + ": getAvgWidth() = 0", this.isInWorld());
            return 0;
        }
    }

    public double getMaxDistanceFromCenter() {
        return Math.max(this.getAvgHeight(), this.getAvgWidth());
    }

    public EventCollection getContentEvents() {
        return contentEvents;
    }

    public ExecutableEvent getOnKillEvent() {
        return this.contentEvents.getEvent("onKill");
    }
}
