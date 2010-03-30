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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * This JUnit 4 test class is a base for integration tests of command-line applications which are packed by maven as a
 * tar.gz assembly.
 * <p/>
 * This class does the following:<ul><li>extracts the tar.gz assembly packed by maven in the {@link BeforeClass
 * BeforeClass phase},<li>executes the application specified in the constructor on calling {@link #executeApp()} and
 * <li>removes the extracted directory in the {@link AfterClass AfterClass phase}.</ul>
 * <p/>
 * The name of the maven assembly has to be specified through {@link System#getProperties() system properties}. The
 * system properties used are the following:<dl><dt>{@code project-artifact-id}<dd>the maven artifact id of the
 * assembly<dt>{@code project-version}<dd>the maven version of the assembly<dt>{@code classifier}<dd>the maven
 * classifier of the assembly</dl>
 * <p/>
 * The tar.gz which will be extracted is:<pre>{@code
 * target/${project-artifact-id}-${project-version}-${classifier}.tar.gz}.</pre>
 * <p/>
 * It is assumed that the test runs in the base directory of the maven module which generates the assembly. As written
 * above it is assumed that the tar.gz can be found in the standard maven {@code target} directory. The tar.gz will be
 * extracted inside the target directory.
 * <p/>
 * <b>This class uses the commands {@code tar} and {@code rm}. So it is not portable.</b>
 *
 * @author Alexander Kiel
 * @version $Id$
 */
public abstract class AbstractAppIT implements AppExecutor {

    static final String ARTIFACT_ID = System.getProperty("project-artifact-id");
    static final String VERSION = System.getProperty("project-version");
    static final String CLASSIFIER = System.getProperty("classifier");
    static final String ASSEMBLY_NAME = ARTIFACT_ID + "-" + VERSION + "-" + CLASSIFIER + ".tar.gz";

    private final String appName;
    private AppExecutor defaultAppExecutor;

    /**
     * Subclasses have to call this constructor to specify the name of the application under test.
     *
     * @param appName the name of the application under test.
     * @see #executeApp()
     */
    protected AbstractAppIT(@NotNull String appName) {
        this.appName = appName;
    }

    @BeforeClass
    public static void extractTarGz() throws Exception {
        Process process = Runtime.getRuntime().exec("tar -C target -xzf target/" + ASSEMBLY_NAME);
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    @AfterClass
    public static void deleteExtractedDir() throws Exception {
        Process process = Runtime.getRuntime().exec("rm -r target/" + ARTIFACT_ID + "-" + VERSION);
        process.waitFor();
        assertEquals(0, process.exitValue());
    }

    protected AppExecutor createAdditionalAppExecutor(@NotNull String appName) {
        AppExecutor appExecutor = new AppExecutorImpl(Runtime.getRuntime());
        appExecutor.setCommand(getCommand(appName));
        return appExecutor;
    }

    private String getCommand(@NotNull String appName) {
        return "target/" + ARTIFACT_ID + "-" + VERSION + "/bin/" + appName;
    }

    /**
     * Prepares the execution of the application under test.
     */
    @Before
    public void prepareApp() {
        defaultAppExecutor = new AppExecutorImpl(Runtime.getRuntime());
        defaultAppExecutor.setCommand(getCommand(appName));
    }

    /**
     * Adds one command-line argument to the application under test.
     *
     * @param arg the command-line argument
     * @see #executeApp()
     */
    public void addArg(@NotNull String arg) {
        if (defaultAppExecutor == null) {
            throw new IllegalStateException("Illegal state: prepareApp() was not called by JUnit 4.");
        }
        defaultAppExecutor.addArg(arg);
    }

    /**
     * Executes the application under test.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void executeApp() throws IOException {
        defaultAppExecutor.executeApp();
    }

    /**
     * Asserts that the application under test outputs the given line on {@link System#out STDOUT}.
     * <p/>
     * Please specify the line without any line terminating characters.
     *
     * @param expectedLine the expected line of output.
     * @throws IOException if an I/O error occurs.
     */
    public void assertLineOfOutput(@NotNull String expectedLine) throws IOException {
        defaultAppExecutor.assertLineOfOutput(expectedLine);
    }

    /**
     * Asserts that the application under test outputs the given expectedLine on {@link System#err STDERR}.
     * <p/>
     * Please specify the expectedLine without any expectedLine terminating characters.
     *
     * @param expectedLine the expected expectedLine of output.
     * @throws IOException if an I/O error occurs.
     */
    public void assertLineOfError(@NotNull String expectedLine) throws IOException {
        defaultAppExecutor.assertLineOfError(expectedLine);
    }

    /**
     * Asserts that there is no more output on {@link System#out STDOUT} of the application under test.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertNoMoreOutput() throws IOException {
        defaultAppExecutor.assertNoMoreOutput();
    }

    /**
     * Asserts that there is no more output on {@link System#err STDERR} of the application under test.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertNoMoreErrors() throws IOException {
        defaultAppExecutor.assertNoMoreErrors();
    }

    /**
     * Asserts that the application under test terminates with a status code of zero.
     *
     * @throws InterruptedException if the current thread is {@link Thread#interrupt() interrupted} by another thread
     *                              while it is waiting for the application to terminate.
     */
    public void assertNormalExit() throws InterruptedException {
        defaultAppExecutor.assertNormalExit();
    }

    /**
     * Asserts that the application under test terminates with the given status code.
     *
     * @param expectedStatusCode the expected status code.
     * @throws InterruptedException if the current thread is {@link Thread#interrupt() interrupted} by another thread
     *                              while it is waiting for the application to terminate.
     */
    public void assertExit(int expectedStatusCode) throws InterruptedException {
        defaultAppExecutor.assertExit(expectedStatusCode);
    }
}
