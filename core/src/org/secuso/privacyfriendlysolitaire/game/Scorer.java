package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.ScoreListener;
import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Observer;
import java.util.Vector;

/**
 * @author: M. Fischer
 */

public abstract class Scorer implements Observer {

    private int score;

    private ScoreListener listener;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ScoreListener getListener() {
        return listener;
    }

    /**
     * @param x the value that is added to the current score, negative values result in subtraction
     */
    public void addScore(int x) {
        this.score += x;
    }

    public void registerScoreListener(ScoreListener scoreListener) {
        this.listener = scoreListener;
    }

    public void notifyListener() {
        if (getListener() != null) {
            getListener().score(getScore());
        }
    }

}
