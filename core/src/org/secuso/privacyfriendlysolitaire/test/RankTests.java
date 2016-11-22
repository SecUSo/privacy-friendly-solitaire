package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Rank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by m0 on 11/22/16.
 */

public class RankTests {

    @Test
    public void isPredecessorTests() {
        assertTrue("failure - ACE is predecessor to TWO", Rank.ACE.isPredecessor(Rank.TWO));
        assertTrue("JACK should be predecessor to QUEEN", Rank.JACK.isPredecessor(Rank.QUEEN));
        assertFalse("failure - TWO is not predecessor to ACE", Rank.TWO.isPredecessor(Rank.ACE));
        assertFalse("NINE should not be predecessor to FIVE", Rank.NINE.isPredecessor(Rank.FIVE));
    }

    @Test
    public void isSuccessorTests() {
        assertFalse("TEN is not successor to THREE", Rank.TEN.isSuccessor(Rank.THREE));
        assertFalse("failure - ACE is not successor to TWO", Rank.ACE.isSuccessor(Rank.TWO));
        assertTrue("failure - TWO is successor to ACE", Rank.TWO.isSuccessor(Rank.ACE));
        assertTrue("EIGHT should be successor to SEVEN", Rank.EIGHT.isSuccessor(Rank.SEVEN));
    }
}
