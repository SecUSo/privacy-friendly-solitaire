package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

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
        if (mode != MODE_ONE_CARD_DEALT && mode != MODE_THREE_CARDS_DEALT) {
            throw new IllegalArgumentException("mode should be 0 or 1 (see Constants for reference");
        }

        SolitaireGame instance = generateInstance(mode);

        // check for playability
        boolean playable = false;
        while (!playable) {
            instance = generateInstance(mode);
            playable = isInstancePlayable(instance, mode);
        }

        return instance;
    }


    /**
     * @return a random solitaire instance
     */
    public static SolitaireGame generateInstance(int mode) {
        Set<Card> allCards = generateAllCards();

        // bring generated cards into random order
        List<Card> scrambledCardList = new ArrayList();
        Iterator iter = allCards.iterator();
        while (iter.hasNext()) {
            scrambledCardList.add((Card) iter.next());
        }

        // generate data container to store the deck and tableaus
        Vector<Card> deck = new Vector<Card>(24);
        HashMap<Integer, Vector<Card>> tableaus = new HashMap(7);
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            int j = i + 1;
            tableaus.put(i, new Vector<Card>(j));
        }

        // 28 cards in tableaus, 24 in deck
        for (int i = 0; i < scrambledCardList.size(); i++) {
            Card c = scrambledCardList.get(i);

            // fill tableaus
            if (i < 28) {
                int indexOfTableau = mapIndexToTableau(i);
                Vector<Card> currentTableau = tableaus.get(indexOfTableau);
                currentTableau.add(c);
                tableaus.put(indexOfTableau, currentTableau);
            }
            // fill deck
            else {
                deck.add(c);
            }
        }

        return constructInstanceFromCardLists(mode, deck, tableaus);
    }

    /**
     * @param i index of a card in the initial scrambled list
     * @return the correct tableau it should be added to
     * @throws IllegalArgumentException if i>27, because this card should not be added into a tableau
     */
    private static int mapIndexToTableau(int i) {
        int firstUpmost = 0, secondUpmost = 2, thirdUpmost = 5, fourthUpmost = 9,
                fifthUpmost = 14, sixthUpmost = 20, seventhUpmost = 27;
        if (i <= firstUpmost) {
            return 0;
        } else if (i <= secondUpmost) {
            return 1;
        } else if (i <= thirdUpmost) {
            return 2;
        } else if (i <= fourthUpmost) {
            return 3;
        } else if (i <= fifthUpmost) {
            return 4;
        } else if (i <= sixthUpmost) {
            return 5;
        } else if (i <= seventhUpmost) {
            return 6;
        } else {
            throw new IllegalArgumentException("index for tableaus may not ");
        }
    }

    /**
     * @param mode     the mode
     * @param deck     a list of deck cards
     * @param tableaus a hashmap of int->Vector<Card> containing the tableaus
     * @return an instance generated from the given lists
     */
    private static SolitaireGame constructInstanceFromCardLists(int mode, Vector<Card> deck,
                                                                HashMap<Integer, Vector<Card>> tableaus) {
        DeckWaste d = new DeckWaste(mode);
        d.setDeck(deck);

        ArrayList<Tableau> tableauList = new ArrayList<Tableau>(NR_OF_TABLEAUS);
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            Vector<Card> t = tableaus.get(i);
            Tableau tableau = new Tableau();
            // add last card (with highest index) as face up
            tableau.addFaceUp(t.lastElement());

            // remove this card from the interim-list and add the rest as face down
            t.removeElement(t.lastElement());
            if (!t.isEmpty()) {
                tableau.setFaceDown(t);
            }

            // add to list
            tableauList.add(tableau);
        }
        ArrayList<Foundation> foundations = new ArrayList<Foundation>(NR_OF_FOUNDATIONS);
        for (int i = 0; i < NR_OF_FOUNDATIONS; i++) {
            foundations.add(new Foundation());
        }

        return new SolitaireGame(d, foundations, tableauList);
    }

    /**
     * @return a hash-map of all cards (should be 52)
     */
    public static HashSet<Card> generateAllCards() {
        HashSet<Card> allCards = new HashSet<Card>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                allCards.add(new Card(rank, suit));
            }
        }

        return allCards;
    }


    // ---------------------------- PLAYABILITY ----------------------------

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
    public static boolean isInstancePlayable(SolitaireGame instance, int mode) {
        int nrOfAces = 0;
        int nrOfPossibleMoves = 0;

        // check for all playable cards in tableaus
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            Card c = instance.getTableauAtPos(i).getFaceUp().lastElement();

            // whether they are an ace
            if (c.getRank() == Rank.ACE) {
                nrOfAces++;
            }

            // whether the upper card can be moved to any other tableau
            for (int j = 0; j < NR_OF_TABLEAUS; j++) {
                if (i != j) {
                    nrOfPossibleMoves += isMovingPossible(c, instance.getTableauAtPos(j));
                }
            }
        }

        // check for all playable cards in deck
        for (int i = 0; i < MAX_NR_IN_DECK; i += mode) {
            Card c = instance.getDeckWaste().getDeck().get(i);

            // whether they are an ace
            if (c.getRank() == Rank.ACE) {
                nrOfAces++;
            }

            // whether the upper card can be moved to any other tableau
            for (int j = 0; j < NR_OF_TABLEAUS; j++) {
                nrOfPossibleMoves += isMovingPossible(c, instance.getTableauAtPos(j));
            }
        }

        return !(nrOfAces == 0 && nrOfPossibleMoves == 0);
    }

    /**
     * @param c the card
     * @param t the tableau
     * @return true if the card can be moved to this tableau, else false
     */
    private static int isMovingPossible(Card c, Tableau t) {
        Vector<Card> testingVector = new Vector<Card>(1);
        testingVector.add(c);

        if (t.isAddingFaceUpVectorPossible(testingVector)) {
            return 1;
        }
        return 0;
    }


    // maybe later
//    private boolean isInstanceWinnable(SolitaireGame instance){
//
//        return false;
//    }
}
