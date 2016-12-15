package org.secuso.privacyfriendlysolitaire.model;

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


