package com.github.simkuenzi.webplay;

import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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

        List<BuildTask> recordTasks = new ArrayList<>();

        Thread recordThread = new Thread(() ->
                recordTasks.addAll(GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("record", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks()));
        recordThread.start();

        // Wait for stop file being created
        Thread.sleep(1_000);

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("stop", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        recordThread.join(3_000);
        recordTasks.forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));
    }
}