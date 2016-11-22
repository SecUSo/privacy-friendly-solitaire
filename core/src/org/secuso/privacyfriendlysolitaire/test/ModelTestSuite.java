package org.secuso.privacyfriendlysolitaire.test;

/**
 * Created by m0 on 11/22/16.
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
