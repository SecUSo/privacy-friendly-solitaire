package org.secuso.privacyfriendlysolitaire.model;

import java.util.Vector;

/**
 * @author: M. Fischer
 */

public class Tableau {
    /**
     * the cards lying face down on this tableau
     */
    private Vector<Card> faceDown;
    /**
     * the cards lying face up on this tableau
     */
    private Vector<Card> faceUp;

    /**
     * Constructs a new Tableau containing no face down or face up cards
     */
    public Tableau() {
        this.faceDown = new Vector<Card>();
        this.faceUp = new Vector<Card>();
    }

    /**
     * @return the vector of cards lying face down on this tableau
     */
    public Vector<Card> getFaceDown() {
        return faceDown;
    }

    /**
     * @param faceDown the vector of cards to be set as lying face down on this tableau
     */
    public void setFaceDown(Vector<Card> faceDown) {
        this.faceDown = faceDown;
    }

    /**
     * @return the vector of cards lying face up on this tableau
     */
    public Vector<Card> getFaceUp() {
        return faceUp;
    }

    /**
     * @param faceUp the vector of cards to be set as lying face up on this tableau
     */
    public void setFaceUp(Vector<Card> faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * @return true if the top face down card could be turned over
     */
    public boolean turnOver() {
        if (this.faceUp.isEmpty() && !this.faceDown.isEmpty()) {//can only turn over if no face up cards and at least one face down card on tableau
            this.faceUp.add(this.faceDown.lastElement());
            this.faceDown.remove(this.faceDown.size() - 1);
            return true;
        } else {
            return false;
        }
    }

    /**
     * undoes turning over a card from face down to face up
     */
    public void undoturnOver() {
        if (faceUp.size() == 1) {
            faceDown.add(faceUp.remove(0));
        }
    }


    /**
     * @param card the card to be added on top of the face down cards
     */
    public void addFaceDown(Card card) {
        this.faceDown.add(card);
    }

    /**
     * @param card the card to be added on top of the face up cards
     */
    public void addFaceUp(Card card) {
        this.faceUp.add(card);
    }

    /**
     * @param vecCards the vector of cards that should be added to this tableau pile
     * @return true if the cards could be added to the tableau
     */
    public boolean addFaceUpVector(Vector<Card> vecCards) {

        return this.faceUp.addAll(vecCards);



    }

    /**
     * @param vecCards the vector of cards that should be added to this tableau pile
     * @return true if adding the cards to the tableau would be allowed
     */
    public boolean isAddingFaceUpVectorPossible(Vector<Card> vecCards) {
        if (!vecCards.isEmpty()) {
            if (this.faceDown.isEmpty() && this.faceUp.isEmpty()) {
                //empty tableau piles can be filled with a stack starting with a king
                return vecCards.firstElement().getRank() == Rank.KING;
            } else if (!this.faceDown.isEmpty() && this.faceUp.isEmpty()) {
                //cannot add cards, face down card has to be turned over first
                return false;
            } else
                return this.faceUp.lastElement().getColor() != vecCards.firstElement().getColor() && vecCards.firstElement().getRank().isPredecessor(this.faceUp.lastElement().getRank());
        } else {
            return true;
        }
    }

    /**
     * @param index the index of the first card in the stack that shall be removed from face up cards on the tableau
     * @return the vector of cards that was removed from the tableau
     */
    public Vector<Card> removeFaceUpVector(int index) {

        Vector<Card> result = getCopyFaceUpVector(index);
        this.faceUp.removeAll(result);
        return result;
    }

    /**
     * @param index the index of the first card in the stack that shall be copied from face up cards on the tableau
     * @return a copy of a vector of cards on this tableau starting with the card specified by index
     */
    public Vector<Card> getCopyFaceUpVector(int index) {
        if ((index < 0) || (index >= this.faceUp.size())) {
            return new Vector<Card>();
        } else {
            Vector<Card> result = new Vector<Card>();
            for (int i = index; i < this.faceUp.size(); ++i) {
                result.add(this.faceUp.get(i));
            }

            return result;
        }
    }

    /**
     * @param vecCards the vector of cards that shall be removed from the tableau
     * @return true if the tableau changed as a result of the call
     */
    public boolean removeAllFaceUpVector(Vector<Card> vecCards) {
        return this.faceUp.removeAll(vecCards);
    }


    public String toString() {
        return "Face-Down: " + faceDown.toString() + "; Face-Up: " + faceUp.toString();
    }

}

