# WebPlay Plugin for Gradle

![Java CI with Gradle](https://github.com/simkuenzi/gradle-webplay-plugin/workflows/Java%20CI%20with%20Gradle/badge.svg)
![Codecov](https://img.shields.io/codecov/c/github/simkuenzi/gradle-webplay-plugin)
![GitHub](https://img.shields.io/github/license/simkuenzi/gradle-webplay-plugin)
![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle+Plugin&color=blue&metadataUrl=https://plugins.gradle.org/m2/com/github/simkuenzi/gradle-webplay-plugin/maven-metadata.xml)

## Summary
The WebPlay Plugin helps you to create tests for your web application. 
It records requests to your web application, which can be replayed in your test suite.

## Usage
### Record from your application
To get started add this configuration to your build.gradle file.
```groovy 
plugins {
    id 'com.github.simkuenzi.webplay' version '0.1'
}   

webplay {
    appPort = 8080 // This is the local port on which your application runs. 
}
```

Before you can start recording, you need to start your web application. 
Your application must listen to the port configured above.

Now, start the record task.
```
$ ./gradlew record
> Task :record
Recording on http://localhost:22016/
```
Open the URL http://localhost:22016/ in your browser. You might need to complete the URL according to 
your application. http://localhost:22016/myApp/index.html for instance.

While you play with your application, WebPlay will record the HTTP requests.

Start the stop task after you are done.

```
$ ./gradlew stop
> Task :stop

BUILD SUCCESSFUL in 4s
1 actionable task: 1 executed
```

The HTTP requests will be saved to build/webplay/test.xml 

### Replay from unit test
Add the webplay library to the test dependencies.
```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation group: 'com.github.simkuenzi', name: 'webplay', version: '1.1'
}
```

Write a test like the one below. Your implementation will look different and depends 
on the server, and the test framework you use.

```java
@Test
public void myTest() throws Exception {
    int port = 8080;
    // 1. Startup your application server. You will need to write something specific for your server/framework here...
    myServer.start(port);
    // 2. Load recorded test
    RecordedTest recordedTest = new RecordedTest(getClass().getResource("test.xml"));
    // 3. replay test
    recordedTest.play("http://localhost:" + port, Assert::assertEquals);
}
```

## Configuration
The WebPlay plugin can be configured in your build.gradle file. All fields are optional.
The default values below will be used as a fallback.
```groovy
webplay {
    // The file for the recorded requests.
   recordTo = file('build/webplay/test.xml')  
    
    // The port on which the recorder is listen
   recorderPort = 22016  

   // The port of your application
   appPort = 8080 

   // Only those MIME types will be recorded.
   mimeTypes = ['text/html', 'application/x-www-form-urlencoded'] 

   // The entry page of your application. 
   // This will be used to provide you with a convenient URL on the console output. 
   startPath = '/' 

   // Recording will end as soon this file gets modified. Needed by the stop task.
   stopFile = file('build/webplay/stop') 
}
```
