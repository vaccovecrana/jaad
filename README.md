# JAAD

> Fork of https://sourceforge.net/projects/jaadec/ containing fixes to make it play nice with other Java Sound Providers.

The original project was licensed under Public Domain and as such this fork is also licensed under Public Domain. Use as you like!

JAAD is an AAC decoder and MP4 demultiplexer library written completely in Java. It uses no native libraries,
is platform-independent and portable. It can read MP4 container from almost every input-stream (files, network
sockets etc.) and decode AAC-LC (Low Complexity) and HE-AAC (High Efficiency/AAC+).

This library is available on Bintray's `jcenter` as a Maven/Gradle download.<br>

[![Download](https://api.bintray.com/packages/vaccovecrana/vacco-oss/jaad/images/download.svg) ](https://bintray.com/vaccovecrana/vacco-oss/jaad/_latestVersion)

```groovy
dependencies {
  compile 'io.vacco.jaad:jaad:<VERSION>'
}
```
