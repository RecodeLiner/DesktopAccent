import OperatingSystem.Linux
import OperatingSystem.Windows
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.io.path.Path
import kotlin.io.path.exists


const val defaultColor = "0xFF0000FF"


fun getSystemAccent(): String {
    return when (currentOperatingSystem) {
        Windows -> {
            getWindowsSystemAccent()
        }

        Linux -> {

            getLinuxSystemAccent()
        }

        else -> {
            "0xFF0000FF"
        }
    }
}

fun readRegistryValue(regPath: String, regKeyName: String): String? {
    try {
        val process = ProcessBuilder("reg", "query", regPath, "/v", regKeyName)
            .redirectErrorStream(true)
            .start()

        process.inputStream.use {
            val reader = BufferedReader(InputStreamReader(it))
            val output = reader.readText()
            return parseRegistryValue(output, regKeyName)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun parseRegistryValue(output: String, keyName: String): String? {
    val regex = Regex("""\s$keyName\s+REG_\w+\s+(.*)""")
    val matchResult = regex.find(output)
    return matchResult?.groups?.get(1)?.value?.trim()
}


fun readByLines(fileName: String): List<String> = File(fileName).useLines { it.toList() }

/**
 * Converts an RGBA color string to an array of integers representing the RGBA channels.
 *
 * This function expects a hexadecimal RGBA string and converts it into an array of integers
 * with the red, green, blue, and alpha values. It supports two formats:
 * - A 10-character string (expected to start with '0x' followed by eight hex digits).
 * - An 8-character string (consisting of eight hex digits).
 *
 * @param rgba A string representing the RGBA color in hexadecimal format.
 * @return An `IntArray` containing the red, green, blue, and alpha values in that order.
 * @throws IllegalArgumentException If the length of the `rgba` string is not 10 or 8.
 */
fun getRGBA(rgba: String): IntArray {
    when (rgba.length) {
        10 -> {
            val r = rgba.substring(2, 4).toInt(16)
            val g = rgba.substring(4, 6).toInt(16)
            val b = rgba.substring(6, 8).toInt(16)
            val a = rgba.substring(8, 10).toInt(16)
            return intArrayOf(r, g, b, a)
        }

        8 -> {
            val r = rgba.substring(0, 2).toInt(16)
            val g = rgba.substring(2, 4).toInt(16)
            val b = rgba.substring(4, 6).toInt(16)
            val a = rgba.substring(6, 8).toInt(16)
            return intArrayOf(r, g, b, a)
        }

        else -> {
            throw IllegalArgumentException("Length is not 9 or 11")
        }
    }
}

/**
 * Gets the current operating system where the application is running.
 *
 * This property checks the 'os.name' system property and determines the operating system
 * based on the value obtained. It matches the operating system name against known patterns for
 * Windows, Linux (and Unix-like), and macOS. If the operating system does not match any of the
 * known patterns, it defaults to 'Unknown'.
 *
 * @return An enum value of type `OperatingSystem` representing the detected operating system.
 *         Possible return values are 'Windows', 'Linux', 'macOS', or 'Unknown'.
 */
private val currentOperatingSystem: OperatingSystem
    get() {
        val operSys = System.getProperty("os.name").lowercase()
        return if (operSys.contains("win")) {
            Windows
        } else if (operSys.contains("nix") || operSys.contains("nux") ||
            operSys.contains("aix")
        ) {
            Linux
        } else if (operSys.contains("mac")) {
            OperatingSystem.MacOS
        } else {
            OperatingSystem.Unknown
        }
    }

/**
 * Retrieves the system accent color for a Windows environment from the registry.
 *
 * This function reads the 'AccentColor' value from the Windows registry path
 * 'HKCU\Software\Microsoft\Windows\DWM', which is where Windows stores the user's
 * accent color preference.
 *
 * @return A string representing the hex code of the Windows system accent color.
 *         The format and exact value depend on how the registry stores this information.
 */
fun getWindowsSystemAccent(): String {
    return readRegistryValue("HKCU\\Software\\Microsoft\\Windows\\DWM", "AccentColor") ?: defaultColor
}

/**
 * Retrieves the system accent color for a Linux environment based on the detected desktop manager.
 *
 * This function first prints a message indicating that it is checking the desktop manager (DM).
 * It then calls `detectDM()` to detect the current DM and decides which method to call to get the accent color.
 *
 * The accent color retrieval is DM-specific:
 * - For GNOME, it calls `getGnomeAccentColor()`.
 * - For KDE Plasma, it calls `getKdePlasmaAccentColor()`.
 * - For other desktop managers or if none is detected, it returns a default color.
 *
 * @return A string representing the hex code of the accent color for the current Linux desktop environment.
 */
fun getLinuxSystemAccent(): String {
    return when (detectDM()) {
        "GNOME" -> getGnomeAccentColor()
        "KDE" -> {
            getKdePlasmaAccentColor()
        }

        else -> defaultColor
    }
}

/**
 * Detects the current desktop manager (DM) in a Linux environment.
 *
 * This function attempts to retrieve the value of the 'XDG_CURRENT_DESKTOP' environment
 * variable, which is a standard way to identify the desktop environment in Linux.
 *
 * @return The name of the current desktop manager as a string if the environment variable is set,
 *         or `null` if the environment variable is not found.
 */
fun detectDM(): String? {
    return System.getenv("XDG_CURRENT_DESKTOP") ?: null
}

/**
 * Retrieves the accent color for a GNOME desktop environment.
 *
 * Currently, this function returns a default color since GNOME (GTK) does not yet have a
 * unified protocol for accessing the accent color. Once such a protocol is implemented in GTK,
 * this method can be updated to retrieve the actual accent color from the system settings.
 *
 * @return A string representing the hex code of the default accent color.
 */
fun getGnomeAccentColor(): String {
    return defaultColor
}
/**
 * Retrieves the accent color from the KDE Plasma desktop environment configuration.
 *
 * This function looks for the 'kdeglobals' configuration file, which is where KDE Plasma
 * stores its color scheme settings, including the accent color. The location of the
 * configuration file is determined based on the 'XDG_CONFIG_HOME' environment variable or
 * defaults to the '.config' directory in the user's home directory.
 *
 * The function searches for the '[colors:button]' section in the 'kdeglobals' file to find the
 * 'AccentColor' entry. If found, it parses the RGB values and constructs a hex color code
 * string with full opacity.
 *
 * @return A string representing the hex code of the KDE Plasma accent color if found,
 *         or a default color as a fallback.
 */
fun getKdePlasmaAccentColor(): String {
    val targetPath: String
    if (System.getenv("XDG_CONFIG_HOME") == null) {
        val homePath = System.getProperty("user.home")
        targetPath = "${homePath}/.config/kdeglobals"
    } else {
        targetPath = System.getenv("XDG_CONFIG_HOME")
    }
    if (!Path(targetPath).exists()) {
        return defaultColor
    }
    val result = readByLines(targetPath)
    val generalIndex = result.indexOfFirst { it.trim().contains("[colors:button]", ignoreCase = true) }
    if (generalIndex == -1) {
        return defaultColor
    } else {
        for (i in generalIndex + 1 until result.size) {
            if (result[i].contains("AccentColor", ignoreCase = true)) {
                return "0x" + result[i].split("=")[1].split(",").joinToString("") { "%02X".format(it.trim().toInt()) } + "FF"
            }
        }
    }
    return defaultColor
}