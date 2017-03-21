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

import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

/**
 * @author M. Fischer
 *         <p>
 *         The standard scorer starts with 0 points and gives the following points:
 *         - Waste->Tab: 5
 *         - Waste->Found: 10
 *         - Tab->Found: 10
 *         - Found->Tab: -15
 *         - Resetting the Deck: -100
 *         <p>
 *         The score can never be below 0
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
                if (getScore() < 0) {
                    setScore(0);
                }
            }
            addScore(game.getTurnedOverTableau() * 5);
            notifyListener();
        }
    }
}
