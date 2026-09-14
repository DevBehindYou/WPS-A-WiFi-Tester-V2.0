# WPS/A Tester

**WPS/A Tester** is a modern, open-source Android network utility and diagnostic tool designed to evaluate Wi-Fi Protected Setup (WPS) Push Button Configuration (PBC) and legitimate WPS PIN authentication on modern rooted Android devices.

---

## 1. Original Project, Attribution & License Notice

This project is a modified and substantially improved version of an open-source project originally distributed under the **GNU Affero General Public License, version 3 (AGPL-3.0)**.

- **Original Project Name**: WiFi WPS WPA Tester - Open Source
- **Original Author & Copyright Holder**: Alessandro Sangiorgi
- **Original Repository**: [https://github.com/fulvius31/wifi-wps-wpa-tester-opensource](https://github.com/fulvius31/wifi-wps-wpa-tester-opensource)
- **Original Concept**: Incorporates concepts from the well-known *WIFI WPS WPA TESTER* application owned by Alessandro Sangiorgi.
- **License**: GNU Affero General Public License v3.0 (AGPL-3.0) — see the [LICENSE](LICENSE) file for complete terms.

### Summary of Major Modifications & Enhancements
This version (**WPS/A Tester**) introduces extensive architectural, UI/UX, security, and functional improvements:
1. **Source Package & Architecture Modernization**: Migrated the entire internal codebase and test suites to the `com.wpsa.tester` namespace following Clean Architecture and Modern Android Development (MAD) practices.
2. **Material 3 UI/UX Design System**: Complete rebrand featuring a dark-first technical aesthetic, custom typography, adaptive mobile NavigationBar and landscape NavigationRail layouts, and fluid micro-animations.
3. **Direct Root Supplicant Integration**: Interfaced directly with low-level `wpa_supplicant` hardware control sockets via `libsu` with strict metacharacter sanitization and timeout controls.
4. **Multi-Tier Spectrum Wi-Fi Scanner**: Multi-stage scanning pipeline combining Android framework `WifiManager` with fallback root shell scanner commands (`cmd wifi list-scan-results` and `wpa_cli scan_results`) to overcome vendor scan throttling.
5. **Hardware & Environment Diagnostics**: In-depth diagnostic suite evaluating SELinux enforcement mode, root manager implementation (Magisk / KernelSU / APatch), Wi-Fi interface binding (`wlan0`, `wlan1`), driver sockets, and command binary availability (`iw`, `ip`, `wpa_cli`).
6. **In-Memory Bounded Live Logger**: High-performance ring buffer with severity filtering (DEBUG, INFO, WARN, ERROR), real-time search, PIN/credential redaction, and one-tap clipboard export.
7. **Privacy & Offline Security**: Complete removal of external telemetry, tracking SDKs, and ad libraries. Operates 100% offline with zero remote network transmission.

---

## 2. Overview & Authorized-Use Scope

Modern Android releases (Android 9 through Android 16) have removed user-facing WPS connection settings from the OS and deprecated framework-level WPS API calls. **WPS/A Tester** provides network administrators and researchers with the ability to diagnose access points using direct root shell communication with the hardware supplicant.

> [!IMPORTANT]
> **Authorized Use Only**: WPS/A Tester is intended solely for testing, diagnostics, and administrative maintenance of Wi-Fi access points and routers owned by the user or for which explicit written permission has been granted.
> This software does **NOT** implement:
> - Automated PIN brute-forcing or dictionary attacks
> - Pixie Dust exploitation
> - Credential harvesting or WPA key theft
> - Attacks against third-party wireless networks

---

## 3. Key Features

- **Material 3 Interface**: Built natively with Jetpack Compose, supporting System, Dark, and Light themes.
- **Adaptive Layouts**: Seamless responsiveness across phone portrait (NavigationBar) and landscape / tablet viewports (NavigationRail).
- **WPS Push Button (PBC)**: 120-second state-machine progression (`Preparing` → `Waiting for Router` → `Authenticating` → `Associating` → `Obtaining IP` → `Connected`).
- **WPS PIN Authentication**: Local 4-digit and 8-digit checksum validation prior to command execution.
- **Multi-Band Wi-Fi Scanner**: Real-time signal strength (dBm), channel spectrum (2.4 GHz, 5 GHz, 6 GHz), and WPS capability detection.
- **Diagnostic Engine**: Comprehensive inspection of OS, kernel, SELinux, root provider, active interface, and network command tools.
- **Privacy-First Live Logs**: Real-time bounded log stream with automatic redaction of sensitive credentials.
- **Offline Operation**: Zero internet dependencies, trackers, or telemetry.

---

## 4. System Requirements

- **Operating System**: Android 7.0 (API 24) through Android 16 (API 36)
- **Root Provider**: Magisk 25+, KernelSU, or APatch (required for supplicant socket operations)
- **Wi-Fi Hardware**: Wireless chipset and driver supporting managed mode and WPS operations

---

## 5. Architecture

```text
app/src/main/java/com/wpsa/tester/
├── MainActivity.kt
├── WpsApplication.kt
├── core/
│   ├── logging/        # AppLogger, ring buffer, credential redaction
│   └── result/         # ShellResult command output encapsulation
├── data/
│   └── SettingsRepository.kt (SharedPreferences + StateFlow)
├── di/
│   └── AppModule.kt    # Hilt dependency injection bindings
├── diagnostics/
│   └── DeviceDiagnostics.kt (Environment audit and report generation)
├── root/
│   ├── RootCapabilityDetector.kt
│   ├── RootManager.kt  # RootState lifecycle state machine
│   └── SuCommandExecutor.kt (libsu execution with sanitization)
├── ui/
│   ├── components/     # WpsDialog, WpsMethodSheet, WpsPinDialog
│   ├── screens/        # HomeScreen, DiagnosticsScreen, LogsScreen, SettingsScreen
│   ├── theme/          # Color, Dimensions, Shape, Theme, Type
│   └── viewmodels/     # ScannerViewModel
├── wifi/
│   ├── ConnectivityVerifier.kt
│   ├── WifiInterfaceDetector.kt (iw dev / ip link parsing)
│   ├── WifiRecoveryManager.kt
│   └── WifiScanner.kt  # Multi-tier scan engine
└── wps/
    ├── AndroidVendorSupplicantAdapter.kt
    ├── CommandAvailabilityDetector.kt
    ├── StandardSupplicantAdapter.kt
    ├── SupplicantAdapter.kt (Interface)
    ├── WpaSupplicantDetector.kt
    ├── WpsEventParser.kt
    ├── WpsPbcManager.kt
    ├── WpsPinManager.kt
    └── WpsState.kt     # Session state machine
```

---

## 6. Permissions

| Permission | Purpose |
| :--- | :--- |
| `ACCESS_FINE_LOCATION` | Required by Android framework for Wi-Fi SSID and BSSID discovery |
| `ACCESS_COARSE_LOCATION` | Fallback coarse positioning for access point scanning |
| `NEARBY_WIFI_DEVICES` | Required on Android 13+ (API 33+) to scan for nearby wireless routers |
| `ACCESS_SUPERUSER` | Declares root interaction to root management managers (Magisk/KernelSU) |
| `WAKE_LOCK` | Keeps the CPU active during the active 120-second WPS negotiation window |
| `VIBRATE` | Haptic feedback during connection state changes and scan updates |

---

## 7. Building from Source

### Prerequisites
- JDK 17 or JDK 21
- Android SDK Platform 36 (Android 16 / API 36)
- Android SDK Build-Tools 36.0.0
- Gradle Wrapper (included)

### Build Commands
```bash
# Clone the repository
git clone https://github.com/fulvius31/wifi-wps-wpa-tester-opensource.git
cd wifi-wps-wpa-tester-opensource

# Run unit tests (24/24 passing)
./gradlew test --no-daemon

# Build Debug APK
./gradlew assembleOpenDebug --no-daemon

# Build Signed Production Release APK
./gradlew assembleOpenRelease --no-daemon
```

---

## 8. Release Artifacts & Verification

- **Application ID**: `com.wpsa.tester`
- **Output Artifact**: `app/build/outputs/apk/open/release/app-open-release.apk`
- **Distribution Package**: `apk_artifacts/WPS-A-Tester-v1.0.0.apk`
- **Checksums**: Verified in `apk_artifacts/SHA256SUMS.txt`
- **Signature Verification**:
  ```powershell
  apksigner verify --verbose --print-certs apk_artifacts/WPS-A-Tester-v1.0.0.apk
  ```

---

## 9. License & Open-Source Acknowledgments

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)** — see the [LICENSE](LICENSE) file for the full license text.

### Acknowledgments
- **Original Concept & Implementation**: Alessandro Sangiorgi ([WIFI WPS WPA TESTER](https://github.com/fulvius31/wifi-wps-wpa-tester-opensource))
- **[libsu](https://github.com/topjohnwu/libsu)** by John Wu (topjohnwu) — Apache License 2.0 (Root shell interaction)
- **[Android Jetpack & Compose](https://developer.android.com/jetpack)** by Google LLC — Apache License 2.0
- **[Dagger Hilt](https://dagger.dev/hilt/)** by Google LLC — Apache License 2.0
- **[Kotlin & Coroutines](https://github.com/Kotlin/kotlinx.coroutines)** by JetBrains s.r.o. — Apache License 2.0
- **[wpa_supplicant](https://w1.fi/wpa_supplicant/)** by Jouni Malinen — BSD License
