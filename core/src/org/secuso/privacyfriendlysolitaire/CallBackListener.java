package org.secuso.privacyfriendlysolitaire;

/**
 *
 */

public interface CallBackListener  {

    void onWon();

    void isUndoRedoPossible(final boolean canUndo, final boolean canRedo);

    void score(int score);
}
