package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Observable;

/**
 * @author: M. Fischer
 */

public class VegasScorer extends Scorer {

    public VegasScorer() {
        setScore(-52);
    }

    @Override
    public void update(Observable observable, Object o) {
        setScore(-52);
        setMoves(((SolitaireGame) observable).getMoves());
        for (Move m : getMoves()) {
            if (m.getAction1().getGameObject() == GameObject.FOUNDATION) {
                addScore(-5);
            }
            if (m.getAction2().getGameObject() == GameObject.FOUNDATION) {
                addScore(5);
            }
        }
    }
}
