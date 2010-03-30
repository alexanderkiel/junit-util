package net.alexanderkiel.junit;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Alexander Kiel
 */
public interface AppExecutor {

    void addArg(@NotNull String arg);

    void executeApp() throws IOException;

    void assertLineOfOutput(@NotNull String expectedLine) throws IOException;

    void assertLineOfError(@NotNull String expectedLine) throws IOException;

    void assertNoMoreOutput() throws IOException;

    void assertNoMoreErrors() throws IOException;

    void assertNormalExit() throws InterruptedException;

    void assertExit(int expectedStatusCode) throws InterruptedException;
}
