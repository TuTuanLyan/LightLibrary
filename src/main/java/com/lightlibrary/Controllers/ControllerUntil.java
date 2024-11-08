package com.lightlibrary.Controllers;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class ControllerUntil {

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
}
