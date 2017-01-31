package org.secuso.privacyfriendlysolitaire.model;

import java.util.Vector;

/**
 * @author: M. Fischer
 */

public class Foundation {

    /**
     * the Suit of this Foundation
     */
    private Suit suit;
    /**
     * the cards in this Foundation
     */
    private Vector<Card> cards;

    /**
     * Constructs a new Foundation that contains no cards and has a null suit
     */
    public Foundation() {
        this.suit = null;
        this.cards = new Vector<Card>(13);
    }

    /**
     * @return the Suit of the Foundation
     */
    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    /**
     * @return the Vector of Cards in this Foundation
     */
    public Vector<Card> getCards() {
        return cards;
    }

    public void setCards(Vector<Card> cards) {
        this.cards = cards;
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    /**
     * @param card the Card that should be added to this Foundation
     * @return true if the card could be added to the Foundation, false if suit did not fit or card is not the successor of the top card of the Foundation
     */
    public boolean addCard(Card card) {
        if (this.getSuit() == null && this.getCards().isEmpty()) { //foundation empty --> only ace can be added, this defines the suit of the foundation
            if (card.getRank() == Rank.ACE) {
                this.getCards().add(card);
                this.suit = card.getSuit();
                return true;
            } else {
                return false;
            }
        } else if (this.getSuit() == card.getSuit()) { //foundation not empty --> suit must fit
            if (this.getCards().lastElement().getRank().isPredecessor(card.getRank())) { // suit fits --> card must be successor of top card
                this.getCards().add(card);
                return true;
            } else {
                return false;
            }
        } else { // foundation not empty and suit does not fit --> cannot add card here
            return false;
        }
    }

    /**
     * @param card the card that will be checked in regard to adding
     * @return true if adding the card would be possible
     */
    public boolean canAddCard(Card card) {
        if (this.getSuit() == null && this.getCards().isEmpty()) { //foundation empty --> only ace can be added, this defines the suit of the foundation
            return card.getRank() == Rank.ACE;
        } else if (this.getSuit() == card.getSuit()) { //foundation not empty --> suit must fit
            // suit fits --> card must be successor of top card
            return this.getCards().lastElement().getRank().isPredecessor(card.getRank());
        } else { // foundation not empty and suit does not fit --> cannot add card here
            return false;
        }
    }

    /**
     * @return the Card on top of this foundation
     */
    public Card getFoundationTop() {
        return this.cards.lastElement();
    }

    /**
     * @return the card that was removed from the top of this foundation
     */
    public Card removeFoundationTop() {
        if (this.cards.size() == 1) {
            this.suit = null;
        }
        return this.cards.remove(this.cards.size() - 1);
    }


    public String toString() {
        return cards.toString();
    }

}
