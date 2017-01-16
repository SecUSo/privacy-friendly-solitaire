package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.GameObject;
import org.secuso.privacyfriendlysolitaire.model.Move;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author M. Fischer
 */

public class MoveTests {

    @Test
    public void cloneTest() {
        Move move1 = new Move(new Action(GameObject.DECK, 0, 1), new Action(GameObject.WASTE, 2, 3));
        Move move2 = move1.clone();
        assertEquals(move1.getAction1().getGameObject(), move2.getAction1().getGameObject());
        assertEquals(move1.getAction1().getStackIndex(), move2.getAction1().getStackIndex());
        assertEquals(move1.getAction1().getCardIndex(), move2.getAction1().getCardIndex());
        assertEquals(move1.getAction2().getGameObject(), move2.getAction2().getGameObject());
        assertEquals(move1.getAction2().getStackIndex(), move2.getAction2().getStackIndex());
        assertEquals(move1.getAction2().getCardIndex(), move2.getAction2().getCardIndex());
        Action action = new Action(GameObject.FOUNDATION, 4, 5);
        move2.setAction1(action);
        assertNotEquals(move1.getAction1().getGameObject(), move2.getAction1().getGameObject());
        assertNotEquals(move1.getAction1().getStackIndex(), move2.getAction1().getStackIndex());
        assertNotEquals(move1.getAction1().getCardIndex(), move2.getAction1().getCardIndex());
    }
}
