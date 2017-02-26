package org.secuso.privacyfriendlysolitaire;

import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;

/**
 * @author M. Fischer
 *  Custom Listener for entities observing the SolitaireGame
 */

public interface GameListener {
    void update(SolitaireGame game);
}
