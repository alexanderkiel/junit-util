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

import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class OutputCollectorTest {

    private OutputCollector outputCollector;

    private PrintStream printStream;

    @Before
    public void setUp() throws Exception {
        outputCollector = new OutputCollector();
        printStream = outputCollector.getStream();
    }

    @Test
    public void testNoOutput() throws Exception {
        outputCollector.assertNoMoreLines();
    }

    @Test
    public void testOneLineOutput() throws Exception {
        printStream.println("foo");

        outputCollector.assertLine("foo");
        outputCollector.assertNoMoreLines();
    }

    @Test
    public void testTwoLineOutput() throws Exception {
        printStream.println("foo");
        printStream.println("bar");

        outputCollector.assertLine("foo");
        outputCollector.assertLine("bar");
        outputCollector.assertNoMoreLines();
    }

    @Test
    public void testManyLineOutput() throws Exception {
        for (int i = 0; i < 10000; i++) {
            printStream.println("foo " + i);
        }

        for (int i = 0; i < 10000; i++) {
            outputCollector.assertLine("foo " + i);
        }
        outputCollector.assertNoMoreLines();
    }

    @Test
    public void testTwoInvocationsOfAssertNoMoreLines() throws Exception {
        outputCollector.assertNoMoreLines();
        outputCollector.assertNoMoreLines();
    }
}
