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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

/**
 * @author Alexander Kiel
 */
@RunWith(MockitoJUnitRunner.class)
public class AppExecutorImplTest {

    private AppExecutorImpl appExecutor;

    @Mock
    private Runtime runtime;

    @Mock
    private Process process;

    @Before
    public void setUp() throws Exception {
        appExecutor = new AppExecutorImpl(runtime);
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteAppWithoutSpecifyingACommand() throws Exception {
        appExecutor.executeApp();
    }

    @Test
    public void testExecuteApp() throws Exception {
        given(runtime.exec(new String[]{"foo"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("normal output"));
        given(process.getErrorStream()).willReturn(getInputStream("error"));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.executeApp();

        appExecutor.assertLineOfOutput("normal output");
        appExecutor.assertLineOfError("error");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithOneArgument() throws Exception {
        given(runtime.exec(new String[]{"foo", "bar"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("normal output"));
        given(process.getErrorStream()).willReturn(getInputStream("error"));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.addArg("bar");
        appExecutor.executeApp();

        appExecutor.assertLineOfOutput("normal output");
        appExecutor.assertLineOfError("error");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithTwoArguments() throws Exception {
        given(runtime.exec(new String[]{"foo", "bar", "baz"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("normal output"));
        given(process.getErrorStream()).willReturn(getInputStream("error"));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.addArg("bar");
        appExecutor.addArg("baz");
        appExecutor.executeApp();

        appExecutor.assertLineOfOutput("normal output");
        appExecutor.assertLineOfError("error");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithTwoArgumentsUsingTheArgsMethod() throws Exception {
        given(runtime.exec(new String[]{"foo", "bar", "baz"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("normal output"));
        given(process.getErrorStream()).willReturn(getInputStream("error"));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.addArgs("bar", "baz");
        appExecutor.executeApp();

        appExecutor.assertLineOfOutput("normal output");
        appExecutor.assertLineOfError("error");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithTwoLinesOfOutput() throws Exception {
        given(runtime.exec(new String[]{"foo"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("first line\nsecond line"));
        given(process.getErrorStream()).willReturn(getInputStream(""));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.executeApp();

        appExecutor.assertLineOfOutput("first line");
        appExecutor.assertLineOfOutput("second line");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithMoreOutputAsExpected() throws Exception {
        given(runtime.exec(new String[]{"foo"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream("first line\nsecond line"));
        given(process.getErrorStream()).willReturn(getInputStream(""));
        given(process.waitFor()).willReturn(0);

        appExecutor.setCommand("foo");
        appExecutor.executeApp();

        try {
            appExecutor.assertNoMoreOutput();
            fail();
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("no more output on STDOUT"));
            assertTrue(e.getMessage().contains("first line"));
            assertTrue(e.getMessage().contains("second line"));
        }
        appExecutor.assertNoMoreErrors();
        appExecutor.assertNormalExit();
    }

    @Test
    public void testExecuteAppWithErrorStatusCode() throws Exception {
        given(runtime.exec(new String[]{"foo"})).willReturn(process);
        given(process.getInputStream()).willReturn(getInputStream(""));
        given(process.getErrorStream()).willReturn(getInputStream("error"));
        given(process.waitFor()).willReturn(1);

        appExecutor.setCommand("foo");
        appExecutor.executeApp();

        appExecutor.assertLineOfError("error");
        appExecutor.assertNoMoreOutput();
        appExecutor.assertNoMoreErrors();
        appExecutor.assertExit(1);
    }

    private InputStream getInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
