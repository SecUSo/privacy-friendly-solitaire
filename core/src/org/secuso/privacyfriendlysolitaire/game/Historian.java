package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;

import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.HistorianListener;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author: M. Fischer
 * administrates a collection of snapshots of game states to provide undo/redo functionality
 */
public class Historian implements GameListener {

    /**
     * a vector of snapshots of game states
     */
    private Vector<SolitaireGame> history;

    /**
     * the index of the current game state
     */
    private int currentStateIndex;

    private HistorianListener historianListener;

    public Historian() {
        history = new Vector<SolitaireGame>();
        currentStateIndex = -1;
    }


    @Override
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            cleanUpHistory();
            history.add(cloneGame(game));
            currentStateIndex++;
            notifyListener();
        }
    }

    /**
     * deletes all game states which indices are greater than the currentStateIndex
     */
    private void cleanUpHistory() {
        if (currentStateIndex < history.size() - 1) {
            for (int i = history.size() - 1; i > currentStateIndex; --i) {
                history.remove(i);
            }
        }
    }

    /**
     * checks if undoing is possible
     *
     * @return true if a at least one game state previous to the current exists
     */
    public boolean canUndo() {
        return currentStateIndex > 0;
    }

    /**
     * checks if redoing is possible
     *
     * @return true if at least one game state subsequent to the current exists
     */
    public boolean canRedo() {
        return currentStateIndex < (history.size() - 1);
    }

    /**
     * @return a copy of the SolitaireGame object that represents the game state previous to the current
     */
    public SolitaireGame undo() {
        currentStateIndex--;
        notifyListener();
        return history.get(currentStateIndex);
    }

    /**
     * @return a copy of the SolitaireGame object that represents the game state subsequent to the current
     */
    public SolitaireGame redo() {
        currentStateIndex++;
        notifyListener();
        return history.get(currentStateIndex);
    }

    public void registerHistorianListener(HistorianListener historianListener) {
        this.historianListener = historianListener;
    }

    void notifyListener() {
        if (historianListener != null) {
            historianListener.possibleActions(canUndo(), canRedo());
        }
    }

    private SolitaireGame cloneGame(SolitaireGame sg) {
        //clone DeckWaste
        DeckWaste dollyDeckWaste = new DeckWaste(sg.getDeckWaste().getNumTurnOver(), sg.getDeckWaste().isVegas());
        //clone Deck
        Vector<Card> dollyDeck = new Vector<Card>();
        for (Card c : sg.getDeckWaste().getDeck()) {
            dollyDeck.add(new Card(c.getRank(), c.getSuit()));
        }
        dollyDeckWaste.setDeck(dollyDeck);
        //clone Waste
        Vector<Card> dollyWaste = new Vector<Card>();
        for (Card c : sg.getDeckWaste().getWaste()) {
            dollyWaste.add(new Card(c.getRank(), c.getSuit()));
        }
        dollyDeckWaste.setWaste(dollyWaste);
        //clone fansize
        dollyDeckWaste.setFanSize(sg.getDeckWaste().getFanSize());

        //clone foundations
        ArrayList<Foundation> dollyFoundations = new ArrayList<Foundation>();
        for (Foundation f : sg.getFoundations()) {
            Vector<Card> curDollyCards = new Vector<Card>();
            for (Card c : f.getCards()) {
                curDollyCards.add(new Card(c.getRank(), c.getSuit()));
            }
            Foundation curDollyFoundation = new Foundation();
            curDollyFoundation.setCards(curDollyCards);
            curDollyFoundation.setSuit(f.getSuit());
            dollyFoundations.add(curDollyFoundation);
        }

        //clone Tableaus
        ArrayList<Tableau> dollyTableaus = new ArrayList<Tableau>();
        for (Tableau t : sg.getTableaus()) {
            Tableau curDollyTableau = new Tableau();
            //clone face down
            Vector<Card> curdollyFaceDown = new Vector<Card>();
            for (Card c : t.getFaceDown()) {
                curdollyFaceDown.add(new Card(c.getRank(), c.getSuit()));
            }
            curDollyTableau.setFaceDown(curdollyFaceDown);
            //clone face up
            Vector<Card> curdollyFaceUp = new Vector<Card>();
            for (Card c : t.getFaceUp()) {
                curdollyFaceUp.add(new Card(c.getRank(), c.getSuit()));
            }
            curDollyTableau.setFaceUp(curdollyFaceUp);
            dollyTableaus.add(curDollyTableau);
        }

        SolitaireGame dolly = new SolitaireGame(dollyDeckWaste, dollyFoundations, dollyTableaus);
        Action prevAction = sg.getPrevAction();
        if (prevAction != null) {
            dolly.setPrevAction(new Action(prevAction.getGameObject(), prevAction.getStackIndex(), prevAction.getCardIndex()));
        }

        //clone Moves
        Vector<Move> dollyMoves = new Vector<Move>();
        for (Move m : sg.getMoves()) {
            //clone actions
            Action action1 = m.getAction1();
            Action dollyAction1;
            if (action1 != null) {
                dollyAction1 = new Action(action1.getGameObject(), action1.getStackIndex(), action1.getCardIndex());
            } else {
                dollyAction1 = null;
            }
            Action action2 = m.getAction2();
            Action dollyAction2;
            if (action2 != null) {
                dollyAction2 = new Action(action2.getGameObject(), action2.getStackIndex(), action2.getCardIndex());
            } else {
                dollyAction2 = null;
            }
            //clone move
            Move dollyMove = new Move(dollyAction1, dollyAction2);
            dollyMoves.add(dollyMove);
        }
        dolly.setMoves(dollyMoves);

        dolly.setTurnedOverTableau(sg.getTurnedOverTableau());
        dolly.setInvalidMove(sg.isInvalidMove());
        dolly.setLastMoveturnedOverTableau(sg.isLastMoveturnedOverTableau());

        return dolly;
    }
}
