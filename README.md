# ANTLR Parser Adapter for JetBrains IDEs

A library to support the use of ANTLR grammars for custom languages in 
jetbrains IDEs plug-in development.

This library has adaptors that convert ANTLR-generated parse trees into 
jetbrains PSI trees.  

# Usage

## Gradle

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




