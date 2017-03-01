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

import java.util.Vector;

/**
 * @author M. Fischer
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

    /**
     * @return the Vector of Cards in this Foundation
     */
    public Vector<Card> getCards() {
        return cards;
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
