package org.secuso.privacyfriendlysolitaire.test;

import org.junit.Test;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.game.View;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.secuso.privacyfriendlysolitaire.game.Constants.MODE_ONE_CARD_DEALT;
import static org.secuso.privacyfriendlysolitaire.game.Constants.MODE_STANDARD;

/**
 * @author: M. Fischer
 */

public class SolitaireGameTests {

    @Test
    public void cloneTest() {
        SolitaireGame s1 = GeneratorSolitaireInstance.generateInstance(MODE_ONE_CARD_DEALT, MODE_STANDARD);
        SolitaireGame s2 = s1.clone();
        assertEquals(s1.getDeckWaste().getNumTurnOver(), s2.getDeckWaste().getNumTurnOver());
        s2.getDeckWaste().setNumTurnOver(3);
        assertNotEquals(s1.getDeckWaste().getNumTurnOver(), s2.getDeckWaste().getNumTurnOver());
    }
}
