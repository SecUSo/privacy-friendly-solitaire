package org.secuso.privacyfriendlysolitaire.game;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

/**
 * @author: I. Dix
 * <p>
 * the view manages the actors on the stage (the stage is given to it by the application). It observes
 * the model (SolitaireGame game) and reacts to changes in the model by re-arranging the actors.
 * The newly arranged actors are then drawn by the application
 */

public class View implements Observer {
    private Stage stage;

    private float width;
    private float height;

    private float widthOneSpace;        // the width is divided into spaces (37)
    private float heightOneSpace;        // the height is divided into spaces (21)

    private float offsetBetweenFaceDownCards = 30;

    private float scalingForCards = 1.4f;
    private float scalingForCardWidth = 1.7f;

    private ArrayList<Image> cards = new ArrayList<Image>(52);

    public View(SolitaireGame game, Stage stage) {
        this.stage = stage;

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        widthOneSpace = width / 31;
        heightOneSpace = height / 21;

        arrangeView(game);
    }


    private void arrangeView(SolitaireGame game) {
        paintInitialFoundations();

        paintInitialTableaus(game.getTableaus());

        paintInitialDeckWaste(game.getDeckWaste());
    }

    private void paintInitialFoundations() {
        for (int i = 0; i < 4; i++) {
            Image emptySpace = getEmptySpaceImageWithoutLogo();
            float x = (2 + i * (1 + 3)) * widthOneSpace;
            float y = 16 * heightOneSpace;
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
                Image faceDownCard = geBacksideImage();
                // x position is dependant on nr of tableau
                float x = (2 + i * (1 + 3)) * widthOneSpace;
                // y position is dependant on nr in faceDown-Vector
                float y = 10 * heightOneSpace - (j * offsetBetweenFaceDownCards);
                setImageScalingAndPositionAndAddToStage(faceDownCard, x, y);
            }

            // add face-up card
            if (t.getFaceUp().size() > 1) {
                Gdx.app.log("!!!!!!!!!!!!!!!!!!!!", "mehr als eine Face Up Karte! Diese Methode sollte nur bei der Initialisierung verwendet werden!");
            }
            Image faceUpCard = getActorForCard(t.getFaceUp().lastElement());
            // x position is dependant on nr of tableau
            float x = (2 + i * (1 + 3)) * widthOneSpace;
            // y position is dependant on nr in faceDown-Vector
            float y = 10 * heightOneSpace - (faceDownSize * offsetBetweenFaceDownCards);
            setImageScalingAndPositionAndAddToStage(faceUpCard, x, y);
        }
    }

    private void paintInitialDeckWaste(DeckWaste deckWaste) {
        // empty waste
        Image emptySpace = getEmptySpaceImageWithoutLogo();
        float x = (2 + 5 * (1 + 3)) * widthOneSpace;
        float y = 16 * heightOneSpace;
        setImageScalingAndPositionAndAddToStage(emptySpace, x, y);

        // deck
        Image deckCard = geBacksideImage();
        float x1 = (2 + 6 * (1 + 3)) * widthOneSpace;
        float y1 = 16 * heightOneSpace;
        setImageScalingAndPositionAndAddToStage(deckCard, x1, y1);
    }


    @Override
    public void update(Observable o, Object arg) {
        // TODO
        SolitaireGame game = (SolitaireGame) o;

        Action prevAction = game.getPrevAction();
        Move prevMove = game.getRecentMove();

        // get whether this was a marking action
        if (prevAction != null) {

        }
        // or a move
        else {

        }
    }


    private Image getActorForCard(Card card) {
        String textureString = card.getRank().toString().toLowerCase() + "_" + card.getSuit().toString().toLowerCase();

        return getImageForPath("cards/" + textureString + ".png");
    }

    private Image getEmptySpaceImageWithLogo() {
        return getImageForPath("cards/empty_space.png");
    }

    private Image getEmptySpaceImageWithoutLogo() {
        return getImageForPath("cards/empty_space_ohne_logo.png");
    }

    private Image geBacksideImage() {
        return getImageForPath("cards/backside.png");
    }


    private Image getImageForPath(String path) {
        try {
            return new Image(new Texture(Gdx.files.internal(path)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    private void setImageScalingAndPositionAndAddToStage(Image cardImage, float x, float y) {
        cardImage.setPosition(x, y);
        cardImage.setScale(scalingForCards);
        cardImage.setScaleX(scalingForCardWidth);
        stage.addActor(cardImage);
    }
}
