package com.PK2D.PK2D_Mav.Textures;

import com.PK2D.PK2D_Mav.Util.Util;
import com.PK2D.PK2D_Mav.World.WorldContent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Textures {

    public static ImageView getTexture(String url, double width, double height) {
        ImageView imageView = null;
        try {
            Image image = new Image(new File(url).toURI().toString());
            imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
        }catch (Exception ignored) {
            Util.log("Error getting texture @ " + url);
        }
        return imageView;
    }

    public static ImageView getTexture(File file, double width, double height) {
        ImageView imageView = new ImageView(new Image(file.getAbsolutePath()));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    public static ImageView getTexture(String url, WorldContent content) {
        return getTexture(url, content.getAvgWidth(), content.getAvgHeight());
    }

    public static ArrayList<ImageView> getFrames(String pathname, double width, double height) {
        File folder = new File(pathname);
        if (Objects.isNull(folder.list())) {
            Util.log("Animation returns 0 frames:\n" + pathname);
            return new ArrayList<>();
        }
        List<String> filenames = Arrays.asList(Objects.requireNonNull(folder.list()));
        filenames.sort(String.CASE_INSENSITIVE_ORDER);
        ArrayList<ImageView> frameList = new ArrayList<>();
        for (String filename:filenames) {
            frameList.add(getTexture(pathname + "/" + filename, width, height));
        }
        return frameList;
    }

    public static ArrayList<ImageView> reflectedFramesX(List<ImageView> frames) {
        ArrayList<ImageView> reflectedFrames = new ArrayList<>(frames.size());
        for (ImageView frame:frames) {
            ImageView imageView = new ImageView();
            imageView.setImage(frame.getImage());
            imageView.setFitWidth(frame.getFitWidth());
            imageView.setFitHeight(frame.getFitHeight());
            imageView.setScaleX(-frame.getScaleX());
            reflectedFrames.add(imageView);
        }
        return reflectedFrames;
    }

    public static ArrayList<ImageView> reflectedFramesY(List<ImageView> frames) {
        ArrayList<ImageView> reflectedFrames = new ArrayList<>(frames.size());
        for (ImageView frame:frames) {
            ImageView imageView = new ImageView();
            imageView.setImage(frame.getImage());
            imageView.setFitWidth(frame.getFitWidth());
            imageView.setFitHeight(frame.getFitHeight());
            imageView.setScaleY(-frame.getScaleY());
            reflectedFrames.add(imageView);
        }
        return reflectedFrames;
    }
}
