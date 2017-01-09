package org.secuso.privacyfriendlysolitaire.model;

/**
 * @author: M. Fischer
 * <p>
 * represents a move in the game
 */

public class Move {
    private Action action1;
    private Action action2;

    public Move(Action action1, Action action2) {
        this.action1 = action1;
        this.action2 = action2;
    }

    public Action getAction1() {
        return action1;
    }

    public Action getAction2() {
        return action2;
    }
}
