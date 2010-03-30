package net.alexanderkiel.junit;

import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
        Assert.assertEquals(0, process.exitValue());
    }

    @AfterClass
    public static void deleteExtractedDir() throws Exception {
        Process process = Runtime.getRuntime().exec("rm -r target/" + ARTIFACT_ID + "-" + VERSION);
        process.waitFor();
        Assert.assertEquals(0, process.exitValue());
    }

    protected AppExecutor createAdditionalAppExecutor(@NotNull String appName) {
        return new AppExecutorImpl(appName);
    }

    /**
     * Prepares the execution of the application under test.
     */
    @Before
    public void prepareApp() {
        defaultAppExecutor = new AppExecutorImpl(appName);
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
     * @throws IOException If an I/O error occurs.
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
     * @throws IOException If an I/O error occurs.
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
     * Asserts that the application under tests terminates with a status code of zero.
     *
     * @throws InterruptedException if the current thread is {@link Thread#interrupt() interrupted} by another thread
     *                              while it is waiting for the application to terminate.
     */
    public void assertNormalExit() throws InterruptedException {
        defaultAppExecutor.assertNormalExit();
    }

    /**
     * Asserts that the application under tests terminates with the given status code.
     *
     * @param expectedStatusCode the expected status code.
     * @throws InterruptedException if the current thread is {@link Thread#interrupt() interrupted} by another thread
     *                              while it is waiting for the application to terminate.
     */
    public void assertExit(int expectedStatusCode) throws InterruptedException {
        defaultAppExecutor.assertExit(expectedStatusCode);
    }

    private class AppExecutorImpl implements AppExecutor {

        private final List<String> args;
        private Process process;
        private BufferedReader standardOut;
        private BufferedReader standardErr;

        private AppExecutorImpl(String appName) {
            this.args = new ArrayList<String>();
            this.args.add("target/" + ARTIFACT_ID + "-" + VERSION + "/bin/" + appName);
        }

        public void addArg(@NotNull String arg) {
            args.add(arg);
        }

        public void executeApp() throws IOException {
            process = Runtime.getRuntime().exec(getArgsAsArray());

            standardOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            standardErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        }

        private String[] getArgsAsArray() {
            return args.toArray(new String[args.size()]);
        }

        public void assertLineOfOutput(@NotNull String expectedLine) throws IOException {
            Assert.assertEquals("line of output on STDOUT", expectedLine, standardOut.readLine());
        }

        public void assertLineOfError(@NotNull String expectedLine) throws IOException {
            Assert.assertEquals("line of output on STDERR", expectedLine, standardErr.readLine());
        }

        public void assertNoMoreOutput() throws IOException {
            Assert.assertEquals("no more output on STDOUT", "", getAllLines(standardOut));
        }

        private String getAllLines(BufferedReader reader) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }

        public void assertNoMoreErrors() throws IOException {
            Assert.assertEquals("no more output on STDERR", "", getAllLines(standardErr));
        }

        public void assertNormalExit() throws InterruptedException {
            assertExit(0);
        }

        public void assertExit(int expectedStatusCode) throws InterruptedException {
            Assert.assertEquals(expectedStatusCode, process.waitFor());
        }
    }
}
