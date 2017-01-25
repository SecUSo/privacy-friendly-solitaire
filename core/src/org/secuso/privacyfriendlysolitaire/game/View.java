package org.secuso.privacyfriendlysolitaire.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

/**
 * @author: I. Dix
 * <p>
 * the view manages the actors on stage (the stage is given to it by the application). It observes
 * the model (SolitaireGame game) and reacts to changes in the model by re-arranging the actors.
 * The newly arranged actors are then drawn by the application
 */

public class View implements Observer {
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

        // set view constants
        ViewConstants.widthScreen = Gdx.graphics.getWidth();
        ViewConstants.heightScreen = Gdx.graphics.getHeight();
        ViewConstants.widthOneSpace = ViewConstants.widthScreen / 31;
        ViewConstants.heightOneSpace = ViewConstants.heightScreen / 21;
        // positions
        ViewConstants.WasteDeckFoundationY = 16 * ViewConstants.heightOneSpace;
        // waste resp. deck will be the same as TableauFoundationX[5 resp. 6]
        // we keep them for transparency reasons
        ViewConstants.WasteX = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace;
        ViewConstants.DeckX = (2 + 6 * (1 + 3)) * ViewConstants.widthOneSpace;
        ViewConstants.TableauFoundationX = new float[7];
        for (int i = 0; i < 7; i++) {
            ViewConstants.TableauFoundationX[i] = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
        }
        ViewConstants.TableauBaseY = 10.5f * ViewConstants.heightOneSpace;
        ViewConstants.scaleXMarkerOneCard = 1.68f;
        ViewConstants.scaleYMarkerOneCard = 1.5f;

        // add mark and make it invisible
        marker = loader.getMarkImage();
        marker.setScale(1.4f);
        marker.setScaleX(ViewConstants.scaleXMarkerOneCard);
        marker.setScaleY(ViewConstants.scaleYMarkerOneCard);
        marker.setVisible(false);
        stage.addActor(marker);

        // add emptySpaceForDeck and make it invisible
        backsideCardOnDeck = loader.getBacksideImage();

        arrangeInitialView(game);
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
                faceDownCard.setGameObject(GameObject.TABLEAU);
                faceDownCard.setWrapperStackIndex(i);
                faceDownCard.setWrapperCardIndex(j);

                faceDownCards.add(faceDownCard);
            }

            // add face-up cards
            int faceUpSize = t.getFaceUp().size();
            float smallestY = 0;
            for (int j = 0; j < faceUpSize; j++) {
                ImageWrapper faceUpCard = loadActorForCardAndSaveInMap(t.getFaceUp().get(j));
                // y position is dependant on nr in faceDown-Vector
                float y = 10.5f * ViewConstants.heightOneSpace -
                        ((faceDownSize + j) * ViewConstants.offsetHeightBetweenCards);

                // save the last y as the smallest
                if (j == faceUpSize - 1) {
                    smallestY = y;
                }

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceUpCard, GameObject.TABLEAU, x, y, i, t.getFaceDown().size());
            }


            // save the y at which the last card (face-up) was positioned
            smallestYForTableau.put(i, smallestY);
//            Gdx.app.log("smallestY für Stack " + i, String.valueOf(smallestY));
        }
    }

    private void paintInitialDeckWaste(DeckWaste deckWaste) {
        // waste
        ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null,
                ViewConstants.WasteX, ViewConstants.WasteDeckFoundationY, -1, -1);

        Vector<Card> waste = deckWaste.getWaste();
        for (int i = 0; i < waste.size(); i++) {
            Card c = waste.get(i);
            ImageWrapper card = loadActorForCardAndSaveInMap(c);
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card, GameObject.WASTE,
                    ViewConstants.WasteX, ViewConstants.WasteDeckFoundationY,
                    5, -1);
        }

        // deck
        ImageWrapper emptySpaceDeck = loader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpaceDeck, null,
                ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(backsideCardOnDeck, GameObject.DECK,
                ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
    }


    // ------------------------------------ Update ------------------------------------

    /**
     * method to react to changes in the model
     *
     * @param o   the observed object (in this case a solitairegame)
     * @param arg some argument, which is unused at the moment
     */
    @Override
    public void update(Observable o, Object arg) {
        Gdx.app.log("Debug", " ");
        Gdx.app.log("Debug", "-----------update-----------");
        SolitaireGame game = (SolitaireGame) o;

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
                    Move prevMove = game.getMoves().lastElement();
//                Gdx.app.log("ac1:", prevMove.getAction1().toString() + "\n");
//                if (prevMove.getAction2() != null) {
//                    Gdx.app.log("ac2:", prevMove.getAction2().toString() + "\n");
//                }
//                Gdx.app.log("game after update:", "\n" + game.toString());
                    handleMove(prevMove, game);
                }
            } catch (Exception e) {
                Gdx.app.log("Error", e.getClass().toString() + ": " + e.getMessage() + ", probably an invalid move");
                e.printStackTrace();
                // maybe an invalid move
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
            marker.setPosition(topElement.getX() - 4, topElement.getY() - 5);
            // TODO: hier nochmal an den Zahlen frickeln
            marker.setHeight(topElement.getHeight() - 3 +
//            marker.setHeight(ViewConstants.heightCard +
                    ((cardsToBeMarked.size() - 1) * ViewConstants.offsetHeightBetweenCards * 0.73f));
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
                    turnDeckCard(game);
                }
                break;


            // possibilities: Waste -> Tableau, Waste -> Foundation
            case WASTE:
                // ------------------------ W -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    String textureStringOldWasteTop =
                            loader.getCardTextureName(game.getTableauAtPos(targetStack).getFaceUp().get(targetCard + 1));

                    Card targetOldTopCard = null;
                    // after moving the waste-card here, this is no more the top
                    // this can be null, if the waste-card was moved to an empty tableau
                    String textureStringOldTableauTop = null;
                    try {
                        targetOldTopCard = game.getTableauAtPos(targetStack).getFaceUp().get(targetCard);
                    } catch (Exception e) {
                    }

                    if (targetOldTopCard != null) {
                        textureStringOldTableauTop = loader.getCardTextureName(targetOldTopCard);
                    }
                    int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDown().size();

                    makeMoveWasteToTableau(textureStringOldWasteTop, textureStringOldTableauTop,
                            targetStack, targetCard, nrOfFaceDownInTargetTableau);
                }
                // ------------------------ W -> F ------------------------
                else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    String textureStringOldWasteTop =
                            loader.getCardTextureName(game.getFoundationAtPos(targetStack).getFoundationTop());

                    makeMoveWasteToFoundation(textureStringOldWasteTop, targetStack);
                }
                break;

            // possibilities: Tableau -> Tableau, Tableau -> Foundation
            case TABLEAU:
                Tableau tabAtSourceStack = game.getTableauAtPos(sourceStack);
                Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);

                nrOfFaceDownInSourceTableauAfterChange =
                        tabAtSourceStack.getFaceDown().size();
                int nrOfFaceUpInSourceTableauAfterChange =
                        tabAtSourceStack.getFaceUp().size();
                // the card beneath the sourceCard,
                // it may be null if after the move, the tableau has become empty
                Card cardBeneathSource = null;
                try {
                    cardBeneathSource = tabAtSourceStack.getFaceUp().get(ac1.getCardIndex());
                } catch (ArrayIndexOutOfBoundsException e) {
                }

                // ------------------------ T -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    List<String> textureStringsMovedCards = new ArrayList<String>();
                    for (int i = targetCard + 1; i < tabAtTargetStack.getFaceUp().size(); i++) {
                        Card cardToBeMoved = tabAtTargetStack.getFaceUp().get(i);
                        textureStringsMovedCards.add(loader.getCardTextureName(cardToBeMoved));
                    }

                    Card targetOldTopCard = null;
                    String textureStringOldTableauTop = null;
                    try {
                        targetOldTopCard = tabAtTargetStack.getFaceUp().get(targetCard);
                    } catch (Exception e) {
                    }

                    if (targetOldTopCard != null) {
                        textureStringOldTableauTop = loader.getCardTextureName(targetOldTopCard);
                    }
                    int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDown().size();

                    makeMoveTableauToTableau(textureStringsMovedCards, textureStringOldTableauTop,
                            cardBeneathSource, sourceStack, sourceCard, targetStack, targetCard,
                            nrOfFaceDownInSourceTableauAfterChange, nrOfFaceDownInTargetTableau);
                }
                // ------------------------ T -> F ------------------------
                else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    String textureStringTableauSource = loader.getCardTextureName(
                            game.getFoundationAtPos(targetStack).getFoundationTop());

                    makeMoveTableauToFoundation(textureStringTableauSource, cardBeneathSource,
                            sourceStack, sourceCard, targetStack, nrOfFaceDownInSourceTableauAfterChange,
                            nrOfFaceUpInSourceTableauAfterChange);
                }
                break;

            // possibilities: Foundation -> Tableau
            case FOUNDATION:
                tabAtTargetStack = game.getTableauAtPos(targetStack);
                // after moving the card the old foundation top is now on top the tableau
                // (on top the targetCard)
                String textureStringFoundationSource = loader.getCardTextureName(
                        tabAtTargetStack.getFaceUp().get(targetCard + 1));
                // ------------------------ F -> T ------------------------
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    String textureStringTableauTarget = loader.getCardTextureName(
                            tabAtTargetStack.getFaceUp().get(targetCard));
                    int nrOfFaceDownInTargetTableau =
                            tabAtTargetStack.getFaceDown().size();

                    makeMoveFoundationToTableau(textureStringFoundationSource, textureStringTableauTarget,
                            targetStack, targetCard, nrOfFaceDownInTargetTableau);
                }
                break;
        }
    }


    private void turnDeckCard(SolitaireGame game) {
        // load card that in the model already lies on top the waste
        Card onWasteCard = game.getDeckWaste().getWasteTop();

        // load imageWrapper (either from map or new)
        String textureName = loader.getCardTextureName(onWasteCard);
        // try pulling it from the faceUpCards (where it is currently invisible)
        ImageWrapper turnedCard = faceUpCards.get(textureName);
        // if this returns no result, load it
        if (turnedCard == null) {
            turnedCard = loadActorForCardAndSaveInMap(onWasteCard);
            float x = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace;
            float y = 16 * ViewConstants.heightOneSpace;
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(turnedCard, GameObject.WASTE, x, y, -1, -1);
        }
        turnedCard.setVisible(true);

        // check if this was the last
        if (game.getDeckWaste().getDeck().isEmpty()) {
            backsideCardOnDeck.setVisible(false);
        }
    }

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

            moveCard(newX, newY, sourceCard, targetStack);

            // set new smallestY for targetStack
            smallestYForTableau.put(targetStack, newY);
//            Gdx.app.log("smallestY für " + targetStack, String.valueOf(newY));

            // set meta-information
            sourceCard.setGameObject(GameObject.TABLEAU);
            sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveWasteToFoundation(String sourceCardTextureString, int targetStack) {
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);

        if (sourceCard != null) {
            // make movement
            moveCard(ViewConstants.TableauFoundationX[targetStack],
                    ViewConstants.WasteDeckFoundationY, sourceCard, targetStack);

            // set meta-information
            sourceCard.setGameObject(GameObject.FOUNDATION);
            sourceCard.setWrapperCardIndex(-1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    /**
     * make a move tab -> tab: involves the actual move as well as the turning of the card below
     * the moved one
     *
     * @param cardsToBeMovedTextureStrings the texture name of the source-card
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
     *                                     (after the change)
     * @param nrOfFaceDownInTargetTableau  analogous to the nrOfFaceDownInSourceTableau
     */
    private void makeMoveTableauToTableau(List<String> cardsToBeMovedTextureStrings,
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

        if (!sourceCards.isEmpty() && !(targetCard == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            boolean targetCardExists = targetCard != null;

            // make movements
            for (int i = 0; i < sourceCards.size(); i++) {
                ImageWrapper sourceCard = sourceCards.get(i);

                float newX = targetCardExists ?
                        targetCard.getX() :
                        ViewConstants.TableauFoundationX[targetStack];
                float newY = targetCardExists ?
                        targetCard.getY() - (i + 1) * ViewConstants.offsetHeightBetweenCards :
                        ViewConstants.TableauBaseY - i * ViewConstants.offsetHeightBetweenCards;

                moveCard(newX, newY, sourceCard, targetStack);
            }

            // set new smallestY for targetStack
            float newSmallestYStart = targetCardExists ?
                    targetCard.getY() :
                    ViewConstants.TableauBaseY;

            float toSubtract = targetCardExists ?
                    sourceCards.size() * ViewConstants.offsetHeightBetweenCards :
                    (sourceCards.size() - 1) * ViewConstants.offsetHeightBetweenCards;

            smallestYForTableau.put(targetStack, newSmallestYStart -
                    toSubtract);
//            Gdx.app.log("smallestY für " + targetStack, String.valueOf(smallestYForTableau.get(targetStack)));

            // set meta-information
            for (ImageWrapper sourceCard : sourceCards) {
                sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
            }

            // set new smallestY for sourceStack
            if (nrOfFaceDownInSourceTableau > 0) {
                smallestYForTableau.put(sourceStack, smallestYForTableau.get(sourceStack) +
                        sourceCards.size() * ViewConstants.offsetHeightBetweenCards);
//                Gdx.app.log("smallestY für " + sourceStack, String.valueOf(smallestYForTableau.get(sourceStack)));
            } else {
                smallestYForTableau.put(sourceStack, ViewConstants.TableauBaseY);
//                Gdx.app.log("smallestY für " + sourceStack, String.valueOf(smallestYForTableau.get(sourceStack)));
            }

            // if there is/was a card beneath the sourceCard, turn it
            if (beneathSourceCardImageWrapper == null && beneathSourceCardTextureString != null) {
                // delete backsideImage
                ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack, sourceCardIndex + nrOfFaceDownInSourceTableau);

                // add asset for newly turned card
                beneathSourceCardImageWrapper = loadActorForCardAndSaveInMap(beneathSourceCard);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImageWrapper, GameObject.TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack, sourceCardIndex - 1);

                faceDownCards.remove(backsideImage);
                backsideImage.remove();
            }

        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    private void makeMoveTableauToFoundation(String sourceCardTextureString, Card beneathSourceCard,
                                             int sourceStack, int sourceCardIndex, int targetStack,
                                             int nrOfFaceDownInSourceTableau,
                                             int nrOfFaceUpInSourceTableau) {
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
                    ViewConstants.WasteDeckFoundationY, sourceCard, targetStack);

            // set meta-information
            sourceCard.setGameObject(GameObject.FOUNDATION);
            sourceCard.setWrapperCardIndex(-1);

            // set new smallestY for sourceStack
            float smallestY = (nrOfFaceDownInSourceTableau + nrOfFaceUpInSourceTableau) > 0 ?
                    smallestYForTableau.get(sourceStack) + ViewConstants.offsetHeightBetweenCards :
                    ViewConstants.TableauBaseY;

            smallestYForTableau.put(sourceStack, smallestY);
//            Gdx.app.log("smallestY für " + sourceStack, String.valueOf(smallestYForTableau.get(sourceStack)));

            // if there is/was a card beneath the sourceCard, turn it
            if (beneathSourceCardImageWrapper == null && beneathSourceCardTextureString != null) {
                // delete backsideImage
                ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack,
                        sourceCardIndex + nrOfFaceDownInSourceTableau);

                // add asset for newly turned card
                beneathSourceCardImageWrapper = loadActorForCardAndSaveInMap(beneathSourceCard);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImageWrapper,
                        GameObject.TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack,
                        sourceCardIndex - 1);

                faceDownCards.remove(backsideImage);
                backsideImage.remove();
            }
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    private void makeMoveFoundationToTableau(String sourceCardTextureString,
                                             String targetCardTextureString, int targetStack,
                                             int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
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

            moveCard(newX, newY, sourceCard, targetStack);

            // set new smallestY for targetStack
            float smallestY = targetCardExists ?
                    targetCard.getY() - ViewConstants.offsetHeightBetweenCards :
                    ViewConstants.TableauBaseY - ViewConstants.offsetHeightBetweenCards;

            smallestYForTableau.put(targetStack, smallestY);
//            Gdx.app.log("smallestY für " + targetStack, String.valueOf(smallestYForTableau.get(targetStack)));

            // set meta-information
            sourceCard.setGameObject(GameObject.TABLEAU);
            sourceCard.setWrapperCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    /**
     * can later be used when animating the game
     *
     * @param targetX new x-position
     * @param targetY new y-position
     * @param card    the ImageWrapper-object to be moved
     */
    private void moveCard(float targetX, float targetY, ImageWrapper card, int targetStack) {
        // https://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/actions/MoveToAction.html
        // and
        // http://stackoverflow.com/questions/15004480/libgdx-actions-gradually-move-actor-from-point-a-to-point-b
        card.addAction(Actions.moveTo(targetX, targetY, 0.2f));
//        card.setPosition(targetX, targetY);
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
//        Gdx.app.log("Debug", " ");
//        Gdx.app.log("Debug", "-----------getActionForTap-----------");
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
//                    Gdx.app.log("biggest-smallest", String.valueOf(biggestY - smallestY));
//                    Gdx.app.log("heightCard", String.valueOf(ViewConstants.heightCard));

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
     * @param cardImage
     * @param x         the x-coordinate of the position
     * @param y         the y-coordinate of the position
     */
    private void setImageScalingAndPositionAndStackCardIndicesAndAddToStage(ImageWrapper cardImage, GameObject gameObject, float x, float y, int stackIndex, int cardIndex) {
        cardImage.setPosition(x, y);
        cardImage.setScale(1.4f);
        cardImage.setScaleX(1.6f);
        cardImage.setWrapperStackIndex(stackIndex);
        cardImage.setWrapperCardIndex(cardIndex);
        cardImage.setGameObject(gameObject);
        stage.addActor(cardImage);

        if (!widthHeightOfCardSet) {
            ViewConstants.heightCard = cardImage.getHeight() * 1.4f;    // for some reason only this works :P
            ViewConstants.widthCard = cardImage.getWidth() * 1.6f;
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

        private static float scaleXMarkerOneCard;
        private static float scaleYMarkerOneCard;

        private static float DeckX;

        private static float WasteX;
        private static float WasteDeckFoundationY;

        private static float[] TableauFoundationX;

        private static float TableauBaseY;
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
