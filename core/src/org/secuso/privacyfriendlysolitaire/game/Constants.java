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

/**
 * @author I. Dix
 * represents a class to store and retrieve constants
 */

public class Constants {

    // unconventional modeling, but this simplifies the usage,
    // because the mode itself says how much cards to draw
    public final static int MODE_ONE_CARD_DEALT = 1;
    public final static int MODE_THREE_CARDS_DEALT = 3;

    public final static int MODE_STANDARD = 0;
    public final static int MODE_VEGAS = 1;
    public final static int MODE_NONE = 2;


    public final static int NR_OF_TABLEAUS = 7;
    public final static int NR_OF_FOUNDATIONS = 4;
    public final static int MAX_NR_IN_DECK = 24;
    public final static int NR_CARDS = 52;
}
