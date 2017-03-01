package org.secuso.privacyfriendlysolitaire.game;
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

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author M. Fischer
 *         <p>
 *         represents the solitaire game
 *         (its current state and all actions to invoke in order to do an action)
 */

public class SolitaireGame {
    /**
     * the deck and the waste of the game
     */
    private DeckWaste deckAndWaste;

    /**
     * the foundations of the game
     */
    private ArrayList<Foundation> foundations;

    /**
     * the tableaus of the game
     */
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

    /**
     * the vector of listeners to be notified in case the game changes
     */
    private Vector<GameListener> gameListeners = new Vector<GameListener>();

    /**
     * a CallBackListener residing in the android part of the app
     */
    private CallBackListener callBackListener;

    /**
     * index of the last move executed in vector moves
     */
    private int movePointer = -1;

    /**
     * indicates that a move was undone
     */
    private boolean undoMove = false;

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

    public Foundation getFoundationAtPos(int n) {
        return foundations.get(n);
    }

    public Tableau getTableauAtPos(int n) {
        return tableaus.get(n);
    }

    ArrayList<Tableau> getTableaus() {
        return tableaus;
    }

    int getTurnedOverTableau() {
        return turnedOverTableau;
    }

    ArrayList<Foundation> getFoundations() {
        return foundations;
    }

    /**
     * @return the previous action the game received, only marks of cards as source of a move
     * will be saved here
     */
    Action getPrevAction() {
        return prevAction;
    }

    /**
     * @return the vector of moves that were made in this game so far
     */
    Vector<Move> getMoves() {
        return moves;
    }

    boolean isLastMoveturnedOverTableau() {
        return lastMoveturnedOverTableau;
    }

    boolean wasUndoMove() {
        return undoMove;
    }

    int getMovePointer() {
        return movePointer;
    }


    /**
     * @param action the action that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    boolean handleAction(Action action, boolean redoMove) {
        switch (action.getGameObject()) {
            case DECK:
                return handleDeck(action, redoMove);
            case WASTE:
                return handleWaste(action);
            case TABLEAU:
                return handleTableau(action, redoMove);
            case FOUNDATION:
                return handleFoundation(action, redoMove);
        }
        return false;
    }

    /**
     * @param action the action regarding the deck that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleDeck(Action action, boolean redoMove) {
        this.saveAction(action);
        int oldFanSize = deckAndWaste.getFanSize();
        if (this.deckAndWaste.canTurnOver()) {
            int prevWasteSize = deckAndWaste.getWaste().size();
            if (this.deckAndWaste.turnOver()) {
                int newFanSize = deckAndWaste.getWaste().size() - prevWasteSize;
                makeMove(null, redoMove, oldFanSize, newFanSize);
                return true;
            }
        } else if (this.deckAndWaste.canReset()) {
            if (this.deckAndWaste.reset()) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
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
            notifyListeners();
            return true;
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding a tableau that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleTableau(Action action, boolean redoMove) {
        if (this.prevAction == null) {
            if (action.getCardIndex() != -1) {
                saveAction(action);
                notifyListeners();
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.TABLEAU) {
            if (handleTableauToTableau(action)) {
                makeMove(action, redoMove);
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.WASTE) {
            int oldFanSize = deckAndWaste.getFanSize();
            if (handleWasteToTableau(action)) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.FOUNDATION) {
            if (handleFoundationToTableau(action)) {
                makeMove(action, redoMove);
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
    private boolean handleFoundation(Action action, boolean redoMove) {
        if (this.prevAction == null) {
            saveAction(action);
            notifyListeners();
            return true;
        } else if (this.prevAction.getGameObject() == GameObject.TABLEAU) {
            if (handleTableauToFoundation(action)) {
                makeMove(action, redoMove);
                return true;
            }
        } else if (this.prevAction.getGameObject() == GameObject.WASTE) {
            int oldFanSize = deckAndWaste.getFanSize();
            if (handleWasteToFoundation(action)) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
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
//        notifyListeners();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action the action that specifies the target of this move
     */
    private void makeMove(Action action, boolean redoMove) {
        lastMoveturnedOverTableau = false;
        //if source of move was a tableau, try to turn over this tableau
        if (prevAction.getGameObject() == GameObject.TABLEAU) {
            if (getTableauAtPos(prevAction.getStackIndex()).turnOver()) {
                turnedOverTableau++;
                lastMoveturnedOverTableau = true;
            }
        }
        if (!redoMove) {
            cleanUpMoves();
            this.moves.add(new Move(prevAction, action, lastMoveturnedOverTableau));
        }
        movePointer++;
        this.prevAction = null;
        undoMove = false;
        notifyListeners();
        notifyCallBackListener();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action     the action that specifies the target of this move
     * @param redoMove   true if the move to be made is caused by a redo
     * @param oldFanSize the number of cards fanned out on the waste before the move
     * @param newFanSize the number of cards fanned out on the waste after the move
     */
    private void makeMove(Action action, boolean redoMove, int oldFanSize, int newFanSize) {
        lastMoveturnedOverTableau = false;
        //if source of move was a tableau, try to turn over this tableau
        if (prevAction.getGameObject() == GameObject.TABLEAU) {
            if (getTableauAtPos(prevAction.getStackIndex()).turnOver()) {
                turnedOverTableau++;
                lastMoveturnedOverTableau = true;
            }
        }
        if (!redoMove) {
            cleanUpMoves();
            this.moves.add(new Move(prevAction, action, lastMoveturnedOverTableau));
            moves.lastElement().setOldfanSize(oldFanSize);
            moves.lastElement().setNewFanSize(newFanSize);
        }
        movePointer++;
        this.prevAction = null;
        undoMove = false;
        notifyListeners();
        notifyCallBackListener();
    }


    /**
     * resets prevAction and notifies observers, to be called if an action could not be handled
     * succesfully
     */
    private void failMove() {
        invalidMove = true;
        this.prevAction = null;
        notifyListeners();
    }

    /**
     * @return whether the last move was invalid
     */
    boolean wasInvalidMove() {
        if (invalidMove) {
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
                action.setCardIndex(getTableauAtPos(action.getStackIndex()).getFaceUp().size());
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

    private void notifyListeners() {
        for (GameListener gl : gameListeners) {
            gl.update(this);
        }
    }

    /**
     * @return true if the game is won
     */
    boolean isWon() {
        boolean allKings = true;
        for (Foundation f : foundations) {
            if (!f.isEmpty()) {
                if (f.getFoundationTop().getRank() != Rank.KING) {
                    allKings = false;
                }
            } else {
                allKings = false;
            }
        }
        return allKings;
    }

    void registerGameListener(GameListener gameListener) {
        this.gameListeners.add(gameListener);
    }

    void registerCallBackListener(CallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }

    private void notifyCallBackListener() {
        if (callBackListener != null) {
            callBackListener.isUndoRedoPossible(canUndo(), canRedo());
        }
    }

    /**
     * removes all moves which indices are greater than the movePointer
     */
    private void cleanUpMoves() {
        if (movePointer < moves.size() - 1) {
            for (int i = moves.size() - 1; i > movePointer; --i) {
                moves.removeElementAt(i);
            }
        }
    }

    /**
     * @return true if undoing is possible
     */
    boolean canUndo() {
        return movePointer >= 0;
    }

    /**
     * @return true if redoing is possible
     */
    boolean canRedo() {
        return movePointer < moves.size() - 1;
    }

    void undo() {
        if (canUndo()) {
            Move toUndo = moves.elementAt(movePointer);
            if (toUndo.getAction1().getGameObject() == GameObject.DECK) {
                undoDeck(toUndo);
            } else if (toUndo.getAction2().getGameObject() == GameObject.TABLEAU) {
                undoTableau(toUndo);
            } else if (toUndo.getAction2().getGameObject() == GameObject.FOUNDATION) {
                undoFoundation(toUndo);
            }
            movePointer--;
            undoMove = true;
            prevAction = null;
            notifyListeners();
            notifyCallBackListener();
        }

    }

    /**
     * undoes a deck move
     *
     * @param toUndo the move to be undone
     */
    private void undoDeck(Move toUndo) {
        if (toUndo.getAction2() != null) {
            deckAndWaste.undoReset(toUndo.getOldfanSize());
        } else {
            deckAndWaste.undoTurnOver(toUndo.getOldfanSize());
        }
    }

    /**
     * undoes a move which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableau(Move toUndo) {
        switch (toUndo.getAction1().getGameObject()) {
            case TABLEAU:
                undoTableauTableau(toUndo);
                break;
            case WASTE:
                undoTableauWaste(toUndo);
                break;
            case FOUNDATION:
                undoTableauFoundation(toUndo);
                break;
        }
    }

    /**
     * undoes a move which source and target were tableaus
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauTableau(Move toUndo) {
        Tableau sourceT = getTableauAtPos(toUndo.getAction1().getStackIndex());
        Tableau targetT = getTableauAtPos(toUndo.getAction2().getStackIndex());
        if (toUndo.isTurnOver()) {
            sourceT.undoturnOver();
            turnedOverTableau--;
        }
        sourceT.addFaceUpVector(targetT.removeFaceUpVector(toUndo.getAction2().getCardIndex()));
    }

    /**
     * undoes a move which source was the Waste and which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauWaste(Move toUndo) {
        Tableau targetT = getTableauAtPos(toUndo.getAction2().getStackIndex());
        deckAndWaste.getWaste().add(targetT.removeFaceUpVector(targetT.getFaceUp().size() - 1).firstElement());
        deckAndWaste.setFanSize(toUndo.getOldfanSize());
    }

    /**
     * undoes a move which source was a Foundation and which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauFoundation(Move toUndo) {
        Tableau targetT = getTableauAtPos(toUndo.getAction2().getStackIndex());
        Foundation sourceF = getFoundationAtPos(toUndo.getAction1().getStackIndex());
        sourceF.addCard(targetT.removeFaceUpVector(targetT.getFaceUp().size() - 1).lastElement());
    }

    /**
     * undoes a move which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundation(Move toUndo) {
        if (toUndo.getAction1().getGameObject() == GameObject.TABLEAU) {
            undoFoundationTableau(toUndo);
        } else if (toUndo.getAction1().getGameObject() == GameObject.WASTE) {
            undoFoundationWaste(toUndo);
        }
    }

    /**
     * undoes a move which source was a Tableau and which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundationTableau(Move toUndo) {
        Foundation targetF = getFoundationAtPos(toUndo.getAction2().getStackIndex());
        Tableau sourceT = getTableauAtPos(toUndo.getAction1().getStackIndex());
        if (toUndo.isTurnOver()) {
            sourceT.undoturnOver();
            turnedOverTableau--;
        }
        sourceT.addFaceUp(targetF.removeFoundationTop());
    }

    /**
     * undoes a move which source was the Waste and which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundationWaste(Move toUndo) {
        Foundation targetF = getFoundationAtPos(toUndo.getAction2().getStackIndex());
        deckAndWaste.getWaste().add(targetF.removeFoundationTop());
        deckAndWaste.setFanSize(toUndo.getOldfanSize());
    }

    /**
     * redoes a move that was undone before
     */
    void redo() {
        if (canRedo()) {
            Move toRedo = moves.elementAt(movePointer + 1);
            handleAction(toRedo.getAction1(), true);
            if (toRedo.getAction1().getGameObject() != GameObject.DECK) {
                handleAction(toRedo.getAction2(), true);
            }
        }
    }
}
