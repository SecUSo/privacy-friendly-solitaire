package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Deck;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Tableau;
import org.secuso.privacyfriendlysolitaire.model.Waste;

import java.util.ArrayList;

/**
 * @author: I. Dix
 *
 * represents the solitaire game (its current state and all actions to invoke to do an action)
 */

public class SolitaireGame {
    private Deck deck;
    private Waste waste;
    private ArrayList<Foundation> foundations = new ArrayList(4);
    private ArrayList<Tableau> tableaus = new ArrayList(7);


}
