package org.secuso.privacyfriendlysolitaire.model;

/**
 * @author: M. Fischer
 * <p>
 * represents an interaction of the user with the game
 */

public class Action {

    private final GameObject gameObject;
    private final int stackIndex;
    private final int cardIndex;

    public Action(GameObject gameObject, int stackIndex, int cardIndex) {
        this.gameObject = gameObject;
        this.stackIndex = stackIndex;
        this.cardIndex = cardIndex;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public String toString(){
        return gameObject+", stack: "+stackIndex+", card: "+cardIndex;
    }

}
