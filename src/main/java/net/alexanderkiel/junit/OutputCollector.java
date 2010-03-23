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

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class is a unit testing helper.
 * <p/>
 * It collects the output written to its {@link #getStream() print stream} and provides methods to assert on this output
 * later on.
 *
 * @author Alexander Kiel
 * @version $Id$
 */
public class OutputCollector {

    private PrintStream printStream;
    private ByteArrayOutputStream outputStorage;
    private boolean printStreamOpen;
    private BufferedReader reader;

    public OutputCollector() throws IOException {
        outputStorage = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStorage);
        printStreamOpen = true;
    }

    /**
     * Returns the print stream to which classes under test can write there output.
     *
     * @return a print stream.
     */
    public PrintStream getStream() {
        return printStream;
    }

    /**
     * Asserts that the next line collected holds the given string.
     *
     * @param expectedLine the expected line without any newline characters.
     */
    public void assertLine(String expectedLine) {
        checkPrintStreamClosed();
        assertEquals(expectedLine, readLine());
    }

    private void checkPrintStreamClosed() {
        if (printStreamOpen) {
            closePrintStream();
        }
    }

    private void closePrintStream() {
        printStream.close();
        byte[] output = outputStorage.toByteArray();
        reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(output)));
        printStreamOpen = false;
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new AssertionError("Reading from a byte array can't throw an IOException but was: " + e.getMessage());
        }
    }

    public void assertNoMoreLines() {
        checkPrintStreamClosed();
        String line = readLine();
        assertNull("No more lines but was: " + line, line);
    }
}
