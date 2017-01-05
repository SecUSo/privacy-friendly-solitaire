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
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
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
    private ImageLoader loader = new ImageLoader();

    private Stage stage;
    private ImageWrapper marker;
    private ImageWrapper backsideCardOnDeck;

    private boolean lastWasAMarkingAction = false;

    private HashMap<String, ImageWrapper> faceUpCards = new HashMap<String, ImageWrapper>(52);
    //    private HashMap<Point, String> cardNameForCardAndStackIndex = new HashMap<Point, String>(52);
    // describes the y at which the given tableau is positioned at the smallest
    private HashMap<Integer, Float> smallestYForTableau = new HashMap<Integer, Float>(7);

    public View(SolitaireGame game, Stage stage) {
        this.stage = stage;

        // set view constants
        ViewConstants.widthScreen = Gdx.graphics.getWidth();
        ViewConstants.heightScreen = Gdx.graphics.getHeight();
        ViewConstants.widthOneSpace = ViewConstants.widthScreen / 31;
        ViewConstants.heightOneSpace = ViewConstants.heightScreen / 21;
        // positions
        ViewConstants.WasteDeckFoundationY = 16 * ViewConstants.heightOneSpace;
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
        paintInitialFoundations();
        paintInitialTableaus(game.getTableaus());
        paintInitialDeckWaste();
    }

    private void paintInitialFoundations() {
        for (int i = 0; i < 4; i++) {
            ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null,
                    ViewConstants.TableauFoundationX[i], ViewConstants.WasteDeckFoundationY, -1, -1);
        }
    }

    private void paintInitialTableaus(ArrayList<Tableau> tableaus) {
//        // TODO: resize when length to high
//        int longest = getLengthOfLongestTableau(tableaus);
        for (int i = 0; i < Constants.NR_OF_TABLEAUS; i++) {
            Tableau t = tableaus.get(i);

            float x = ViewConstants.TableauFoundationX[i];

            // add empty space beneath
            ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null, x,
                    10.5f * ViewConstants.heightOneSpace, -1, -1);

            // add face-down faceUpCards
            int faceDownSize = t.getFaceDown().size();
            for (int j = 0; j < faceDownSize; j++) {
                ImageWrapper faceDownCard = loader.getBacksideImage();
                float y = 10.5f * ViewConstants.heightOneSpace - (j * ViewConstants.offsetHeightBetweenCards);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceDownCard, GameObject.TABLEAU, x, y, i, j);
            }

            // add face-up card
            if (t.getFaceUp().size() > 1) {
                Gdx.app.log("!!!!!!!!!!!!!!!!", "mehr als eine Face Up Karte! Diese Methode sollte nur bei der Initialisierung verwendet werden!");
            }
            ImageWrapper faceUpCard = loadActorForCardAndSaveInMap(t.getFaceUp().lastElement());
            // y position is dependant on nr in faceDown-Vector
            float y = 10.5f * ViewConstants.heightOneSpace - (faceDownSize * ViewConstants.offsetHeightBetweenCards);
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceUpCard, GameObject.TABLEAU, x, y, i, t.getFaceDown().size());

            // save the y at which the last card (face-up) was positioned
            smallestYForTableau.put(i, y);
        }
    }

    private void paintInitialDeckWaste() {
        // empty waste
        ImageWrapper emptySpace = loader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null,
                ViewConstants.WasteX, ViewConstants.WasteDeckFoundationY, -1, -1);

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
        Gdx.app.log("Debug_game", game.toString());

        Action prevAction = game.getPrevAction();

        // get whether this was a marking action
        if (prevAction != null) {
            int stackIndex = prevAction.getStackIndex();

            List<Card> cardsToBeMarked = new ArrayList<Card>();
//            Card cardToBeMarked = null;
//            int nrOfAdditionalCardsToBeMarked = 0;

            switch (prevAction.getGameObject()) {
                case TABLEAU:
                    Vector<Card> faceUpList = game.getTableauAtPos(stackIndex).getFaceUp();
                    cardsToBeMarked = faceUpList.subList(prevAction.getCardIndex(), faceUpList.size());
                    Gdx.app.log("Debug", faceUpList.toString());
                    Gdx.app.log("Debug", cardsToBeMarked.toString());
//                    nrOfAdditionalCardsToBeMarked = cardsToBeMarked.size() - 1;

//                    cardToBeMarked = game.getTableauAtPos(stackIndex).getFaceUp().get(prevAction.getCardIndex());
                    break;
                case FOUNDATION:
                    cardsToBeMarked.add(game.getFoundationAtPos(stackIndex).getFoundationTop());
//                    cardToBeMarked = game.getFoundationAtPos(stackIndex).getFoundationTop();
                    break;
                case WASTE:
                    cardsToBeMarked.add(game.getDeckWaste().getWasteTop());
//                    cardToBeMarked = game.getDeckWaste().getWasteTop();
                    break;
            }

            List<String> textureStrings = new ArrayList<String>(cardsToBeMarked.size());
            for (Card c : cardsToBeMarked) {
                textureStrings.add(loader.getCardTextureName(c));
            }
//            String textureStringMarkCard = loader.getCardTextureName(cardToBeMarked);
            markCards(textureStrings);
            lastWasAMarkingAction = true;
        }
        // or a move
        else {
            // with successful move, remove marker
            marker.setVisible(false);
            lastWasAMarkingAction = false;

            try {
                Move prevMove = game.getMoves().lastElement();
                handleMove(prevMove, game);

            } catch (Exception e) {
                Gdx.app.log("Error", e.getClass().toString() + ": " + e.getMessage());
//                e.printStackTrace();
                // maybe an invalid move
            }
        }
    }

    // ---------------------------- ACTIONS ----------------------------
    private void markCards(List<String> textureStrings) {
        Gdx.app.log("Debug", "markCards");
        List<ImageWrapper> cardsToBeMarked = new ArrayList<ImageWrapper>(textureStrings.size());
        for (String texString : textureStrings) {
            cardsToBeMarked.add(faceUpCards.get(texString));
        }
//        ImageWrapper cardToBeMarked = faceUpCards.get(textureStringMarkCard);

        if (!cardsToBeMarked.isEmpty()) {
            // move marker to correct position and make visible
            ImageWrapper topElement = cardsToBeMarked.get(0);
            marker.setPosition(topElement.getX() - 4, topElement.getY() - 7);
            marker.setHeight(topElement.getHeight() +
                    (cardsToBeMarked.size() - 1) * ViewConstants.offsetHeightBetweenCards);
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
        int targetStack = -1, targetCard = -1, nrOfFaceDownInSourceTableau = -1;
        if (ac2 != null) {
            targetStack = ac2.getStackIndex();
            targetCard = ac2.getCardIndex();
        }

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

                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    String textureStringWasteTop = loader.getCardTextureName(game.getTableauAtPos(ac2.getStackIndex()).getFaceUp().get(ac2.getCardIndex() + 1));

                    Card targetTopCard = game.getTableauAtPos(ac2.getStackIndex()).getFaceUp().get(ac2.getCardIndex());
                    String textureStringOldTableauTop = null;
                    if (targetTopCard != null) {
                        textureStringOldTableauTop = loader.getCardTextureName(targetTopCard);
                    }
                    int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDown().size();

                    makeMoveWasteToTableau(textureStringWasteTop, textureStringOldTableauTop,
                            targetStack, targetCard, nrOfFaceDownInTargetTableau);


                } else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    String textureStringWasteTop = loader.getCardTextureName(game.getFoundationAtPos(ac2.getStackIndex()).getFoundationTop());
                    Gdx.app.log("Debug", textureStringWasteTop);

                    makeMoveWasteToFoundation(textureStringWasteTop, targetStack);
                }
                break;

            // possibilities: Tableau -> Tableau, Tableau -> Foundation
            case TABLEAU:
                nrOfFaceDownInSourceTableau = game.getTableauAtPos(sourceStack).getFaceDown().size();

                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDown().size();
                    makeMoveTableauToTableau(sourceStack, sourceCard, nrOfFaceDownInSourceTableau, targetStack, targetCard, nrOfFaceDownInTargetTableau);

                } else if (ac2.getGameObject().equals(GameObject.FOUNDATION)) {
                    makeMoveTableauToFoundation(sourceStack, sourceCard, nrOfFaceDownInSourceTableau, targetStack);
                }
                break;

            // possibilities: Foundation -> Tableau
            case FOUNDATION:
                if (ac2.getGameObject().equals(GameObject.TABLEAU)) {
                    int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDown().size();
                    makeMoveFoundationToTableau(sourceStack, targetStack, targetCard, nrOfFaceDownInTargetTableau);
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

        Gdx.app.log("Debug_game", game.toString());

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

    private void makeMoveWasteToTableau(String sourceCardTextureString, String textureStringOldTableauTop, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);
        ImageWrapper targetCard = faceUpCards.get(textureStringOldTableauTop);

        // targetCard may be null, but only if there are no cards in the targetStack
        if (sourceCard != null && !(targetCard == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            // move to new position
            if (targetCard != null) {
                moveCard(sourceCard.getX(), sourceCard.getY(), targetCard.getX(),
                        targetCard.getY() - ViewConstants.offsetHeightBetweenCards, sourceCard);

                // set new smallestYForTableau
                smallestYForTableau.put(targetStack, targetCard.getY() - ViewConstants.offsetHeightBetweenCards);
            } else {
                moveCard(sourceCard.getX(), sourceCard.getY(), ViewConstants.TableauFoundationX[targetStack],
                        ViewConstants.TableauBaseY, sourceCard);

                // set new smallestYForTableau
                smallestYForTableau.put(targetStack, ViewConstants.TableauBaseY - ViewConstants.offsetHeightBetweenCards);
            }

        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveWasteToFoundation(String sourceCardTextureString, int targetStack) {
        ImageWrapper sourceCard = faceUpCards.get(sourceCardTextureString);

        if (sourceCard != null) {
            // move to new position
            moveCard(sourceCard.getX(), sourceCard.getY(), ViewConstants.TableauFoundationX[targetStack],
                    ViewConstants.WasteDeckFoundationY, sourceCard);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveFoundationToTableau(int sourceStack, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        // TODO
    }


    private void makeMoveTableauToTableau(int sourceStack, int sourceCardIndex, int nrOfFaceDownInSourceTableau, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
        ImageWrapper sourceCard = null;
        ImageWrapper targetCard = null;

        for (String textureString : faceUpCards.keySet()) {
            ImageWrapper cardToBeCompared = faceUpCards.get(textureString);

            if (cardToBeCompared.getWrapperStackIndex() == sourceStack
                    && (cardToBeCompared.getWrapperCardIndex() - nrOfFaceDownInSourceTableau) == sourceCardIndex
                    && cardToBeCompared.getGameObject().equals(GameObject.TABLEAU)) {
                sourceCard = cardToBeCompared;

            } else if (cardToBeCompared.getWrapperStackIndex() == targetStack
                    && (cardToBeCompared.getWrapperCardIndex() - nrOfFaceDownInTargetTableau) == targetCardIndex
                    && cardToBeCompared.getGameObject().equals(GameObject.TABLEAU)) {
                targetCard = cardToBeCompared;
            }
        }

        if (sourceCard != null && targetCard != null) {
            // move to new position
            // TODO: set new smallestYForTableau
            moveCard(sourceCard.getX(), sourceCard.getY(), targetCard.getX(),
                    targetCard.getY() - ViewConstants.offsetHeightBetweenCards, sourceCard);
            // TODO: turn card lying beneath moved card
        } else {
            Gdx.app.log("Fehler", "Fehler: source or target of move could not be found");
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveTableauToFoundation(int sourceStack, int sourceCardIndex, int nrOfFaceDownInSourceTableau, int targetStack) {
        // TODO
    }

    /**
     * can later be used when animating the game
     *
     * @param sourceX
     * @param sourceY
     * @param targetX
     * @param targetY
     * @param card
     */
    private void moveCard(float sourceX, float sourceY, float targetX, float targetY, ImageWrapper card) {
        card.setPosition(targetX, targetY);
    }

    // ------------------------------------ getActionForTap for Controller ------------------------------------

    /**
     * for a given tap from the user, the view returns information about the position where the tap occurred
     *
     * @param x
     * @param y
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
                    float biggestY = 10 * ViewConstants.heightOneSpace + ViewConstants.heightCard;

                    if (y >= smallestY && y <= biggestY) {
                        // a tableau can at most hold 20 faceUpCards (14 in a row from king to ace + 6 face-down)
                        for (int i = 0; i < 20; i++) {
                            if ((y <= biggestY - (i * ViewConstants.offsetHeightBetweenCards) &&
                                    (y >= biggestY - ((i + 1) * ViewConstants.offsetHeightBetweenCards))
                                    || Math.abs(biggestY - (i * ViewConstants.offsetHeightBetweenCards) - smallestY) <= ViewConstants.heightCard)) {

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
            ViewConstants.heightCard = cardImage.getHeight() * 1.6f;    // for some reason only this works :P
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
    }
}
