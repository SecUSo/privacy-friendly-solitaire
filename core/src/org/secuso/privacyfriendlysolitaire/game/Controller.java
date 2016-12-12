package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import org.secuso.privacyfriendlysolitaire.model.Action;

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

        // TODO comment in other line as soon as model is finished
        return false;
//        return actionForClick == null ? false : game.handleAction(actionForClick);
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
