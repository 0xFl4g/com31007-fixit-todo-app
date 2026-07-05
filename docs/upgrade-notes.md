# Build toolchain upgrade notes

## Why this project stays on Gradle 8.14.5 (not 9.x)

_Last investigated: 2026-07-05._

The project is intentionally pinned to:

| Component | Version |
|-----------|---------|
| Gradle | 8.14.5 |
| Android Gradle Plugin (AGP) | 8.9.0 |
| Kotlin | 2.1.10 |
| KSP | 2.1.10-1.0.31 |

Upgrading to the newest Gradle (9.6.1) was attempted and **reverted**, because on the
current ecosystem it can't be done without breaking this project's zero-warning build.

### The blocker chain

1. **Gradle 9.6.1 requires AGP 9.x.** AGP 8.x does not run on Gradle 9. So "just bump the
   wrapper" isn't possible — it forces an AGP major upgrade.
2. **AGP 9 defaults to built-in Kotlin**, and **KSP is not compatible with it**. Since this
   project uses Room via KSP, the build fails with:
   > KSP is not compatible with Android Gradle Plugin's built-in Kotlin. Please disable by
   > adding `android.builtInKotlin=false` to gradle.properties and apply `kotlin("android")` plugin.
3. Falling back to the standalone `org.jetbrains.kotlin.android` plugin then fails because
   AGP 9's new DSL removed the legacy `BaseExtension` the Kotlin plugin casts to:
   > class ...ApplicationExtensionImpl$AgpDecorated_Decorated cannot be cast to class
   > com.android.build.gradle.BaseExtension
   Working around that requires a second flag, `android.newDsl=false`.
4. With **both** `android.builtInKotlin=false` and `android.newDsl=false`, the build works —
   but emits two deprecation warnings that cannot be cleanly resolved today:
   - `w: Deprecated 'org.jetbrains.kotlin.android' plugin usage`
   - `WARNING: android.newDsl=false is deprecated` (AGP says it will be removed in AGP 10)

   That breaks the project's zero-warning invariant, and `newDsl=false` is a temporary
   escape hatch on a removal path — not a stable configuration.

### Verified-working (but warning-laden) combination

If a Gradle 9 build is ever required urgently, this combination builds, passes unit tests,
and runs correctly on-device — accept the two warnings above:

- Gradle 9.6.1
- AGP 9.2.1
- Kotlin 2.3.21 + `org.jetbrains.kotlin.plugin.compose` 2.3.21
- KSP 2.3.9 (KSP switched to independent versioning at Kotlin 2.3.0)
- `gradle.properties`: `android.builtInKotlin=false` and `android.newDsl=false`
- Replace `android { kotlinOptions { jvmTarget = "21" } }` with a top-level
  `kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }` block.

### When to revisit

Retry the upgrade once **KSP supports AGP 9's built-in Kotlin**. At that point the clean
path is: AGP 9.x + built-in Kotlin, dropping both `builtInKotlin`/`newDsl` flags and the
standalone `kotlin.android` plugin. Watch:

- KSP releases: <https://github.com/google/ksp/releases>
- AGP built-in Kotlin guide: <https://developer.android.com/build/migrate-to-built-in-kotlin>

## Build requirement

Builds must run on **JDK 21**. Gradle 8.14.5 rejects newer default JDKs (e.g. JDK 25):

```bash
export JAVA_HOME=/path/to/jdk-21
./gradlew assembleDebug
```
