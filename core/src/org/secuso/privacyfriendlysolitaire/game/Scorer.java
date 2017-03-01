package org.secuso.privacyfriendlysolitaire.game;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
