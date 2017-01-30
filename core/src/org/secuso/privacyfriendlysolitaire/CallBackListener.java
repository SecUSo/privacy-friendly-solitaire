package org.secuso.privacyfriendlysolitaire;

/**
 *
 */

public interface CallBackListener  {

    void onWon();

    void possibleActionsHistorian(final boolean canUndo, final boolean canRedo);

    void score(int score);
}
