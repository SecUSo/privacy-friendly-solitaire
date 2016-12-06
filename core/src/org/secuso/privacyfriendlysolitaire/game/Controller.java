package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

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

    SolitaireGame game;
    View view;

    public Controller(SolitaireGame initialGame, View initialView) {
        game = initialGame;
        view = initialView;
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    /**
     * use this when
     * @param x
     * @param y
     * @param count
     * @param button
     * @return
     */
    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
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
}
