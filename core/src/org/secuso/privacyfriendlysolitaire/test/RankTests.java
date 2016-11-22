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
        assertFalse("failure - TWO is not predecessor to ACE", Rank.TWO.isPredecessor(Rank.ACE));
    }

    @Test
    public void isSuccessorTests() {
        assertFalse("failure - ACE is not successor to TWO", Rank.ACE.isSuccessor(Rank.TWO));
        assertTrue("failure - TWO is successor to ACE", Rank.TWO.isSuccessor(Rank.ACE));
    }
}
