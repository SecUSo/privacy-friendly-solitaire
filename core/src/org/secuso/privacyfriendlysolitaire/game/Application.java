package org.secuso.privacyfriendlysolitaire.game;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

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
    private Controller controller;

    private Scorer scorer;

    private int cardDrawMode;
    private int scoreMode;
    private boolean playSounds;

    private Color backgroundColour;

    private boolean dragAndDrop;

    private boolean won = false;
    private boolean practicallyWon = false;
    private boolean clickPossible = true;

    private int intervallBetweenAutoMoves = 0;

    public void customConstructor(int cardDrawMode, int scoreMode, boolean playSounds,
                                  Color backgroundColour, boolean dragAndDrop) {
        this.cardDrawMode = cardDrawMode;
        this.scoreMode = scoreMode;
        this.playSounds = playSounds;
        this.backgroundColour = backgroundColour;
        this.dragAndDrop = dragAndDrop;
    }

    @Override
    public void create() {
        stage = new Stage();
        initMVC();
    }

    private void initMVC() {
        game = GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode);
        initVC();

        InputProcessor inputProcessorStage = stage;
        InputProcessor inputProcessorController = new GestureDetector(controller);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessorStage);
        inputMultiplexer.addProcessor(inputProcessorController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initVC() {
        View view = new View(game, stage, playSounds, dragAndDrop);
        game.registerGameListener(view);

        if (scoreMode == Constants.MODE_STANDARD) {
            scorer = new StandardScorer();
        } else if (scoreMode == Constants.MODE_VEGAS) {
            scorer = new VegasScorer();
        } else if (scoreMode == Constants.MODE_NONE) {
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

        if (game.isWon() && listener != null && !won) {
            listener.onWon();
            playWonSound();
            won = true;
        } else if (practicallyWon && !won) {
            if (intervallBetweenAutoMoves >= 4) {
                autoMove();
                intervallBetweenAutoMoves = 0;
            } else {
                intervallBetweenAutoMoves++;
            }
        }
        if (game.isPracticallyWon() && !won && listener != null && !practicallyWon) {
            Gdx.input.setInputProcessor(null);
            practicallyWon = true;
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
        if (clickPossible && game.canUndo()) {
            clickPossible = false;
            playUndoRedoSound();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.undo();

                    // re-enable button after animation finished
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            clickPossible = true;
                        }
                    }, .3f);
                }
            });
        }
    }

    public void redo() {
        if (clickPossible && game.canRedo()) {
            clickPossible = false;
            playUndoRedoSound();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.redo();
                    // re-enable button after animation finished
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            clickPossible = true;
                        }
                    }, .3f);
                }
            });
        }
    }

    @Override
    public void score(int score) {
        listener.score(score);
    }

    public void autoFoundations() {
        if (clickPossible) {
            clickPossible = false;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {

                    Move move;
                    while (true) {
                        move = MoveFinder.findMoveTableauToFoundation(game);
                        if (move == null) {
                            break;
                        }
                        game.handleAction(move.getAction1(), false);
                        game.handleAction(move.getAction2(), false);
                        // re-enable button after animation finished
                        Timer.schedule(new Timer.Task(){
                            @Override
                            public void run() {
                                clickPossible = true;
                            }
                        }, .3f);
                    }
                }

            });
        }
    }

    public void autoMove() {
        if (clickPossible || practicallyWon) {
            clickPossible = false;
            // all of this needs to run on libgdx's open gl rendering thread
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {

                    Move move = MoveFinder.findMove(game, listener);
                    try {
                        if (move != null) {
                            //break;
                            game.handleAction(move.getAction1(), false);

                            if (move.getAction2() != null) {
                                game.handleAction(move.getAction2(), false);
                            }

                            if (!practicallyWon) {
                                // re-enable button after animation finished
                                Timer.schedule(new Timer.Task(){
                                    @Override
                                    public void run() {
                                        clickPossible = true;
                                    }
                                }, .3f);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private void playWonSound() {
        playSoundWithName("success.mp3");
    }

    private void playUndoRedoSound() {
        playSoundWithName("button.mp3");
    }

    private void playSoundWithName(String fileName) {
        if (playSounds) {
            Music music = Gdx.audio.newMusic(Gdx.files.getFileHandle("sounds/" + fileName,
                    Files.FileType.Internal));

            try {
                music.setVolume(0.5f);
                music.play();
                music.setLooping(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
