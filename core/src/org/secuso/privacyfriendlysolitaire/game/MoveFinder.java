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
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;


import java.util.Vector;

/**
 * @author M. Fischer
 *         <p>
 *         class to find possible moves in a given game
 */

class MoveFinder {
    private static int nrCardsInDeck = 52;
    private static int nrOfConsecutiveMovesThroughDeck = 0;

    /**
     * @param game the SolitaireGame in which a Move shall be found
     * @return a possible Move to progress the Game or null if none could be found
     */
    public static Move findMove(SolitaireGame game, CallBackListener listener) {
        nrCardsInDeck = game.getDeckWaste().getSizeOfDeckAndWaste();
        checkWhetherNoMoves(listener);

        Move foundMove = findMoveTableauToFoundation(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveWasteToTableau(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveWasteToFoundation(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveTableauToTableau(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveDeck(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck++;
            return foundMove;
        }
        return null;
    }


    /**
     * resets the nrOfConsecutiveMovesThroughDeck to 0, if another moves was made
     */
    protected static void resetNrOfMovesThroughDeck() {
        nrOfConsecutiveMovesThroughDeck = 0;
    }


    /**
     * check whether there are no more moves (we already clicked through the deck as often as
     * there are cards in there)
     *
     * @param listener the CallBackListener, that should react if the game is lost
     */
    private static void checkWhetherNoMoves(CallBackListener listener) {
        if (nrOfConsecutiveMovesThroughDeck > nrCardsInDeck) {
            listener.onLost();
        }
    }


    /**
     * @param game the SolitaireGame in which a Move from Tableau to Foundation shall be found
     * @return a possible Move from Tableau to Foundation or null if none could be found
     */
    static Move findMoveTableauToFoundation(SolitaireGame game) {
        for (int t = 0; t < game.getTableaus().size(); t++) {
            if (game.getTableauAtPos(t).getFaceUp().isEmpty()) {
                continue;
            }
            for (int f = 0; f < game.getFoundations().size(); f++) {
                if (game.getFoundationAtPos(f).canAddCard(game.getTableauAtPos(t).getFaceUp().lastElement())) {
                    //check if reversal of previous move
                    if (!game.getMoves().isEmpty()) {
                        Move prevMove = game.getMoves().get(game.getMovePointer());
                        if (prevMove.getAction1().getGameObject() == GameObject.FOUNDATION &&
                                prevMove.getAction2().getGameObject() == GameObject.TABLEAU &&
                                prevMove.getAction1().getStackIndex() == f &&
                                prevMove.getAction2().getStackIndex() == t) {
                            continue;
                        }
                    }
                    int tableauCardIndex = game.getTableauAtPos(t).getFaceUp().size() - 1;
                    Action sourceAction = new Action(GameObject.TABLEAU, t, tableauCardIndex);
                    Action targetAction = new Action(GameObject.FOUNDATION, f, 0);
                    return new Move(sourceAction, targetAction);
                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Tableau to Tableau shall be found
     * @return a possible Move from Tableau to Tableau or null if none could be found
     */
    private static Move findMoveTableauToTableau(SolitaireGame game) {
        for (int sourceT = 0; sourceT < Constants.NR_OF_TABLEAUS; sourceT++) {
            Tableau sourceTab = game.getTableauAtPos(sourceT);
            if (sourceTab.getFaceUp().isEmpty()) {
                continue;
            }
            for (int targetT = 0; targetT < Constants.NR_OF_TABLEAUS; targetT++) {
                if (sourceT == targetT) {
                    continue;
                }
                Tableau targetTab = game.getTableauAtPos(targetT);
                if (targetTab.getFaceDown().isEmpty() && sourceTab.getFaceDown().isEmpty()) {
                    continue;
                }
                int sourceCardIndex = 0;
//                for (int sourceCardIndex = 0; sourceCardIndex < sourceTab.getFaceUp().size(); sourceCardIndex++) {
                Vector<Card> toBeMoved = sourceTab.getCopyFaceUpVector(sourceCardIndex);
                if (targetTab.isAddingFaceUpVectorPossible(toBeMoved)) {
                    //check if reversal of previous move
                    if (!game.getMoves().isEmpty()) {
                        Move prevMove = game.getMoves().get(game.getMovePointer());
                        if (prevMove.getAction1().getGameObject() == GameObject.TABLEAU &&
                                prevMove.getAction2().getGameObject() == GameObject.TABLEAU &&
                                prevMove.getAction1().getStackIndex() == targetT &&
                                prevMove.getAction2().getStackIndex() == sourceT &&
                                !game.isLastMoveturnedOverTableau()) {
                            continue;
                        }
                    }
                    Action sourceAction = new Action(GameObject.TABLEAU, sourceT, sourceCardIndex);
                    Action targetAction = new Action(GameObject.TABLEAU, targetT, 0);
                    return new Move(sourceAction, targetAction);
                }
//                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Waste to Tableau shall be found
     * @return a possible Move from Waste to Tableau or null if none could be found
     */
    private static Move findMoveWasteToTableau(SolitaireGame game) {
        if (!game.getDeckWaste().isWasteEmpty()) {
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(game.getDeckWaste().getWasteTop());
            for (int t = 0; t < game.getTableaus().size(); t++) {
                Tableau targetTab = game.getTableauAtPos(t);

                if (targetTab.isAddingFaceUpVectorPossible(toBeMoved)) {
                    Action sourceAction = new Action(GameObject.WASTE, 0, 0);
                    Action targetAction;
                    int nrOfFaceUpInTarget = targetTab.getFaceUp().size();
                    if (nrOfFaceUpInTarget == 0) {
                        targetAction = new Action(GameObject.TABLEAU, t, -1);
                    } else {
                        targetAction = new Action(GameObject.TABLEAU, t, nrOfFaceUpInTarget - 1);
                    }
                    return new Move(sourceAction, targetAction);
                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Waste to Foundation shall be found
     * @return a possible Move from Waste to Foundation or null if none could be found
     */
    private static Move findMoveWasteToFoundation(SolitaireGame game) {
        if (!game.getDeckWaste().isWasteEmpty()) {
            Card toBeMoved = game.getDeckWaste().getWasteTop();
            for (int f = 0; f < game.getFoundations().size(); f++) {
                if (game.getFoundationAtPos(f).canAddCard(toBeMoved)) {
                    Action sourceAction = new Action(GameObject.WASTE, 0, 0);
                    Action targetAction = new Action(GameObject.FOUNDATION, f, 0);
                    return new Move(sourceAction, targetAction);
                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Foundation to Tableau shall be found
     * @return a possible Move from Foundation to Tableau or null if none could be found
     */
    private static Move findMoveFoundationToTableau(SolitaireGame game) {
        for (int f = 0; f < game.getFoundations().size(); f++) {
            if (game.getFoundationAtPos(f).isEmpty()) {
                continue;
            }
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(game.getFoundationAtPos(f).getCards().lastElement());
            for (int t = 0; t < game.getTableaus().size(); t++) {
                if (game.getTableauAtPos(t).isAddingFaceUpVectorPossible(toBeMoved)) {
                    //check if reversal of previous move
                    Move prevMove = game.getMoves().get(game.getMovePointer());
                    if (prevMove.getAction1().getGameObject() == GameObject.TABLEAU &&
                            prevMove.getAction2().getGameObject() == GameObject.FOUNDATION &&
                            prevMove.getAction1().getStackIndex() == t &&
                            prevMove.getAction2().getStackIndex() == f) {
                        continue;
                    }
                    Action sourceAction = new Action(GameObject.FOUNDATION, f, 0);
                    Action targetAction = new Action(GameObject.TABLEAU, t, 0);
                    return new Move(sourceAction, targetAction);
                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move involving the Deck shall be found
     * @return a possible Move involving the Deck or null if none could be found
     */
    private static Move findMoveDeck(SolitaireGame game) {
        Move foundMove = null;
        Action action = new Action(GameObject.DECK, 0, 0);
        if (game.getDeckWaste().canTurnOver()) {
            foundMove = new Move(action, null);
        } else if (game.getDeckWaste().canReset()) {
            foundMove = new Move(action, action);
        }
        return foundMove;
    }

}
