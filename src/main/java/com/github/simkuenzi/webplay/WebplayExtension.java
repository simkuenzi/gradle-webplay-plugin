package com.github.simkuenzi.webplay;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public class WebplayExtension {

    private final Property<File> recordTo;
    private final Property<Integer> recorderPort;
    private final Property<Integer> appPort;
    private final ListProperty<String> mimeTypes;
    private final Property<String> startPath;
    private final Property<File> stopFile;

    public WebplayExtension(ObjectFactory objectFactory, Project project) {
        Path webplayDir = project.getBuildDir().toPath().resolve("webplay");
        recordTo = objectFactory.property(File.class);
        recordTo.set(webplayDir.resolve("scenario.xml").toFile());
        recorderPort = objectFactory.property(Integer.class);
        recorderPort.set(22016);
        appPort = objectFactory.property(Integer.class);
        appPort.set(8080);
        mimeTypes = objectFactory.listProperty(String.class);
        mimeTypes.set(List.of("text/html", "application/x-www-form-urlencoded"));
        startPath = objectFactory.property(String.class);
        startPath.set("/");
        stopFile = objectFactory.property(File.class);
        stopFile.set(webplayDir.resolve("stop").toFile());
    }

    public Property<File> getRecordTo() {
        return recordTo;
    }

    public void setRecordTo(File recordTo) {
        this.recordTo.set(recordTo);
    }

    public Property<Integer> getRecorderPort() {
        return recorderPort;
    }

    public void setRecorderPort(int recorderPort) {
        this.recorderPort.set(recorderPort);
    }

    public Property<Integer> getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort.set(appPort);
    }

    public ListProperty<String> getMimeTypes() {
        return mimeTypes;
    }

    public void setMimeTypes(Iterable<? extends String> mimeTypes) {
        this.mimeTypes.set(mimeTypes);
    }

    public Property<String> getStartPath() {
        return startPath;
    }

    public void setStartPath(String startPath) {
        this.startPath.set(startPath);
    }

    public Property<File> getStopFile() {
        return stopFile;
    }

    public void setStopFile(File stopFile) {
        this.stopFile.set(stopFile);
    }
}
