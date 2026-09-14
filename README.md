# WPS/A Tester

**WPS/A Tester** is a modern, open-source Android network utility and diagnostic tool designed to restore and evaluate Wi-Fi Protected Setup (WPS) Push Button Configuration (PBC) and legitimate WPS PIN authentication on modern rooted Android devices.

---

## 1. Overview & Authorized-Use Scope

Modern Android versions (Android 9 through Android 15) removed WPS user-facing options from system settings and deprecated direct framework WPS calls. **WPS/A Tester** bridges this gap by directly interfacing with low-level `wpa_supplicant` control interfaces and hardware network sockets via `libsu`.

> [!IMPORTANT]
> **Authorized Use Only**: WPS/A Tester is intended solely for testing, diagnostics, and administrative maintenance of Wi-Fi access points and routers owned by the user or for which explicit administrative authorization has been granted.
> This application does **NOT** implement:
> - Automated PIN brute-forcing or dictionary attacks
> - Pixie Dust exploitation
> - Credential harvesting or WPA key theft
> - Attacks against third-party wireless networks

---

## 2. Key Features

- **Material 3 UI/UX**: Dark-first technical design system built natively with Jetpack Compose.
- **Adaptive Screen Layouts**: Optimized for mobile phones (NavigationBar) and foldables/tablets (NavigationRail) with zero layout overflow.
- **WPS Push Button (PBC)**: Real-time 120-second state-machine progression (`Preparing` → `Waiting for Router` → `Authenticating` → `Associating` → `Obtaining IP` → `Connected`).
- **WPS PIN Authentication**: Local 4-digit and 8-digit checksum validation before shell execution.
- **Multi-Tier Wi-Fi Scanner**: Combines standard Android `WifiManager` scan results with root shell fallbacks (`cmd wifi list-scan-results` and `wpa_cli scan_results`) to bypass vendor scan throttling.
- **Device Diagnostics**: Detailed inspection of root provider (Magisk/KernelSU/APatch), SELinux enforcement mode, active Wi-Fi driver interface (`wlan0`, `wlan1`), supplicant sockets, and command availability (`iw`, `ip`, `wpa_cli`).
- **Bounded Live Logger**: Ring-buffered in-memory log stream with severity filters (DEBUG, INFO, WARN, ERROR), search filtering, PIN/credential redaction, and one-tap clipboard export.
- **Zero Telemetry & Offline Operation**: 100% local execution with no internet dependencies, no third-party trackers, and no ad libraries.

---

## 3. System Requirements

- **Operating System**: Android 7.0 (API 24) through Android 16 (API 36)
- **Root Provider**: Magisk 25+, KernelSU, or APatch (required for supplicant socket interaction)
- **Wi-Fi Hardware**: Wireless chipset and kernel driver supporting managed mode and WPS operations.

---

## 4. Architecture

WPS/A Tester is engineered according to Clean Architecture and Modern Android Development (MAD) practices:

```text
app
├── core
│   ├── logging (AppLogger - bounded ring buffer, redaction)
│   └── result  (ShellResult)
├── data
│   └── SettingsRepository (SharedPreferences + StateFlow)
├── di
│   └── AppModule (Hilt dependency injection bindings)
├── diagnostics
│   └── DeviceDiagnostics (Environment audit and Markdown reporting)
├── root
│   ├── RootCapabilityDetector
│   ├── RootManager (StateFlow<RootState>)
│   └── SuCommandExecutor (libsu wrapper with metacharacter sanitization)
├── ui
│   ├── components (WpsDialog, WpsMethodSheet, WpsPinDialog)
│   ├── screens    (HomeScreen, DiagnosticsScreen, LogsScreen, SettingsScreen)
│   ├── theme      (Color, Dimensions, Shape, Theme, Type)
│   └── viewmodels (ScannerViewModel)
├── wifi
│   ├── ConnectivityVerifier
│   ├── WifiInterfaceDetector (iw dev / ip link parser with wlan filtering)
│   ├── WifiRecoveryManager
│   └── WifiScanner
└── wps
    ├── AndroidVendorSupplicantAdapter
    ├── CommandAvailabilityDetector
    ├── StandardSupplicantAdapter
    ├── SupplicantAdapter (interface)
    ├── WpaSupplicantDetector
    ├── WpsEventParser
    ├── WpsPbcManager
    ├── WpsPinManager
    └── WpsState (state machine enum)
```

---

## 5. Permissions

| Permission | Purpose |
| :--- | :--- |
| `ACCESS_FINE_LOCATION` | Required by Android framework for Wi-Fi SSID and BSSID discovery. |
| `ACCESS_COARSE_LOCATION` | Fallback coarse positioning for access point scanning. |
| `NEARBY_WIFI_DEVICES` | Required on Android 13+ (API 33+) to scan for nearby wireless routers. |
| `ACCESS_SUPERUSER` | Declares root interaction to root management managers (Magisk/KernelSU). |
| `WAKE_LOCK` | Keeps the CPU active during the active 120-second WPS negotiation window. |

---

## 6. Building from Source

### Prerequisites
- JDK 17 or JDK 21
- Android SDK Platform 36
- Gradle Wrapper (included)

### Build Commands
```bash
# Clone the repository
git clone https://github.com/fulvius31/wifi-wps-wpa-tester-opensource.git
cd wifi-wps-wpa-tester-opensource

# Run unit tests
./gradlew testOpenDebugUnitTest --no-daemon

# Build Debug APK
./gradlew assembleOpenDebug --no-daemon

# Build Release APK
./gradlew assembleOpenRelease --no-daemon
```

---

## 7. APK Artifacts

- **Application ID**: `com.wpsa.tester`
- **Output Directory**: `app/build/outputs/apk/open/debug/` and `apk_artifacts/`
- **Debug Artifact**: `app-open-debug.apk` / `WPS-A-Tester-debug.apk`
