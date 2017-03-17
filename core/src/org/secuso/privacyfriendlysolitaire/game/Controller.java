package org.secuso.privacyfriendlysolitaire.game;
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

/**
 * @author: I. Dix
 * <p>
 * the controller manages the game, it
 * <ul>
 * <li>translates the user-input (given by (x,y)-coordinates) to an action for the model using the view</li>
 * <li>checks whether the game was won after every action involving a foundation</li>
 * </ul>
 */

public class Controller implements GestureDetector.GestureListener {

    private final SolitaireGame game;
    private final View view;

    public Controller(SolitaireGame initialGame, View initialView) {
        game = initialGame;
        view = initialView;
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    /**
     * use this when user taps on the screen (thereby creating an action)
     *
     * @param x
     * @param y
     * @param count  how many consecutive taps (taps within a specified time interval)
     * @param button
     * @return true if a valid action was done, false else (e.g. because the user did not tap a sensible location)
     */
    @Override
    public boolean tap(float x, float y, int count, int button) {
        y = invertHeight(y);

        Action actionForClick = view.getActionForTap(x, y);

        if (actionForClick != null && actionForClick.getGameObject() != null &&
                actionForClick.getGameObject().equals(GameObject.TABLEAU)) {
            int index = actionForClick.getStackIndex();
            Tableau tableau = game.getTableauAtPos(index);
            int cardIndex = actionForClick.getCardIndex();

            // maybe the view made an error and the index is not a valid card of this tableau
            // therefore: check for sanity
            if (cardIndex > tableau.getFaceDown().size() + tableau.getFaceUp().size()) {
                actionForClick = null;
            } else {
                int cardIndexInFaceUp = cardIndex - tableau.getFaceDown().size();
                if (cardIndexInFaceUp < 0) {
                    actionForClick = null;
                } else {
                    // View can not distinguish between just one card on the stack and no card
                    if (tableau.getFaceDown().size() + tableau.getFaceUp().size() == 0) {
                        actionForClick = new Action(GameObject.TABLEAU, index, -1);
                    } else {
                        actionForClick = new Action(GameObject.TABLEAU, index, cardIndexInFaceUp);
                    }
                }
            }
        }

        // if we get a user-move that is not a deck-move we reset the MoveFinder's counter
        // if we get a deck-action we do not reset the number, but we also do not increment it
        // this is a behaviour that we implemented, such that the user can tap through the deck
        // by hand, if he wants to see its content, but if he is 'asking the game for help' by
        // invoking the MoveFinder and only gets deck-moves the game is definitely lost
        if (actionForClick != null) {
            if (actionForClick.getGameObject() != GameObject.DECK) {
                MoveFinder.resetNrOfMovesThroughDeck();
            }
        }

        return actionForClick != null && game.handleAction(actionForClick, false);
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }


    /**
     * the positions for y are inverted for positioning and input checking, so we invert it here!
     *
     * @param y
     * @return heightScreen-y resulting in just the opposite y
     */
    private float invertHeight(float y) {
        return Gdx.graphics.getHeight() - y;
    }
}
