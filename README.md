# `rules_jvm_external` Maven Provided Dependencies Reproduction

Steps to reproduce:

- (optional) run `./bazelisk run @maven//:pin` 
- run `./bazelisk build //:main`

Compilation should fail due to:

```
ERROR: ./rules_jvm_external-provided-issue/BUILD.bazel:16:12: Building main.jar (1 source file) and running annotation processors (UnitsChecker) failed: (Exit 1): java failed: error executing Javac command (from target //:main) external/rules_java++toolchains+remotejdk21_linux/bin/java '--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED' '--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED' ... (remaining 19 arguments skipped)
error: SourceChecker.typeProcess: unexpected Throwable (CompletionFailure) while processing src/main/java/com/example/Main.java; message: class file for io.vertx.codegen.annotations.DataObject not found
  ; The Checker Framework crashed.  Please report the crash.
  Compilation unit: src/main/java/com/example/Main.java
  Last visited tree at line 17 column 9:
          final HttpServerOptions serverOptions = new HttpServerOptions()
  Exception: com.sun.tools.javac.code.Symbol$CompletionFailure: class file for io.vertx.codegen.annotations.DataObject not found; com.sun.tools.javac.code.Symbol$CompletionFailure: class file for io.vertx.codegen.annotations.DataObject not found
Target //:main failed to build
Use --verbose_failures to see the command lines of failed build steps.
```

The build can be "fixed" by performing the following steps:

- add `"io.vertx:vertx-codegen:4.5.10"` to `maven.install()` in `MODULE.bazel`;
- add `artifact("io.vertx:vertx-codegen")` to the `deps` in `BUILD.bazel`;
- run `./bazelisk run @maven//:pin` to regenerate the lock-file

Then,

- run `./bazelisk build //:main`

The build will still fail (intentionally) but for the "correct" reason:

```
ERROR: ./rules_jvm_external-provided-issue/BUILD.bazel:16:12: Building main.jar (1 source file) and running annotation processors (UnitsChecker) failed: (Exit 1): java failed: error executing Javac command (from target //:main) external/rules_java++toolchains+remotejdk21_linux/bin/java '--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED' '--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED' ... (remaining 19 arguments skipped)
src/main/java/com/example/Main.java:26: error: [assignment] incompatible types in assignment.
        final @kg int weight = 100;
                               ^
  found   : @UnknownUnits int
  required: @g(Prefix.kilo) int
Target //:main failed to build
Use --verbose_failures to see the command lines of failed build steps.
```

This proves that Checker Framework is actually running and has got past the Vert.x code generation issue.
