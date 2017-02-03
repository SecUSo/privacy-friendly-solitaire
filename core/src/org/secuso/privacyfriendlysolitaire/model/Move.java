package org.secuso.privacyfriendlysolitaire.model;


/**
 * @author: M. Fischer
 * <p>
 * represents a move in the game
 */

public class Move {
    private Action action1;
    private Action action2;
    private boolean turnOver = false;
    private int oldfanSize = -1;

    public Move(Action action1, Action action2) {
        this.action1 = action1;
        this.action2 = action2;
    }

    public Move(Action action1, Action action2, boolean turnOver) {
        this.action1 = action1;
        this.action2 = action2;
        this.turnOver = turnOver;
    }

    public Action getAction1() {
        return action1;
    }

    public void setAction1(Action action1) {
        this.action1 = action1;
    }

    public Action getAction2() {
        return action2;
    }

    public void setAction2(Action action2) {
        this.action2 = action2;
    }

    public boolean isTurnOver() {
        return turnOver;
    }

    public int getOldfanSize() {
        return oldfanSize;
    }

    public void setOldfanSize(int oldfanSize) {
        this.oldfanSize = oldfanSize;
    }

    @Override
    public String toString() {
        return "Move{" +
                "action1=" + action1 +
                ", action2=" + action2 +
                ", turnOver=" + turnOver +
                ", oldfanSize=" + oldfanSize +
                '}';
    }
}
