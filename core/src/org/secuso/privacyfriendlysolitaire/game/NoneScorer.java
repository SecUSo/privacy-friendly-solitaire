package org.secuso.privacyfriendlysolitaire.game;

/**
 * @author M. Saracoglu
 *         Empty scorer, if the user does not want to count his points
 */

public class NoneScorer extends Scorer {

    NoneScorer() {
        setScore(0);
    }

    @Override
    public void update(SolitaireGame game) {

    }
}

