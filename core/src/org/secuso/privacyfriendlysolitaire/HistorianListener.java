package org.secuso.privacyfriendlysolitaire;

/**
 * @author: M. Fischer
 */


public interface HistorianListener {
    void possibleActions(boolean canUndo, boolean canRedo);
}
