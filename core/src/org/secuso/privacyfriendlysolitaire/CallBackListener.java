package org.secuso.privacyfriendlysolitaire;
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
 * @author I. Dix
 *         <p>
 *         A listener that can be used to communicate with the outside Android activity.
 *         Namely communicate the score to the outside so it can be used in the AppBar
 *         and communicating when the game is won.
 */

public interface CallBackListener {

    void onWon();

    void isUndoRedoPossible(final boolean canUndo, final boolean canRedo);

    void score(int score);
}
