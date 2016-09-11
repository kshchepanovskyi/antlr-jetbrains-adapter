# ANTLR support in jetbrains IDEs

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

Add library as a dependency - `org.antlr:antlr4-jetbrains-adapter:1.0.0`:

```groovy
dependencies {
    compile 'org.antlr:antlr4-runtime:4.5.1'
    compile 'org.antlr:antlr4-jetbrains-adapter:1.0.0'
}
```

Sample plug-in that demonstrates the use of this library: [antlr/jetbrains-plugin-sample](https://github.com/antlr/jetbrains-plugin-sample).

# Notes

Mostly this library is about adapting ANTLR 
parsers and trees, but there is considerable support to examine PSI 
trees derived from ANTLR parse trees. For example, if you're building 
a structure view for your plug-in and you want to get the list of 
function names you can use XPath-like specs such as `"/script/function/ID"`:

```java
Collection<? extends PsiElement> allfuncs =
    XPath.findAll(SampleLanguage.INSTANCE, tree,
                  "/script/function/ID");
```




