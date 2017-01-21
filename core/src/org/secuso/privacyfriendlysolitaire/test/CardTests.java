package org.secuso.privacyfriendlysolitaire.test;


import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author M. Fischer
 */

public class CardTests {

    @Test
    public void cloneTest() {
        Card card1 = new Card(Rank.ACE, Suit.SPADES);
        Card card2 = card1.clone();
        assertEquals(card1.getRank(), card2.getRank());
        assertEquals(card1.getSuit(), card2.getSuit());
        card1.setRank(Rank.QUEEN);
        card2.setSuit(Suit.HEARTS);
        assertNotEquals(card1.getRank(), card2.getRank());
        assertNotEquals(card1.getSuit(), card2.getSuit());
    }
}
