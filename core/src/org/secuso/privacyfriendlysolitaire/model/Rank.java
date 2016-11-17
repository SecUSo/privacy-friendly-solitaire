package org.secuso.privacyfriendlysolitaire.model;

/**
 * Created by m0 on 11/17/16.
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
