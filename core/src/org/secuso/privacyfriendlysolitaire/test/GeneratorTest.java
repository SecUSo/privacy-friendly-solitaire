package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.game.GeneratorSolitaireInstance;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
        assertEquals(allCards.size(), 52);

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
            SolitaireGame instance = GeneratorSolitaireInstance.generateInstance(MODE_ONE_CARD_DEALT);

            Vector<Card> allCards = new Vector(NR_CARDS);

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
        for (int j = 0; j < 1000; j++) {
            int mode;
            if (j % 2 == 0) {
                mode = MODE_ONE_CARD_DEALT;
            } else {
                mode = MODE_THREE_CARDS_DEALT;
            }

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
            SolitaireGame instance = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(mode);

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
        // build random instance than swap cards until it is unplayable
//        SolitaireGame instance = GeneratorSolitaireInstance.generateInstance(mode);
//
//        Vector<Card> deck = instance.getDeckWaste().getDeck();
//        ArrayList<Tableau> tableaus = instance.getTableaus();
//
//        for (int i = 0; i < MAX_NR_IN_DECK; i += mode) {
//            Card c = deck.get(i);
//
//            if (c.getRank() == Rank.ACE) {
//
//            }
//        }


        HashSet<Card> allCards = GeneratorSolitaireInstance.generateAllCards();

        // at least 21 cards are hidden in the tableaus, if mode=MODE_THREE_CARDS_DEALT,
        // then an additional 2/3 * 24 are unplayable --> in total: 21+16=37
        ArrayList<Card> initiallyUnplayableCards, initiallyPlayableCards;
        if (mode == MODE_ONE_CARD_DEALT) {
            initiallyUnplayableCards = new ArrayList<Card>(21);
            initiallyPlayableCards = new ArrayList<Card>(52 - 21);
        } else {
            initiallyUnplayableCards = new ArrayList<Card>(37);
            initiallyPlayableCards = new ArrayList<Card>(52 - 37);
        }

        // arrange cards in correct list
        boolean stableInstance = false;     // stable meaning that the
        while (!stableInstance) {
            Iterator iter = allCards.iterator();
            while (iter.hasNext()) {
                Card c = (Card) iter.next();

                if (c.getRank() == Rank.ACE) {
                    initiallyUnplayableCards.add(c);
                } else {
                    // go through all playable cards already placed and check whether adding this card
                    // to the playables would add a possible move (undesired, since we want no moves)
                    for (int i = 0; i < initiallyPlayableCards.size(); i++) {
                        // move is possible if the rank is predecessor or successor to the other
                        // and the color is reverse
                        Card otherCard = initiallyPlayableCards.get(i);

                        if (c.getRank().isPredecessor(otherCard.getRank()) ||
                                c.getRank().isSuccessor(otherCard.getRank()) &&
                                        c.getColor() != otherCard.getColor()) {
                            initiallyUnplayableCards.add(c);
                            break;
                        }
                    }
                    initiallyPlayableCards.add(c);
                }
            }

            // check if it is a stable instance
            if (mode == MODE_ONE_CARD_DEALT) {
                stableInstance = (initiallyUnplayableCards.size() == 21 &&
                        initiallyPlayableCards.size() == (52 - 21));
            } else {
                stableInstance = (initiallyUnplayableCards.size() == 37 &&
                        initiallyPlayableCards.size() == (52 - 37));
            }
        }

        // build instance from




        return null;
    }


}
