package com.github.simkuenzi.webplay;

import com.github.simkuenzi.webplay.record.Recorder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.util.Objects;

public class WebplayGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(@Nullable Project project) {
        WebplayExtension config = new WebplayExtension(Objects.requireNonNull(project).getObjects(), project);
        project.getExtensions().add("webplay", config);

        project.getTasks().register("record", new WebplayTask(
                String.format("Starts recording on port %d.", config.getRecorderPort().get()),
                () -> {
                    Files.createDirectories(config.getStopFile().get().toPath().toAbsolutePath().getParent());
                    if (!Files.exists(config.getStopFile().get().toPath())) {
                        Files.createFile(config.getStopFile().get().toPath());
                    }
                    Recorder recorder = new Recorder(
                            config.getRecordTo().get().toPath(),
                            config.getRecorderPort().get(),
                            config.getAppPort().get(),
                            config.getMimeTypes().get(),
                            config.getStartPath().get());
                    recorder.start().waitTillStop(config.getStopFile().get().toPath());
                }
        ));

        project.getTasks().register("stop", new WebplayTask(
                String.format("Stops recording and writes output to %s.", config.getRecordTo().get().toString()),
                () -> Files.writeString(config.getStopFile().get().toPath(), "stop")
        ));
    }
}
