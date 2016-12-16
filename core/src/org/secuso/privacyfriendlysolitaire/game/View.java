package org.secuso.privacyfriendlysolitaire.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

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

    private Stage stage;

    private HashMap<String, Image> cards = new HashMap<String, Image>(52);
    // describes the y at which the given tableau is positioned at the smallest
    private HashMap<Integer, Float> smallestYForTableau = new HashMap<Integer, Float>(7);

    public View(SolitaireGame game, Stage stage) {
        this.stage = stage;

        ViewConstants.widthScreen = Gdx.graphics.getWidth();
        ViewConstants.heightScreen = Gdx.graphics.getHeight();

        ViewConstants.widthOneSpace = ViewConstants.widthScreen / 31;
        ViewConstants.heightOneSpace = ViewConstants.heightScreen / 21;

        arrangeInitialView(game);
    }


    private void arrangeInitialView(SolitaireGame game) {
        paintInitialFoundations();
        paintInitialTableaus(game.getTableaus());
//        Gdx.app.log("Debug", "-------------------------");
        paintInitialDeckWaste();
    }

    private void paintInitialFoundations() {
        for (int i = 0; i < 4; i++) {
            Image emptySpace = ImageLoader.getEmptySpaceImageWithoutLogo();
            float x = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
            float y = 16 * ViewConstants.heightOneSpace;
            setImageScalingAndPositionAndAddToStage(emptySpace, x, y);
        }
    }

    private void paintInitialTableaus(ArrayList<Tableau> tableaus) {
        // TODO: resize when length to high
        int longest = getLengthOfLongestTableau(tableaus);

        for (int i = 0; i < Constants.NR_OF_TABLEAUS; i++) {
            Tableau t = tableaus.get(i);

            // add face-down cards
            int faceDownSize = t.getFaceDown().size();
            for (int j = 0; j < faceDownSize; j++) {
                Image faceDownCard = ImageLoader.geBacksideImage();
                // x position is dependant on nr of tableau
                float x = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
                // y position is dependant on nr in faceDown-Vector
                float y = 10.5f * ViewConstants.heightOneSpace - (j * ViewConstants.offsetHeightBetweenCards);
                setImageScalingAndPositionAndAddToStage(faceDownCard, x, y);
            }

            // add face-up card
            if (t.getFaceUp().size() > 1) {
                Gdx.app.log("!!!!!!!!!!!!!!!!", "mehr als eine Face Up Karte! Diese Methode sollte nur bei der Initialisierung verwendet werden!");
            }
            Image faceUpCard = loadActorForCardAndSaveInMap(t.getFaceUp().lastElement());
            // x position is dependant on nr of tableau
            float x = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
            // y position is dependant on nr in faceDown-Vector
            float y = 10.5f * ViewConstants.heightOneSpace - (faceDownSize * ViewConstants.offsetHeightBetweenCards);
            setImageScalingAndPositionAndAddToStage(faceUpCard, x, y);

            // save the y at which the last card (face-up) was positioned
            smallestYForTableau.put(i, y);
        }
    }

    private void paintInitialDeckWaste() {
        // empty waste
        Image emptySpace = ImageLoader.getEmptySpaceImageWithoutLogo();
        float x = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace;
        float y = 16 * ViewConstants.heightOneSpace;
        setImageScalingAndPositionAndAddToStage(emptySpace, x, y);

        // deck
        Image deckCard = ImageLoader.geBacksideImage();
        float x1 = (2 + 6 * (1 + 3)) * ViewConstants.widthOneSpace;
        float y1 = 16 * ViewConstants.heightOneSpace;
        setImageScalingAndPositionAndAddToStage(deckCard, x1, y1);
    }


    /**
     * method to react to changes in the model
     *
     * @param o   the observed object (in this case a solitairegame)
     * @param arg some argument, which is unused at the moment
     */
    @Override
    public void update(Observable o, Object arg) {
        Gdx.app.log("Debug", "-----------update-----------");
        // TODO
        SolitaireGame game = (SolitaireGame) o;

        Action prevAction = game.getPrevAction();
//        Move prevMove = game.getMoves().lastElement();
//
//        // get whether this was a marking action
//        if (prevAction != null) {
//            Gdx.app.log("Debug: Action", prevAction.toString());
//        }
//        // or a move
//        else {
//            Gdx.app.log("Debug: Move", prevMove.toString());
//        }
    }


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
                        // a tableau can at most hold 20 cards (14 in a row from king to ace + 6 face-down)
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
     * returns the length (nr of cards) of the longest tableau
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
    private void setImageScalingAndPositionAndAddToStage(Image cardImage, float x, float y) {
        cardImage.setPosition(x, y);
        cardImage.setScale(1.4f);
        cardImage.setScaleX(1.6f);
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
    private Image getImageForCard(Card card) {
        Image cardImage = cards.get(ImageLoader.getCardTextureName(card));

        if (cardImage != null) {
            return cardImage;
        } else {
            return loadActorForCardAndSaveInMap(card);
        }
    }


    /**
     * loads the texture for the card given and saves it to the cards-map
     *
     * @param card
     * @return the correct texture for this card
     */
    private Image loadActorForCardAndSaveInMap(Card card) {
        String textureString = ImageLoader.getCardTextureName(card);
        Image textureForCard = ImageLoader.getImageForPath("cards/" + textureString + ".png");

        cards.put(textureString, textureForCard);

        return textureForCard;
    }


    /**
     * class to load images
     */
    private static class ImageLoader {
        protected static Image getEmptySpaceImageWithLogo() {
            return getImageForPath("cards/empty_space.png");
        }

        private static Image getEmptySpaceImageWithoutLogo() {
            return getImageForPath("cards/empty_space_ohne_logo.png");
        }

        private static Image geBacksideImage() {
            return getImageForPath("cards/backside.png");
        }

        /**
         * computes the textureString for a card (rank_suit)
         *
         * @param card
         * @return a string containing the card's rank and suit
         */
        private static String getCardTextureName(Card card) {
            return card.getRank().toString().toLowerCase() + "_" + card.getSuit().toString().toLowerCase();
        }

        /**
         * loads an image for a given (relative) path
         *
         * @param path
         * @return the image lying at this path
         */
        private static Image getImageForPath(String path) {
            try {
                return new Image(new Texture(Gdx.files.internal(path)));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private static class ViewConstants {
        private static float widthScreen;
        private static float heightScreen;

        private static float widthOneSpace;        // the widthScreen is divided into spaces (37)
        private static float heightOneSpace;        // the heightScreen is divided into spaces (21)

        private static float offsetHeightBetweenCards = 30;

        private static float heightCard;
        private static float widthCard;
    }
}
