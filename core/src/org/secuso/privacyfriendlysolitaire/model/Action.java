package org.secuso.privacyfriendlysolitaire.model;

import com.badlogic.gdx.Game;

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

    public GameObject getGameObject() {
        return gameObject;
    }

    public int getCardIndex() {
        return cardIndex;
    }

}
