package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.ScoreListener;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;
import org.secuso.privacyfriendlysolitaire.model.Move;

import static java.lang.Thread.*;

/**
 * @author I. Dix
 *         the outer application, holding everything together (model, view, controller)
 *         it is responsible for creating and redrawing the stage and is the contact point from the Android app
 */
public class Application extends ApplicationAdapter implements ScoreListener {
    private Stage stage;

    private CallBackListener listener;

    // state of game
    private SolitaireGame game;
    //  private View view;
    private Controller controller;

    private Scorer scorer;

    private int cardDrawMode;
    private int scoreMode;

    private Color backgroundColour;

    public void customConstructor(int cardDrawMode, int scoreMode, Color backgroundColour) {
        this.cardDrawMode = cardDrawMode;
        this.scoreMode = scoreMode;
        this.backgroundColour = backgroundColour;
    }

    @Override
    public void create() {
        stage = new Stage();
        initMVC();
    }

    private void initMVC() {
      game=GeneratorSolitaireInstance.buildAlmostWonSolitaireInstance();
   //     game = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode);
        initVC();

        Gdx.input.setInputProcessor(new GestureDetector(controller));
    }

    private void initVC() {
        View view = new View(game, stage);
        game.registerGameListener(view);

        if (scoreMode == Constants.MODE_STANDARD) {
            scorer = new StandardScorer();
        } else if (scoreMode == Constants.MODE_VEGAS) {
            scorer = new VegasScorer();
        }else if(scoreMode == Constants.MODE_NONE){
            scorer = new NoneScorer();
        }
        game.registerGameListener(scorer);
        scorer.registerScoreListener(this);
        scorer.update(game);

        game.registerCallBackListener(listener);

        controller = new Controller(game, view);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (game.isWon() && listener != null) {
            listener.onWon();
        }

        stage.act();
        stage.draw();
    }


    @Override
    public void dispose() {
        for (Actor actor : stage.getActors()) {
            actor.addAction(Actions.removeActor());
        }
    }

    public void registerCallBackListener(CallBackListener listener) {
        this.listener = listener;
    }

    public void undo() {
        if (game.canUndo()) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.undo();
                }
            });
        }
    }

    public void redo() {
        if (game.canRedo()) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.redo();
                }
            });
        }
    }

    @Override
    public void score(int score) {
        listener.score(score);
    }

    public void autoFoundations() {
        Move move;
        while (true) {
            move = MoveFinder.findMoveTableauToFoundation(game);
            if (move == null) {
                break;
            }
            game.handleAction(move.getAction1(), false);
            game.handleAction(move.getAction2(), false);

            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void autoMove() {
        // all of this needs to run on libgdx's open gl rendering thread
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                Move move;
                //while (true) {
                move = MoveFinder.findMove(game);
                try {
                    if (move != null) {
                        //break;
                        Gdx.app.log("autoMove action1", move.getAction1().toString());
                        game.handleAction(move.getAction1(), false);

                        if (move.getAction2() != null) {
                            Gdx.app.log("autoMove action2", move.getAction2().toString());
                            game.handleAction(move.getAction2(), false);
                        } else {
                            Gdx.app.log("autoMove action2", "null action was not passed to model");
                        }
                    } else {
                        Gdx.app.log("autoMove", "move war null");
                    }
                } catch (Exception e) {
                    Gdx.app.log("----FEHLER----, gesamter Move war ", move.toString());
                    e.printStackTrace();
                }
                //}
            }
        });
    }
}
