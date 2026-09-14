# WPS/A Tester v1.0.0

## Highlights

- Complete WPS/A Tester rebrand and clean architecture migration
- Modern Material 3 interface with dark-first theme and custom typography
- Root-aware Wi-Fi diagnostics with fallback shell scanner
- Nearby Wi-Fi scanning with 2.4 GHz, 5 GHz, and 6 GHz spectrum indicators
- WPS Push Button Configuration (PBC) and authorized PIN workflows
- Real-time WPS state machine with timeout enforcement
- Comprehensive device diagnostics (SELinux, root provider, network interfaces, driver sockets)
- In-memory bounded runtime log viewer with severity filters and credential redaction
- Light / Dark / System themes
- Adaptive mobile and landscape navigation layouts

## Platform & Compatibility

- **minSdk**: 24 (Android 7.0 Nougat)
- **targetSdk**: 36 (Android 16 / API 36)
- **Application ID**: `com.wpsa.tester`
- **Source Package**: `com.wpsa.tester`
- **Native Architectures**: `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`

## Release Verification

- **Signed Production Release**: Signed using RSA 4096-bit release key
- **Signing Scheme**: APK Signature Scheme v2
- **Certificate SHA-256**: `eb628594f042128b1d257aab88a8cd13e227aed49eea72076cddce6cc04b39f6`
- **Code Shrinking & Obfuscation**: R8 enabled
- **Resource Shrinking**: Enabled (unused resources removed)
- **Debuggable**: `false`
- **Physical Device Validation**: Completed on physical Android hardware running Android 14 / API 34
- **Update Compatibility**: Seamless over-the-air update capability verified

## Authorized Use

WPS/A Tester is intended strictly for evaluating, testing, and troubleshooting Wi-Fi networks and routers that you own or are explicitly authorized to assess.
