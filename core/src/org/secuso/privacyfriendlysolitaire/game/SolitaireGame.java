package org.secuso.privacyfriendlysolitaire.game;


import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Tableau;


import java.util.ArrayList;

/**
 * @author: I. Dix
 * <p>
 * represents the solitaire game (its current state and all actions to invoke in order to do an action)
 */

public class SolitaireGame {
    private DeckWaste deckAndWaste;
    private ArrayList<Foundation> foundations;
    private ArrayList<Tableau> tableaus;

    public SolitaireGame(DeckWaste initialDeck, ArrayList<Foundation> initialFoundations,
                         ArrayList<Tableau> initialTableaus) {
        deckAndWaste = initialDeck;
        foundations = initialFoundations;
        tableaus = initialTableaus;
    }


    public DeckWaste getDeckWaste() {
        return deckAndWaste;
    }

    public Foundation getFoundationAtPos(int n) {
        return foundations.get(n);
    }

    public Tableau getTableauAtPos(int n) {
        return tableaus.get(n);
    }

    public ArrayList<Tableau> getTableaus() {
        return tableaus;
    }


}
