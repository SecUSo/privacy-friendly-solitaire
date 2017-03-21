package org.secuso.privacyfriendlysolitaire.generator;
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
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundation;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;
import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_TABLEAUS;

/**
 * @author I. Dix
 * <p>
 * holds the methods that are helpful for both generator and generator-test, but not
 * exactly part of the generation
 */
public class GeneratorUtils {

    /**
     * @param i index of a card in the initial scrambled list
     * @return the correct tableau it should be added to
     * @throws IllegalArgumentException if i>27, because this card should not be added into a tableau
     */
    public static int mapIndexToTableau(int i) {
        int firstUpmost = 0, secondUpmost = 2, thirdUpmost = 5, fourthUpmost = 9,
                fifthUpmost = 14, sixthUpmost = 20, seventhUpmost = 27;
        if (i <= firstUpmost) {
            return 0;
        } else if (i <= secondUpmost) {
            return 1;
        } else if (i <= thirdUpmost) {
            return 2;
        } else if (i <= fourthUpmost) {
            return 3;
        } else if (i <= fifthUpmost) {
            return 4;
        } else if (i <= sixthUpmost) {
            return 5;
        } else if (i <= seventhUpmost) {
            return 6;
        } else {
            throw new IllegalArgumentException("index for tableaus may not ");
        }
    }


    /**
     * @param cardDrawMode the mode
     * @param deck         a list of deck cards
     * @param tableaus     a hashmap of int->Vector<Card> containing the tableaus
     * @return an instance generated from the given lists
     */
    public static SolitaireGame constructInstanceFromCardLists(int cardDrawMode, boolean scoreIsVegas, Vector<Card> deck,
                                                               HashMap<Integer, Vector<Card>> tableaus) {
        HashMap<Integer, Vector<Card>> emptyFoundations = new HashMap<Integer, Vector<Card>>(NR_OF_FOUNDATIONS);
        for (int i = 0; i < NR_OF_FOUNDATIONS; i++) {
            emptyFoundations.put(i, new Vector<Card>());
        }

        return constructInstanceFromCardLists(cardDrawMode, scoreIsVegas, deck, tableaus, emptyFoundations);
    }


    /**
     * @param cardDrawMode the mode
     * @param deck         a list of deck cards
     * @param tableaus     a hashmap of int->Vector<Card> containing the tableaus
     * @param foundations  a hashmap of int->Vector<Card> containing the foundations
     * @return an instance generated from the given lists
     */
    static SolitaireGame constructInstanceFromCardLists(int cardDrawMode, boolean scoreIsVegas,
                                                               Vector<Card> deck,
                                                               HashMap<Integer, Vector<Card>> tableaus,
                                                               HashMap<Integer, Vector<Card>> foundations) {
        DeckWaste d = new DeckWaste(cardDrawMode, scoreIsVegas);
        d.setDeck(deck);

        ArrayList<Tableau> tableauList = new ArrayList<Tableau>(NR_OF_TABLEAUS);
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            Vector<Card> t = tableaus.get(i);
            Tableau tableau = new Tableau();

            try {
                // add last card (with highest index) as face up
                tableau.addFaceUp(t.lastElement());

                // remove this card from the interim-list and add the rest as face down
                t.removeElement(t.lastElement());
                if (!t.isEmpty()) {
                    tableau.setFaceDown(t);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            // add to list
            tableauList.add(tableau);
        }


        ArrayList<Foundation> foundationList = new ArrayList<Foundation>(NR_OF_FOUNDATIONS);
        for (int i = 0; i < NR_OF_FOUNDATIONS; i++) {
            Vector<Card> f = foundations.get(i);
            Foundation foundation = new Foundation();

            for (Card c : f) {
                foundation.addCard(c);
            }

            foundationList.add(foundation);
        }

        return new SolitaireGame(d, foundationList, tableauList);
    }
}
