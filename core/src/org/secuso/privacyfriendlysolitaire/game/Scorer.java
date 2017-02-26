package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.ScoreListener;

/**
 * @author M. Fischer
 *         <p>
 *         An abstract Scorer from which all other (Standard, Vegas, NoneScorer) can extend
 */

abstract class Scorer implements GameListener {

    private int score;

    private ScoreListener listener;

    int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }

    private ScoreListener getListener() {
        return listener;
    }

    /**
     * @param x the value that is added to the current score, negative values result in subtraction
     */
    void addScore(int x) {
        this.score += x;
    }

    void registerScoreListener(ScoreListener scoreListener) {
        this.listener = scoreListener;
    }

    void notifyListener() {
        if (getListener() != null) {
            getListener().score(getScore());
        }
    }

}
