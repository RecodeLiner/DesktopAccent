# DesktopAccent

Kotlin Multiplatform Library that provides functionality to retrieve the accent color from various desktop environments.

### Features

- `getRGBA(rgba: String): IntArray`: Converts an RGBA color string to an array of integers representing the RGBA channels.
- `getGnomeAccentColor(): String`: Returns a default color string as GNOME (GTK) does not currently have a unified protocol for accent colors.
- `getKdePlasmaAccentColor(): String`: Retrieves the accent color from the KDE Plasma desktop environment's configuration files.
- `getWindowsSystemAccent(): String`: Retrieves the accent color from the Windows Registry or default if null.
