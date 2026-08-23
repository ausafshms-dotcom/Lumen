# Lumen — IR Light Controller

A Kotlin/Compose Android app that drives your phone's built-in IR blaster
using the NEC protocol. Built to grow beyond lights: the color ring, the
brightness bar, and the mode buttons are all just thin UI over a generic
`IrTransmitter.send(address, command)` call, and there's a "Devices" tab and
a raw-code sender in Settings ready for whatever you add next.

## Build an APK with no PC (GitHub Actions)

1. **Get the code onto GitHub**
   - Create a free account at github.com (mobile browser is fine).
   - Create a new repository (e.g. `lumen`).
   - Tap **Add file → Upload files**, then upload every file/folder from
     this project, keeping the folder structure intact (including the
     hidden `.github/workflows/build.yml` file — GitHub's uploader will
     preserve folder paths if you drag a whole extracted folder in).
   - If your browser won't let you upload a `.github` folder, install a
     simple Git client app (e.g. **Termux + git**, or **Working Copy** on
     iOS/Android alternatives) and push the folder instead — either works.

2. **Let it build**
   - Once pushed, open the repo's **Actions** tab. The `Build APK` workflow
     starts automatically.
   - Wait ~3–5 minutes for it to finish (green checkmark).

3. **Download the APK**
   - Open the finished workflow run → scroll to **Artifacts** →
     download `lumen-debug-apk` (this works right from your phone browser).
   - Unzip it to get `app-debug.apk`.

4. **Install**
   - Tap the APK in your file manager. Allow "install unknown apps" for
     your browser/file manager if prompted. Install.
   - On first launch, grant any permissions Android asks for. The IR
     permission itself (`TRANSMIT_IR`) doesn't need a runtime prompt — if
     your phone has a real IR blaster, it'll just work.

## Extending it later

- **New device**: add commands to an enum like `LightCommand`, give it its
  own NEC address constant, and call `irTransmitter.send(address, command)`.
- **New protocol**: `NecCodes.pattern()` is isolated in its own file — add
  a sibling encoder if a future device doesn't speak NEC.
- **New screen**: add a route in `MainActivity.kt`'s `tabs` list and a
  composable in `ui/screens/`.
