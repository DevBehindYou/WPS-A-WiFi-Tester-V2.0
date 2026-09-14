# WPS Android App — Complete UI/UX Design Specification

**Document Type:** Implementation-Grade UI/UX Design Specification  
**Platform:** Android  
**UI Framework:** Jetpack Compose  
**Design System:** Material 3  
**Primary Input:** Touch  
**Primary Target:** Mobile phones  
**Secondary Target:** Foldables and tablets  
**Theme:** Light + Dark  
**Architecture Alignment:** MVVM + StateFlow + Hilt  
**Priority:** Reliability → Performance → Usability → Visual Polish → Animation

---

# 1. Product Vision

The WPS application should feel like a professional Android network diagnostics utility rather than a legacy root tool.

The interface should make technically complex operations understandable without hiding important diagnostic information.

The user should immediately understand:

- whether root access is available
- whether Wi-Fi is enabled
- whether required permissions are granted
- whether the Wi-Fi interface was detected
- whether the supplicant environment is accessible
- what nearby networks are visible
- whether an authorized WPS operation can be started
- what the application is currently doing
- why an operation failed
- how to diagnose the device when something is unsupported

The application must never sacrifice stability, responsiveness, or clarity for decorative effects.

---

# 2. Product Principles

## 2.1 Reliability First

The UI must accurately represent backend state.

Never show a successful visual state before the backend confirms success.

Bad:

```text
User presses Connect
→ UI instantly shows Connected
→ backend operation still running
```

Correct:

```text
User presses Connect
→ Preparing
→ Waiting
→ Authenticating
→ Associating
→ Obtaining IP
→ Connected
```

---

## 2.2 No Fake Progress

Progress indicators should represent real application state.

If exact progress cannot be measured, use an indeterminate indicator.

Never display artificial:

```text
25%
50%
75%
100%
```

for operations without measurable progress.

---

## 2.3 Technical but Understandable

Use human-readable labels first.

Example:

```text
Wi-Fi interface

wlan0
```

rather than showing only:

```text
INTERFACE=wlan0
```

Raw technical information can remain available in expandable details.

---

# 3. Application Information Architecture

Primary application areas:

```text
App
│
├── Home / Scanner
│   ├── Environment status
│   ├── Wi-Fi scanner
│   ├── Network details
│   └── Authorized WPS session
│
├── Diagnostics
│   ├── Device
│   ├── Android
│   ├── Root
│   ├── Wi-Fi
│   ├── Interface
│   ├── Supplicant
│   ├── Permissions
│   └── System
│
├── Logs
│   ├── Live log
│   ├── Filters
│   └── Export / Copy
│
└── Settings
    ├── General
    ├── Scanner
    ├── Session
    ├── Logging
    ├── Appearance
    ├── Diagnostics
    └── About
```

---

# 4. Main Navigation

Use a Material 3 `NavigationBar` on phones.

Primary destinations:

```text
Home
Diagnostics
Logs
Settings
```

Recommended icons:

```text
Home          → Wifi / Radar
Diagnostics   → Troubleshoot
Logs          → Article / Terminal
Settings      → Settings
```

Navigation should remain visible on primary screens.

Do not display bottom navigation inside:

- modal screens
- WPS session dialogs
- permission explanations
- full-screen error states
- onboarding/setup flow

---

# 5. Adaptive Navigation

## Compact Width

Use:

```text
NavigationBar
```

Typical phones.

---

## Medium Width

Use either:

```text
NavigationRail
```

or bottom navigation depending on available vertical space.

---

## Expanded Width

Use:

```text
NavigationRail
+
content pane
```

or a two-pane layout.

Example:

```text
┌─────────────┬─────────────────────────────────┐
│ Navigation  │                                 │
│ Rail        │ Main Content                    │
│             │                                 │
│ Home        │                                 │
│ Diagnose    │                                 │
│ Logs        │                                 │
│ Settings    │                                 │
└─────────────┴─────────────────────────────────┘
```

---

# 6. Core Screen List

Required screens and surfaces:

1. Splash / startup state
2. First-launch introduction
3. Permission setup
4. Root setup/status
5. Home / Scanner
6. Network details
7. WPS action selector
8. WPS session
9. Connection success
10. Connection failure
11. Diagnostics
12. Diagnostic detail
13. Logs
14. Log detail
15. Settings
16. About
17. Unsupported environment
18. Permission denied state
19. Wi-Fi disabled state
20. Root unavailable state

---

# 7. Startup Flow

Recommended application startup:

```text
Launch
↓
Initialize app
↓
Check minimum environment
↓
Load preferences
↓
Check root asynchronously
↓
Check permissions
↓
Check Wi-Fi state
↓
Navigate Home
```

Do not block the splash screen waiting for slow root operations.

---

# 8. Splash Screen

Use Android SplashScreen API.

Layout:

```text
Centered application icon
Application name
```

No loading spinner unless initialization genuinely requires visible waiting.

Transition should be short and subtle.

---

# 9. First Launch Screen

Purpose:

Explain what the application does before requesting sensitive permissions.

Structure:

```text
[App icon]

WPS Network Utility

Scan compatible Wi-Fi networks, inspect your
device environment and perform authorized WPS
operations on networks you own or have permission
to test.

[ Continue ]

Authorized networks only
```

Optional secondary action:

```text
Learn more
```

---

# 10. First Launch Requirements

Explain individually:

### Wi-Fi access

Needed for nearby network discovery.

### Nearby devices

Needed by newer Android versions for Wi-Fi scanning.

### Location

May be required by Android/device implementation for scan results.

### Root

Required for advanced supplicant and network operations.

Do not request every permission immediately without context.

---

# 11. Permission Flow

Use a dedicated permission explanation screen.

Example:

```text
Wi-Fi access

This permission allows the app to discover nearby
Wi-Fi networks.

The app does not upload your Wi-Fi scan results.

[ Allow Wi-Fi Access ]

Not now
```

After explanation, trigger native Android permission dialog.

---

# 12. Root Setup Screen

Layout:

```text
Root Access

Advanced WPS functionality requires root access
because Android does not expose all required Wi-Fi
operations to normal applications.

Status

● Checking...

or

✓ Root granted

or

! Root denied

or

× Root unavailable

[ Check Again ]

[ Open Diagnostics ]
```

Root should never be automatically requested repeatedly.

---

# 13. Home Screen

The Home screen is the primary operating surface.

Recommended layout:

```text
Top App Bar

Environment Status Card

Scanner Header

Network List

Floating / Primary Scan Action
```

---

# 14. Home Top App Bar

Content:

```text
WPS Utility                     [more]
```

Optional actions:

```text
Refresh
Help
```

Avoid overloading the top bar.

---

# 15. Environment Status Card

This card gives the user an immediate device readiness overview.

Example:

```text
Device Status

✓ Root              Granted
✓ Wi-Fi             Enabled
✓ Interface         wlan0
! Supplicant        Limited

3 of 4 systems ready

[ View diagnostics ]
```

---

# 16. Status Card States

### Ready

```text
All systems ready
```

### Limited

```text
Some advanced functions may be unavailable
```

### Blocked

```text
Action required before scanning
```

Use icons + text, not color alone.

---

# 17. Status Indicators

Use:

```text
Success
Warning
Error
Checking
Unavailable
```

Suggested semantic behavior:

```text
Success     check_circle
Warning     warning
Error       error
Checking    progress indicator
Unavailable block
```

---

# 18. Scanner Header

Layout:

```text
Nearby Networks                     [Refresh]
Last scanned 12 sec ago
```

When scanning:

```text
Scanning nearby networks...
```

Use a small linear progress indicator.

---

# 19. Scan Button

Primary action:

```text
Scan Networks
```

States:

```text
Idle
Pressed
Scanning
Disabled
Permission Required
Wi-Fi Disabled
```

During scanning:

```text
Scanning...
```

and disable repeated requests.

---

# 20. Network List

Use:

```kotlin
LazyColumn
```

Each network should use a Material 3 card/list item.

---

# 21. Network Card

Recommended hierarchy:

```text
┌──────────────────────────────────┐
│ Home Wi-Fi                  ▂▄▆█ │
│ WPA2 • WPS available             │
│                                  │
│ BSSID                            │
│ A4:22:XX:XX:XX:XX               │
│                                  │
│ -48 dBm                [Details] │
└──────────────────────────────────┘
```

Do not display sensitive credentials.

---

# 22. Network Card Content

Possible fields:

- SSID
- BSSID
- signal strength
- security type
- channel
- frequency
- WPS capability
- saved status
- connected status

Primary display should stay concise.

Technical details belong on the Network Details screen.

---

# 23. Hidden Network

Display:

```text
Hidden network
```

instead of blank SSID.

---

# 24. Signal Visualization

Recommended categories:

```text
Excellent
Good
Fair
Weak
Very weak
```

Example thresholds may be handled by backend logic.

Display icon + optionally dBm.

Do not rely only on bars.

---

# 25. Connected Network

Card:

```text
My Wi-Fi
Connected
```

Use a Material 3 status chip.

Example:

```text
[ Connected ]
```

---

# 26. WPS Status Chip

Possible labels:

```text
WPS
No WPS
Unknown
```

Avoid implying compatibility if not confidently detected.

---

# 27. Empty Scan State

```text
No networks found

No Wi-Fi networks were returned by the system.

Try moving closer to the router or refreshing the scan.

[ Scan Again ]
```

---

# 28. Wi-Fi Disabled State

Replace network list with:

```text
Wi-Fi is turned off

Enable Wi-Fi before scanning nearby networks.

[ Enable Wi-Fi ]
```

If Android prevents direct enablement, open the appropriate system panel.

---

# 29. Permission Missing State

```text
Wi-Fi permission required

Android requires permission before nearby Wi-Fi
networks can be discovered.

[ Grant Permission ]

[ Why is this needed? ]
```

---

# 30. Network Details Screen

Open when tapping a network.

Layout:

```text
Top App Bar
← Network Details

SSID
Home Wi-Fi

Status / signal

Connection information

Security information

WPS information

Technical details

Primary action
```

---

# 31. Network Details Information

Display:

```text
SSID
BSSID
Signal
Channel
Frequency
Security
WPS support
Current connection
Interface
```

Optional advanced section:

```text
Raw capabilities
```

collapsed by default.

---

# 32. Primary Network Action

If WPS is available:

```text
Start WPS
```

If WPS cannot be confirmed:

```text
WPS unavailable
```

with explanation.

---

# 33. WPS Action Selector

Use Material 3 bottom sheet.

Example:

```text
Connect using WPS

Choose a method supported by your authorized router.

Push Button
Press the WPS button on your router.

PIN
Enter the WPS PIN shown on or provided by your router.

Cancel
```

Do not provide brute-force, generated, or guessed PIN workflows.

---

# 34. WPS Push Button Flow

Bottom sheet:

```text
Push Button WPS

1. Press the WPS button on your router.
2. Return here.
3. Start the connection.

The router usually keeps WPS active for a short time.

[ Start Connection ]

Cancel
```

---

# 35. PIN Entry Screen

Use:

```text
WPS PIN

Enter the WPS PIN provided by your router.

[ _ _ _ _ _ _ _ _ ]

[ Connect ]

Cancel
```

Numeric keyboard.

Validation occurs before command execution.

---

# 36. PIN Field

Behavior:

- numeric only
- max permitted length
- no arbitrary shell characters
- trim whitespace
- inline error
- keyboard Done action

Error examples:

```text
Enter a valid WPS PIN.
```

or:

```text
The PIN checksum is invalid.
```

---

# 37. WPS Session Surface

Preferred component:

```text
ModalBottomSheet
```

or full-screen screen on very small devices.

Do not use a tiny dialog for long diagnostic/session information.

---

# 38. WPS Session States

Required state machine:

```text
Idle
Preparing
Waiting
Authenticating
Associating
Obtaining IP
Connected
Failed
Cancelled
Timed Out
Unsupported
```

---

# 39. WPS Session UI

Example:

```text
Connecting to

Home Wi-Fi

          ◉
      Authenticating

Communicating with the router...

Preparing      ✓
Waiting        ✓
Authenticating ●
Associating    ○
Obtaining IP   ○

[ Cancel ]
```

---

# 40. Session Timeline

Use a vertical status timeline.

Completed:

```text
✓
```

Current:

```text
animated indicator
```

Pending:

```text
○
```

Failure:

```text
!
```

---

# 41. Session Animation

Current step can use a subtle pulse.

Avoid:

- large rotating effects
- particle systems
- continuous glow
- full-screen animated gradients

Animation should make state easier to understand.

---

# 42. Connected State

```text
Connected

Connection to Home Wi-Fi was established.

Network
Home Wi-Fi

IP address
192.168.x.x

[ Done ]

[ View diagnostics ]
```

Only display after backend confirms connection.

---

# 43. Failure State

```text
Connection failed

The router did not complete the WPS session.

Reason
Authentication timed out.

[ Try Again ]

[ View Details ]

Cancel
```

---

# 44. Failure Details

Expandable technical details:

```text
Technical details

State:
Authenticating

Command:
WPS operation

Exit code:
1

System response:
<safe diagnostic text>
```

Never expose sensitive credentials.

---

# 45. Timeout State

```text
Session timed out

The router did not respond before the WPS session
expired.

Try activating WPS on the router again.

[ Retry ]
```

---

# 46. Unsupported State

```text
WPS unavailable

The current device environment does not expose
the required supplicant functionality.

[ Open Diagnostics ]
```

---

# 47. Diagnostics Screen

The diagnostics area must be one of the strongest parts of the application.

Top:

```text
Diagnostics

Device environment and capability checks

[ Refresh ]     [ Copy All ]
```

Sections:

```text
Device
Android
Root
Wi-Fi
Interface
Supplicant
Permissions
SELinux
Application
```

---

# 48. Diagnostic Summary

At top:

```text
Environment Status

7 checks passed
2 warnings
1 unavailable
```

Optional circular indicator is acceptable but not required.

---

# 49. Diagnostic Card

Example:

```text
Root

✓ Access granted

Provider
Magisk

Shell
Available

[ View details ]
```

---

# 50. Device Diagnostics

Fields:

```text
Manufacturer
Model
Device
Android version
API level
Architecture
```

Avoid unique IDs such as IMEI or serial.

---

# 51. Root Diagnostics

Display:

```text
Root status
Root shell
Root provider
Command execution
Last check
```

---

# 52. Wi-Fi Diagnostics

Display:

```text
Wi-Fi enabled
Current connection
Scanner available
Scan permission
Location services
Nearby devices permission
```

---

# 53. Interface Diagnostics

Display:

```text
Detected interface
Interface state
MAC availability if permitted
Source used for detection
```

Example:

```text
Interface
wlan0

Detected via
iw dev
```

---

# 54. Supplicant Diagnostics

Fields:

```text
wpa_cli available
binary path
socket path
socket accessible
selected adapter
last response
```

---

# 55. SELinux Diagnostics

Example:

```text
SELinux

Enforcing

Some vendor Wi-Fi sockets may be inaccessible
depending on the device policy.
```

Never encourage disabling SELinux globally.

---

# 56. Permissions Diagnostics

Display each permission:

```text
Nearby Wi-Fi Devices      Granted
Fine Location             Granted
Notifications             Not required
```

Use contextual action:

```text
Grant
Open Settings
```

only where appropriate.

---

# 57. Diagnostic Detail Screen

For complex checks:

```text
Supplicant

Status
Limited

Detected path
/data/vendor/...

Accessible
No

Last error
Permission denied

Possible reason
Vendor SELinux policy

[ Copy Details ]
```

---

# 58. Logs Screen

Header:

```text
Logs                         [Clear]
```

Secondary controls:

```text
[All] [Info] [Warning] [Error]

Component ▼
```

---

# 59. Log Entry

Example:

```text
16:42:31   INFO

WifiScanner

Scan completed with 12 networks.
```

Collapsed by default.

Tap to expand.

---

# 60. Expanded Log Entry

```text
16:42:31.148
INFO

Component
WifiScanner

Message
Scan completed with 12 networks.

Thread
DefaultDispatcher-worker-2

[ Copy ]
```

Only include useful metadata.

---

# 61. Log Severity

Levels:

```text
DEBUG
INFO
WARNING
ERROR
```

Production builds may hide verbose debug logs by default.

---

# 62. Log Viewer Performance

Use:

```kotlin
LazyColumn
```

Maintain bounded entries.

Example:

```text
maximum 500–2000 UI entries
```

depending on implementation.

Do not allow indefinite memory growth.

---

# 63. Log Search

Optional but recommended:

```text
Search logs...
```

Filtering should happen efficiently.

Debounce if required.

---

# 64. Settings Screen

Sections:

```text
General
Scanner
WPS Session
Logging
Appearance
Diagnostics
Advanced
About
```

---

# 65. General Settings

Potential options:

```text
Keep screen awake during active session
Automatically refresh status
Confirm before cancelling connection
```

Defaults should favor stability.

---

# 66. Scanner Settings

Options:

```text
Auto scan on Home opening
Scan result sorting
Show BSSID
Show signal in dBm
Show channel
```

Avoid excessive automatic scanning.

---

# 67. Sorting Options

```text
Signal strength
SSID
Security
WPS availability
```

Default:

```text
Signal strength
```

---

# 68. Session Settings

Options:

```text
WPS timeout
Confirm before cancellation
Show advanced progress details
```

Use sensible limits.

---

# 69. Logging Settings

Options:

```text
Log level
Auto-scroll logs
Include diagnostic commands
Clear logs on restart
```

Sensitive information must remain sanitized regardless of setting.

---

# 70. Appearance Settings

Options:

```text
Theme
System default
Light
Dark

Animations
Standard
Reduced
```

Optional:

```text
Dynamic color
```

if compatible with branding/design goals.

---

# 71. Diagnostics Settings

Options:

```text
Detailed device diagnostics
Show raw command output
Show developer information
```

Advanced raw output should be clearly separated from normal UX.

---

# 72. Advanced Settings

Only include settings that are genuinely needed.

Example:

```text
Refresh environment detection
Reset cached interface detection
Reset app preferences
```

Do not expose unsafe root commands.

---

# 73. About Screen

Content:

```text
Application icon

WPS Utility
Version x.x.x

A network diagnostics utility for authorized
Wi-Fi environments.

Application ID
Version
Build type

Open-source licenses

Privacy information
```

---

# 74. Material 3 Design System

Use:

```kotlin
MaterialTheme
```

Centralize all colors, typography, and shapes.

---

# 75. Color Roles

Use Material semantic roles instead of random hardcoded colors.

Required:

```text
primary
onPrimary
primaryContainer
onPrimaryContainer

secondary
onSecondary

surface
surfaceVariant
surfaceContainer

background

error
onError

outline
outlineVariant
```

---

# 76. Status Colors

Use semantic state colors carefully.

Examples:

```text
Success → green family
Warning → amber family
Error → Material error
Information → primary
```

However every state must also have:

- icon
- label
- description

Color cannot be the only signal.

---

# 77. Dark Theme

Dark theme should use Material dark surfaces.

Avoid pure black everywhere.

Recommended hierarchy:

```text
background
surface
surfaceContainerLow
surfaceContainer
surfaceContainerHigh
```

---

# 78. Typography

Suggested Material typography:

```text
displaySmall
headlineMedium
headlineSmall
titleLarge
titleMedium
titleSmall
bodyLarge
bodyMedium
bodySmall
labelLarge
labelMedium
```

---

# 79. Typography Usage

Example:

```text
Screen title        headlineSmall
Section title       titleMedium
Card heading        titleSmall / titleMedium
Primary text        bodyLarge
Secondary text      bodyMedium
Metadata            bodySmall
Button              labelLarge
Chip                labelMedium
```

---

# 80. Spacing System

Use consistent spacing tokens.

Recommended:

```text
4dp
8dp
12dp
16dp
20dp
24dp
32dp
40dp
48dp
```

Default page horizontal padding:

```text
16dp
```

Larger displays:

```text
24dp
```

---

# 81. Card Design

Prefer Material 3:

```text
FilledCard
ElevatedCard
OutlinedCard
```

Use cards for meaningful grouping.

Do not put every single row inside its own card.

---

# 82. Shape System

Suggested:

```text
Small components      8–12dp
Cards                 16dp
Large sheets          24–28dp top corners
Chips                 pill
Buttons               Material 3 defaults
```

---

# 83. Buttons

Primary:

```text
Button
```

Secondary:

```text
OutlinedButton
```

Low priority:

```text
TextButton
```

Icon-only:

```text
IconButton
```

---

# 84. Primary Actions

Only one dominant primary action per context.

Examples:

Home:

```text
Scan Networks
```

Network:

```text
Start WPS
```

PIN:

```text
Connect
```

Failure:

```text
Retry
```

---

# 85. Chips

Useful for:

```text
Connected
WPS
WPA2
Root
Warning
```

Use Material `AssistChip`, `FilterChip`, or `SuggestionChip` according to semantics.

---

# 86. Snackbar Usage

Use Snackbars for transient information:

```text
Scan refreshed
Logs copied
Settings saved
Diagnostics copied
```

Do not use snackbars for critical errors requiring action.

---

# 87. Dialog Usage

Use dialogs only when the user must make a decision.

Examples:

```text
Cancel WPS session?
Clear all logs?
Reset settings?
```

Avoid using dialogs as general information containers.

---

# 88. Bottom Sheet Usage

Use bottom sheets for:

- WPS method selection
- network quick actions
- filters
- additional information

---

# 89. Loading Indicators

Use:

```text
CircularProgressIndicator
LinearProgressIndicator
```

Avoid custom loaders unless there is a compelling reason.

---

# 90. Skeleton Loading

For scan results, optional lightweight placeholders may be used.

Do not show skeletons for very short operations.

---

# 91. Animation System

Animations should be purposeful.

Suggested durations:

```text
Micro interaction
100–180ms

Normal transition
200–300ms

Large content transition
300–400ms
```

Avoid excessive animation above ~500ms for routine actions.

---

# 92. Navigation Animation

Use subtle:

```text
fade
slide
shared axis style
```

No elaborate 3D transitions.

---

# 93. Card Animation

Network list items can use a small:

```text
fade + vertical translation
```

on first appearance.

Do not replay animations on every minor recomposition.

---

# 94. Status Animation

When status changes:

```text
Checking → Ready
```

animate icon and supporting text with `AnimatedContent`.

---

# 95. Scanner Animation

During scanning:

- subtle radar/pulse indicator is acceptable
- animation must stop when scanning stops
- do not run it permanently

---

# 96. WPS Progress Animation

Current timeline step:

```text
subtle pulsing dot
```

Completed states:

```text
check transition
```

Success:

```text
small scale/fade confirmation
```

No confetti.

---

# 97. Reduced Motion

If reduced animation is enabled:

- remove decorative transitions
- keep functional state changes
- use quick fades where needed

---

# 98. Responsive Layout Requirements

The application must support:

```text
320 × 568
360 × 640
360 × 720
360 × 800
375 × 812
393 × 873
412 × 915
432 × 960
480 × 960
```

and larger devices.

---

# 99. Absolutely Prohibited Layout Problems

There must be:

```text
NO horizontal overflow
NO clipped action buttons
NO overlapping text
NO text outside cards
NO clipped dialogs
NO hidden controls
NO unusable landscape view
NO content under navigation bar
NO content under status bar
NO keyboard-covered confirmation button
```

---

# 100. Responsive Width Rules

Prefer:

```kotlin
fillMaxWidth()
weight()
widthIn()
heightIn()
```

Avoid unnecessary hardcoded widths.

Bad:

```kotlin
Modifier.width(380.dp)
```

Preferred:

```kotlin
Modifier
    .fillMaxWidth()
    .widthIn(max = 600.dp)
```

---

# 101. Content Maximum Width

On large displays, center primary content.

Example:

```text
max content width ≈ 600–840dp
```

depending on screen.

Do not stretch settings rows across a huge tablet screen.

---

# 102. Scrolling

Primary screens with uncertain vertical content must support scrolling.

Use:

```kotlin
LazyColumn
```

for lists.

Use:

```kotlin
verticalScroll()
```

only for relatively small fixed structures.

---

# 103. Long SSID Handling

Possible SSID:

```text
Office_Conference_Room_5GHz_Restricted_Test_Network
```

The UI must:

- wrap where appropriate
- use maximum lines
- ellipsize if required
- never overflow horizontally

---

# 104. Long Diagnostic Text

Errors can contain very long paths and messages.

Use:

```text
monospace optional
selectable text
line wrapping
expand/collapse
```

Never let a long command output dictate card width.

---

# 105. Landscape Mode

In landscape:

Home may use:

```text
Status card | Scanner
```

on wider displays.

Otherwise preserve vertical scrolling.

Do not compress components excessively.

---

# 106. Font Scaling

Test at least:

```text
100%
130%
150%
200%
```

Where possible.

Critical buttons must remain accessible.

---

# 107. Touch Targets

Interactive elements should have at least approximately:

```text
48 × 48dp
```

touch target.

Icons may visually appear smaller inside that target.

---

# 108. System Insets

Use:

```text
WindowInsets
safeDrawing
navigationBars
statusBars
ime
```

appropriately.

---

# 109. Edge-to-Edge

Support modern edge-to-edge design.

Top bars should account for status bar inset.

Bottom navigation should account for gesture/navigation area.

---

# 110. Keyboard Handling

PIN screen must remain usable with keyboard open.

Use:

```text
imePadding()
bringIntoView
scrollable container
```

as required.

---

# 111. Accessibility

Every actionable icon needs a meaningful content description.

Examples:

Good:

```text
Refresh networks
Open diagnostics
Clear logs
```

Bad:

```text
Icon
Button
Wifi icon
```

---

# 112. TalkBack State Descriptions

Examples:

```text
Root status: granted
Wi-Fi status: enabled
Signal strength: excellent
WPS support: available
```

---

# 113. Accessibility and Color

Never use:

```text
green = success
red = failure
```

without accompanying symbol/text.

---

# 114. Error UX Philosophy

Every error should answer:

1. What happened?
2. Why might it have happened?
3. Can the user fix it?
4. What action should they take?

---

# 115. Error Example

Bad:

```text
ERROR 12
```

Good:

```text
Unable to access Wi-Fi interface

The system detected wlan0, but root access to the
interface was denied.

[ Check Root ]

[ View Diagnostics ]
```

---

# 116. Root Denied UX

```text
Root access denied

Advanced network operations cannot continue
without root access.

You can continue using available diagnostics and
standard Wi-Fi scanning.

[ Request Again ]

[ Continue Limited ]
```

---

# 117. Root Unavailable UX

```text
Root not detected

This device does not appear to provide a root shell.

Basic Wi-Fi scanning remains available, but advanced
WPS functionality is disabled.
```

---

# 118. Supplicant Missing UX

```text
Supplicant interface unavailable

The app could not locate a compatible Wi-Fi
supplicant control interface on this device.

This can vary between Android manufacturers.

[ View Diagnostics ]
```

---

# 119. SELinux Restriction UX

```text
Access restricted by Android

The required Wi-Fi control socket exists, but Android
security policy prevents access.

The app will not modify SELinux security settings.

[ View Technical Details ]
```

---

# 120. Offline State

The application should not require internet connectivity for core local operations unless a future feature explicitly needs it.

No generic "No Internet" warning should appear unless relevant.

---

# 121. Network Privacy

Never expose:

- saved passwords
- PSKs
- private system credentials
- secrets
- root tokens

UI logs and copied diagnostics must sanitize sensitive values.

---

# 122. Home Screen Wireframe

```text
┌───────────────────────────────────┐
│ WPS Utility                  ⋮    │
├───────────────────────────────────┤
│                                   │
│ ┌───────────────────────────────┐ │
│ │ Device Status                 │ │
│ │                               │ │
│ │ ✓ Root        Granted         │ │
│ │ ✓ Wi-Fi       Enabled         │ │
│ │ ✓ Interface   wlan0           │ │
│ │ ! Supplicant  Limited         │ │
│ │                               │ │
│ │ View diagnostics →            │ │
│ └───────────────────────────────┘ │
│                                   │
│ Nearby Networks          Refresh  │
│ Last scanned 14 sec ago           │
│                                   │
│ ┌───────────────────────────────┐ │
│ │ Home Wi-Fi              ▂▄▆█ │ │
│ │ WPA2 • WPS                    │ │
│ │ -48 dBm                       │ │
│ └───────────────────────────────┘ │
│                                   │
│ ┌───────────────────────────────┐ │
│ │ Office                     ▂▄ │ │
│ │ WPA3                          │ │
│ │ -71 dBm                       │ │
│ └───────────────────────────────┘ │
│                                   │
├───────────────────────────────────┤
│ Home  Diagnose  Logs  Settings    │
└───────────────────────────────────┘
```

---

# 123. Diagnostics Wireframe

```text
┌───────────────────────────────────┐
│ Diagnostics                       │
│ Device capability checks          │
│                                   │
│ [Refresh]              [Copy All] │
│                                   │
│ ┌───────────────────────────────┐ │
│ │ Environment                   │ │
│ │ 7 passed • 2 warnings         │ │
│ └───────────────────────────────┘ │
│                                   │
│ ▼ Device                          │
│ Manufacturer       Realme         │
│ Android            16             │
│ API                XX             │
│                                   │
│ ▼ Root                            │
│ Status              Granted       │
│ Shell               Available     │
│                                   │
│ ▼ Wi-Fi                           │
│ Enabled             Yes           │
│ Interface           wlan0         │
│                                   │
│ ▶ Supplicant                      │
│ ▶ Permissions                     │
│ ▶ SELinux                         │
│                                   │
├───────────────────────────────────┤
│ Home  Diagnose  Logs  Settings    │
└───────────────────────────────────┘
```

---

# 124. WPS Session Wireframe

```text
┌───────────────────────────────────┐
│                                   │
│ Connecting                        │
│                                   │
│ Home Wi-Fi                        │
│                                   │
│              ◉                    │
│       Authenticating              │
│                                   │
│ ✓ Preparing                       │
│ ✓ Waiting                         │
│ ● Authenticating                  │
│ ○ Associating                     │
│ ○ Obtaining IP                    │
│                                   │
│ Communicating with your router... │
│                                   │
│         [ Cancel ]                │
│                                   │
└───────────────────────────────────┘
```

---

# 125. Logs Wireframe

```text
┌───────────────────────────────────┐
│ Logs                       Clear  │
│                                   │
│ [All] [Info] [Warning] [Error]    │
│                                   │
│ Search logs...                    │
│                                   │
│ 16:42:31 INFO                     │
│ WifiScanner                       │
│ Scan returned 12 networks.        │
│                                   │
│ 16:42:29 WARNING                  │
│ Supplicant                        │
│ Primary socket unavailable.       │
│                                   │
│ 16:42:28 INFO                     │
│ RootManager                       │
│ Root access granted.              │
│                                   │
├───────────────────────────────────┤
│ Home  Diagnose  Logs  Settings    │
└───────────────────────────────────┘
```

---

# 126. Settings Wireframe

```text
┌───────────────────────────────────┐
│ Settings                          │
│                                   │
│ General                           │
│ Keep screen awake             ○   │
│                                   │
│ Scanner                           │
│ Auto scan on launch           ●   │
│ Sort networks      Signal      >  │
│                                   │
│ Session                           │
│ Timeout             120 sec    >  │
│                                   │
│ Appearance                        │
│ Theme               System     >  │
│ Animations          Standard   >  │
│                                   │
│ Logging                           │
│ Log level           Info       >  │
│                                   │
│ About                         >   │
│                                   │
├───────────────────────────────────┤
│ Home  Diagnose  Logs  Settings    │
└───────────────────────────────────┘
```

---

# 127. Compose Component Architecture

Suggested structure:

```text
ui/
├── components/
│   ├── AppTopBar.kt
│   ├── AppNavigationBar.kt
│   ├── StatusCard.kt
│   ├── StatusRow.kt
│   ├── NetworkCard.kt
│   ├── SignalIndicator.kt
│   ├── StatusChip.kt
│   ├── EmptyState.kt
│   ├── ErrorState.kt
│   ├── LoadingState.kt
│   ├── DiagnosticCard.kt
│   ├── DiagnosticRow.kt
│   ├── LogEntry.kt
│   ├── SectionHeader.kt
│   └── WpsProgressTimeline.kt
│
├── home/
│   ├── HomeScreen.kt
│   ├── HomeRoute.kt
│   └── HomeUiState.kt
│
├── network/
│   ├── NetworkDetailsScreen.kt
│   └── WpsMethodSheet.kt
│
├── wps/
│   ├── WpsSessionScreen.kt
│   ├── WpsPinScreen.kt
│   └── WpsSessionUiState.kt
│
├── diagnostics/
│   ├── DiagnosticsScreen.kt
│   └── DiagnosticDetailsScreen.kt
│
├── logs/
│   └── LogsScreen.kt
│
├── settings/
│   ├── SettingsScreen.kt
│   └── AboutScreen.kt
│
└── theme/
    ├── Color.kt
    ├── Theme.kt
    ├── Type.kt
    ├── Shape.kt
    └── Dimensions.kt
```

---

# 128. Reusable Component: StatusRow

API concept:

```kotlin
StatusRow(
    icon = ...,
    title = "Root",
    value = "Granted",
    state = Status.Success
)
```

States:

```text
Success
Warning
Error
Loading
Neutral
```

---

# 129. Reusable Component: NetworkCard

Conceptual API:

```kotlin
NetworkCard(
    ssid = network.ssid,
    bssid = network.bssid,
    signal = network.signal,
    security = network.security,
    wpsAvailable = network.wpsAvailable,
    connected = network.connected,
    onClick = ...
)
```

---

# 130. Reusable Component: EmptyState

Inputs:

```text
icon
title
description
primary action
optional secondary action
```

Use everywhere instead of creating unrelated empty-state layouts.

---

# 131. Reusable Component: ErrorState

Inputs:

```text
error type
title
message
details
primary action
secondary action
```

---

# 132. State Management Rule

Composable screens must receive UI state rather than querying managers directly.

Correct:

```text
Manager
↓
Repository
↓
ViewModel
↓
StateFlow
↓
Composable
```

Avoid:

```text
Composable → Root shell
```

---

# 133. Recommended UI State Pattern

Example:

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val rootStatus: RootStatus = RootStatus.Checking,
    val wifiStatus: WifiStatus = WifiStatus.Checking,
    val interfaceName: String? = null,
    val supplicantStatus: SupplicantStatus = SupplicantStatus.Checking,
    val networks: List<WpsNetworkUiModel> = emptyList(),
    val scanStatus: ScanStatus = ScanStatus.Idle,
    val message: UiMessage? = null
)
```

---

# 134. One-Time Events

Use separate event flow for:

- snackbar
- permission launcher
- system settings launcher
- navigation
- share sheet

Do not store one-time events permanently in UI state.

---

# 135. UI Performance Rules

Avoid:

```text
large mutable objects in Compose
repeated sorting inside Composables
shell calls from Composables
large string parsing during recomposition
unbounded log state
```

---

# 136. Lazy List Rules

Use stable keys.

Example:

```kotlin
items(
    items = networks,
    key = { it.bssid }
)
```

Avoid using list index when stable identity exists.

---

# 137. Derived State

Use:

```kotlin
derivedStateOf
```

only when meaningful.

Do not overcomplicate straightforward state.

---

# 138. Resource Loading

Do not synchronously read large resources during composition.

All I/O belongs outside UI rendering.

---

# 139. Device Rotation

Active WPS session must not restart merely because orientation changed.

UI should reattach to existing ViewModel/session state.

---

# 140. Background / Foreground

If an active operation continues:

```text
App backgrounded
↓
session remains correctly managed
↓
user returns
↓
UI reflects latest state
```

Do not start duplicate jobs.

---

# 141. Process Recreation

Persist only what should survive process recreation.

Do not restore an already-dead root process as though it were active.

The app should revalidate environment when necessary.

---

# 142. Confirmation Dialogs

Required confirmations:

```text
Cancel active WPS session?
Clear logs?
Reset settings?
```

Avoid confirmation for reversible actions such as refreshing scan results.

---

# 143. Haptic Feedback

Use sparingly.

Appropriate:

```text
successful connection
critical toggle
confirmation
```

Do not vibrate on every card tap.

---

# 144. Screen Reader Ordering

Ensure semantic traversal follows visual hierarchy.

Example Home:

```text
Screen title
Device Status
Root
Wi-Fi
Interface
Supplicant
Diagnostics action
Nearby Networks
Network cards
Navigation
```

---

# 145. Testing Matrix

Every primary screen should be reviewed in:

```text
Light theme
Dark theme

320dp-ish width
360dp width
393dp width
412dp width
480dp width

Portrait
Landscape

Normal font
Large font
Very large font
```

---

# 146. Required Visual QA Cases

Test:

```text
very long SSID
hidden SSID
100+ networks
no networks
root denied
root unavailable
Wi-Fi disabled
permission denied
location disabled
supplicant unavailable
long diagnostic error
WPS timeout
WPS failure
successful session
```

---

# 147. Device-Specific QA

Because development uses a rooted physical Android phone, verify:

```text
system status bar
gesture navigation
display cutout
keyboard
permission dialogs
root prompt
bottom navigation
screen density
real font rendering
```

---

# 148. Screenshot Validation

Capture each important screen through ADB during implementation.

Review screenshots for:

```text
overflow
misalignment
unexpected wrapping
poor contrast
incorrect spacing
cutoff content
bottom navigation collision
status bar collision
```

---

# 149. Final UX Acceptance Criteria

The UI/UX is considered complete only if:

- all main screens exist
- all major backend states have visual representations
- no dead-end screens exist
- no important action lacks feedback
- all failures explain the problem
- technical diagnostics are available
- root status is understandable
- network list remains smooth
- scan state is visible
- WPS state is visible
- cancellation works
- success is backend-confirmed
- settings persist
- logs are usable
- navigation is predictable
- light mode works
- dark mode works
- large text works
- small screens work
- landscape works
- system insets work
- keyboard does not break forms
- accessibility labels exist
- animations remain lightweight
- no horizontal overflow exists

---

# 150. Final Visual Character

The finished application should feel:

```text
Professional
Modern
Technical
Clean
Trustworthy
Fast
Stable
Focused
Native Android
```

It should NOT feel:

```text
Hacker-themed
Neon-heavy
Over-animated
Game-like
Outdated
Crowded
Experimental
Unstable
```

---

# 151. Final Developer Directive

Implement the UI directly from this specification using Jetpack Compose and Material 3.

Do not treat this document as only a visual concept.

Every component must be connected to real application state.

Use reusable design-system components rather than duplicating UI patterns.

Test every major screen using actual ViewModel states.

Validate layouts on the connected physical Android device.

During implementation continuously check:

```text
Does it fit?
Does it scroll?
Does it remain usable with large text?
Does it correctly represent the backend?
Does it remain responsive?
Does it survive rotation?
Does it survive errors?
Does it remain understandable without technical expertise?
```

Reliability and performance always take precedence over decorative polish.

---

# 152. Required App Pages — Final Checklist

```text
[ ] Splash
[ ] First Launch
[ ] Permission Setup
[ ] Root Setup
[ ] Home
[ ] Scanner
[ ] Network Details
[ ] WPS Method Sheet
[ ] WPS Push Button Flow
[ ] WPS PIN Flow
[ ] Active WPS Session
[ ] Success State
[ ] Failure State
[ ] Timeout State
[ ] Unsupported State
[ ] Diagnostics
[ ] Diagnostic Details
[ ] Logs
[ ] Log Details
[ ] Settings
[ ] About
[ ] Root Denied State
[ ] Wi-Fi Disabled State
[ ] Permission Denied State
[ ] Empty Scanner State
```

---

# 153. Required Core Components — Final Checklist

```text
[ ] AppTopBar
[ ] AppNavigationBar
[ ] StatusCard
[ ] StatusRow
[ ] StatusChip
[ ] SectionHeader
[ ] NetworkCard
[ ] SignalIndicator
[ ] EmptyState
[ ] ErrorState
[ ] LoadingState
[ ] WpsMethodSheet
[ ] WpsProgressTimeline
[ ] DiagnosticCard
[ ] DiagnosticRow
[ ] LogEntry
[ ] SearchField
[ ] FilterChipGroup
[ ] ConfirmationDialog
[ ] Snackbar handling
[ ] PermissionExplanation
```

---

# 154. Required Interaction States — Final Checklist

```text
[ ] Default
[ ] Loading
[ ] Pressed
[ ] Disabled
[ ] Success
[ ] Warning
[ ] Error
[ ] Empty
[ ] Permission required
[ ] Root required
[ ] Wi-Fi disabled
[ ] Scanning
[ ] Connecting
[ ] Cancelling
[ ] Timed out
[ ] Unsupported
```

---

# 155. Definition of UI/UX Done

The design implementation is complete only when a real user can:

1. install the APK
2. open the application without confusion
3. understand device readiness
4. grant necessary permissions
5. understand root status
6. scan nearby Wi-Fi networks
7. inspect a network
8. start an authorized WPS flow
9. understand every connection stage
10. cancel safely
11. understand failures
12. inspect diagnostics
13. review logs
14. change appropriate settings
15. use the application on a small phone without overflow
16. rotate the device without breaking state
17. use large font settings
18. navigate using TalkBack
19. use both light and dark themes
20. perform all of the above without noticeable UI freezes or avoidable instability

The final result should be a **complete Material 3 Android network utility UI**, connected directly to the actual application architecture and tested against the connected rooted development device.