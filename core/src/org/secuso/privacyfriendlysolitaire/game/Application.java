package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
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

    public void customConstructor(int cardDrawMode, int scoreMode) {
        this.cardDrawMode = cardDrawMode;
        this.scoreMode = scoreMode;
    }

    @Override
    public void create() {
        stage = new Stage();
        initialiseModelViewAndController();
    }

    private void initialiseModelViewAndController() {
        // comment in for directly won game ;-)
//        game = GeneratorSolitaireInstance.buildAlmostWonSolitaireInstance();
        game = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode);
        view = new View(game, stage);
        game.addObserver(view);
        if (scoreMode == Constants.MODE_STANDARD) {
            scorer = new StandardScorer();
        } else if (scoreMode == Constants.MODE_VEGAS) {
            scorer = new VegasScorer();
        }
        game.addObserver(scorer);
        historian = new Historian();
        game.addObserver(historian);
        historian.update(game, null);
        controller = new Controller(game, view);
        Gdx.input.setInputProcessor(new GestureDetector(controller));
    }

    @Override
    public void render() {
        // make transparent, so the background can be set from android, instead of here
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

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
}
