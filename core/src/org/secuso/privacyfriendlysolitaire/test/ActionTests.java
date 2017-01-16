package org.secuso.privacyfriendlysolitaire.test;


import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.GameObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author M. Fischer
 */

public class ActionTests {

    @Test
    public void cloneTest() {
        Action action1 = new Action(GameObject.FOUNDATION, 4, 2);
        Action action2 = action1.clone();
        assertEquals(action1.getGameObject(), action2.getGameObject());
        assertEquals(action1.getStackIndex(), action2.getStackIndex());
        assertEquals(action1.getCardIndex(), action2.getCardIndex());
        action1.setStackIndex(7);
        action2.setCardIndex(8);
        assertNotEquals(action1.getStackIndex(), action2.getStackIndex());
        assertNotEquals(action1.getCardIndex(), action2.getCardIndex());
    }
}
