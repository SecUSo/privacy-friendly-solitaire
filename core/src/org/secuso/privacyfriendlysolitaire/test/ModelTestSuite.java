package org.secuso.privacyfriendlysolitaire.test;

/**
 * @author M. Fischer
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RankTests.class,
        DeckWasteTests.class,
        FoundationTests.class,
        TableauTests.class
})

public class ModelTestSuite {
}
