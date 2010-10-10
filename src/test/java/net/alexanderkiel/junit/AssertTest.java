/******************************************************************************
 * Copyright 2010 Alexander Kiel                                              *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package net.alexanderkiel.junit;

import org.junit.Test;

import static net.alexanderkiel.junit.Assert.assertBasicEqualsAndHashCodeBehavior;

/**
 * @author Alexander Kiel
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

        private final int value;

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

        private final int value;

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

    @Test(expected = AssertionError.class)
    public void testAssertBasicEqualsAndHashCodeBehaviorThrowsAssertionErrorOnHashCodeReturnsDifferentValuesOnEqualInstances() {
        HashCodeReturnsDifferentValuesOnEqualInstances foo1 = new HashCodeReturnsDifferentValuesOnEqualInstances(1, 1);
        HashCodeReturnsDifferentValuesOnEqualInstances foo2 = new HashCodeReturnsDifferentValuesOnEqualInstances(1, 2);
        HashCodeReturnsDifferentValuesOnEqualInstances bar = new HashCodeReturnsDifferentValuesOnEqualInstances(2, 1);

        assertBasicEqualsAndHashCodeBehavior(foo1, foo2, bar);
    }

    private static class HashCodeReturnsDifferentValuesOnEqualInstances {

        private final int value1, value2;

        private HashCodeReturnsDifferentValuesOnEqualInstances(int value1, int value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HashCodeReturnsDifferentValuesOnEqualInstances)) return false;

            HashCodeReturnsDifferentValuesOnEqualInstances that = (HashCodeReturnsDifferentValuesOnEqualInstances) o;

            return value1 == that.value1;
        }

        @Override
        public int hashCode() {
            return value2;
        }
    }
}
