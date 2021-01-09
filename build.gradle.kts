plugins { id("io.vacco.common-build") version "0.5.3" }

group = "io.vacco.jaad"
version = "0.8.7"

configure<io.vacco.common.CbPluginProfileExtension> {
  addGoogleJavaFormat()
  addSpotBugs()
  addClasspathHell()
  setPublishingUrlTransform { repo -> "${repo.url}/${project.name}" }
  sharedLibrary()
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Main-Class"] = "net.sourceforge.jaad.Radio"
  }
}
