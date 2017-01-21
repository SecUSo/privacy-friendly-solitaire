package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Observable;

/**
 * @author: M. Fischer
 */

public class StandardScorer extends Scorer {

    public StandardScorer() {
        setScore(0);
    }

    @Override
    public void update(Observable observable, Object o) {
        setScore(0);
        setMoves(((SolitaireGame) observable).getMoves());
        for (Move m : getMoves()) {
            if (m.getAction1().getGameObject() == GameObject.WASTE) {
                if (m.getAction2().getGameObject() == GameObject.TABLEAU) {
                    addScore(5);
                } else if (m.getAction2().getGameObject() == GameObject.FOUNDATION) {
                    addScore(10);
                }
            } else if (m.getAction1().getGameObject() == GameObject.TABLEAU) {
                if (m.getAction2().getGameObject() == GameObject.FOUNDATION) {
                    addScore(10);
                }
            } else if (m.getAction1().getGameObject() == GameObject.FOUNDATION) {
                if (m.getAction2().getGameObject() == GameObject.TABLEAU) {
                    addScore(-15);
                }
            } else if (((SolitaireGame) observable).getDeckWaste().getNumTurnOver() == 1) {
                if (m.getAction1().getGameObject() == GameObject.DECK && m.getAction2().getGameObject() == GameObject.DECK) {
                    addScore(-100);
                }
            }
        }
        addScore(((SolitaireGame) observable).getTurnedOverTableau() * 5);
        if (getScore() < 0) {
            setScore(0);
        }
    }
}
