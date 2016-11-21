package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Card;

import java.util.ArrayList;

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
        SolitaireGame instance = new SolitaireGame();

        // TODO: generate instance

        // check for playability
        boolean playable = false;
        while (!playable) {
            playable = isInstancePlayable(instance, mode);
        }

        return instance;
    }


    /**
     * an instance is unplayable if the following conditions hold
     * <ul>
     * <li>No aces are in the initial playable cards</li>
     * <li>None of the seven playable cards in the tableaus can be moved to a different tableau</li>
     * <li>None of the 8/24 playable cards in the deck can be moved to any of the seven tableaus</li>
     * </ul>
     *
     * @param instance
     * @return
     */
    private static boolean isInstancePlayable(SolitaireGame instance, int mode) {
        // playable cards in tableaus = 7
        // playable cards in deck (mode=MODE_THREE_CARDS_DEALT) = 8 or (MODE_ONE_CARD_DEALT) = 24
        int nrOfAces = 0;
        ArrayList<Card> playableCardsTableaus = new ArrayList(7);
        ArrayList<Card> playableCardsDeck = new ArrayList(24/mode);




        return false;
    }


    // maybe later
//    private boolean isInstanceWinnable(SolitaireGame instance){
//
//        return false;
//    }
}
