package net.alexanderkiel.junit;

import org.junit.Test;

import static net.alexanderkiel.junit.Assert.assertBasicEqualsAndHashCodeBehavior;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class AssertTest {

    @Test
    public void testAssertBasicEqualsAndHashCodeBehavior() {
        assertBasicEqualsAndHashCodeBehavior("foo", "foo", "bar");
    }

    @Test(expected = AssertionError.class)
    public void testAssertBasicEqualsAndHashCodeBehaviorThrowsAssertionErrorOnEqualsNotTestingNull() {
        EqualsNotTestingNull foo1 = new EqualsNotTestingNull(1);
        EqualsNotTestingNull foo2 = new EqualsNotTestingNull(1);
        EqualsNotTestingNull bar = new EqualsNotTestingNull(2);

        assertBasicEqualsAndHashCodeBehavior(foo1, foo2, bar);
    }

    private static class EqualsNotTestingNull {

        private int value;

        private EqualsNotTestingNull(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (getClass() != o.getClass()) return false;

            HashCodeReturnsAlways42 that = (HashCodeReturnsAlways42) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    @Test(expected = AssertionError.class)
    public void testAssertBasicEqualsAndHashCodeBehaviorThrowsAssertionErrorOnHashCodeReturnsAlways42() {
        HashCodeReturnsAlways42 foo1 = new HashCodeReturnsAlways42(1);
        HashCodeReturnsAlways42 foo2 = new HashCodeReturnsAlways42(1);
        HashCodeReturnsAlways42 bar = new HashCodeReturnsAlways42(2);

        assertBasicEqualsAndHashCodeBehavior(foo1, foo2, bar);
    }

    private static class HashCodeReturnsAlways42 {

        private int value;

        private HashCodeReturnsAlways42(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HashCodeReturnsAlways42)) return false;

            HashCodeReturnsAlways42 that = (HashCodeReturnsAlways42) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }
}
