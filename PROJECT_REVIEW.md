# Project Review: Astronomic-Engine

## What looks good
- The codebase has a clear package structure (`graphics`, `comp`, `shapes`, `util`, `window`) that maps well to an engine architecture.
- You already have test/example fixtures and resource packs under `lib/src/test`, which is a strong base for demo-driven development.
- Build setup uses version catalogs (`gradle/libs.versions.toml`), which is good for dependency governance.

## Highest-priority issues to address

### 1) Repository hygiene (build artifacts are checked in)
The repository currently contains generated outputs (`.gradle/`, `build/`, `lib/build/`, compiled `.class` files, and reports). These create noisy diffs and can hide real source changes.

**Recommendation:**
- Keep generated artifacts out of version control (this PR adds a practical `.gitignore`).
- Optionally clean history in a future maintenance PR if repository size has grown due to binaries.

### 2) Build reproducibility in constrained environments
Running tests failed due to dependency resolution returning HTTP 403 from Maven Central in this environment. This blocks CI-like verification.

**Recommendation:**
- Validate repository/proxy configuration for CI runners.
- Consider dependency caching or mirror setup for restricted networks.
- Add a lightweight `:lib:compileJava` CI job in addition to tests, so compilation can still be verified when test dependencies are temporarily unavailable.

### 3) Error handling quality in utility layer
`FileUtils.internal(...)` currently catches `URISyntaxException`, prints stack traces, and continues with an empty path fallback, which can produce less actionable downstream errors.

**Recommendation:**
- Throw a descriptive unchecked exception that includes the resource path and cause.
- Avoid silent fallbacks that return invalid `File` objects.

## Medium-priority improvements
- Add a root-level `CONTRIBUTING.md` with local build/test commands and Java version expectations.
- Add CI (GitHub Actions): `./gradlew :lib:compileJava` and `./gradlew :lib:test`.
- Normalize naming consistency between historical `astronomicengine` artifact paths and current `org.astroEngine` package paths to reduce confusion.

## Suggested roadmap
1. **Stability pass:** tighten utility exception handling + basic CI compile gate.
2. **Developer experience pass:** contributor docs + deterministic local run/test script.
3. **Feature pass:** add focused unit tests around ECS/component lifecycle and resource loading.

## Commands run for this review
- `./gradlew test` (failed in this environment due to dependency fetch 403).
- File inspection of README, Gradle config, and selected engine source files.
