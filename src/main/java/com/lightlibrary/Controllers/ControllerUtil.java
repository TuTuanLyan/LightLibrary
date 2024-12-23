package com.lightlibrary.Controllers;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ControllerUtil {

    /**
     * Create a Fade Out Animation with 0.3 seconds duration.
     * @param node is the node which want to have Fade animation.
     * @return FadeTransition is the animation of the node.
     */
    public static FadeTransition creatFadeOutAnimation(Node node) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    /**
     * Create a Fade Out Animation with 0.3 seconds duration.
     * @param node is the node which want to have Fade animation.
     * @param durationSeconds is the duration time measured in seconds.
     * @return FadeTransition is the animation of the node.
     */
    public static FadeTransition creatFadeOutAnimation(Node node, double durationSeconds) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(durationSeconds), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        return fadeOut;
    }

    /**
     * Create a Fade In Animation with 0.3 seconds duration.
     * @param node is the node which want to have Fade animation.
     * @return FadeTransition is the animation of the node.
     */
    public static FadeTransition creatFadeInAnimation(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        return fadeIn;
    }

    /**
     * Create a Fade In Animation with 0.3 seconds duration.
     * @param node is the node which want to have Fade animation.
     * @param durationSeconds is the duration time measured in seconds.
     * @return FadeTransition is the animation of the node.
     */
    public static FadeTransition creatFadeInAnimation(Node node, double durationSeconds) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(durationSeconds), node);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        return fadeOut;
    }

    /**
     * Create a book block contain thumbnail, title,
     * author name, isbn with a button go to view detail of this.
     * @param thumbnailUrl is a String thumbnail image link to display.
     * @param title is title String of book.
     * @param author is name of author.
     * @param ISBN is book's ISBN String.
     * @param description is book's description String.
     * @return a Pane is container of all element.
     */
    public static Pane createBookBlock(String thumbnailUrl, String title,
                                 String author, String ISBN, String description) {
        ImageView thumbnail = new ImageView();
        if (thumbnailUrl != null) {
            thumbnail.setImage(new Image(thumbnailUrl));
            thumbnail.setPreserveRatio(false);
            thumbnail.setFitHeight(160);
            thumbnail.setFitWidth(125);
            thumbnail.setLayoutX(15);
            thumbnail.setLayoutY(15);
        }


        Pane thumbnailBorder = new Pane();
        thumbnailBorder.setPrefSize(145, 180);
        thumbnailBorder.setLayoutX(5);
        thumbnailBorder.setLayoutY(5);
        thumbnailBorder.getStyleClass().add("thumbnail-result-border");

        Label titleLabel = new Label(title);
        titleLabel.setPrefSize(415, 40);
        titleLabel.setLayoutX(155);
        titleLabel.setLayoutY(15);
        titleLabel.getStyleClass().add("book-title-label");

        Label authorLabel = new Label(author);
        authorLabel.setWrapText(true);
        authorLabel.setPrefSize(125, 50);
        authorLabel.setLayoutX(15);
        authorLabel.setLayoutY(180);
        authorLabel.getStyleClass().add("book-info-label");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setPrefSize(415, 120);
        descriptionLabel.setLayoutX(155);
        descriptionLabel.setLayoutY(55);
        descriptionLabel.getStyleClass().add("book-info-label");

        Label ISBNLabel = new Label(ISBN);
        ISBNLabel.setWrapText(true);
        ISBNLabel.setPrefSize(295, 40);
        ISBNLabel.setLayoutX(155);
        ISBNLabel.setLayoutY(190);
        ISBNLabel.getStyleClass().add("book-info-label");

        Line line = new Line();
        line.setStartX(-150);
        line.setStartY(-9);
        line.setEndX(337);
        line.setEndY(-9);
        line.setLayoutX(196);
        line.setLayoutY(258);

        return new Pane(thumbnail, thumbnailBorder, titleLabel, authorLabel,
                descriptionLabel, ISBNLabel, line);
    }

    public static Pane createTopBookBlock(String thumbnailURL, String title, String author) {
        ImageView thumbnail = new ImageView(new Image(thumbnailURL));
        thumbnail.setFitHeight(140);
        thumbnail.setFitWidth(140);
        thumbnail.setLayoutX(0);
        thumbnail.setLayoutY(0);

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setPrefSize(140, 60);
        titleLabel.setLayoutX(0);
        titleLabel.setLayoutY(128);
        titleLabel.getStyleClass().add("top-book-title");

        Label authorLabel = new Label(author);
        authorLabel.setWrapText(true);
        authorLabel.setPrefSize(140, 35);
        authorLabel.setLayoutX(0);
        authorLabel.setLayoutY(175);
        authorLabel.getStyleClass().add("top-book-author");

        Pane topBookBlock = new Pane();
        topBookBlock.getStyleClass().add("top-book-block");
        topBookBlock.getChildren().addAll(thumbnail, titleLabel, authorLabel);
        topBookBlock.setPrefSize(140, 210);
        return topBookBlock;
    }
}
