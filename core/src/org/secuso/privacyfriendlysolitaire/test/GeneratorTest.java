package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorUtils;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.game.Constants.*;

/**
 * @author: I. Dix
 */

public class GeneratorTest {

    @Test
    public void generateAllCardsTest() {
        HashSet<Card> allCards = GeneratorSolitaireInstance.generateAllCards();

        // check that there are 52 cards
        assertEquals(allCards.size(), NR_CARDS);

        // and not card is equal to another (meaning that we must have all 52 different cards)
        for (Card c : allCards) {
            int nrOfEquals = 0;
            for (Card anotherCard : allCards) {
                if (c.getSuit() == anotherCard.getSuit() &&
                        c.getRank() == anotherCard.getRank()) {
                    nrOfEquals++;
                }
            }
            assertEquals(nrOfEquals, 1);
        }
    }

    @Test
    public void generateInstanceTest() {
        // since randomness is involved, we test 100 instances
        for (int j = 0; j < 1000; j++) {
            SolitaireGame instance = GeneratorSolitaireInstance.generateInstance(MODE_ONE_CARD_DEALT, MODE_STANDARD);

            Vector<Card> allCards = new Vector<Card>(NR_CARDS);

            // assert all cards are in the deck and none in the waste
            DeckWaste d = instance.getDeckWaste();
            assertEquals(d.getDeck().size(), MAX_NR_IN_DECK);
            assertEquals(d.getWaste().size(), 0);
            allCards.addAll(d.getDeck());

            // assert all foundations are empty
            for (int i = 0; i < NR_OF_FOUNDATIONS; i++) {
                Foundation f = instance.getFoundationAtPos(i);
                assertEquals(f.getCards().size(), 0);
                assertNull(f.getSuit());
            }

            // assert all tableaus have the correct number of cards face-up and face-down
            for (int i = 0; i < NR_OF_TABLEAUS; i++) {
                Tableau t = instance.getTableauAtPos(i);
                assertEquals(t.getFaceUp().size(), 1);
                assertEquals(t.getFaceDown().size(), i);    // for 0th row are no face-down, for 1st 1, ...
            }
        }
    }

    @Test
    public void isInstancePlayableTest() {
        for (int j = 0; j < 100; j++) {
            // unplayability is only possible for MODE_THREE_CARDS_DEALT
            // (otherwise: too many playable cards to make it unplayable)
            int mode = MODE_THREE_CARDS_DEALT;

            SolitaireGame unplayableInstance = buildUnplayableInstance(mode);

            assertFalse(GeneratorSolitaireInstance.isInstancePlayable(unplayableInstance, mode));
        }
    }

    @Test
    public void buildPlayableSolitaireInstanceTest() {
        for (int j = 0; j < 1000; j++) {
            int mode;
            if (j % 2 == 0) {
                mode = MODE_ONE_CARD_DEALT;
            } else {
                mode = MODE_THREE_CARDS_DEALT;
            }
            SolitaireGame instance = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(mode, MODE_STANDARD);

            assertTrue(GeneratorSolitaireInstance.isInstancePlayable(instance, mode));
        }
    }


    /**
     * for unplayability the following conditions must hold:
     * <ul>
     * <li>No aces are in the initial playable cards</li>
     * <li>None of the seven playable cards in the tableaus can be moved to a different tableau</li>
     * <li>None of the 8/24 playable cards in the deck can be moved to any of the seven tableaus</li>
     * </ul>
     */
    private SolitaireGame buildUnplayableInstance(int mode) {
        HashSet<Card> allCards = GeneratorSolitaireInstance.generateAllCards();

        // at least 21 cards are hidden in the tableaus,
        // then an additional 2/3 * 24 are unplayable --> in total: 21+16=37
        ArrayList<Card> unplayableCards = new ArrayList<Card>(37);
        ArrayList<Card> playableCards = new ArrayList<Card>(NR_CARDS - 37);

        // arrange cards in correct list
        boolean stableInstance = false;     // stable meaning that the
        while (!stableInstance) {
            unplayableCards.clear();
            playableCards.clear();

            for (Card currentCard : allCards) {
                // get new card same as card in set, because sets don't like concurrent changes :P
                Card c = new Card(currentCard.getRank(), currentCard.getSuit());

                if (c.getRank() == Rank.ACE) {
                    unplayableCards.add(c);
                } else {
                    boolean playable = true;

                    // go through all playable cards already placed and check whether adding this card
                    // to the playables would add a possible move (undesired, since we want no moves)
                    for (int i = 0; i < playableCards.size(); i++) {
                        // move is possible if the rank is predecessor or successor to the other
                        // and the color is reverse
                        Card otherCard = playableCards.get(i);

                        if ((c.getRank().isPredecessor(otherCard.getRank()) ||
                                c.getRank().isSuccessor(otherCard.getRank())) &&
                                c.getColor() != otherCard.getColor()) {
                            unplayableCards.add(c);
                            // set playable false so card will not be added to playable-list as well
                            playable = false;
                            break;
                        }
                    }
                    if (playable) {
                        playableCards.add(c);
                    }
                }
            }

            // check if it is a stable instance
            int desiredNrUnplayable;
            int desiredNrPlayable;

            desiredNrUnplayable = 37;
            desiredNrPlayable = 52 - 37;        // 15

            // if this is not yet the case, but we have too many playable cards,
            // we can simply move the overlap to the unplayable ones
            if (!stableInstance && playableCards.size() > desiredNrPlayable) {
                for (int i = desiredNrPlayable; i < playableCards.size(); i++) {
                    Card c = playableCards.get(i);
                    unplayableCards.add(c);
                }
                playableCards.removeAll(unplayableCards);
            }

            stableInstance = (unplayableCards.size() == desiredNrUnplayable
                    && playableCards.size() == desiredNrPlayable);
        }

        // build instance from lists
        Vector<Card> deck = new Vector<Card>(MAX_NR_IN_DECK);
        deck.setSize(MAX_NR_IN_DECK);
        HashMap<Integer, Vector<Card>> tableaus = new HashMap<Integer, Vector<Card>>(7);
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            int j = i + 1;
            tableaus.put(i, new Vector<Card>(j));
        }

        // ------------- distribute unplayable cards -------------
        for (int i = 0; i < unplayableCards.size(); i++) {
            Card unplayableCardToBeAdded = unplayableCards.get(i);

            // face-down cards of tableaus
            if (i < 21) {
                // shifted one to the right for face-down cards
                int indexOfTableau = GeneratorUtils.mapIndexToTableau(i) + 1;
                Vector<Card> currentTableau = tableaus.get(indexOfTableau);
                currentTableau.add(unplayableCardToBeAdded);
                tableaus.put(indexOfTableau, currentTableau);
            }
            // unplayable cards in deck
            else {
                for (int j = 0; j < MAX_NR_IN_DECK; j++) {
                    // for mode=3, fill 0,1, not 2, 3,4, not 5, ...
                    if ((j + 1) % mode != 0) {
                        deck.add(j, unplayableCardToBeAdded);
                    }
                }
            }
        }

        // ------------- distribute playable cards -------------
        for (int i = 0; i < playableCards.size(); i++) {
            Card playableCardToBeAdded = playableCards.get(i);

            // face-up cards of tableaus
            if (i < 7) {
                Vector<Card> currentTableau = tableaus.get(i);
                currentTableau.add(playableCardToBeAdded);
                tableaus.put(i, currentTableau);
            }
            // playable cards in deck
            else {
                for (int j = 0; j < MAX_NR_IN_DECK; j++) {
                    // for mode=3, fill not 0,not 1, 2, not 3,not 4, 5, ...
                    if ((j + 1) % mode == 0) {
                        deck.add(j, playableCardToBeAdded);
                    }
                }
            }
        }

        return GeneratorUtils.constructInstanceFromCardLists(mode, false, deck, tableaus);
    }


}
