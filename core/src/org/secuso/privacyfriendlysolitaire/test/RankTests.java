package org.secuso.privacyfriendlysolitaire.test;
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

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Rank;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author M. Fischer
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
