package com.example.terminallauncher

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView

class TerminalActivity : Activity() {

    private lateinit var outputText: TextView
    private lateinit var inputField: EditText
    private lateinit var promptLabel: TextView
    private lateinit var submitButton: TextView
    private lateinit var scrollView: ScrollView

    // --- USER DATA STORAGE ---
    // SharedPreferences is Android built-in key-value string storage.
    // Data written here survives the app being closed.
    private lateinit var prefs: SharedPreferences

    private val KEY_ENTRY   = "setting_entry"
    private val KEY_PROMPT  = "setting_prompt"
    private val KEY_COLOR   = "setting_color"

    private val DEFAULT_ENTRY  = "Welcome."
    private val DEFAULT_PROMPT = "$"
    private val DEFAULT_COLOR  = "#FFFFFF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)

        outputText   = findViewById(R.id.outputText)
        inputField   = findViewById(R.id.inputField)
        promptLabel  = findViewById(R.id.promptLabel)
        submitButton = findViewById(R.id.submitButton)
        scrollView   = findViewById(R.id.scrollView)

        prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        applyStoredSettings()

        appendOutput(prefs.getString(KEY_ENTRY, DEFAULT_ENTRY)!!)

        inputField.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT)

        // Submit on button tap — no keyboard dependency
        submitButton.setOnClickListener {
            val input = inputField.text.toString().trim()
            if (input.isNotEmpty()) {
                handleCommand(input)
                inputField.setText("")
            }
        }
    }

    private fun applyStoredSettings() {
        val prompt = prefs.getString(KEY_PROMPT, DEFAULT_PROMPT)!!
        val color  = prefs.getString(KEY_COLOR, DEFAULT_COLOR)!!

        promptLabel.text = "$prompt "

        try {
            val parsed = Color.parseColor(color)
            outputText.setTextColor(parsed)
        } catch (e: IllegalArgumentException) {
            outputText.setTextColor(Color.WHITE)
        }
    }

    private fun handleCommand(input: String) {
        val lower = input.lowercase().trim()

        appendOutput("> $input")

        when {
            lower == "list apps"           -> listApps()
            lower.startsWith("launch ")    -> launchApp(input.substring(7).trim())
            lower == "clear"               -> clearOutput()
            lower == "help"                -> showHelp()
            lower == "settings"            -> appendOutput("Run 'settings help' for available settings.")
            lower == "settings help"       -> showSettingsHelp()
            lower.startsWith("settings -") -> handleSettings(input.trim())
            else -> appendOutput("Unknown command. Type 'help' for available commands.")
        }

        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun handleSettings(input: String) {
        val parts = input.split(" ", limit = 3)

        if (parts.size < 2) {
            appendOutput("Usage: settings -[flag] [value]")
            appendOutput("Run 'settings help' for available flags.")
            return
        }

        val flag  = parts[1].lowercase()
        val value = if (parts.size >= 3) parts[2] else ""

        when (flag) {

            "-entry" -> {
                if (value.isEmpty()) {
                    appendOutput("Usage: settings -entry [text]")
                    return
                }
                prefs.edit().putString(KEY_ENTRY, value).apply()
                appendOutput("Welcome message set to: $value")
            }

            "-prompt" -> {
                if (value.isEmpty()) {
                    appendOutput("Usage: settings -prompt [symbol]")
                    return
                }
                prefs.edit().putString(KEY_PROMPT, value).apply()
                promptLabel.text = "$value "
                appendOutput("Prompt set to: $value")
            }

            "-color" -> {
                if (value.isEmpty()) {
                    appendOutput("Usage: settings -color [color]")
                    appendOutput("Examples: white, green, red, cyan, #FF8800")
                    return
                }
                val hex = namedColorToHex(value.lowercase()) ?: value
                try {
                    val parsed = Color.parseColor(hex)
                    prefs.edit().putString(KEY_COLOR, hex).apply()
                    outputText.setTextColor(parsed)
                    appendOutput("Output color set to: $value")
                } catch (e: IllegalArgumentException) {
                    appendOutput("Unknown color: '$value'")
                    appendOutput("Try: white, green, red, cyan, yellow, or a hex code like #FF8800")
                }
            }

            "-suffering" -> {
                // The rant. Do not touch.
                appendOutput("if youre reading this, sincerely, i hate you. you are the very thing that i despise. i built this abode so that i could live in peace and you must waltz through the wjndow? why do you use this? Why must you read thia? If there is a day thatfor some magical purpose obviously not meant to blanket the greed for income of currency, big corp decides to turn this into a \"decent\" app of their own, i have one thing to say: screw you, infertile almagamation of inbreeding. Etch my words and take them to the grave of your subconscioua. This thing has no intent of adaptation or to work in a device of youra which i never knew. Get some help. Use niagara launcher or something. If youre going to use this and you have the heart to, tell me your issues through the github thing or whatever so i can laugh at you properly. Love yourself rn.")
            }

            else -> {
                appendOutput("Unknown flag: $flag")
                appendOutput("Run 'settings help' for available flags.")
            }
        }
    }

    private fun namedColorToHex(name: String): String? = when (name) {
        "white"   -> "#FFFFFF"
        "green"   -> "#00FF41"
        "red"     -> "#FF3333"
        "cyan"    -> "#00FFFF"
        "yellow"  -> "#FFFF00"
        "orange"  -> "#FF8800"
        "magenta" -> "#FF00FF"
        else      -> null
    }

    private fun showHelp() {
        appendOutput("help")
        appendOutput("  list apps       — show all installed apps")
        appendOutput("  launch [name]   — open an app by name")
        appendOutput("  settings        — configure the terminal")
        appendOutput("  clear           — clear this screen")
        appendOutput("  help            — show this message")
        appendOutput("Press Back to return to home.")
    }

    private fun showSettingsHelp() {
        appendOutput("settings")
        appendOutput("  -entry [text]     — set the welcome message")
        appendOutput("  -prompt [symbol]  — change the prompt symbol (default: $)")
        appendOutput("  -color [color]    — change output text color")
        appendOutput("                      options: white, green, red, cyan, yellow,")
        appendOutput("                               orange, magenta, or a hex code")
        appendOutput("  -suffering        — you probably shouldn't")
    }

    private fun listApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, 0)
            .map { it.loadLabel(pm).toString() }
            .sorted()

        if (apps.isEmpty()) {
            appendOutput("No apps found.")
        } else {
            appendOutput("Installed apps:")
            apps.forEach { appendOutput("  $it") }
        }
    }

    private fun launchApp(appName: String) {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, 0)

        val match = apps.firstOrNull {
            it.loadLabel(pm).toString().equals(appName, ignoreCase = true)
        } ?: apps.firstOrNull {
            it.loadLabel(pm).toString().contains(appName, ignoreCase = true)
        }

        if (match == null) {
            appendOutput("App not found: '$appName'")
            appendOutput("Try 'list apps' to see exact names.")
            return
        }

        val launchIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(match.activityInfo.packageName, match.activityInfo.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        appendOutput("Launching ${match.loadLabel(pm)}...")
        startActivity(launchIntent)
    }

    private fun clearOutput() {
        outputText.text = ""
    }

    private fun appendOutput(line: String) {
        outputText.append(line + "\n")
    }
}
