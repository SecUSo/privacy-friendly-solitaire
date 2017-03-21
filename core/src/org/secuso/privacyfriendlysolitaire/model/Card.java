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

public class Card {

    /**
     * the rank of the card
     */
    private Rank rank;
    /**
     * the suit of the card
     */
    private Suit suit;

    /**
     * @param rank the rank of the card to be constructed
     * @param suit the suit of the card to be constructed
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * @return the rank of the card
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * @param r the Rank to be set for this Card
     */
    public void setRank(Rank r) {
        this.rank = r;
    }

    /**
     * @return the suit of the card
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * @return the color of the card according to its suit
     */
    public Color getColor() {
        if (this.getSuit() == Suit.CLUBS || this.getSuit() == Suit.SPADES) {
            return Color.BLACK;
        } else {
            return Color.RED;
        }
    }


    public String toString() {
        return rank + " of " + suit;
    }

}


