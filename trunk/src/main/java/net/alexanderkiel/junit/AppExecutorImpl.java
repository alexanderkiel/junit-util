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

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Kiel
 */
class AppExecutorImpl implements AppExecutor {

    private final Runtime runtime;
    private final List<String> args;
    private boolean commandSet;
    private Process process;
    private BufferedReader standardOut;
    private BufferedReader standardErr;

    AppExecutorImpl(@NotNull Runtime runtime) {
        this.runtime = runtime;
        args = new ArrayList<String>();
    }

    /**
     * Sets the command name of the application of this app executor.
     *
     * @param command the command name.
     */
    public void setCommand(@NotNull String command) {
        commandSet = true;
        args.add(command);
    }

    public void addArg(@NotNull String arg) {
        args.add(arg);
    }

    public void addArgs(String... args) {
        this.args.addAll(asList(args));
    }

    public void executeApp() throws IOException {
        if (!commandSet) {
            throw new IllegalStateException("The was no command set.");
        }

        process = runtime.exec(getArgsAsArray());

        standardOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        standardErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    private String[] getArgsAsArray() {
        return args.toArray(new String[args.size()]);
    }

    public void assertLineOfOutput(@NotNull String expectedLine) throws IOException {
        String line = standardOut.readLine();
        assertNotNull(lineExistsMessage("STDOUT"), line);
        assertEquals("line of output on STDOUT", expectedLine, line);
    }

    private static String lineExistsMessage(String streamName) {
        return String.format("output on %s exists", streamName);
    }

    public void assertLineOfOutputMatches(@NotNull String expectedLineRegExp) throws IOException {
        String line = standardOut.readLine();
        assertNotNull(lineExistsMessage("STDOUT"), line);
        assertTrue(matchingMessage("STDOUT", expectedLineRegExp, line), line.matches(expectedLineRegExp));
    }

    private static String matchingMessage(String streamName, String expectedLineRegExp, String line) {
        return String.format("line of output on %s matches /%s/ but was: %s", streamName, expectedLineRegExp, line);
    }

    public void assertLineOfError(@NotNull String expectedLine) throws IOException {
        assertEquals("line of output on STDERR", expectedLine, standardErr.readLine());
    }

    public void assertLineOfErrorMatches(@NotNull String expectedLineRegExp) throws IOException {
        String line = standardErr.readLine();
        assertNotNull(lineExistsMessage("STDERR"), line);
        assertTrue(matchingMessage("STDERR", expectedLineRegExp, line), line.matches(expectedLineRegExp));
    }

    public void assertNoMoreOutput() throws IOException {
        assertEquals("no more output on STDOUT", "", getAllLines(standardOut));
    }

    private static String getAllLines(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    public void assertNoMoreErrors() throws IOException {
        assertEquals("no more output on STDERR", "", getAllLines(standardErr));
    }

    public void assertNormalExit() throws InterruptedException {
        assertExit(0);
    }

    public void assertExit(int expectedStatusCode) throws InterruptedException {
        assertEquals(displayCommand() + " status code is", expectedStatusCode, process.waitFor());
    }

    private String displayCommand() {
        Iterator<String> iterator = args.iterator();
        StringBuilder buffer = new StringBuilder(iterator.next());
        while (iterator.hasNext()) {
            buffer.append(' ').append(iterator.next());
        }
        return buffer.toString();
    }

    public void destroy() {
        process.destroy();
    }

    @Override
    public String toString() {
        return "AppExecutorImpl[args = " + args + "]";
    }
}
