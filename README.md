# ANTLR Parser Adapter for JetBrains IDEs

A library to support the use of ANTLR grammars for custom languages in 
jetbrains IDEs plug-in development.

This library has adaptors that convert ANTLR-generated parse trees into 
jetbrains PSI trees.  

# Usage

Instruction below is suitable for plugin that are using [gradle build system](https://github.com/JetBrains/gradle-intellij-plugin).

Add repository to the build script - `https://dl.bintray.com/antlr/maven/`:

```groovy
repositories {
    jcenter()
    mavenLocal()
    maven {
        url "https://dl.bintray.com/antlr/maven/"
    }
}
```

Add library as a dependency - `org.antlr:antlr4-jetbrains-adapter:1.2.0`:

```groovy
dependencies {
    compile 'org.antlr:antlr4-runtime:4.5.1'
    compile ('org.antlr:antlr4-jetbrains-adapter:1.2.0') {
        exclude group: 'com.jetbrains'
    }
}
```

Plug-in that demonstrates the use of this library: [protostuff/protobuf-jetbrains-plugin](protostuff/protobuf-jetbrains-plugin).

# Build HOW-TO

Build:

```bash
./gradlew clean build
```

Deploy to bintray:

```bash
./gradlew bintrayUpload
```

Install to local maven repository:

```bash
./gradlew publishMainPublicationToMavenLocal
```

# CI

Builds run automatically: https://jenkins.ksprojects.org/job/kshchepanovskyi/

Artifacts are uploaded to bintray:

https://bintray.com/antlr/maven/antlr4-jetbrains-adapter/

Version number computation logic:

```sh
if [[ "$BRANCH_NAME" =~ ^(release/).*$ ]]; then
  VERSION=$(cat release.txt).${BUILD_ID}
elif [[ "$BRANCH_NAME" =~ ^(master).*$ ]]; then
  VERSION=$(cat release.txt).alpha.${BUILD_ID}                                                              
else
  VERSION=$(cat release.txt).snapshot.$(git rev-parse HEAD)
fi
```
