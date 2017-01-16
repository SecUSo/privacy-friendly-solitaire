package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.game.View;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author: M. Fischer
 */

public class SolitaireGameTests {

    @Test
    public void cloneTest() {
        SolitaireGame s1 = GeneratorSolitaireInstance.generateInstance(1);
        SolitaireGame s2 = s1.clone();
        assertEquals(s1.getDeckWaste().getNumTurnOver(), s2.getDeckWaste().getNumTurnOver());
        s2.getDeckWaste().setNumTurnOver(3);
        assertNotEquals(s1.getDeckWaste().getNumTurnOver(), s2.getDeckWaste().getNumTurnOver());
    }
}
