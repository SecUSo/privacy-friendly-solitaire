package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Observer;
import java.util.Vector;

/**
 * @author: M. Fischer
 */

public abstract class Scorer implements Observer {

    private Vector<Move> moves;

    private int score;

    public Vector<Move> getMoves() {
        return moves;
    }

    public void setMoves(Vector<Move> moves) {
        this.moves = moves;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @param x the value that is added to the current score, negative values result in subtraction
     */
    public void addScore(int x) {
        this.score += x;
    }

}
