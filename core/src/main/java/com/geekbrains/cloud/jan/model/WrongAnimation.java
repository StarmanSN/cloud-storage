package com.geekbrains.cloud.jan.model;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class WrongAnimation {

    private TranslateTransition tt;

    public WrongAnimation(Node node) {
        tt = new TranslateTransition(Duration.millis(70), node);
        tt.setFromX(0f);
        tt.setByX(10f);
        tt.setCycleCount(3);
        tt.setAutoReverse(true);
    }

    public void playAnimation() {
        tt.playFromStart();
    }
}
