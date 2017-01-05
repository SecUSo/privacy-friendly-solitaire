package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

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
     * the vector of moves that were made in this game so far
     */
    private Vector<Move> moves;

    /**
     * the index of the most recent move in the moves vector
     */
    private int moveIndex;

    public SolitaireGame(DeckWaste initialDeck, ArrayList<Foundation> initialFoundations,
                         ArrayList<Tableau> initialTableaus) {
        deckAndWaste = initialDeck;
        foundations = initialFoundations;
        tableaus = initialTableaus;
        prevAction = null;
        moves = new Vector<Move>();
        moveIndex = -1;
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

    /**
     * @return the previous action the game received, only marks of cards as source of a move
     * will be saved here
     */
    public Action getPrevAction() {
        return prevAction;
    }

    /**
     * @return the vector of moves that were made in this game so far
     */
    public Vector<Move> getMoves() {
        return moves;
    }

    /**
     * @param action the action that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    public boolean handleAction(Action action) {
        Gdx.app.log("Debug", "handleAction");
        switch (action.getGameObject()) {
            case DECK:
                return handleDeck(action);
            case WASTE:
                return handleWaste(action);
            case TABLEAU:
                return handleTableau(action);
            case FOUNDATION:
                return handleFoundation(action);
        }
        return false;
    }

    /**
     * @param action the action regarding the deck that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleDeck(Action action) {
        this.saveAction(action);
        if (this.deckAndWaste.canTurnOver()) {
            if (this.deckAndWaste.turnOver()) {
                makeMove(null);
                return true;
            }
        } else if (this.deckAndWaste.canReset()) {
            if (this.deckAndWaste.reset()) {
                makeMove(action);
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
            customNotify();
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
            customNotify();
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
            customNotify();
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
//        customNotify();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action the action that specifies the target of this move
     */
    private void makeMove(Action action) {
        cleanUpMoves();
        this.moves.add(new Move(prevAction, action));
        this.prevAction = null;
        customNotify();
    }

    /**
     * deletes all moves which indices are greater than the current moveIndex
     */
    private void cleanUpMoves() {
        if (moveIndex < moves.size() - 1) {
            for (int i = moves.size() - 1; i > moveIndex; --i) {
                moves.remove(i);
            }
        }
    }

    /**
     * resets prevAction and notifies observers, to be called if an action could not be handled
     * succesfully
     */
    private void failMove() {
        this.prevAction = null;
        customNotify();
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if the cards could be moved between two tableaus
     */
    private boolean handleTableauToTableau(Action action) {
        //prevent moves where source and target tableau are the same
        if (prevAction.getStackIndex() != action.getStackIndex()) {
            //get cards from source tableau
            Vector<Card> toBeMoved = this.getTableauAtPos(prevAction.getStackIndex()).getCopyFaceUpVector(prevAction.getCardIndex());
            Gdx.app.log("Debug_toBeMoved: ", toBeMoved.toString());         // THIS IS EMPTY
            //check if they can be added to the target tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddingFaceUpVectorPossible(toBeMoved)) {
                this.getTableauAtPos(action.getStackIndex()).addFaceUpVector(this.getTableauAtPos(prevAction.getStackIndex()).removeFaceUpVector(prevAction.getCardIndex()));
                Gdx.app.log("faceUpVectorAfterMoved: ", getTableauAtPos(action.getStackIndex()).getFaceUp().toString());
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from waste to tableau
     */
    private boolean handleWasteToTableau(Action action) {
        //check if a card is on top of the waste
        if (!deckAndWaste.isWasteEmpty()) {
            //get card from the waste
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(deckAndWaste.getWasteTop());
            //check if it can be added to the tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddingFaceUpVectorPossible(toBeMoved)) {
                this.getTableauAtPos(action.getStackIndex()).addFaceUpVector(toBeMoved);
                this.deckAndWaste.removeWasteTop();
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from foundation to tableau
     */
    private boolean handleFoundationToTableau(Action action) {
        //get card to be moved from the foundation
        if (!this.getFoundationAtPos(prevAction.getStackIndex()).isEmpty()) {
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(this.getFoundationAtPos(prevAction.getStackIndex()).getFoundationTop());
            //check if it can be added to the tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddingFaceUpVectorPossible(toBeMoved)) {
                this.getTableauAtPos(action.getStackIndex()).addFaceUpVector(toBeMoved);
                this.getFoundationAtPos(prevAction.getStackIndex()).removeFoundationTop();
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from tableau to foundation
     */
    private boolean handleTableauToFoundation(Action action) {
        //get cards from source tableau
        Vector<Card> toBeMoved = this.getTableauAtPos(prevAction.getStackIndex()).getCopyFaceUpVector(prevAction.getCardIndex());
        if (toBeMoved.size() == 1) {
            if (this.getFoundationAtPos(action.getStackIndex()).canAddCard(toBeMoved.firstElement())) {
                this.getFoundationAtPos(action.getStackIndex()).addCard(toBeMoved.firstElement());
                this.getTableauAtPos(prevAction.getStackIndex()).removeFaceUpVector(prevAction.getCardIndex());
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from waste to foundation
     */
    private boolean handleWasteToFoundation(Action action) {
        //check if a card is on top of the waste
        if (!deckAndWaste.isWasteEmpty()) {
            //get card from the waste
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(deckAndWaste.getWasteTop());
            //check if it can be added to the foundation
            if (toBeMoved.size() == 1) {
                if (this.getFoundationAtPos(action.getStackIndex()).canAddCard(toBeMoved.firstElement())) {
                    this.getFoundationAtPos(action.getStackIndex()).addCard(toBeMoved.firstElement());
                    this.deckAndWaste.removeWasteTop();
                    return true;
                }
            }

        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.deckAndWaste.toString());
        sb.append("\n");
        for (Tableau t : tableaus) {
            sb.append("Tableau: ");
            sb.append(t.toString());
            sb.append("\n");
        }
        for (Foundation f : foundations) {
            sb.append("Foundation: ");
            sb.append(f.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private void customNotify() {
        setChanged();
        notifyObservers();
    }

}
