package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

/**
 * @author M. Fischer
 */

class VegasScorer extends Scorer {

    VegasScorer() {
        setScore(-52);
    }

    @Override
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            setScore(-52);
            for (int i = 0; i < game.getMovePointer() + 1; i++) {
                Move m = game.getMoves().get(i);
                if (m.getAction1().getGameObject() == GameObject.FOUNDATION) {
                    addScore(-5);
                }
                if (m.getAction2() != null) {
                    if (m.getAction2().getGameObject() == GameObject.FOUNDATION) {
                        addScore(5);
                    }
                }
            }
            notifyListener();
        }
    }
}
