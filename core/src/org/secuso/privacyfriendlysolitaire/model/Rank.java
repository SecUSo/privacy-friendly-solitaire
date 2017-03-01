package org.secuso.privacyfriendlysolitaire.model;
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
 * @author M. Fischer
 */

public enum Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;

    /**
     * @param r the rank that this rank might be predecessor to
     * @return true if this is the predecessor of r
     */
    public boolean isPredecessor(Rank r) {
        // find indices of this rank and rank r
        int thisIndex = 0;
        int rIndex = 0;
        for (int i = 0; i < Rank.values().length; ++i) {
            if (Rank.values()[i] == this) {
                thisIndex = i;
            }
            if (Rank.values()[i] == r) {
                rIndex = i;
            }
        }
        return thisIndex + 1 == rIndex;
    }

    /**
     * @param r the rank that this rank might be successor to
     * @return true if this is the successor of r
     */
    public boolean isSuccessor(Rank r) {
        return r.isPredecessor(this);
    }
}
