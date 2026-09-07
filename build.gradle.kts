plugins {
    java
}

group = "com.bookzilla"
version = "0.0.1-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    repositories {
        mavenCentral()
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}