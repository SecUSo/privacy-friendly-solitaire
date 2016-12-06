package org.secuso.privacyfriendlysolitaire.game;


import com.badlogic.gdx.Game;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;


import java.util.ArrayList;
import java.util.Observable;

/**
 * @author: I. Dix
 * <p>
 * represents the solitaire game (its current state and all actions to invoke in order to do an action)
 */

public class SolitaireGame extends Observable {
    private DeckWaste deckAndWaste;
    private ArrayList<Foundation> foundations;
    private ArrayList<Tableau> tableaus;

    /**
     * the previous action
     */
    private Action prevAction;

    /**
     * the recent succesful move done in game
     */
    private Move recentMove;

    public SolitaireGame(DeckWaste initialDeck, ArrayList<Foundation> initialFoundations,
                         ArrayList<Tableau> initialTableaus) {
        deckAndWaste = initialDeck;
        foundations = initialFoundations;
        tableaus = initialTableaus;
        prevAction = null;
        recentMove = null;
    }

    public DeckWaste getDeckWaste() {
        return deckAndWaste;
    }

    public Foundation getFoundationAtPos(int n) {
        return foundations.get(n);
    }

    public Tableau getTableauAtPos(int n) {
        return tableaus.get(n);
    }

    public ArrayList<Tableau> getTableaus() {
        return tableaus;
    }

    public ArrayList<Foundation> getFoundations() {
        return foundations;
    }

    public Action getPrevAction() {
        return prevAction;
    }

    public Move getRecentMove() {
        return recentMove;
    }

    /**
     * @param action the action that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    public boolean handleAction(Action action) {
        switch (action.getGameObject()) {
            case DECK:
                return handleDeck(action);
            case WASTE:
                return handleWaste(action);
            case TABLEAU:
                return handleTableau(action);
            case FOUNDATION:
                return handleFoundation(action);
            //TODO tableau face down card is target of action?!
        }
        return false;
    }

    /**
     * @param action the action regarding the deck that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleDeck(Action action) {
        if (this.deckAndWaste.canTurnOver()) {
            if (this.deckAndWaste.turnOver()) {
                this.recentMove = new Move(action, null);
                notifyObservers();
                return true;
            }
        } else if (this.deckAndWaste.canReset()) {
            if (this.deckAndWaste.reset()) {
                this.recentMove = new Move(action, action);
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action regarding the waste that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleWaste(Action action) {
        if (this.prevAction == null) {
            saveAction(action);
            return true;
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding a tableau that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleTableau(Action action) {
        if (this.prevAction == null) {
            saveAction(action);
            return true;
        } else if (this.prevAction.getGameObject() == GameObject.TABLEAU) {
            if (handleTableauToTableau(action)) {
                makeMove(action);
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.WASTE) {
            if (handleWasteToTableau(action)) {
                makeMove(action);
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.FOUNDATION) {
            if (handleFoundationToTableau(action)) {
                makeMove(action);
                return true;
            }
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding a foundation that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleFoundation(Action action) {
        if (this.prevAction == null) {
            saveAction(action);
            return true;
        } else if (this.prevAction.getGameObject() == GameObject.TABLEAU) {
            if (handleTableauToFoundation(action)) {
                makeMove(action);
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.WASTE) {
            if (handleWasteToFoundation(action)) {
                makeMove(action);
                return true;
            }
        }
        failMove();
        return false;
    }

    /**
     * saves an action in the prevAction variable and notifies the observers
     *
     * @param action the action that will be saved
     */
    private void saveAction(Action action) {
        this.prevAction = action;
        notifyObservers();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action the action that specifies the target of this move
     */
    private void makeMove(Action action) {
        this.recentMove = new Move(prevAction, action);
        this.prevAction = null;
        notifyObservers();
    }

    /**
     * resets prevAction and notifies observers, to be called if an action could not be handled
     * succesfully
     */
    private void failMove() {
        this.prevAction = null;
        notifyObservers();
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if the cards could be moved between two tableaus
     */
    private boolean handleTableauToTableau(Action action) {
        //TODO implement
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from waste to tableau
     */
    private boolean handleWasteToTableau(Action action) {
        //TODO implement
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from foundation to tableau
     */
    private boolean handleFoundationToTableau(Action action) {
        //TODO implement
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from tableau to foundation
     */
    private boolean handleTableauToFoundation(Action action) {
        //TODO implement
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from waste to foundation
     */
    private boolean handleWasteToFoundation(Action action) {
        //TODO implement
        return false;
    }

}
