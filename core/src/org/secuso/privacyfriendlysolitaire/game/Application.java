package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Application extends ApplicationAdapter {
    Stage stage;

    // state of game
    private SolitaireGame game;
    private View view;
    private Controller controller;

    private int mode;

    public void customConstructor(int mode) {
        this.mode = mode;
    }

    @Override
    public void create() {
        stage = new Stage();

        initialiseModelViewAndController();
    }

    private void initialiseModelViewAndController() {
        game = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(mode);
        view = new View(game, stage);
        controller = new Controller(game, view);
        Gdx.input.setInputProcessor(new GestureDetector(controller));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(204 / 255f, 255 / 255f, 255 / 255f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }


    @Override
    public void dispose() {
        for (Actor actor : stage.getActors()) {
            actor.addAction(Actions.removeActor());
        }
    }
}
