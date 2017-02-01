package org.secuso.privacyfriendlysolitaire.model;

import java.util.Vector;

/**
 * @author M. Fischer
 */

public class DeckWaste {
    /**
     * the vector of cards representing the deck
     */
    private Vector<Card> deck;

    /**
     * the vector of cards representing the waste
     */
    private Vector<Card> waste;

    /**
     * the number of cards that is turned over simultaneously
     */
    private int numTurnOver;

    public boolean isVegas() {
        return vegas;
    }

    /**
     * true if vegas variant is played
     */
    private boolean vegas;


    public void setFanSize(int fanSize) {
        this.fanSize = fanSize;
    }

    /**
     * the number of cards currently fanned out on the waste
     */
    private int fanSize=0;


    //TODO DEPRECATED remove if not used anymore
    /**
     * @param numTurnOver the number of cards that is turned over simultaneously
     */
    public DeckWaste(int numTurnOver) {
        this.deck = new Vector<Card>();
        this.waste = new Vector<Card>();
        this.numTurnOver = numTurnOver;
    }

    /**
     */
    public DeckWaste(int numTurnOver, boolean vegas) {
        this.deck = new Vector<Card>();
        this.waste = new Vector<Card>();
        this.numTurnOver = numTurnOver;
        this.vegas = vegas;
    }

    /**
     * @return the vector of cards representing the deck
     */
    public Vector<Card> getDeck() {
        return deck;
    }

    /**
     * @param deck the vector of cards representing the deck
     */
    public void setDeck(Vector<Card> deck) {
        this.deck = deck;
    }

    /**
     * @return the vector of cards representing the waste
     */
    public Vector<Card> getWaste() {
        return waste;
    }

    /**
     * @param waste the vector of cards representing the waste
     */
    public void setWaste(Vector<Card> waste) {
        this.waste = waste;
    }

    public int getNumTurnOver() {
        return numTurnOver;
    }

    public void setNumTurnOver(int numTurnOver) {
        this.numTurnOver = numTurnOver;
    }

    public int getFanSize() {
        return fanSize;
    }

    /**
     * tries to turn over cards from deck to waste
     *
     * @return true if cards could be turned over from deck to waste
     */
    public boolean turnOver() {
        if (this.canTurnOver()) {
            int newfanSize = 0;
            for (int i = 0; i < this.numTurnOver; ++i) {
                if (this.deck.isEmpty()) {
                    break;
                }
                this.waste.add(this.deck.remove(this.deck.size() - 1));
                newfanSize++;
            }
            this.fanSize = newfanSize;
            return true;
        } else {
            return false;
        }
    }

    public void undoTurnOver(int oldFanSize) {
        for (int i = 0; i < numTurnOver; i++) {
            deck.add(removeWasteTop());
        }
        setFanSize(oldFanSize);
    }

    /**
     * @return true if the waste is empty
     */
    public boolean isWasteEmpty() {
        return this.waste.isEmpty();
    }

    /**
     * just probes if turning over would be possible
     *
     * @return true if cards could be turned over from deck to waste
     */
    public boolean canTurnOver() {
        return !this.deck.isEmpty();
    }

    /**
     * tries to reset the deck from the waste, can only be done if the deck is empty
     * in vegas mode the deck can never be reset
     * @return true if the deck was succesfully reset from the waste
     */
    public boolean reset() {
        if (this.deck.isEmpty() && !vegas) {
            while (!this.waste.isEmpty()) {
                this.deck.add(this.waste.remove(this.waste.size() - 1));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * just probes if resetting the deck would be possible
     *
     * @return true if the deck is empty and waste is not empty
     */
    public boolean canReset() {
        return this.deck.isEmpty() && !this.waste.isEmpty();
    }

    public void undoReset(int origFansize) {
        if (isWasteEmpty() && !deck.isEmpty()) {
            while (canTurnOver()) {
                turnOver();
            }
            setFanSize(origFansize);
        }
    }

    /**
     * @return the card on top of the waste
     */
    public Card getWasteTop() {
        return this.waste.lastElement();
    }

    /**
     * Removes the card on top of the waste from it
     *
     * @return the card on top of the waste that was removed from it
     */
    public Card removeWasteTop() {
        if (fanSize > 0) {
            fanSize--;
        }
        return this.waste.remove(this.waste.size() - 1);
    }


    public String toString() {
        return "Deck: " + deck.toString() + ";\nWaste: " + waste.toString();
    }

}
