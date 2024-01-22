# DesktopAccent

Kotlin Multiplatform Library that provides functionality to retrieve the accent color from various desktop environments.

### Features

- `getRGBA(rgba: String): IntArray`: Converts an RGBA color string to an array of integers representing the RGBA channels.
- `getGnomeAccentColor(): String`: Returns a default color string as GNOME (GTK) does not currently have a unified protocol for accent colors.
- `getKdePlasmaAccentColor(): String`: Retrieves the accent color from the KDE Plasma desktop environment's configuration files.
- `getWindowsSystemAccent(): String`: Retrieves the accent color from the Windows Registry or default if null.

### How to use

To import in your project, use jitpack as dependencyResolutionManagement:
```markdown
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
and import in desktop part of your kmp project:
```toml
desktopAccent = { group = "com.github.RecodeLiner", name = "DesktopAccent", version.ref = "desktopAccent" }
```
```kotlin
val desktopMain by getting {
            dependencies {
                implementation(libs.desktopAccent)
            }
        }
```

### Example of usage

It's very simple to use in compose:
```kotlin
val data = getRGBA(getSystemAccent())
App(root, Color(data[0], data[1], data[2], data[3]))
```