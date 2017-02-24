package org.secuso.privacyfriendlysolitaire.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;
import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_TABLEAUS;

/**
 * @author I. Dix
 *         <p>
 *         the view manages the actors on stage (the stage is given to it by the application). It observes
 *         the model (SolitaireGame game) and reacts to changes in the model by re-arranging the actors.
 *         The newly arranged actors are then drawn by the application
 */

public class View implements GameListener {
    private boolean widthHeightOfCardSet = false;
    private final ImageLoader loader = new ImageLoader();

    private final Stage stage;
    private final ImageWrapper marker;
    private final ImageWrapper backsideCardOnDeck;

    private final HashMap<String, ImageWrapper> faceUpCards = new HashMap<String, ImageWrapper>(52);
    private final List<ImageWrapper> faceDownCards = new ArrayList<ImageWrapper>(21);
    // describes the y at which the given tableau is positioned at the smallest
    private final HashMap<Integer, Float> smallestYForTableau = new HashMap<Integer, Float>(7);

    public View(SolitaireGame game, Stage stage) {
        this.stage = stage;
        initialiseViewConstants();

        // add mark and make it invisible
        marker = loader.getMarkImage();
        marker.setWidth(ViewConstants.scalingWidthMarker * ViewConstants.widthOneSpace);
        marker.setHeight(ViewConstants.scalingHeightMarker * ViewConstants.heightOneSpace);
        marker.setVisible(false);
        stage.addActor(marker);

        // add emptySpaceForDeck and make it invisible
        backsideCardOnDeck = loader.getBacksideImage();

        arrangeInitialView(game);
    }

    private void initialiseViewConstants() {
        // screen scale
        ViewConstants.widthScreen = Gdx.graphics.getWidth();
        ViewConstants.heightScreen = Gdx.graphics.getHeight();
        ViewConstants.widthOneSpace = ViewConstants.widthScreen / 31;
        ViewConstants.heightOneSpace = ViewConstants.heightScreen / 21;

        // positions
        ViewConstants.WasteDeckFoundationY = 16 * ViewConstants.heightOneSpace;
        // different x positions for different fanSizes in the waste
        // fan.size=1
        ViewConstants.WasteX1Fan = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace;
        // fan.size=2
        ViewConstants.WasteX2Fan1 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace -
                0.2f * ViewConstants.widthOneSpace;
        ViewConstants.WasteX2Fan2 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace +
                0.2f * ViewConstants.widthOneSpace;
        // fan.size=3
        ViewConstants.WasteX3Fan1 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace -
                0.4f * ViewConstants.widthOneSpace;
        ViewConstants.WasteX3Fan2 = ViewConstants.WasteX1Fan;
        ViewConstants.WasteX3Fan3 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace +
                0.4f * ViewConstants.widthOneSpace;

        ViewConstants.DeckX = (2 + 6 * (1 + 3)) * ViewConstants.widthOneSpace;
        ViewConstants.TableauFoundationX = new float[7];
        for (int i = 0; i < 7; i++) {
            ViewConstants.TableauFoundationX[i] = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
        }
        ViewConstants.TableauBaseY = 10.5f * ViewConstants.heightOneSpace;
    }


    // ------------------------------------ Initial ------------------------------------
    private void arrangeInitialView(SolitaireGame game) {
        paintInitialFoundations(game.getFoundations());
        paintInitialTableaus(game.getTableaus());
        paintInitialDeckWaste(game.getDeckWaste());
    }

    private void paintInitialFoundations(ArrayList<Foundation> foundations) {
        for (int i = 0; i < 4; i++) {
            // paint empty spaces
            ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null,
                    ViewConstants.TableauFoundationX[i], ViewConstants.WasteDeckFoundationY, -1, -1);

            // paint foundations
            Foundation foundation = foundations.get(i);
            for (int j = 0; j < foundation.getCards().size(); j++) {
                Card c = foundation.getCards().get(j);
                ImageWrapper card = loadActorForCardAndSaveInMap(c);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card, GameObject.FOUNDATION,
                        ViewConstants.TableauFoundationX[i], ViewConstants.WasteDeckFoundationY,
                        i, -1);
            }
        }
    }

    private void paintInitialTableaus(ArrayList<Tableau> tableaus) {
//        Gdx.app.log("TableauBaseY ", String.valueOf(ViewConstants.TableauBaseY));
//        Gdx.app.log("biggestY ", String.valueOf(ViewConstants.TableauBaseY + ViewConstants.heightCard - 1));
        for (int i = 0; i < Constants.NR_OF_TABLEAUS; i++) {
            Tableau t = tableaus.get(i);

            float x = ViewConstants.TableauFoundationX[i];

            // add empty space beneath
            ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null, x,
                    10.5f * ViewConstants.heightOneSpace, -1, -1);

            // add face-down cards
            int faceDownSize = t.getFaceDown().size();
            for (int j = 0; j < faceDownSize; j++) {
                ImageWrapper faceDownCard = loader.getBacksideImage();
                float y = 10.5f * ViewConstants.heightOneSpace - (j * ViewConstants.offsetHeightBetweenCards);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceDownCard, GameObject.TABLEAU, x, y, i, j);

                // add to faceDownCards (so it can be destroyed later, when it the card is turned)
                faceDownCards.add(faceDownCard);
            }

            // add face-up cards
            int faceUpSize = t.getFaceUp().size();
            for (int j = 0; j < faceUpSize; j++) {
                ImageWrapper faceUpCard = loadActorForCardAndSaveInMap(t.getFaceUp().get(j));
                // y position is dependant on nr in faceDown-Vector
                float y = 10.5f * ViewConstants.heightOneSpace -
                        ((faceDownSize + j) * ViewConstants.offsetHeightBetweenCards);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceUpCard, GameObject.TABLEAU, x, y, i, t.getFaceDown().size());
            }

            setNewSmallestY(i, t);
        }
    }

    private void paintInitialDeckWaste(DeckWaste deckWaste) {
        // ----- waste -----
        // draw empty space card
        ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null,
                ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, -1, -1);

        // then draw the open fan
        paintWaste(deckWaste, true, false);


        // ----- deck -----
        ImageWrapper emptySpaceDeck = loader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpaceDeck, null,
                ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(backsideCardOnDeck, GameObject.DECK,
                ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
        if (deckWaste.getDeck().isEmpty()) {
            backsideCardOnDeck.setVisible(false);
        }
    }


    // ------------------------------------ Update ------------------------------------

    /**
     * method to react to changes in the model
     *
     * @param game the observed object (in this case a solitairegame)
     */
    @Override
    public void update(SolitaireGame game) {
        Action prevAction = game.getPrevAction();

        // get whether this was a marking action
        if (prevAction != null) {
            int stackIndex = prevAction.getStackIndex();

            List<Card> cardsToBeMarked = new ArrayList<Card>();

            switch (prevAction.getGameObject()) {
                case TABLEAU:
                    Vector<Card> faceUpList = game.getTableauAtPos(stackIndex).getFaceUp();
                    cardsToBeMarked = faceUpList.subList(prevAction.getCardIndex(), faceUpList.size());
                    break;
                case FOUNDATION:
                    cardsToBeMarked.add(game.getFoundationAtPos(stackIndex).getFoundationTop());
                    break;
                case WASTE:
                    cardsToBeMarked.add(game.getDeckWaste().getWasteTop());
                    break;
            }

            List<String> textureStrings = new ArrayList<String>(cardsToBeMarked.size());
            for (Card c : cardsToBeMarked) {
                textureStrings.add(loader.getCardTextureName(c));
            }

            markCards(textureStrings);
        }
        // or a move
        else {
            // with successful or invalid move, remove marker
            marker.setVisible(false);

            try {
                if (!game.wasInvalidMove()) {
                    if (!game.wasUndoMove()) {
                        // usual move
                        Move prevMove = game.getMoves().elementAt(game.getMovePointer());
                        handleMove(prevMove, game);
                    } else {
                        // undo move
                        Move undoMove = game.getMoves().elementAt(game.getMovePointer() + 1);
                        handleUndoMove(undoMove, game);
                    }
                }
            } catch (Exception e) {
                Gdx.app.log("Error", e.getClass().toString() + ": " + e.getMessage() + ", probably an invalid move");
                e.printStackTrace();

            }
        }
//        Gdx.app.log("---VIEW--- game after ", game.toString());

        // TODO: delete later, only for debug reasons
        checkModelAndViewCorrect(game);
    }


    private void checkModelAndViewCorrect(SolitaireGame game) {
        Vector<Card> waste = game.getDeckWaste().getWaste();
        Vector<Card> deck = game.getDeckWaste().getDeck();

        // check deck
        for (Card deckC : deck) {
            String texString = loader.getCardTextureName(deckC);
            ImageWrapper card = faceUpCards.get(texString);

            if (card != null && card.isVisible()) {
//                try {
                throw new RuntimeException("Karte " + texString + " ist sichtbar, " +
                        "aber sollte unsichtbar sein");
//                } catch (Exception e) {
//                    Gdx.app.log("Exception ", e.getMessage());
//                    Gdx.app.log("Deck ", deck.toString());
//                    Gdx.app.log("Waste ", waste.toString());
//                }
            }
        }

        // check waste
        for (Card wasteC : waste) {
            String texString = loader.getCardTextureName(wasteC);
            ImageWrapper card = faceUpCards.get(texString);

            if (card == null) {
                throw new RuntimeException("Karte " + texString + " war null");
            } else if (!card.isVisible()) {
                throw new RuntimeException("Karte " + texString + " ist unsichtbar, " +
                        "aber sollte sichtbar sein");
            }
        }

        // check foundations
        for (int stack = 0; stack < NR_OF_FOUNDATIONS; stack++) {
            Foundation found = game.getFoundationAtPos(stack);

            for (Card foundC : found.getCards()) {
                String texString = loader.getCardTextureName(foundC);
                ImageWrapper card = faceUpCards.get(texString);

                if (card == null) {
                    throw new RuntimeException("Karte " + texString + " war null");
                } else {
                    assert (card.isVisible());
                    assert (card.getWrapperStackIndex() == stack);
                    assert (card.getGameObject().equals(GameObject.FOUNDATION));
                    assert (Math.abs(card.getX() - ViewConstants.TableauFoundationX[stack]) <= 1);
                    assert (Math.abs(card.getY() - ViewConstants.WasteDeckFoundationY) <= 1);
                }
            }
        }

        // check tableaus
        for (int stack = 0; stack < NR_OF_TABLEAUS; stack++) {
            Tableau tab = game.getTableauAtPos(stack);
            Vector<Card> faceDowns = tab.getFaceDown();
            Vector<Card> faceUps = tab.getFaceUp();

            for (int cardIndex = 0; cardIndex < faceDowns.size(); cardIndex++) {
                ImageWrapper backside = getBackSideCardForStackAndCardIndex(stack, cardIndex);
                if (backside == null) {
                    throw new RuntimeException("Backside an stack=" + stack + " und card=" +
                            cardIndex + " war null");
                } else {
                    assert (backside.isVisible());
                    assert (backside.getGameObject().equals(GameObject.TABLEAU));
                    assert (Math.abs(backside.getX() - ViewConstants.TableauFoundationX[stack]) <= 1);
                    float shouldBeY = ViewConstants.TableauBaseY -
                            (cardIndex * ViewConstants.offsetHeightBetweenCards);
                    assert (Math.abs(backside.getY() - shouldBeY) <= 1);
                }
            }


            for (int cardIndex = 0; cardIndex < faceUps.size(); cardIndex++) {
                Card faceU = faceUps.get(cardIndex);
                String texString = loader.getCardTextureName(faceU);
                ImageWrapper card = faceUpCards.get(texString);

                if (card == null) {
                    throw new RuntimeException("Karte " + texString + " war null");
                } else {
                    assert (card.isVisible());
                    assert (card.getWrapperStackIndex() == stack);
                    assert (card.getWrapperCardIndex() == cardIndex);
                    assert (card.getGameObject().equals(GameObject.TABLEAU));
                    assert (Math.abs(card.getX() - ViewConstants.TableauFoundationX[stack]) <= 1);
                    float shouldBeY = ViewConstants.TableauBaseY -
                            ((cardIndex + faceDowns.size()) * ViewConstants.offsetHeightBetweenCards);
                    assert (Math.abs(card.getY() - shouldBeY) <= 1);
                }
            }
        }


    }


    // ---------------------------- ACTIONS ----------------------------
    private void markCards(List<String> textureStrings) {
        List<ImageWrapper> cardsToBeMarked = new ArrayList<ImageWrapper>(textureStrings.size());
        for (String texString : textureStrings) {
            cardsToBeMarked.add(faceUpCards.get(texString));
        }

        if (!cardsToBeMarked.isEmpty()) {
            // move marker to correct position and make visible
            ImageWrapper topElement = cardsToBeMarked.get(cardsToBeMarked.size() - 1);
            ImageWrapper bottomElement = cardsToBeMarked.get(0);
            float height = Math.abs(topElement.getY() - bottomElement.getTop()) + 10;

            marker.setPosition(topElement.getX() - 4, topElement.getY() - 5);
            marker.setHeight(height);
            marker.setVisible(true);
            marker.toFront();


            // move the card to the front
            for (ImageWrapper cardToBeMarked : cardsToBeMarked) {
                cardToBeMarked.toFront();
            }
        } else {
            throw new RuntimeException("Card to be marked could not be found! Should not happen! Probably an error in the view.");
        }
    }


    // ---------------------------- MOVES ----------------------------
    private void handleMove(Move move, SolitaireGame game) {
        Action ac1 = move.getAction1();
        Action ac2 = move.getAction2();

        int sourceStack = ac1.getStackIndex();
        int sourceCard = ac1.getCardIndex();
        int targetStack = -1, targetCard = -1, nrOfFaceDownInSourceTableauAfterChange = -1;
        if (ac2 != null) {
            targetStack = ac2.getStackIndex();
            targetCard = ac2.getCardIndex();
        }

        // in order to understand the following code, it is important to understand, that the model
        // has already performed the change
        // => therefore we find the moved card already at the new position
        switch (ac1.getGameObject()) {
            // possibilities: Deck -> Waste, Deck-Reset
            // both are initiated by a click on the deck and therefore have the deck as ac1
            case DECK:
                // if after the move was handled (in the game) the waste is empty, this was a reset
                if (game.getDeckWaste().isWasteEmpty()) {
                    resetDeck();
                } else {
                    turnOrUnturnDeckCard(game);
                }
                break;


            // possibilities: Waste -> Tableau, Waste -> Foundation
            case WASTE:
                // ------------------------ W -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    Tableau tab = game.getTableauAtPos(targetStack);
                    String textureStringOldWasteTop =
                            loader.getCardTextureName(tab.getFaceUp().get(targetCard + 1));

                    Card targetOldTopCard = null;
                    // after moving the waste-card here, this is no more the top
                    // this can be null, if the waste-card was moved to an empty tableau
                    String textureStringOldTableauTop = null;
                    try {
                        targetOldTopCard = game.getTableauAtPos(targetStack).getFaceUp().get(targetCard);
                    } catch (Exception e) {
                        // leave at null
                    }

                    if (targetOldTopCard != null) {
                        textureStringOldTableauTop = loader.getCardTextureName(targetOldTopCard);
                    }
                    int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDown().size();

                    makeMoveWasteToTableau(textureStringOldWasteTop, textureStringOldTableauTop,
                            targetStack, targetCard, nrOfFaceDownInTargetTableau);

                    // set new smallestY for target
                    setNewSmallestY(targetStack, tab);
                }
                // ------------------------ W -> F ------------------------
                else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    String textureStringOldWasteTop =
                            loader.getCardTextureName(game.getFoundationAtPos(targetStack).getFoundationTop());

                    makeMoveWasteToFoundation(textureStringOldWasteTop, targetStack);
                }

                paintWaste(game.getDeckWaste(), false, true);
                break;

            // possibilities: Tableau -> Tableau, Tableau -> Foundation
            case TABLEAU:
                Tableau tabAtSourceStack = game.getTableauAtPos(sourceStack);

                nrOfFaceDownInSourceTableauAfterChange =
                        tabAtSourceStack.getFaceDown().size();
                // the card beneath the sourceCard,
                // it may be null if after the move, the tableau has become empty
                Card cardBeneathSource = null;
                try {
                    cardBeneathSource = tabAtSourceStack.getFaceUp().get(ac1.getCardIndex());
                } catch (ArrayIndexOutOfBoundsException e) {
                    // leave at null
                }

                // ------------------------ T -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                    Vector<Card> faceUpAtTargetStack = tabAtTargetStack.getFaceUp();

                    int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDown().size();
                    // distinguish empty target tab from tab with exactly one card
                    targetCard--;

                    List<String> textureStringsMovedCards = new ArrayList<String>();
                    for (int i = targetCard + 1; i < faceUpAtTargetStack.size(); i++) {
                        Card cardToBeMoved = faceUpAtTargetStack.get(i);
                        textureStringsMovedCards.add(loader.getCardTextureName(cardToBeMoved));
                    }

                    Card targetOldTopCard = null;
                    String textureStringOldTableauTop = null;
                    try {
                        targetOldTopCard = faceUpAtTargetStack.get(targetCard);
                    } catch (Exception e) {
                        // leave at null
                    }

                    if (targetOldTopCard != null) {
                        textureStringOldTableauTop = loader.getCardTextureName(targetOldTopCard);
                    }

                    makeMoveTableauToTableau(textureStringsMovedCards, textureStringOldTableauTop,
                            cardBeneathSource, sourceStack, sourceCard, targetStack, targetCard,
                            nrOfFaceDownInSourceTableauAfterChange, nrOfFaceDownInTargetTableau);

                    // set new smallestY for target
                    setNewSmallestY(targetStack, tabAtTargetStack);
                }
                // ------------------------ T -> F ------------------------
                else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    String textureStringTableauSource = loader.getCardTextureName(
                            game.getFoundationAtPos(targetStack).getFoundationTop());

                    makeMoveTableauToFoundation(textureStringTableauSource, cardBeneathSource,
                            sourceStack, sourceCard, targetStack, nrOfFaceDownInSourceTableauAfterChange);
                }

                // set new smallestY for source
                setNewSmallestY(sourceStack, tabAtSourceStack);
                break;

            // possibilities: Foundation -> Tableau
            case FOUNDATION:
                // ------------------------ F -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                    Vector<Card> faceUpAtTargetStack = tabAtTargetStack.getFaceUp();

                    // after moving the card the old foundation top is now on top the tableau
                    // (on top the targetCard)
                    String textureStringFoundationSource = loader.getCardTextureName(
                            faceUpAtTargetStack.get(targetCard + 1));

                    String textureStringTableauTarget = null;
                    if (tabAtTargetStack.getNrOfAllCards() != 1) {
                        textureStringTableauTarget = loader.getCardTextureName(
                                faceUpAtTargetStack.get(targetCard));
                    }
                    int nrOfFaceDownInTargetTableau =
                            tabAtTargetStack.getFaceDown().size();

                    makeMoveFoundationToTableau(textureStringFoundationSource,
                            textureStringTableauTarget, targetStack, targetCard,
                            nrOfFaceDownInTargetTableau);

                    // set new smallestY for target
                    setNewSmallestY(targetStack, tabAtTargetStack);
                }
                break;
        }
    }


    private void turnOrUnturnDeckCard(SolitaireGame game) {
        turnOrUnturnDeckCard(game, null);
    }


    private void turnOrUnturnDeckCard(SolitaireGame game, Vector<String> cardsToBeUnturned) {
        DeckWaste deckWaste = game.getDeckWaste();

        paintWaste(deckWaste, false, false);

        if (cardsToBeUnturned != null) {
            for (String texString : cardsToBeUnturned) {
                try {
                    faceUpCards.get(texString).setVisible(false);
                } catch (Exception e) {
                    // in this case we added to many cards into cardsToBeUnturned
                }
            }
        }

        // check if this was the last
        if (deckWaste.getDeck().isEmpty()) {
            backsideCardOnDeck.setVisible(false);
        } else {
            backsideCardOnDeck.setVisible(true);
        }
    }

    /**
     * paints the waste in its current state
     *
     * @param deckWaste              the deckWaste object from the game
     * @param isInitialization       a boolean depicting whether this was called by paintInitialDeckWaste
     *                               (true) or turnOrUnturnDeckCard (false)
     * @param fanCardsToBeRearranged a boolean depicting whether this was called by paintInitialDeckWaste
     *                               (true) or turnOrUnturnDeckCard (false)
     */
    private void paintWaste(DeckWaste deckWaste, boolean isInitialization,
                            boolean fanCardsToBeRearranged) {
        Gdx.app.log("paintWaste ", deckWaste.toString());
        // draw first few cards before the open fan
        Vector<Card> waste = deckWaste.getWaste();
        for (int i = 0; i < waste.size() - deckWaste.getFanSize(); i++) {
            Card c = waste.get(i);
            String textureName = loader.getCardTextureName(c);

            ImageWrapper wasteCard;
            if (isInitialization) {
                wasteCard = loadActorForCardAndSaveInMap(c);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(wasteCard, GameObject.WASTE,
                        ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, -1, 5);
            } else {
                wasteCard = faceUpCards.get(textureName);

                moveCard(ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, wasteCard, 5,
                        false);
            }
        }

        // get nr of open cards in fan
        int fanSize = deckWaste.getFanSize();
        Vector<ImageWrapper> fanImages = new Vector<ImageWrapper>(3);


        // draw fan if it is bigger than 0
        if (fanSize > 0) {
            for (int i = fanSize - 1; i >= 0; i--) {
                Card toBeAdded = waste.get(waste.size() - i - 1);
                String textureName = loader.getCardTextureName(toBeAdded);
                ImageWrapper turnedCard = faceUpCards.get(textureName);

                if (turnedCard == null) {
                    turnedCard = loadActorForCardAndSaveInMap(toBeAdded);
                }
                fanImages.add(turnedCard);
            }

            if (fanSize == 1) {
                ImageWrapper card = fanImages.get(0);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, card, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card, GameObject.WASTE,
                            ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, -1, 5);
                }

                card.setVisible(true);
                card.toFront();

            } else if (fanSize == 2) {
                ImageWrapper card0 = fanImages.get(0);
                ImageWrapper card1 = fanImages.get(1);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX2Fan1, ViewConstants.WasteDeckFoundationY, card0, 5, true);
                    moveCard(ViewConstants.WasteX2Fan2, ViewConstants.WasteDeckFoundationY, card1, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card0, GameObject.WASTE,
                            ViewConstants.WasteX2Fan1, ViewConstants.WasteDeckFoundationY, -1, 5);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card1, GameObject.WASTE,
                            ViewConstants.WasteX2Fan2, ViewConstants.WasteDeckFoundationY, -1, 5);
                }

                card0.setVisible(true);
                card0.toFront();
                card1.setVisible(true);
                card1.toFront();

            } else if (fanSize == 3) {
                ImageWrapper card0 = fanImages.get(0);
                ImageWrapper card1 = fanImages.get(1);
                ImageWrapper card2 = fanImages.get(2);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX3Fan1, ViewConstants.WasteDeckFoundationY, card0, 5, true);
                    moveCard(ViewConstants.WasteX3Fan2, ViewConstants.WasteDeckFoundationY, card1, 5, true);
                    moveCard(ViewConstants.WasteX3Fan3, ViewConstants.WasteDeckFoundationY, card2, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card0, GameObject.WASTE,
                            ViewConstants.WasteX3Fan1, ViewConstants.WasteDeckFoundationY, -1, -1);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card1, GameObject.WASTE,
                            ViewConstants.WasteX3Fan2, ViewConstants.WasteDeckFoundationY, -1, -1);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card2, GameObject.WASTE,
                            ViewConstants.WasteX3Fan3, ViewConstants.WasteDeckFoundationY, -1, -1);
                }

                card0.setVisible(true);
                card0.toFront();
                card1.setVisible(true);
                card1.toFront();
                card2.setVisible(true);
                card2.toFront();
            }
        }
    }


    /**
     * reset the deck by 'moving all cards from waste to deck'
     * in our case this means simply setting the backsideCard on the deck visible and
     * all waste cards invisible
     */
    private void resetDeck() {
        backsideCardOnDeck.setVisible(true);

        // set all waste-cards invisible
        for (String textureName : faceUpCards.keySet()) {
            ImageWrapper potentiallyWasteCard = faceUpCards.get(textureName);
            if (potentiallyWasteCard.getGameObject().equals(GameObject.WASTE)) {
                potentiallyWasteCard.setVisible(false);
            }
        }
    }


    /**
     * the inverse of resetDeck, sets all waste cards visible and the backside of the deck invisible
     * is the undo of resetDeck
     */
    private void resetWaste() {
        backsideCardOnDeck.setVisible(false);

        // set all waste-cards invisible
        for (String textureName : faceUpCards.keySet()) {
            ImageWrapper potentiallyWasteCard = faceUpCards.get(textureName);
            if (potentiallyWasteCard.getGameObject().equals(GameObject.WASTE)) {
                potentiallyWasteCard.setVisible(true);
            }
        }
    }

    /**
     * sets a new smallest y for tableau at index stackIndex
     *
     * @param stackIndex the stackIndex is also the index where to put the new smallestY
     * @param tab        the tableau that has changed
     */
    private void setNewSmallestY(int stackIndex, Tableau tab) {
        int nrOfCardsInTableau = tab.getFaceUp().size() + tab.getFaceDown().size();
        if (nrOfCardsInTableau == 0 || nrOfCardsInTableau == 1) {
            smallestYForTableau.put(stackIndex, ViewConstants.TableauBaseY);
        } else {
            smallestYForTableau.put(stackIndex, ViewConstants.TableauBaseY -
                    (nrOfCardsInTableau - 1) * ViewConstants.offsetHeightBetweenCards);
        }
    }

    /**
     * move W->T
     *
     * @param sourceCardTextureString     the texture string of the card to be moved
     * @param targetCardTextureString     analogous to the sourceCardTextureString (may be null, if
     *                                    the target is an empty tableau)
     * @param targetStack                 the index of the target stack (in [0,6])
     * @param targetCardIndex             the index of the target card in the faceUp cards of that
     *                                    stack
     * @param nrOfFaceDownInTargetTableau the number of face-down cards in the target tableau
     */
    private void makeMoveWasteToTableau(String sourceCardTextureString, String targetCardTextureString,
                                        int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);
        ImageWrapper targetCard = null;
        if (targetCardTextureString != null) {
            targetCard = faceUpCards.get(targetCardTextureString);
        }

        // targetCard may be null, but only if there are no cards in the targetStack
        if (sourceCard != null && !(targetCard == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            boolean targetCardExists = targetCard != null;

            // make movement
            float newX = targetCardExists ?
                    targetCard.getX() :
                    ViewConstants.TableauFoundationX[targetStack];
            float newY = targetCardExists ?
                    targetCard.getY() - ViewConstants.offsetHeightBetweenCards :
                    ViewConstants.TableauBaseY;

            moveCard(newX, newY, sourceCard, targetStack, true);

            // set meta-information
            sourceCard.setGameObject(GameObject.TABLEAU);
            sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    /**
     * move W->F
     *
     * @param sourceCardTextureString the texture string of the card to be moved
     * @param targetStack             the index of the target stack (in [0,6])
     */
    private void makeMoveWasteToFoundation(String sourceCardTextureString, int targetStack) {
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);

        if (sourceCard != null) {
            // make movement
            moveCard(ViewConstants.TableauFoundationX[targetStack],
                    ViewConstants.WasteDeckFoundationY, sourceCard, targetStack, true);

            // set meta-information
            sourceCard.setGameObject(GameObject.FOUNDATION);
            sourceCard.setWrapperCardIndex(-1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    /**
     * move T->T: involves the actual move as well as the turning of the card below the moved one
     *
     * @param cardsToBeMovedTextureStrings the texture strings of the cards to be moved
     * @param wasTurnOver                  whether the action we inverted, was a turn over
     * @param targetCardTextureString      analogous to the sourceCardTextureString (may be null, if
     *                                     the target is an empty tableau)
     * @param beneathSourceCard            the card beneath the source card (may be null, if the
     *                                     source card was the last one); is needed to be turned
     *                                     after making the actual move
     * @param sourceStack                  the index of the source stack (in [0,6])
     * @param sourceCardIndex              the index of the source card in the faceUp cards of that
     *                                     stack
     * @param targetStack                  analogous to the sourceStack
     * @param targetCardIndex              analogous to the sourceCardIndex
     * @param nrOfFaceDownInSourceTableau  the number of face-down cards in the source tableau
     * @param nrOfFaceDownInTargetTableau  analogous to the nrOfFaceDownInSourceTableau
     */
    private void makeMoveTableauToTableau(List<String> cardsToBeMovedTextureStrings, boolean wasTurnOver,
                                          String targetCardTextureString, Card beneathSourceCard,
                                          int sourceStack, int sourceCardIndex, int targetStack,
                                          int targetCardIndex, int nrOfFaceDownInSourceTableau,
                                          int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
        List<ImageWrapper> sourceCards = new ArrayList<ImageWrapper>(cardsToBeMovedTextureStrings.size());
        for (int i = 0; i < cardsToBeMovedTextureStrings.size(); i++) {
            String texString = cardsToBeMovedTextureStrings.get(i);
            sourceCards.add(faceUpCards.get(texString));
        }
        ImageWrapper targetCard = faceUpCards.get(targetCardTextureString);
        // and maybe (if it exists), the card beneath
        ImageWrapper beneathSourceCardImageWrapper = null;
        String beneathSourceCardTextureString = null;
        if (beneathSourceCard != null) {
            beneathSourceCardTextureString = loader.getCardTextureName(beneathSourceCard);
            beneathSourceCardImageWrapper = faceUpCards.get(beneathSourceCardTextureString);
        }

        if (!sourceCards.isEmpty() && !(targetCard == null
                && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            boolean targetCardExists = targetCard != null;

            // if the action, that we are currently inverting turned the card beneath the
            // one we are now putting back, we have to turn it back around
            if (wasTurnOver) {

                if (targetCardExists) {
                    // set it invisible, if the card is turned again,
                    // it does not have to be loaded again
                    targetCard.setVisible(false);

                    ImageWrapper faceDownCard = getBackSideCardForStackAndCardIndex(targetStack,
                            nrOfFaceDownInTargetTableau - 1);
                    faceDownCard.setVisible(true);
                }
            }

            // make movements
            for (int i = 0; i < sourceCards.size(); i++) {
                ImageWrapper sourceCard = sourceCards.get(i);

                float newX = targetCardExists ?
                        targetCard.getX() :
                        ViewConstants.TableauFoundationX[targetStack];
                float newY = targetCardExists ?
                        targetCard.getY() - (i + 1) * ViewConstants.offsetHeightBetweenCards :
                        ViewConstants.TableauBaseY - i * ViewConstants.offsetHeightBetweenCards;

                moveCard(newX, newY, sourceCard, targetStack, true);
            }

            // set meta-information
            for (ImageWrapper sourceCard : sourceCards) {
                sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
            }

            // if there is/was a card beneath the sourceCard, turn it
            if (beneathSourceCardTextureString != null) {
                // delete backsideImage
                ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack,
                        sourceCardIndex + nrOfFaceDownInSourceTableau);

                backsideImage.setVisible(false);


                // add asset for newly turned card
                if (beneathSourceCardImageWrapper == null) {
                    beneathSourceCardImageWrapper = loadActorForCardAndSaveInMap(beneathSourceCard);
                }
                beneathSourceCardImageWrapper.setVisible(true);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImageWrapper,
                        GameObject.TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack,
                        0);
            }

        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveTableauToTableau(List<String> cardsToBeMovedTextureStrings,
                                          String targetCardTextureString, Card beneathSourceCard,
                                          int sourceStack, int sourceCardIndex, int targetStack,
                                          int targetCardIndex, int nrOfFaceDownInSourceTableau,
                                          int nrOfFaceDownInTargetTableau) {
        makeMoveTableauToTableau(cardsToBeMovedTextureStrings, false, targetCardTextureString,
                beneathSourceCard, sourceStack, sourceCardIndex, targetStack, targetCardIndex,
                nrOfFaceDownInSourceTableau, nrOfFaceDownInTargetTableau);
    }

    private void makeMoveTableauToTableau(List<String> cardsToBeMovedTextureStrings,
                                          boolean wasTurnOver, String targetCardTextureString,
                                          int sourceStack, int sourceCardIndex, int targetStack,
                                          int targetCardIndex, int nrOfFaceDownInSourceTableau,
                                          int nrOfFaceDownInTargetTableau) {
        makeMoveTableauToTableau(cardsToBeMovedTextureStrings, wasTurnOver, targetCardTextureString,
                null, sourceStack, sourceCardIndex, targetStack, targetCardIndex,
                nrOfFaceDownInSourceTableau, nrOfFaceDownInTargetTableau);
    }

    /**
     * move T->F: involves the actual move as well as the turning of the card below the moved one
     *
     * @param sourceCardTextureString     the texture string of the card to be moved
     * @param beneathSourceCard           the card beneath the source card (may be null, if the
     *                                    source card was the last one); is needed to be turned
     *                                    after making the actual move
     * @param sourceStack                 the index of the source stack (in [0,6])
     * @param sourceCardIndex             the index of the source card in the faceUp cards of that
     *                                    stack
     * @param targetStack                 analogous to the sourceStack
     * @param nrOfFaceDownInSourceTableau the number of face-down cards in the source tableau
     */
    private void makeMoveTableauToFoundation(String sourceCardTextureString, Card beneathSourceCard,
                                             int sourceStack, int sourceCardIndex, int targetStack,
                                             int nrOfFaceDownInSourceTableau) {
        // find correct card that should be moved and card to move it to
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);
        // and maybe (if it exists), the card beneath
        ImageWrapper beneathSourceCardImageWrapper = null;
        String beneathSourceCardTextureString = null;
        if (beneathSourceCard != null) {
            beneathSourceCardTextureString = loader.getCardTextureName(beneathSourceCard);
            beneathSourceCardImageWrapper = faceUpCards.get(beneathSourceCardTextureString);
        }

        if (sourceCard != null) {
            // make movement
            moveCard(ViewConstants.TableauFoundationX[targetStack],
                    ViewConstants.WasteDeckFoundationY, sourceCard, targetStack, true);

            // set meta-information
            sourceCard.setGameObject(GameObject.FOUNDATION);
            sourceCard.setWrapperCardIndex(-1);

            // if there is/was a card beneath the sourceCard, turn it
            if (beneathSourceCardTextureString != null) {
                // ---------- set backsideImage invisible ----------
                ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack,
                        sourceCardIndex + nrOfFaceDownInSourceTableau);

                backsideImage.setVisible(false);


                // ---------- add asset for newly turned card ----------
                if (beneathSourceCardImageWrapper == null) {
                    beneathSourceCardImageWrapper = loadActorForCardAndSaveInMap(beneathSourceCard);
                }
                beneathSourceCardImageWrapper.setVisible(true);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImageWrapper,
                        GameObject.TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack,
                        0);
            }
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveTableauToFoundation(String sourceCardTextureString, int sourceStack,
                                             int sourceCardIndex, int targetStack,
                                             int nrOfFaceDownInSourceTableau) {
        makeMoveTableauToFoundation(sourceCardTextureString, null, sourceStack, sourceCardIndex,
                targetStack, nrOfFaceDownInSourceTableau);
    }

    /**
     * move F->T: can be used for do or undo, in case of undo, we have to save, whether the move
     * we are currently inverting was a turn over and we have to undo the turn as well
     *
     * @param sourceCardTextureString     the texture string of the card to be moved
     * @param wasTurnOver                 whether the action we inverted, was a turn over
     * @param textureStringTableauTarget  analogous to the sourceCardTextureString (may be null, if
     *                                    the target is an empty tableau)
     * @param targetStack                 the index of the target stack (in [0,6])
     * @param targetCardIndex             the index of the target card in the faceUp cards of that
     *                                    stack
     * @param nrOfFaceDownInTargetTableau the number of face-down cards in the target tableau
     */
    private void makeMoveFoundationToTableau(String sourceCardTextureString, boolean wasTurnOver,
                                             String textureStringTableauTarget, int targetStack,
                                             int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);
        ImageWrapper targetCard = null;
        if (textureStringTableauTarget != null) {
            targetCard = faceUpCards.get(textureStringTableauTarget);
        }

        // targetCard may be null, but only if there are no cards in the targetStack
        if (sourceCard != null &&
                !(targetCard == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {

            boolean targetCardExists = targetCard != null;

            // make movement
            float newX = targetCardExists ?
                    targetCard.getX() :
                    ViewConstants.TableauFoundationX[targetStack];
            float newY = targetCardExists ?
                    targetCard.getY() - ViewConstants.offsetHeightBetweenCards :
                    ViewConstants.TableauBaseY;

            // if the action, that we are currently inverting turned the card beneath the
            // one we are now putting back, we have to turn it back around
            if (wasTurnOver) {

                if (targetCardExists) {
                    // set it invisible, if the card is turned again,
                    // it does not have to be loaded again
                    targetCard.setVisible(false);

                    ImageWrapper faceDownCard = getBackSideCardForStackAndCardIndex(targetStack,
                            nrOfFaceDownInTargetTableau - 1);
                    faceDownCard.setVisible(true);
                }
            }

            moveCard(newX, newY, sourceCard, targetStack, true);

            // set meta-information
            sourceCard.setGameObject(GameObject.TABLEAU);
            sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    private void makeMoveFoundationToTableau(String sourceCardTextureString,
                                             String textureStringTableauTarget, int targetStack,
                                             int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        makeMoveFoundationToTableau(sourceCardTextureString, false, textureStringTableauTarget,
                targetStack, targetCardIndex, nrOfFaceDownInTargetTableau);
    }

    /**
     * undo move X->W (T->W and F->W)
     *
     * @param sourceCardTextureString the texture string of the card to be moved
     */
    private void makeUndoMoveXToWaste(String sourceCardTextureString) {
        // find correct card that should be moved and card to move it to
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);


        if (sourceCard != null) {
            // make movement
            moveCard(ViewConstants.WasteX1Fan,
                    ViewConstants.WasteDeckFoundationY, sourceCard, 5, true);

            sourceCard.toFront();

            // set meta-information
            sourceCard.setGameObject(GameObject.WASTE);
            sourceCard.setWrapperCardIndex(-1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    // ---------------------------- UNDO MOVES ----------------------------
    private void handleUndoMove(Move move, SolitaireGame game) {
        Action ac1 = move.getAction1();
        Action ac2 = move.getAction2();

        // CAUTION: target and source are inverted (as if the undo move was a valid move from
        // target to source
        int targetStack = ac1.getStackIndex();
        int targetCard = ac1.getCardIndex();
        int sourceStack = -1, sourceCard = -1;
        if (ac2 != null) {
            sourceStack = ac2.getStackIndex();
            sourceCard = ac2.getCardIndex();
        }


        // click on deck
        if (ac2 == null ||
                (ac2.getGameObject().equals(GameObject.DECK)) &&
                        (ac1.getGameObject().equals(GameObject.DECK))) {
            DeckWaste deckWaste = game.getDeckWaste();
            Vector<Card> deck = deckWaste.getDeck();

            if (game.getDeckWaste().isWasteEmpty()) {
                resetDeck();
            } else if (deck.isEmpty()) {
                resetWaste();
            } else {
                Vector<String> cardsToBeUnturned = new Vector<String>(3);

                // turn all cards that are in the deck after size-newFanSize (fanSize after move
                // that is currently being undone)
                for (int i = deck.size() - move.getNewFanSize(); i < deck.size(); i++) {
                    try {
                        cardsToBeUnturned.add(loader.getCardTextureName(deck.get(i)));
                    } catch (Exception E) {
                        // if this does not exist, don't add it
                    }
                }
                turnOrUnturnDeckCard(game, cardsToBeUnturned);
            }
        } else {
            // works analogous to handleMove (game has already done the undo)
            // plus: if an action was X->Y, we have to perform the inverse move Y->X
            switch (ac2.getGameObject()) {

                case TABLEAU:
                    Tableau tabAtSourceStack = game.getTableauAtPos(sourceStack);
                    int nrOfFaceDownInSourceTableauAfterChange =
                            tabAtSourceStack.getFaceDown().size();
                    String sourceCardTextureString;

                    // ------------------------ T -> F ------------------------
                    if (ac1.getGameObject().equals(GameObject.FOUNDATION)) {
                        sourceCardTextureString = loader.getCardTextureName(
                                game.getFoundationAtPos(targetStack).getFoundationTop());

                        makeMoveTableauToFoundation(sourceCardTextureString, sourceStack,
                                sourceCard, targetStack, nrOfFaceDownInSourceTableauAfterChange);
                    }

                    // ------------------------ T -> T ------------------------
                    else if (ac1.getGameObject().equals(GameObject.TABLEAU)) {
                        // was a card turned over?
                        boolean wasTurnOver = move.isTurnOver();

                        // get texture string of cards that were moved
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.getFaceUp();
                        int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDown().size();
                        targetCard--;

                        List<String> cardsToBeMovedTextureStrings = new ArrayList<String>();
                        for (int i = targetCard + 1; i < faceUpAtTargetStack.size(); i++) {
                            Card cardToBeMoved = faceUpAtTargetStack.get(i);
                            cardsToBeMovedTextureStrings.add(loader.getCardTextureName(cardToBeMoved));
                        }

                        // get targetCard, options:
                        // 1) move to a tableau, where we need to unturn a card (wasTurnOver and cardIndex==-1)
                        // 2) move to a tableau with valid cardIndex, simple move T->T
                        // 3) move to an empty tab (!wasTurnOver, but cardIndex==-1)
                        String textureStringOldTableauTop = null;
                        if (wasTurnOver && targetCard == -1) {
                            textureStringOldTableauTop =
                                    loader.getCardTextureName(tabAtTargetStack.getFaceDown().lastElement());
                        } else if (targetCard != -1) {
                            textureStringOldTableauTop = loader.getCardTextureName(
                                    faceUpAtTargetStack.get(targetCard));
                        }

                        makeMoveTableauToTableau(cardsToBeMovedTextureStrings, wasTurnOver,
                                textureStringOldTableauTop, sourceStack, sourceCard, targetStack,
                                targetCard, nrOfFaceDownInSourceTableauAfterChange,
                                nrOfFaceDownInTargetTableau);

                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                    // ------------------------ T -> W ------------------------
                    else if (ac1.getGameObject().equals(GameObject.WASTE)) {
                        sourceCardTextureString = loader.getCardTextureName(
                                game.getDeckWaste().getWasteTop());

                        makeUndoMoveXToWaste(sourceCardTextureString);

                        paintWaste(game.getDeckWaste(), false, true);
                    }


                    // set new smallestY for source
                    setNewSmallestY(sourceStack, tabAtSourceStack);

                    break;


                case FOUNDATION:
                    // ------------------------ F -> W ------------------------
                    if (ac1.getGameObject().equals(GameObject.WASTE)) {
                        sourceCardTextureString = loader.getCardTextureName(
                                game.getDeckWaste().getWasteTop());

                        makeUndoMoveXToWaste(sourceCardTextureString);

                        paintWaste(game.getDeckWaste(), false, true);
                    }
                    // ------------------------ F -> T ------------------------
                    else if (ac1.getGameObject().equals(GameObject.TABLEAU)) {

                        // was a card turned over?
                        boolean wasTurnOver = move.isTurnOver();

                        // get texture string of card that was moved
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.getFaceUp();
                        boolean emptyTargetStack = tabAtTargetStack.getNrOfAllCards() == 1;

                        // for an undo move, we have to decrement the targetCard
                        targetCard--;

                        String textureStringFoundationSource = loader.getCardTextureName(
                                faceUpAtTargetStack.lastElement());

                        String textureStringTableauTargetTop = null;
                        if (!wasTurnOver && !emptyTargetStack) {
                            // either this was the card tapped on and it was and is still open
                            textureStringTableauTargetTop = loader.getCardTextureName(
                                    faceUpAtTargetStack.get(targetCard));
                        } else {
                            try {
                                // or this was an undo of a turn over, so the prior top of the stack
                                // is now the last element in the facedown list
                                textureStringTableauTargetTop = loader.getCardTextureName(
                                        tabAtTargetStack.getFaceDown().lastElement());

                            } catch (Exception e) {
                                // leave textureString at null
                            }
                        }

                        int nrOfFaceDownInTargetTableau =
                                tabAtTargetStack.getFaceDown().size();

                        makeMoveFoundationToTableau(textureStringFoundationSource, wasTurnOver,
                                textureStringTableauTargetTop, targetStack, targetCard,
                                nrOfFaceDownInTargetTableau);


                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                    break;
            }
        }
    }


    /**
     * @param targetX new x-position
     * @param targetY new y-position
     * @param card    the ImageWrapper-object to be moved
     * @param animate whether to animate the moving or not
     */
    private void moveCard(float targetX, float targetY, ImageWrapper card, int targetStack,
                          boolean animate) {
        if (animate) {
            card.addAction(Actions.moveTo(targetX, targetY, 0.2f));
        } else {
            card.setPosition(targetX, targetY);
        }
        card.setWrapperStackIndex(targetStack);
    }

    // ------------------------------------ getActionForTap for Controller ------------------------------------

    /**
     * for a given tap from the user, the view returns information about the position where the tap occurred
     *
     * @param x x-position of tap
     * @param y y-position of tap
     * @return an action containing whether the click was on deck, waste, foundation or tableau,
     * which foundation/tableau was tapped and which card in this tableau
     */
    protected Action getActionForTap(float x, float y) {
        GameObject gameObject = null;
        int stackIndex = getStackIndexForX(x);      // caution can be -1
        int cardIndex = -1;

        if (stackIndex != -1) {
            // ------------ FOUNDATION, WASTE, DECK ------------
            if (y >= 16 * ViewConstants.heightOneSpace &&
                    y <= 16 * ViewConstants.heightOneSpace + ViewConstants.heightCard) {

                // 0, 1, 2, 3 are the four foundations, 4 is empty, 5 is waste, 6 is deck
                if (stackIndex < 4) {
                    gameObject = GameObject.FOUNDATION;
                } else if (stackIndex == 5) {
                    gameObject = GameObject.WASTE;
                } else if (stackIndex == 6) {
                    gameObject = GameObject.DECK;
                }
            }
            // ------------ TABLEAU ------------
            else {
                try {
                    float smallestY = smallestYForTableau.get(stackIndex);
                    // to prevent rounding errors, we subtract 1 and prevent, that
                    // biggest-smallest == heightCard + 0.0002
                    float biggestY = ViewConstants.TableauBaseY + ViewConstants.heightCard - 1;

                    if (y >= smallestY && y <= biggestY) {

                        // a tableau can at most hold 20 faceUpCards (14 in a row from king to ace + 6 face-down)
                        for (int i = 0; i < 20; i++) {
                            // example for visualisation:
                            //  ----    <- biggestY (- 0 * offsetHeight)
                            //  |  |
                            //  ----    <- biggestY - 1 * offsetHeight
                            //  |  |
                            //  ----    <- biggestY - 2 * offsetHeight
                            //  |  |
                            //  |  |
                            //  ----    <- smallestY
                            float biggestYAtPosI = biggestY - (i * ViewConstants.offsetHeightBetweenCards);
                            float biggestYAtPosAfterI = biggestY - ((i + 1) * ViewConstants.offsetHeightBetweenCards);
                            float remainingSpaceUntilTableauEnd = Math.abs(biggestY - (i * ViewConstants.offsetHeightBetweenCards) - smallestY);

                            if ((y <= biggestYAtPosI &&
                                    (y >= biggestYAtPosAfterI || remainingSpaceUntilTableauEnd <= ViewConstants.heightCard))) {
                                gameObject = GameObject.TABLEAU;
                                cardIndex = i;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // return Action if click was somewhere sensible or null else
        return gameObject == null ? null : new Action(gameObject, stackIndex, cardIndex);
    }


    // ------------------------------------ Helper ------------------------------------
    private int getStackIndexForX(float x) {
        if (x >= 2 * ViewConstants.widthOneSpace && x <= 2 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 0;
        } else if (x >= 6 * ViewConstants.widthOneSpace && x <= 6 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 1;
        } else if (x >= 10 * ViewConstants.widthOneSpace && x <= 10 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 2;
        } else if (x >= 14 * ViewConstants.widthOneSpace && x <= 14 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 3;
        } else if (x >= 18 * ViewConstants.widthOneSpace && x <= 18 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 4;
        } else if (x >= 22 * ViewConstants.widthOneSpace && x <= 22 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 5;
        } else if (x >= 26 * ViewConstants.widthOneSpace && x <= 26 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 6;
        } else {
            return -1;
        }
    }

    /**
     * returns the length (nr of faceUpCards) of the longest tableau
     *
     * @param tableaus all 7 tableaus
     * @return the length of the longest
     */
    private int getLengthOfLongestTableau(ArrayList<Tableau> tableaus) {
        int longest = -1;

        for (Tableau t : tableaus) {
            int length = t.getFaceDown().size() + t.getFaceUp().size();
            if (length > longest) {
                longest = length;
            }
        }

        return longest;
    }


    private ImageWrapper getBackSideCardForStackAndCardIndex(int stackIndex, int cardIndex) {
        for (ImageWrapper c : faceDownCards) {
            if (c.getWrapperStackIndex() == stackIndex && c.getWrapperCardIndex() == cardIndex) {
                return c;
            }
        }
        return null;
    }


    /**
     * set the scaling, position and add the card to the stage, so every image is svaled the same
     *
     * @param cardImage image whose parameters are set and which is added to the stage
     * @param x         the x-coordinate of the position
     * @param y         the y-coordinate of the position
     */
    private void setImageScalingAndPositionAndStackCardIndicesAndAddToStage(ImageWrapper cardImage,
                                                                            GameObject gameObject,
                                                                            float x, float y,
                                                                            int stackIndex, int cardIndex) {
        cardImage.setPosition(x, y);
        cardImage.setWidth(ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace);
        cardImage.setHeight(ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace);
        cardImage.setWrapperStackIndex(stackIndex);
        cardImage.setWrapperCardIndex(cardIndex);
        cardImage.setGameObject(gameObject);
        stage.addActor(cardImage);

        if (!widthHeightOfCardSet) {
            // for some reason only this works
            ViewConstants.widthCard = ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace;
            ViewConstants.heightCard = ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace;
            widthHeightOfCardSet = true;
        }
    }


    /**
     * tries to get the image/texture for this card if it was already loaded or loads it
     *
     * @param card
     * @return the image/texture for it, either newly loaded or just from the map
     */
    private ImageWrapper getImageForCard(Card card) {
        ImageWrapper cardImage = faceUpCards.get(loader.getCardTextureName(card));

        if (cardImage != null) {
            return cardImage;
        } else {
            return loadActorForCardAndSaveInMap(card);
        }
    }


    /**
     * loads the texture for the card given and saves it to the faceUpCards-map
     *
     * @param card
     * @return the correct texture for this card
     */
    private ImageWrapper loadActorForCardAndSaveInMap(Card card) {
        String textureString = loader.getCardTextureName(card);
        ImageWrapper textureForCard = loader.getImageForPath("cards/" + textureString + ".png");

        faceUpCards.put(textureString, textureForCard);

        return textureForCard;
    }


    /**
     * class to load images
     */
    private class ImageLoader {
        protected ImageWrapper getEmptySpaceImageWithLogo() {
            return getImageForPath("cards/empty_space.png");
        }

        private ImageWrapper getEmptySpaceImageWithoutLogo() {
            return getImageForPath("cards/empty_space_ohne_logo.png");
        }

        private ImageWrapper getBacksideImage() {
            return getImageForPath("cards/backside.png");
        }

        private ImageWrapper getMarkImage() {
            return getImageForPath("cards/mark.png");
        }

        /**
         * computes the textureString for a card (rank_suit)
         *
         * @param card
         * @return a string containing the card's rank and suit
         */
        private String getCardTextureName(Card card) {
            return card.getRank().toString().toLowerCase() + "_" + card.getSuit().toString().toLowerCase();
        }

        /**
         * loads an image for a given (relative) path
         *
         * @param path
         * @return the image lying at this path
         */
        private ImageWrapper getImageForPath(String path) {
            try {
                return new ImageWrapper(new Texture(Gdx.files.internal(path)));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    /**
     * class to contain view constants
     */
    private static class ViewConstants {
        private static float widthScreen;
        private static float heightScreen;

        private static float widthOneSpace;        // the widthScreen is divided into spaces (37)
        private static float heightOneSpace;        // the heightScreen is divided into spaces (21)

        private static float offsetHeightBetweenCards = 30;

        private static float heightCard;
        private static float widthCard;

        private static float scalingWidthCard = 2.3f;
        private static float scalingHeightCard = 4f;
        private static float scalingWidthMarker = 2.4f;
        private static float scalingHeightMarker = 4.5f;

        private static float DeckX;

        //        private static float WasteX;
        private static float WasteDeckFoundationY;

        private static float[] TableauFoundationX;

        private static float TableauBaseY;

        private static float WasteX1Fan;
        private static float WasteX2Fan1;
        private static float WasteX2Fan2;
        private static float WasteX3Fan1;
        private static float WasteX3Fan2;
        private static float WasteX3Fan3;
    }


    /**
     * Wrapper Information to wrap additional information aside the image/actor
     */
    private class ImageWrapper extends Image {
        int stackIndex = -1;
        int cardIndex = -1;
        GameObject gameObject = null;

        protected GameObject getGameObject() {
            return gameObject;
        }

        protected void setGameObject(GameObject gameObject) {
            this.gameObject = gameObject;
        }

        protected ImageWrapper(Texture texture) {
            super(texture);
        }

        protected int getWrapperStackIndex() {
            return stackIndex;
        }

        protected void setWrapperStackIndex(int stackIndex) {
            this.stackIndex = stackIndex;
        }

        protected int getWrapperCardIndex() {
            return cardIndex;
        }

        protected void setWrapperCardIndex(int cardIndex) {
            this.cardIndex = cardIndex;
        }

        public String toString() {
            return super.toString() + ", stack: " + stackIndex + ", card: " + cardIndex + ", gameObject: " + gameObject;
        }
    }
}
