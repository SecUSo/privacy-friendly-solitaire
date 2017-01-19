package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Tableau;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

/**
 * @author: I. Dix
 * <p>
 * represents the solitaire game (its current state and all actions to invoke in order to do an action)
 */

public class SolitaireGame extends Observable implements Cloneable {
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
     * number of face down tableau cards that where turned over
     */
    private int turnedOverTableau = 0;


    /**
     * indicated whether the last move was invalid (e.g. moving a card on itself)
     */
    private boolean invalidMove = false;

    /**
     * indicates if the last move allowed turning over a face down tableau card
     */
    private boolean lastMoveturnedOverTableau = false;

    public SolitaireGame(DeckWaste initialDeck, ArrayList<Foundation> initialFoundations,
                         ArrayList<Tableau> initialTableaus) {
        deckAndWaste = initialDeck;
        foundations = initialFoundations;
        tableaus = initialTableaus;
        prevAction = null;
        moves = new Vector<Move>();
    }

    public DeckWaste getDeckWaste() {
        return deckAndWaste;
    }

    public void setDeckAndWaste(DeckWaste deckAndWaste) {
        this.deckAndWaste = deckAndWaste;
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

    public int getTurnedOverTableau() {
        return turnedOverTableau;
    }

    public ArrayList<Foundation> getFoundations() {
        return foundations;
    }

    public void setFoundations(ArrayList<Foundation> foundations) {
        this.foundations = foundations;
    }

    public void setTableaus(ArrayList<Tableau> tableaus) {
        this.tableaus = tableaus;
    }

    public void setMoves(Vector<Move> moves) {
        this.moves = moves;
    }

    public void setPrevAction(Action prevAction) {
        this.prevAction = prevAction;
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

    public boolean isLastMoveturnedOverTableau() {
        return lastMoveturnedOverTableau;
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
            if (action.getCardIndex() != -1) {
                saveAction(action);
                customNotify();
                return true;
            }
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
        lastMoveturnedOverTableau = false;
        //if source of move was a tableau, try to turn over this tableau
        if (prevAction.getGameObject() == GameObject.TABLEAU) {
            if (getTableauAtPos(prevAction.getStackIndex()).turnOver()) {
                turnedOverTableau++;
                lastMoveturnedOverTableau = true;
            }
        }
        this.moves.add(new Move(prevAction, action));
        this.prevAction = null;
        customNotify();
    }


    /**
     * resets prevAction and notifies observers, to be called if an action could not be handled
     * succesfully
     */
    private void failMove() {
        invalidMove = true;
        this.prevAction = null;
        customNotify();
    }

    /**
     * @return whether the last move was invalid
     */
    protected boolean wasInvalidMove() {
        if (invalidMove == true) {
            invalidMove = false;
            return true;
        } else {
            return false;
        }
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
            //check if they can be added to the target tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddingFaceUpVectorPossible(toBeMoved)) {
                this.getTableauAtPos(action.getStackIndex()).addFaceUpVector(this.getTableauAtPos(prevAction.getStackIndex()).removeFaceUpVector(prevAction.getCardIndex()));
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


    /**
     * @return true if the game is won
     */
    public boolean isWon() {
        for (Foundation f : foundations) {
            if (f.getFoundationTop().getRank() != Rank.KING) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SolitaireGame clone() {
        SolitaireGame dolly;
        try {
            dolly = (SolitaireGame) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error();
        }
        //deep copy...
        //...of deckAndWaste
        dolly.setDeckAndWaste(this.deckAndWaste.clone());
        //...of foundations
        dolly.setFoundations(new ArrayList<Foundation>());
        for (Foundation f : this.foundations) {
            dolly.getFoundations().add(f.clone());
        }
        //...of tableaus
        dolly.setTableaus(new ArrayList<Tableau>());
        for (Tableau t : this.tableaus) {
            dolly.getTableaus().add(t.clone());
        }
        //...of previous action
        if (this.prevAction != null) {
            dolly.setPrevAction(this.prevAction.clone());
        } else {
            dolly.setPrevAction(null);
        }
        //...of moves
        dolly.setMoves(new Vector<Move>());
        for (Move m : this.moves) {
            dolly.getMoves().add(m.clone());
        }
        //get rid of original observers, need to be added by application in case clone is used
        dolly.deleteObservers();
        return dolly;
    }

}
