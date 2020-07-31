package com.github.simkuenzi.webplay;

import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class WebplayGradlePluginTest {
    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void recordTaskExplicitConfig() throws Exception {
        recordAndStop("explicit.build.gradle.txt");
    }

    @Test
    public void recordTaskDefaultConfig() throws Exception {
        recordAndStop("defaults.build.gradle.txt");
    }

    @Test
    public void stopTask() throws Exception {
        Path stopFile = testProjectDir.getRoot().toPath().resolve(Path.of("build", "webplay", "stop"));
        Files.createDirectories(stopFile.getParent());
        Files.writeString(stopFile, "init");

        Files.write(testProjectDir.newFile("build.gradle").toPath(),
                getClass().getResourceAsStream("defaults.build.gradle.txt").readAllBytes());

        GradleRunner.create()
                .forwardOutput()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("stop", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks()
                .forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
    }

    @Test
    public void stopTaskNoStopFile() throws Exception {
        Files.write(testProjectDir.newFile("build.gradle").toPath(),
                getClass().getResourceAsStream("defaults.build.gradle.txt").readAllBytes());

        GradleRunner.create()
                .forwardOutput()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("stop", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks()
                .forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
    }

    public void recordAndStop(String buildFile) throws Exception {
        Files.write(testProjectDir.newFile("build.gradle").toPath(),
                getClass().getResourceAsStream(buildFile).readAllBytes());

        List<BuildTask> recordTasks = new ArrayList<>();
        Throwable[] recordException = new Throwable[1];

        Thread recordThread = new Thread(() -> {
            try {
                recordTasks.addAll(GradleRunner.create()
                        .forwardOutput()
                        .withDebug(true)
                        .withProjectDir(testProjectDir.getRoot())
                        .withArguments("record", "--stacktrace")
                        .withPluginClasspath()
                        .build()
                        .getTasks());
            } catch (Throwable e) {
                e.printStackTrace();
                recordException[0] = e;
            }
        });

        recordThread.start();

        try {
            int maxStopTry = 100;
            for (int stopTry = 0; stopTry < maxStopTry; stopTry++) {
                GradleRunner.create()
                        .forwardOutput()
                        .withProjectDir(testProjectDir.getRoot())
                        .withArguments("stop", "--stacktrace")
                        .withPluginClasspath()
                        .build()
                        .getTasks()
                        .forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
                recordThread.join(200);
                if (!recordThread.isAlive()) {
                    break;
                }
            }
            assertFalse("Stopping of recorder thread takes too long.", recordThread.isAlive());
        } finally {
            if (recordThread.isAlive()) {
                recordThread.interrupt();
                recordThread.join();
            }
        }

        assertNull("Exception thrown by recorder thread.", recordException[0]);
        recordTasks.forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
    }
}