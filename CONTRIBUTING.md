# How to submit a bug report

If you received an error message, please include it and any exceptions.

We commonly need to know what platform you are on:

*   JDK/JRE version (i.e., `java -version`)
*   Operating system (i.e., `uname -a`)

# How to contribute

We definitely welcome patches and contributions to Instrumentation! Here are
some guidelines and information about how to do so.

## Before getting started

In order to protect both you and ourselves, you will need to sign the
[Contributor License Agreement](https://cla.developers.google.com/clas).

We follow the [Google Java Style
Guide](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html). Our
build automatically will provide warnings for style issues.
[Eclipse](https://google-styleguide.googlecode.com/svn/trunk/eclipse-java-google-style.xml)
and
[IntelliJ](https://google-styleguide.googlecode.com/svn/trunk/intellij-java-google-style.xml)
style configurations are commonly useful. For IntelliJ 14, copy the style to
`~/.IdeaIC14/config/codestyles/`, start IntelliJ, go to File > Settings > Code
Style, and set the Scheme to `GoogleStyle`.

## Proposing changes

Make sure that `./gradlew clean assemble check` completes successfully without
any new warnings. Then create a Pull Request with your changes. When the changes
are accepted, they will be merged or cherry-picked by an Instrumentation core
developer.
