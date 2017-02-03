package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 * @author M. Fischer
 */

public class FoundationTests {

    private Foundation f1;

    @Test
    public void addCardTests() {
        f1 = new Foundation();
        assertNull("suit of new foundation should be null", f1.getSuit());
        assertFalse("adding a Card that is not an Ace should return false", f1.addCard(new Card(Rank.FOUR, Suit.HEARTS)));
        assertFalse("adding a Card that is not an Ace should return false", f1.addCard(new Card(Rank.KING, Suit.SPADES)));
        assertFalse("adding a Card that is not an Ace should return false", f1.addCard(new Card(Rank.SEVEN, Suit.DIAMONDS)));
        assertTrue("adding an ace to an empty foundation should return true", f1.addCard(new Card(Rank.ACE, Suit.SPADES)));
        assertTrue("suit of foundation should now be SPADES", f1.getSuit() == Suit.SPADES);
        assertTrue("f1 should now contain the ACE of SPADES", f1.getCards().firstElement().getRank() == Rank.ACE);
        assertTrue("f1 should now contain the ACE of SPADES", f1.getCards().firstElement().getSuit() == Suit.SPADES);
        assertFalse("adding TWO of HEARTS should return false", f1.addCard(new Card(Rank.TWO, Suit.HEARTS)));
        assertFalse("adding the THREE of SPADES should return false", f1.addCard(new Card(Rank.THREE, Suit.SPADES)));
        assertTrue("adding the TWO of SPADES should return true", f1.addCard(new Card(Rank.TWO, Suit.SPADES)));
        assertTrue("TWO of SPADES should have been added", f1.getCards().get(1).getRank() == Rank.TWO);
        assertTrue("TWO of SPADES should have been added", f1.getCards().get(1).getSuit() == Suit.SPADES);
    }
}
