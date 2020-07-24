package com.github.simkuenzi.webplay;

import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class WebplayGradlePluginTest {
    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void explicitConfig() throws Exception {
        recordAndStop("explicit.build.gradle.txt");
    }

    @Test
    public void defaultConfig() throws Exception {
        recordAndStop("defaults.build.gradle.txt");
    }

    public void recordAndStop(String buildFile) throws Exception {
        Files.write(testProjectDir.newFile("build.gradle").toPath(),
                getClass().getResourceAsStream(buildFile).readAllBytes());

        Future<List<BuildTask>> recordTasks = Executors.newSingleThreadExecutor().submit(() -> GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("record", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks());

        // Wait for stop file being created
        Thread.sleep(1_000);

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("stop", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        recordTasks.get(3, TimeUnit.SECONDS).forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
    }
}