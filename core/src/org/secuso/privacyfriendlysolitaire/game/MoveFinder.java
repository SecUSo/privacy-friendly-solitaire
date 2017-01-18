package org.secuso.privacyfriendlysolitaire.game;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;


import java.util.Vector;

/**
 * @author: M. Fischer
 */

public class MoveFinder {

    /**
     * @param game the SolitaireGame in which a Move shall be found
     * @return a possible Move to progress the Game or null if none could be found
     */
    public static Move findMove(SolitaireGame game) {
        Move foundMove = findMoveTableauToFoundation(game);
        if (foundMove != null) {
            return foundMove;
        }
        foundMove = findMoveTableauToTableau(game);
        if (foundMove != null) {
            return foundMove;
        }
        foundMove = findMoveWasteToTableau(game);
        if (foundMove != null) {
            return foundMove;
        }
        foundMove = findMoveWasteToFoundation(game);
        if (foundMove != null) {
            return foundMove;
        }
        foundMove = findMoveFoundationToTableau(game);
        if (foundMove != null) {
            return foundMove;
        }
        foundMove = findMoveDeck(game);
        if (foundMove != null) {
            return foundMove;
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Tableau to Foundation shall be found
     * @return a possible Move from Tableau to Foundation or null if none could be found
     */
    public static Move findMoveTableauToFoundation(SolitaireGame game) {
        for (int t = 0; t < game.getTableaus().size(); t++) {
            if (game.getTableauAtPos(t).getFaceUp().isEmpty()) {
                continue;
            }
            for (int f = 0; f < game.getFoundations().size(); f++) {
                if (game.getFoundationAtPos(f).canAddCard(game.getTableauAtPos(t).getFaceUp().lastElement())) {
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
        for (int sourceT = 0; sourceT < game.getTableaus().size(); sourceT++) {
            if (game.getTableauAtPos(sourceT).getFaceUp().isEmpty()) {
                continue;
            }
            for (int targetT = 0; targetT < game.getTableaus().size(); targetT++) {
                if (sourceT == targetT) {
                    continue;
                }
                for (int cardIndex = 0; cardIndex < game.getTableauAtPos(targetT).getFaceUp().size(); cardIndex++) {
                    Vector<Card> toBeMoved = game.getTableauAtPos(sourceT).getCopyFaceUpVector(cardIndex);
                    if (game.getTableauAtPos(targetT).isAddingFaceUpVectorPossible(toBeMoved)) {
                        Action sourceAction = new Action(GameObject.TABLEAU, sourceT, cardIndex);
                        Action targetAction = new Action(GameObject.TABLEAU, targetT, 0);
                        return new Move(sourceAction, targetAction);
                    }
                }
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
                if (game.getTableauAtPos(t).isAddingFaceUpVectorPossible(toBeMoved)) {
                    Action sourceAction = new Action(GameObject.WASTE, 0, 0);
                    Action targetAction = new Action(GameObject.TABLEAU, t, 0);
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
