package org.secuso.privacyfriendlysolitaire.game;

import static org.secuso.privacyfriendlysolitaire.game.Constants.*;

/**
 * @author: I. Dix
 * <p>
 * generates a playable (but not necessarily winnable) instance of solitaire
 * <p>
 * The rules for playability are given here: <url>http://www.techuser.net/klondikeprob.html</url>
 */

public class GeneratorSolitaireInstance {

    public static SolitaireGame buildPlayableSolitaireInstance(int mode) {
        if (mode != 1 && mode != 0) {
            throw new IllegalArgumentException("mode should be 0 or 1 (see Constants for reference");
        }


        return null;
    }


    private boolean isInstancePlayable(SolitaireGame instance) {

        return false;
    }


    // maybe later
//    private boolean isInstanceWinnable(SolitaireGame instance){
//
//        return false;
//    }
}
