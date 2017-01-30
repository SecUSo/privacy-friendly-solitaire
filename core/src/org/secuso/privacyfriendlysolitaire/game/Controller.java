package org.secuso.privacyfriendlysolitaire.game;

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

        Gdx.app.log("game in controller ", game.toString());

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

        return actionForClick == null ? false : game.handleAction(actionForClick);
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
