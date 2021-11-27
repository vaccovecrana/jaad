plugins { id("io.vacco.oss.gitflow") version "0.9.7" }

group = "io.vacco.jaad"
version = "0.8.8"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
  sharedLibrary(true, false)
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Main-Class"] = "net.sourceforge.jaad.Radio"
  }
}
