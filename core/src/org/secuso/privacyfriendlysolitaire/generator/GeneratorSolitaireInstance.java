package org.secuso.privacyfriendlysolitaire.generator;

import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Vector;

import static org.secuso.privacyfriendlysolitaire.game.Constants.*;
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
/**
 * @author I. Dix
 * <p>
 * generates a playable (but not necessarily winnable) instance of solitaire
 * <p>
 * The rules for playability are given here: <url>http://www.techuser.net/klondikeprob.html</url>
 */

public class GeneratorSolitaireInstance {

    public static SolitaireGame buildPlayableSolitaireInstance(int cardDrawMode, int scoreMode) {
        if (cardDrawMode != MODE_ONE_CARD_DEALT && cardDrawMode != MODE_THREE_CARDS_DEALT) {
            throw new IllegalArgumentException("mode should be 0 or 1 (see Constants for reference");
        }

        SolitaireGame instance = generateInstance(cardDrawMode, scoreMode);

        // check for playability
        boolean playable = false;
        while (!playable) {
            instance = generateInstance(cardDrawMode, scoreMode);
            playable = isInstancePlayable(instance, cardDrawMode);
        }

        return instance;
    }


    /**
     * @return a random solitaire instance
     */
    public static SolitaireGame generateInstance(int cardDrawMode, int scoreMode) {
        Set<Card> allCards = generateAllCards();

        // bring generated cards into random order
        List<Card> scrambledCardList = new ArrayList<Card>(allCards);
        Collections.shuffle(scrambledCardList);

        // generate data container to store the deck and tableaus
        Vector<Card> deck = new Vector<Card>(24);
        HashMap<Integer, Vector<Card>> tableaus = new HashMap<Integer, Vector<Card>>(7);
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            int j = i + 1;
            tableaus.put(i, new Vector<Card>(j));
        }

        // 28 cards in tableaus, 24 in deck
        for (int i = 0; i < scrambledCardList.size(); i++) {
            Card c = scrambledCardList.get(i);

            // fill tableaus
            if (i < 28) {
                int indexOfTableau = GeneratorUtils.mapIndexToTableau(i);
                Vector<Card> currentTableau = tableaus.get(indexOfTableau);
                currentTableau.add(c);
                tableaus.put(indexOfTableau, currentTableau);
            }
            // fill deck
            else {
                deck.add(c);
            }
        }

        boolean isVegas = scoreMode == 1;

        return GeneratorUtils.constructInstanceFromCardLists(cardDrawMode, isVegas, deck, tableaus);
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
     * @param instance the instance to be checked
     * @return whether it is playable, meaning that at least one of the conditions given above is false
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
     * @return 1 if the card can be moved to this tableau, else 0
     */
    private static int isMovingPossible(Card c, Tableau t) {
        Vector<Card> testingVector = new Vector<Card>(1);
        testingVector.add(c);

        if (t.isAddingFaceUpVectorPossible(testingVector)) {
            return 1;
        }
        return 0;
    }


    // for testing reasons
    public static SolitaireGame buildAlmostWonSolitaireInstance() {
        Vector<Card> deck = new Vector<Card>(2);
        HashMap<Integer, Vector<Card>> tableaus = new HashMap<Integer, Vector<Card>>(NR_OF_TABLEAUS);
        HashMap<Integer, Vector<Card>> foundations = new HashMap<Integer, Vector<Card>>(NR_OF_FOUNDATIONS);

        // fill all cards into the foundation
        int i = 0;
        for (Suit suit : Suit.values()) {
            Vector<Card> foundation = new Vector<Card>();
            Vector<Card> tableau = new Vector<Card>();
            for (Rank rank : Rank.values()) {
                Card c = new Card(rank, suit);

                if (rank != Rank.KING || suit != Suit.CLUBS) {
                    foundation.add(c);
                } else {
                    // except for the 4 kings
                    tableau.add(c);
                }
            }
            foundations.put(i, foundation);
            tableaus.put(i, tableau);
            i++;
        }

        for (int j = 4; j < NR_OF_TABLEAUS; j++) {
            Vector<Card> tableau = new Vector<Card>();
            tableaus.put(j, tableau);
        }

        return GeneratorUtils.constructInstanceFromCardLists(MODE_ONE_CARD_DEALT, false, deck,
                tableaus, foundations);
    }
}
