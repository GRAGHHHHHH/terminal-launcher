# TerminalLauncher

A minimal Android launcher. Home screen is empty (just your wallpaper + system UI).
Swipe up anywhere to open the command palette.

## Commands
- `list apps` — lists all installed apps
- `launch [name]` — opens an app (partial names work, e.g. "launch cam")
- `clear` — clears the terminal output
- `help` — shows available commands
- Back button — returns to home

---

## How to install (Android Studio)

1. **Download Android Studio** from https://developer.android.com/studio if you don't have it.

2. **Open the project**: File → Open → select the `TerminalLauncher` folder.

3. **Wait for Gradle sync** to finish (it downloads dependencies automatically).

4. **Enable Developer Options on your Redmi 10A**:
   - Settings → About phone → tap "MIUI version" 7 times
   - Settings → Additional settings → Developer options → Enable USB debugging

5. **Connect your phone** via USB. Accept the "Allow USB debugging?" prompt on the phone.

6. **Run the app**: press the green ▶ Run button in Android Studio. It will install on your phone.

7. **Set as default launcher**:
   - Press the Home button — Android will ask which launcher to use
   - Select "TerminalLauncher" and tap "Always"
   - Or go to Settings → Home screen → Default home screen app

## MIUI note
MIUI sometimes restricts third-party launchers. If the home button doesn't trigger the chooser:
- Settings → Apps → Manage apps → search "TerminalLauncher" → set as default
- Or: Settings → Home screen → scroll down to find default launcher setting

## Switching back
If you want to switch back to MIUI launcher:
- Open TerminalLauncher → type `launch settings` → navigate to Home screen settings
- Or connect USB and run: adb shell cmd package set-home-activity com.miui.home/.launcher.Launcher
