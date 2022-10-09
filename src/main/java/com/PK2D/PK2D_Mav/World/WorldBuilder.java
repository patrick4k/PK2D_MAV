package com.PK2D.PK2D_Mav.World;

import com.PK2D.PK2D_Mav.Content.Entity.Entity;
import com.PK2D.PK2D_Mav.Content.Scenery.Scenery;
import com.PK2D.PK2D_Mav.ExecutableEvent.EventCollection;
import com.PK2D.PK2D_Mav.ExecutableEvent.GameEvent;
import com.PK2D.PK2D_Mav.Input.Input;
import com.PK2D.PK2D_Mav.Input.Keybinds;
import com.PK2D.PK2D_Mav.Phys.Position.ContentPosition;
import com.PK2D.PK2D_Mav.Phys.Position.Position;
import com.PK2D.PK2D_Mav.Phys.Vector;
import com.PK2D.PK2D_Mav.Util.LogPreference;
import com.PK2D.PK2D_Mav.Util.Util;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class WorldBuilder extends World {
    private final double quickConnectButtonSize = 7.5;
    private final List<Class<? extends WorldContent>> contentClasses = new ArrayList<>();
    private boolean isShowingQCButtons;
    private int contentTypeSelectIdx;
    private Node selectHLight;
    private ContentPosition selectHLightPosition;
    protected interactType interactionMode = interactType.SELECT;
    protected interactType lastInteractionMode;
    protected enum interactType {
        ADD, SELECT, ERASE
    }
    protected Cursor addCursor = Cursor.HAND;
    protected Cursor selectCursor = Cursor.DEFAULT;
    protected Cursor eraseCursor = Cursor.CROSSHAIR;

    public WorldBuilder(Pane worldPane, Pane backgroundHUD, Pane frontHUD) {
        super(worldPane, backgroundHUD, frontHUD);
        this.worldTimeline.stop();
        this.addClassesToList(contentClasses);
        this.getOnStartEvent().addListener(this::pausePlayTimeline);
    }

    protected abstract void addClassesToList(List<Class<? extends WorldContent>> contentClasses);

    @Override
    protected void worldSetUp() {
        try {
            WorldBundle importBundle = WorldBuildZIP.importBuild(this, this.getClass());
            importBundle.forAllContent(this::generateQCButtons);
            this.add(importBundle);
        }catch (Exception e) {
            Util.log("ERROR LOADING PREVIOUS BUILD");
        }
    }

    @Override
    protected void onWorldStart() {
        super.onWorldStart();
    }

    @Override
    protected void worldEventSetup(EventCollection worldEvents) {
        worldEvents.createEvent("showQCButtons").addListener(() -> this.isShowingQCButtons = true);
        worldEvents.createEvent("hideQCButtons").addListener(() -> this.isShowingQCButtons = false);
        super.worldEventSetup(worldEvents);
    }

    @Override
    protected void worldTick() {
        super.worldTick();
        //if (this.isShowingQCButtons) this.redoQuickConnectButtons();
    }

    @Override
    protected void worldBigTick() {
        super.worldBigTick();
    }

    @Override
    protected void setKeyBinds() {
        super.setKeyBinds();
        Input.addOnKeyPressed(Keybinds.KILL_ALL_IN_WORLD, event -> {
            this.killAllContent();
        });

        // Modified Selected content
        Input.addOnKeyPressed(KeyCode.PERIOD, event -> {
            if (!(this.contentTypeSelectIdx + 1 >= contentClasses.size())) this.contentTypeSelectIdx++;
        });
        Input.addOnKeyPressed(KeyCode.COMMA, event -> {
            if (!(this.contentTypeSelectIdx - 1 < 0)) this.contentTypeSelectIdx--;
        });

        // Click to add new content
        Input.addOnMouseClick(event -> {
            switch (interactionMode) {
                case ADD: {
                    this.addModeClick(event);
                    break;
                }
                case ERASE: {
                    this.eraseModeClick(event);
                    break;
                }
                case SELECT: {
                    this.selectModeClick(event);
                    break;
                }
            }
        });

        // Undo
        Input.addOnKeyPressed(Input.keyBind(Keybinds.cmdCtrl(), KeyCode.Z), event -> {
            if (this.worldContents.size() > 0) this.worldContents.get(this.worldContents.size()-1).kill();
        });

        // Show quick connect buttons
        Input.addOnKeyPressed(KeyCode.TAB, event -> {
            if (!this.isShowingQCButtons) this.worldEvents.getEvent("showQCButtons").fireEvent();
            else this.worldEvents.getEvent("hideQCButtons").fireEvent();
        });

        // Erase mode
        Input.addOnKeyPressed(KeyCode.E, event -> {
            if (interactionMode == interactType.ERASE) this.setInteractionMode(interactType.SELECT);
            else this.setInteractionMode(interactType.ERASE);
        });

        // Add mode
        Input.addOnKeyPressed(KeyCode.SHIFT, event -> {
            if (interactionMode == interactType.SELECT) this.setInteractionMode(interactType.ADD);
        });
        Input.addOnKeyReleased(KeyCode.SHIFT, event -> {
            if (interactionMode == interactType.ADD) this.setInteractionMode(interactType.SELECT);
        });

        Input.addOnMouseDrag(((event, dragStartPos, dragEndPos) -> {
        }));

        Input.addOnKeyPressed(Input.keyBind(Keybinds.cmdCtrl(), KeyCode.S), event -> {
            WorldBuildZIP.exportBuild(this);
        });

        // Class inspect mode


        // Add save (Command + S) keybind
    }



    protected void setInteractionMode(interactType interactType) {
        this.lastInteractionMode = interactionMode;
        this.interactionMode = interactType;
        switch (interactionMode) {
            case ADD: {
                Input.SCENE.setCursor(this.addCursor);
                break;
            }
            case SELECT: {
                Input.SCENE.setCursor(this.selectCursor);
                break;
            }
            case ERASE: {
                Input.SCENE.setCursor(this.eraseCursor);
                break;
            }
        }
    }

    protected WorldContent generateContent(int contentNum, Position position) {
        for (Constructor<?> constructor: contentClasses.get(contentNum).getConstructors()) {
            try {
                return (WorldContent) constructor.newInstance(this, position);
            } catch (IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Util.log("Constructor mismatched", LogPreference.NULL_CATCH);
            }
        }
        return null;
    }

    protected void selectModeClick(MouseEvent event) {
        this.worldPane.getChildren().remove(this.selectHLight);
        final WorldContent contentSelected = this.getTopContentAtPos(this.mouseEventToWorldPos(event));
        if (Objects.isNull(contentSelected)) {
            return;
        }
        this.selectHLight = new Rectangle(contentSelected.getAvgWidth(), contentSelected.getAvgHeight());
        ((Rectangle) this.selectHLight).setFill(Color.TRANSPARENT);
        ((Rectangle) this.selectHLight).setStroke(new Color(0.37254903, 0.61960787, 0.627451, 0.5));
        ((Rectangle) this.selectHLight).setStrokeWidth(Math.min(contentSelected.getAvgWidth(), contentSelected.getAvgHeight())/25);
        ((Rectangle) this.selectHLight).setStrokeType(StrokeType.OUTSIDE);
        ((Rectangle) this.selectHLight).setStrokeDashOffset(1);
        this.selectHLightPosition = new ContentPosition(this.selectHLight);
        this.selectHLightPosition.set(contentSelected.getPosition());
        contentSelected.getOnKillEvent().addListener(() -> this.worldPane.getChildren().remove(this.selectHLight));
        if (contentSelected instanceof Entity) {
            ((Entity) contentSelected).addOnEntityMove(() -> this.selectHLightPosition.set(contentSelected.getPosition()));
        }
        this.worldPane.getChildren().add(this.selectHLight);
    }

    protected void addModeClick(MouseEvent event) {
        if (contentClasses.size() == 0) {
            Util.log("No Content Loaded", LogPreference.NULL_CATCH);
            return;
        }
        final Position clickPos = this.mouseEventToWorldPos(event);
        final WorldContent generatedContent = generateContent(this.contentTypeSelectIdx, clickPos);
        if (isShowingQCButtons) {
            for (WorldContent content: this.worldContents) {
                if ((content instanceof Scenery) && (generatedContent instanceof Scenery))  {
                    for (Position position: ((Scenery) content).generateFourMidpoints()) {
                        if (position.distanceFrom(clickPos) < this.quickConnectButtonSize) {
                            final Vector sideOfScene = new Vector(content.getPosition(), position);
                            if (sideOfScene.getAbsX() > sideOfScene.getAbsY()) {
                                if (sideOfScene.getX() > 0) ((Scenery) generatedContent).setRightOf(((Scenery) content));
                                else ((Scenery) generatedContent).setLeftOf(((Scenery) content));
                            }
                            else {
                                if (sideOfScene.getY() > 0) ((Scenery) generatedContent).setTopOf(((Scenery) content));
                                else ((Scenery) generatedContent).setBottomOf(((Scenery) content));
                            }
                        }
                    }
                }
            }
        }
        this.generateQCButtons(generatedContent);
        this.add(generatedContent);
    }

    protected void eraseModeClick(MouseEvent event) {
        WorldContent contentToRemove = this.getTopContentAtPos(this.mouseEventToWorldPos(event));
        if (Objects.nonNull(contentToRemove)) {
            contentToRemove.kill();
        }
    }

    private void generateQCButtons(WorldContent content) {
        if (content instanceof Scenery) {
            for (Position position: (((Scenery) content)).generateFourMidpoints()) {
                position.setNodeToPos(getNewQCButton(((Scenery) content)));
            }
        }
    }

    private Node getNewQCButton(Scenery scenery) {
        Circle circle =  new Circle(this.quickConnectButtonSize);
        circle.setFill(Color.MEDIUMPURPLE);
        circle.setVisible(this.isShowingQCButtons);
        this.worldPane.getChildren().add(circle);
        GameEvent showEvent = () -> {
            circle.setVisible(true);
        };
        GameEvent hideEvent = () -> {
            circle.setVisible(false);
        };
        scenery.getOnKillEvent().addListener(() -> {
            this.worldPane.getChildren().remove(circle);
            this.worldEvents.getEvent("showQCButtons").removeListener(showEvent);
            this.worldEvents.getEvent("hideQCButtons").removeListener(hideEvent);
        });
        this.worldEvents.getEvent("showQCButtons").addListener(showEvent);
        this.worldEvents.getEvent("hideQCButtons").addListener(hideEvent);
        return circle;
    }
}
