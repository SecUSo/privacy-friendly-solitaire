package org.secuso.privacyfriendlysolitaire;

/**
 * @author I. Dix
 *
 *  A listener that can be used to communicate with the outside Android activity.
 *  Namely communicate the score to the outside so it can be used in the AppBar
 *  and communicating when the game is won.
 */

public interface CallBackListener  {

    void onWon();

    void isUndoRedoPossible(final boolean canUndo, final boolean canRedo);

    void score(int score);
}
