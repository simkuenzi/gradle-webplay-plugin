package com.github.simkuenzi.webplay;

import org.gradle.api.Action;
import org.gradle.api.Task;

public class WebplayTask implements Action<Task> {
    private String description;
    private Execution execution;

    public WebplayTask(String description, Execution execution) {
        this.description = description;
        this.execution = execution;
    }

    @Override
    public void execute(Task task) {
        task.setGroup("webplay");
        task.setDescription(description);
        task.doLast((t) -> {
            try {
                execution.exec();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    interface Execution {
        void exec() throws Exception;
    }
}
