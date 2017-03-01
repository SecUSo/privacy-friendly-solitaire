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
 *         <p>
 *         represents a move in the game
 */

public class Move {
    private Action action1;
    private Action action2;
    private boolean turnOver = false;
    private int oldfanSize = -1;
    private int newFanSize = -1;

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

    public Action getAction2() {
        return action2;
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

    public int getNewFanSize() {
        return newFanSize;
    }

    public void setNewFanSize(int newFanSize) {
        this.newFanSize = newFanSize;
    }

    @Override
    public String toString() {
        return "Move{" +
                "action1=" + action1 +
                ", action2=" + action2 +
                ", turnOver=" + turnOver +
                ", oldfanSize=" + oldfanSize +
                ", newFanSize=" + newFanSize +
                '}';
    }
}
