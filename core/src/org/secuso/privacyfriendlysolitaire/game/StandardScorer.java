package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

/**
 * @author M. Fischer
 */

class StandardScorer extends Scorer {

    StandardScorer() {
        setScore(0);
    }

    @Override
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            setScore(0);
            for (int i = 0; i < game.getMovePointer() + 1; i++) {
                Move m = game.getMoves().get(i);
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
                } else if (game.getDeckWaste().getNumTurnOver() == 1) {
                    if (m.getAction2() != null) {
                        if (m.getAction1().getGameObject() == GameObject.DECK && m.getAction2().getGameObject() == GameObject.DECK) {
                            addScore(-100);
                        }
                    }
                }
            }
            addScore(game.getTurnedOverTableau() * 5);
            if (getScore() < 0) {
                setScore(0);
            }
            notifyListener();
        }
    }
}
