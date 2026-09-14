# WPS/A Tester — Release Signing & Verification Guide

This guide details how production release signing is securely configured, built, and verified for **WPS/A Tester**.

---

## 1. Security Architecture

- **Keystore Isolation**: The production keystore is stored outside the Git repository at:
  ```text
  C:\AndroidKeys\WPS-A-Tester\wps-a-tester-release.jks
  ```
- **Key Specifications**:
  - Key Algorithm: RSA 4096-bit
  - Key Alias: `wps_a_tester`
  - Valid: Through 2054
- **Secret Protection**:
  - `.gitignore` strictly ignores `keystore.properties`, `*.jks`, and `*.keystore`.
  - Passwords are never hardcoded in Gradle or committed to source control.
  - Same-key signature preserves seamless over-the-air and direct APK updates.

---

## 2. Local Configuration (`keystore.properties`)

A template file is provided at `keystore.properties.example`:

```properties
storeFile=C:/AndroidKeys/WPS-A-Tester/wps-a-tester-release.jks
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=wps_a_tester
keyPassword=YOUR_KEY_PASSWORD
```

For local release builds:
1. Copy `keystore.properties.example` to `keystore.properties`.
2. Fill in `storePassword` and `keyPassword`.
3. Do not commit `keystore.properties`.

---

## 3. CI/CD Environment Variables

In automated environments (such as GitHub Actions), signing credentials can be supplied as encrypted secrets:

- `WPS_KEYSTORE_FILE` or `KEYSTORE_FILE`: Absolute path to keystore
- `WPS_KEYSTORE_PASSWORD` or `KEYSTORE_PASSWORD`: Keystore password
- `WPS_KEY_ALIAS` or `KEY_ALIAS`: Key alias (`wps_a_tester`)
- `WPS_KEY_PASSWORD` or `KEY_PASSWORD`: Key password

---

## 4. Building the Signed Release APK

Execute:

```powershell
.\gradlew assembleOpenRelease --no-daemon
```

- If signing credentials are missing, Gradle halts with a clean error message and will not generate an insecure build.
- Debug builds (`assembleOpenDebug`, `testOpenDebugUnitTest`) remain unblocked and never require release credentials.

Output artifact:
```text
app/build/outputs/apk/open/release/app-open-release.apk
```

---

## 5. Signature & Cryptographic Verification

Locate `apksigner.bat` from Android SDK Build Tools (API 35+):

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --verbose --print-certs "app/build/outputs/apk/open/release/app-open-release.apk"
```

Expected output:
- `apksigner` must exit successfully (exit code 0) and verbose output must confirm `Verifies`.
- `Verified using v2 scheme (APK Signature Scheme v2): true`
- `Signer #1 certificate DN: CN=DevBehindYou, OU=DevBehindYou, O=DevBehindYou, L=_, ST=_, C=_`
- `Signer #1 certificate SHA-256 digest: eb628594f042128b1d257aab88a8cd13e227aed49eea72076cddce6cc04b39f6`

---

## 6. Computing APK Checksum

```powershell
Get-FileHash "app/build/outputs/apk/open/release/app-open-release.apk" -Algorithm SHA256
```

---

## 7. Installation & Update Verification

### Fresh Installation
When replacing a previous debug build:
```powershell
adb uninstall com.wpsa.tester
adb install -r "app/build/outputs/apk/open/release/app-open-release.apk"
```

### Direct Update (Same-Key)
Subsequent releases signed with the same key update seamlessly without uninstalling or losing data:
```powershell
adb install -r "app/build/outputs/apk/open/release/app-open-release.apk"
```
