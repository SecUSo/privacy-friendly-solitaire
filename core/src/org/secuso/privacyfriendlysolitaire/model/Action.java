package org.secuso.privacyfriendlysolitaire.model;

/**
 * @author: M. Fischer
 * <p>
 * represents an interaction of the user with the game
 */

public class Action {

    private GameObject gameObject;
    private int stackIndex;
    private int cardIndex;

    public Action(GameObject gameObject, int stackIndex, int cardIndex) {
        this.gameObject = gameObject;
        this.stackIndex = stackIndex;
        this.cardIndex = cardIndex;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public void setStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public String toString(){
        return gameObject+", stack: "+stackIndex+", card: "+cardIndex;
    }

}
