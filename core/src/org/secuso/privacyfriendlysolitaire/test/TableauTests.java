package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Before;
import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author M. Fischer
 */

public class TableauTests {
    private Vector<Card> clubs;
    private Vector<Card> hearts;

    @Before
    public void init() {
        clubs = new Vector<Card>();
        for (Rank r : Rank.values()) {
            clubs.add(new Card(r, Suit.CLUBS));
        }
        hearts = new Vector<Card>();
        for (Rank r : Rank.values()) {
            hearts.add(new Card(r, Suit.HEARTS));
        }
    }

    @Test
    public void turnOverTests() {
        Tableau t1 = new Tableau();
        assertFalse("turning over on empty tableau should return false", t1.turnOver());
        t1.setFaceDown(hearts);
        assertTrue("turning over should be possible now", t1.turnOver());
        assertTrue("face up card should now be KING of HEARTS", t1.getFaceUp().firstElement().getRank() == Rank.KING);
        assertTrue("face up card should now be KING of HEARTS", t1.getFaceUp().firstElement().getSuit() == Suit.HEARTS);
        assertTrue("top face down card should now be QUEEN of HEARTS", t1.getFaceDown().lastElement().getRank() == Rank.QUEEN);
        assertTrue("top face down card should now be QUEEN of HEARTS", t1.getFaceDown().lastElement().getSuit() == Suit.HEARTS);
        assertFalse("turning over should not be possible now", t1.turnOver());
    }

    @Test
    public void addFaceUpVectorTests() {
        Tableau t = new Tableau();
        Vector<Card> cv = new Vector<Card>();
        cv.add(new Card(Rank.KING, Suit.SPADES));
        assertTrue("empty tableau should be usable with a KING", t.addFaceUpVector(cv));
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getRank() == Rank.KING);
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getSuit() == Suit.SPADES);
        cv = new Vector<Card>();
        assertTrue("adding an empty vector of cards to tableau should return true", t.addFaceUpVector(cv));
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getRank() == Rank.KING);
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getSuit() == Suit.SPADES);
        cv.add(new Card(Rank.QUEEN, Suit.HEARTS));
        cv.add(new Card(Rank.JACK, Suit.CLUBS));
        assertTrue("should be able to add QUEEN of HEARTS and JACK of CLUBS to tableau", t.addFaceUpVector(cv));
        assertTrue("QUEEN of HEARTS should now be in tableau", t.getFaceUp().get(1).getRank() == Rank.QUEEN);
        assertTrue("QUEEN of HEARTS should now be in tableau", t.getFaceUp().get(1).getSuit() == Suit.HEARTS);
        assertTrue("JACK of CLUBS should now be in tableau", t.getFaceUp().get(2).getRank() == Rank.JACK);
        assertTrue("JACK of CLUBS should now be in tableau", t.getFaceUp().get(2).getSuit() == Suit.CLUBS);
        assertFalse("adding stack with KING of HEARTS should return false", t.addFaceUpVector(hearts));
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getRank() == Rank.KING);
        assertTrue("face up of tableau should now contain KING of SPADES", t.getFaceUp().firstElement().getSuit() == Suit.SPADES);
        assertTrue("QUEEN of HEARTS should now be in tableau", t.getFaceUp().get(1).getRank() == Rank.QUEEN);
        assertTrue("QUEEN of HEARTS should now be in tableau", t.getFaceUp().get(1).getSuit() == Suit.HEARTS);
        assertTrue("JACK of CLUBS should now be in tableau", t.getFaceUp().get(2).getRank() == Rank.JACK);
        assertTrue("JACK of CLUBS should now be in tableau", t.getFaceUp().get(2).getSuit() == Suit.CLUBS);
    }

    @Test
    public void removeFaceUpVectorTests() {
        Tableau t = new Tableau();
        t.setFaceUp((Vector<Card>) clubs.clone());
        assertTrue("removing with invalid index should return empty vector", t.removeFaceUpVector(-42).isEmpty());
        assertTrue("removing with invalid index should return empty vector", t.removeFaceUpVector(1337).isEmpty());
        Vector<Card> removed = t.removeFaceUpVector(0);
        for (int i = 0; i < removed.size(); ++i) {
            assertTrue("removed should be equal to clubs", removed.get(i).getRank() == clubs.get(i).getRank());
            assertTrue("removed should be equal to clubs", removed.get(i).getSuit() == clubs.get(i).getSuit());
        }
        assertTrue("face up of tableau should be empty now", t.getFaceUp().isEmpty());
        t.setFaceUp((Vector<Card>) hearts.clone());
        removed = t.removeFaceUpVector(10);
        assertEquals("3 cards should have been removed", 3, removed.size());
        assertTrue("element 0 of removed should be JACK of HEARTS", removed.firstElement().getRank() == Rank.JACK);
        assertTrue("element 0 of removed should be JACK of HEARTS", removed.firstElement().getSuit() == Suit.HEARTS);
        assertTrue("element 1 of removed should be QUEEN of HEARTS", removed.get(1).getRank() == Rank.QUEEN);
        assertTrue("element 1 of removed should be QUEEN of HEARTS", removed.get(1).getSuit() == Suit.HEARTS);
        assertTrue("element 2 of removed should be KING of HEARTS", removed.get(2).getRank() == Rank.KING);
        assertTrue("element 2 of removed should be KING of HEARTS", removed.get(2).getSuit() == Suit.HEARTS);
        assertEquals("10 cards should remain on the tableau", 10, t.getFaceUp().size());
        assertTrue("last element of face up of tableau should be TEN of HEARTS", t.getFaceUp().lastElement().getRank() == Rank.TEN);
        assertTrue("last element of face up of tableau should be TEN of HEARTS", t.getFaceUp().lastElement().getSuit() == Suit.HEARTS);
    }

    @Test
    public void cloneTest() {
        Tableau t1 = new Tableau();
        t1.setFaceDown(clubs);
        t1.setFaceUp(hearts);
        Tableau t2 = t1.clone();
        assertEquals(t1.getFaceDown().size(), t2.getFaceDown().size());
        assertEquals(t1.getFaceUp().size(), t2.getFaceUp().size());
        if (t1.getFaceDown().size() == t2.getFaceDown().size()) {
            for (int i = 0; i < t1.getFaceDown().size(); i++) {
                assertEquals(t1.getFaceDown().get(i).getSuit(), t2.getFaceDown().get(i).getSuit());
                assertEquals(t1.getFaceDown().get(i).getRank(), t2.getFaceDown().get(i).getRank());
            }
        }
        if (t1.getFaceUp().size() == t2.getFaceUp().size()) {
            for (int i = 0; i < t1.getFaceUp().size(); i++) {
                assertEquals(t1.getFaceUp().get(i).getSuit(), t2.getFaceUp().get(i).getSuit());
                assertEquals(t1.getFaceUp().get(i).getRank(), t2.getFaceUp().get(i).getRank());
            }
        }
        t2.setFaceDown(hearts);
        t2.setFaceUp(clubs);
        assertEquals(t1.getFaceDown().size(), t2.getFaceDown().size());
        assertEquals(t1.getFaceUp().size(), t2.getFaceUp().size());
        if (t1.getFaceDown().size() == t2.getFaceDown().size()) {
            for (int i = 0; i < t1.getFaceDown().size(); i++) {
                assertNotEquals(t1.getFaceDown().get(i).getSuit(), t2.getFaceDown().get(i).getSuit());
            }
        }
        if (t1.getFaceUp().size() == t2.getFaceUp().size()) {
            for (int i = 0; i < t1.getFaceUp().size(); i++) {
                assertNotEquals(t1.getFaceUp().get(i).getSuit(), t2.getFaceUp().get(i).getSuit());
            }
        }
    }
}
