package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;

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
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            setScore(-52);
            for (Move m : game.getMoves()) {
                if (m.getAction1().getGameObject() == GameObject.FOUNDATION) {
                    addScore(-5);
                }
                try {
                    if (m.getAction2().getGameObject() == GameObject.FOUNDATION) {
                        addScore(5);
                    }
                } catch (Exception e) {
                }
            }
            notifyListener();
        }
    }
}
