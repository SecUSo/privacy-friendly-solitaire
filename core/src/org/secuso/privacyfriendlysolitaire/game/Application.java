package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.HistorianListener;
import org.secuso.privacyfriendlysolitaire.ScoreListener;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;


import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * @author: I. Dix
 * the outer application, holding everything together (model, view, controller)
 * it is responsible for creating and redrawing the stage and is the contact point from the Android app
 */
public class Application extends ApplicationAdapter {
    private Stage stage;

    private CallBackListener listener;

    // state of game
    private SolitaireGame game;
    private View view;
    private Controller controller;

    private Scorer scorer;
    private Historian historian;

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
        initialiseModelViewAndController();
    }

    private void initialiseModelViewAndController() {
        game = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode);

        initialiseViewAndController();

        Gdx.input.setInputProcessor(new GestureDetector(controller));
    }

    private void initialiseViewAndController() {
//        Gdx.app.log("stage", stage.toString());
        view = new View(game, stage);
        game.addObserver(view);
        if (scoreMode == Constants.MODE_STANDARD && false) {
            scorer = new StandardScorer();
        } else if (scoreMode == Constants.MODE_VEGAS || true) {
            scorer = new VegasScorer();
        }
        game.addObserver(scorer);
        scorer.update(game, null);
        historian = new Historian();
        game.addObserver(historian);
        historian.update(game, null);
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

    public void print() {
        Gdx.app.log("debug", game.toString());
    }

    public void registerCallBackListener(CallBackListener listener) {
        this.listener = listener;
    }

    public void registerHistorianListener(HistorianListener historianListener) {
        this.historian.registerHistorianListener(historianListener);
    }

    public void registerScoreListener(ScoreListener scoreListener) {
        this.scorer.registerScoreListener(scoreListener);
    }

    public void undo() {
        if (historian.canUndo()) {
            game = historian.undo();

            // clear stage
            final Viewport v = stage.getViewport();
            stage.clear();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    stage = new Stage(v, new SpriteBatch());

                    reinitViewAndController();
                }
            });
        }
    }

    public void redo() {
        if (historian.canRedo()) {
            game = historian.redo();

            // clear stage
            final Viewport v = stage.getViewport();
            stage.clear();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    stage = new Stage(v, new SpriteBatch());

                    reinitViewAndController();
                }
            });
        }
    }

    private void reinitViewAndController() {
        view = new View(game, stage);
        game.addObserver(view);
        controller = new Controller(game, view);
    }
}
