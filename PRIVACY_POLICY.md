# WPS/A Tester — Privacy Policy

**Last Updated:** September 14, 2026  
**Application ID:** `com.wpsa.tester`  
**License:** GNU Affero General Public License v3.0 (AGPL-3.0)

---

## 1. Core Commitment: 100% Offline & Zero Telemetry

**WPS/A Tester** is an open-source, privacy-first Android diagnostic utility. The application is architected from the ground up to operate completely offline without internet connectivity.

- **Zero Remote Telemetry**: We do not collect, transmit, share, or monetize any user data.
- **Zero Third-Party SDKs**: No analytics platforms (Google Analytics, Firebase, Mixpanel), no crash reporting frameworks (Crashlytics, Sentry), and no advertising networks are integrated into the application.
- **Zero Cloud Infrastructure**: There are no remote servers, databases, or API endpoints associated with this application.

---

## 2. Information Handled Locally

All data processed by WPS/A Tester is processed **strictly in volatile system memory** on your local device:

| Data Type | How It Is Handled | Persistence |
| :--- | :--- | :--- |
| **Wi-Fi Access Point Data** (SSID, BSSID, RSSI, Frequency, WPS Attributes) | Scanned in real-time via Android `WifiManager` and hardware supplicant (`wpa_cli` / `cmd wifi`). Used only to populate the in-app spectrum list. | Ephemeral; held in RAM during the active session only. |
| **Diagnostic Logs** | High-performance circular ring buffer (1,000 entries) capturing shell commands, supplicant state transitions, and diagnostic checks. | In-memory only. Erased on app exit unless manually copied to clipboard by the user. |
| **User Preferences** (Theme Mode, Scan Interval, WPS Handshake Timeout, Filter Flags) | Persisted locally on the device using Android's private `SharedPreferences` (`/data/data/com.wpsa.tester/shared_prefs/`). | Kept private to the app sandbox on device. |
| **WPS PIN Inputs** | Validated in memory during PIN authentication tests against the access point. | Never logged in plaintext; automatically redacted if outputted to logcat. |

---

## 3. Permissions & Superuser Access

WPS/A Tester requests only the minimum permissions required by the Android operating system to conduct local wireless diagnostics:

### 3.1 Android Runtime Permissions
- **`ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`**:
  *Mandated by Google Android OS* (Android 8.0 through Android 12) for applications that perform Wi-Fi discovery scans. Without this permission, the OS blanks scan results to protect location privacy. WPS/A Tester does **NOT** access GPS hardware or log physical geographical coordinates.
- **`NEARBY_WIFI_DEVICES`**:
  *Mandated on Android 13+ (API 33+)* to scan for nearby wireless routers without accessing fine physical location.
- **`WAKE_LOCK`**:
  Prevents the system CPU from entering deep sleep during the active 120-second WPS Push Button (PBC) association window.
- **`VIBRATE`**:
  Provides tactile haptic confirmation when a scan finishes or when WPS connection state changes.

### 3.2 Superuser (Root) Access
- **`ACCESS_SUPERUSER`**:
  Declares root interaction to root management managers (Magisk, KernelSU, APatch). Root privileges are executed strictly via `libsu` for:
  - Interacting with `wpa_cli` sockets on modern Android releases where userland WPS APIs have been removed by Google.
  - Querying low-level Linux networking tools (`iw`, `ip`, `getenforce`).
  - Accessing `/data/misc/wifi/` supplicant sockets for legitimate diagnostic purposes.
  - All shell commands undergo strict metacharacter sanitization and timeout enforcement.

---

## 4. Automatic Credential Redaction

WPS/A Tester includes an automated log sanitization engine (`AppLogger`). If diagnostic shell outputs contain sensitive tokens, PINs, or credentials, they are masked in-memory before being rendered in the live log screen:
- 8-digit and 4-digit WPS PIN inputs are displayed as `[REDACTED_PIN]` in diagnostic output.
- Authentication hashes and PSKs are prevented from appearing in application logs.

---

## 5. Security & Open-Source Auditing

Because WPS/A Tester is fully open-source under the **AGPL-3.0** license, all source code is publicly accessible and auditable. You can independently verify that no network sockets connect to remote hosts, no tracking libraries exist, and all Wi-Fi data stays on your local hardware.

---

## 6. Policy Updates & Inquiries

Any future modifications to this policy will be reflected in the repository and the in-app Settings screen. Because this application does not collect user contact information, users are encouraged to review updates directly in the Git repository commit history.
