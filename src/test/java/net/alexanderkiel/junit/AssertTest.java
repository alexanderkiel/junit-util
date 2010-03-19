package net.alexanderkiel.junit;

import org.junit.Test;

import static net.alexanderkiel.junit.Assert.assertBasicEqualsAndHashCodeBehavior;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class AssertTest {

    @Test
    public void testAssertBasicEqualsAndHashCodeBehavior() throws Exception {
        assertBasicEqualsAndHashCodeBehavior("foo", "foo", "bar");
    }
}
