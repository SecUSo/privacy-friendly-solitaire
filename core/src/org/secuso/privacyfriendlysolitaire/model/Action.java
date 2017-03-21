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

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public String toString() {
        return gameObject + ", stack: " + stackIndex + ", card: " + cardIndex;
    }

}
