---
name: WPS Precision Diagnostics
colors:
  surface: '#111417'
  surface-dim: '#111417'
  surface-bright: '#36393e'
  surface-container-lowest: '#0b0e12'
  surface-container-low: '#191c20'
  surface-container: '#1e2228'
  surface-container-high: '#282c34'
  surface-container-highest: '#323539'
  on-surface: '#e1e2e8'
  on-surface-variant: '#bec9c7'
  inverse-surface: '#e1e2e8'
  inverse-on-surface: '#2e3135'
  outline: '#889391'
  outline-variant: '#3f4947'
  surface-tint: '#89d4cd'
  primary: '#9ce7e0'
  on-primary: '#003734'
  primary-container: '#80cbc4'
  on-primary-container: '#005652'
  inverse-primary: '#136964'
  secondary: '#a9c8fb'
  on-secondary: '#0a315b'
  secondary-container: '#274773'
  on-secondary-container: '#98b7e9'
  tertiary: '#bbddff'
  on-tertiary: '#003352'
  tertiary-container: '#7fc4ff'
  on-tertiary-container: '#00517d'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#a4f0e9'
  primary-fixed-dim: '#89d4cd'
  on-primary-fixed: '#00201e'
  on-primary-fixed-variant: '#00504b'
  secondary-fixed: '#d5e3ff'
  secondary-fixed-dim: '#a9c8fb'
  on-secondary-fixed: '#001c3b'
  on-secondary-fixed-variant: '#274773'
  tertiary-fixed: '#cde5ff'
  tertiary-fixed-dim: '#94ccff'
  on-tertiary-fixed: '#001d32'
  on-tertiary-fixed-variant: '#004b74'
  background: '#111417'
  on-background: '#e1e2e8'
  surface-variant: '#323539'
  surface-canvas: '#111318'
  surface-base: '#191c20'
  status-success: '#4caf50'
  status-warning: '#ffb74d'
  status-error: '#ef5350'
  text-muted: '#8e9199'
  outline-border: '#43474e'
typography:
  headline-lg:
    fontFamily: Roboto Flex
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Roboto Flex
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
  headline-md:
    fontFamily: Roboto Flex
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-lg:
    fontFamily: Roboto Flex
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  code-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '400'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  margin: 1rem
  margin-tablet: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
---

## Brand & Style

This design system establishes a high-precision, utilitarian diagnostic interface engineered for telecom technicians, network administrators, and systems engineers. The aesthetic avoids consumer "hacker" tropes—such as neon matrix glows, aggressive terminal green monochromes, or decorative HUD animations—in favor of crisp, institutional clarity and dependable technical transparency.

The visual style blends Material Design 3 surface containers with an engineered data-density model:
- **Tone:** Objective, measured, resilient, and mission-critical.
- **Visual Language:** Tiered slate-gray dark surfaces, precise borders, standardized status badging, and strict functional feedback.
- **Clarity over Flash:** Every interaction reflects verified socket/supplicant state. Transitions are immediate and purposeful, prioritizing data legibility and operational reliability under field conditions.

## Colors

The color palette implements strict Material 3 tonal surface tiers optimized for low-glare, high-contrast dark environments. Pure black (`#000000`) is strictly avoided across standard views to prevent black smear on OLED panels and to maintain distinct spatial layering.

### Key Applications
- **Surface Elevation Hierarchy:** Canvas sits at `#111318`, elevating to `#191c20` for standard surfaces, `#1e2228` for contained cards, and `#282c34` for elevated dialogs, popovers, and bottom sheets.
- **Accents:** The primary color (`#80cbc4`, muted teal) drives primary confirmations, interactive buttons, and verified connection checkpoints. Secondary (`#a8c7fa`, soft blue) handles interactive filters and informational badges.
- **Semantic Statuses:** Color is never used as the single signifier of state; every status pair requires an explicit symbol and descriptive text:
  - **Success (`#4caf50`):** Root granted, handshake verified, interface UP.
  - **Warning (`#ffb74d`):** SELinux restricted, rate-limited supplicant, high channel interference.
  - **Error (`#ef5350`):** WPS transaction timeout, authentication rejected, packet drop.
  - **Muted (`#8e9199`):** Inactive parameters, timestamp metadata, interface flags.

## Typography

Typography relies on a bifurcated structural system: **Roboto Flex** provides ultra-legible, system-level UI clarity across all standard Android resolutions and accessibility scales, while **JetBrains Mono** delivers fixed-width alignment for hardware identifiers, network addresses, and raw system outputs.

### Typographic Rules
- **Technical Addresses:** MAC addresses, BSSIDs, IPv4/IPv6 values, socket paths, and interface names (`wlan0`, `p2p0`) must strictly render in `JetBrains Mono` (`label-md` or `code-sm`).
- **Data Densities:** SSIDs utilize `title-md` or `body-lg`. If truncation occurs, strings must wrap up to two lines before applying a middle or end ellipsis.
- **Accessibility:** Text scaling up to 200% must not clip headers or overlap data values. Containers must wrap horizontally or transition into vertical stacked viewports when accessibility scales exceed 130%.

## Layout & Spacing

Layout geometry follows an 8dp baseline grid with a 4dp micro-step for dense technical telemetry.

### Screen Adaptations
- **Compact (Mobile: 320dp – 599dp):** Single-column vertical stream. Screen outer margin is `1rem` (16dp), item spacing is `0.75rem` (12dp). Top navigation app bar transitions to bottom navigation bar with persistent action anchors.
- **Medium / Expanded (Tablets & Foldables: 600dp+):** Screen outer margins increase to `1.5rem` (24dp). The layout splits into a dual-pane architecture: left pane displays persistent device telemetry and diagnostics; right pane displays the active scan list or packet console.
- **Landscape Phone Mode:** Splits into a 2-column format with status summaries anchored to the left and live logs or network feeds scrolling independently on the right.
- **Touch Ergonomics:** All interactive targets (switches, buttons, chip selectors, row tap regions) maintain an absolute minimum touch boundary of 48dp × 48dp, even when visual indicators are smaller.

## Elevation & Depth

This design system uses Material 3 tonal layering rather than high-contrast drop shadows. Depth is communicated by lighter surface fills rather than simulated physical light sources.

### Depth Hierarchy
1. **Level 0 (Canvas):** `#111318` - The base OS background window canvas.
2. **Level 1 (Base Cards / Sections):** `#1e2228` with a 1dp hairline border (`#43474e`, 40% opacity). Used for unselected network rows, Wi-Fi channel graphs, and static configurations.
3. **Level 2 (Active Cards / Modals):** `#282c34` with an ambient shadow (`rgba(0, 0, 0, 0.35)`, blur radius 8dp, vertical offset 2dp). Applied to active scanning targets, highlighted diagnostics, and persistent floating controls.
4. **Level 3 (Overlays & Sheets):** `#282c34` with a 32% black scrim backdrop. Used for WPS transaction workflows, credential dialogs, and filter sheets.

## Shapes

The shape system balances technical utility with modern Android standards:
- **Cards & Data Panels:** Formed with consistent 16dp rounded corners (`rounded-lg`), creating distinct, soft containers that group dense metrics without visual harshness.
- **Chips & Status Tags:** Built with 8dp to 12dp rounded corners or full pill shapes (`rounded-xl` / pill) depending on interactivity. Static badges use 8dp; interactive filter chips use full 100dp pill radii.
- **Inputs & Action Triggers:** Standard inputs, buttons, and segmented pickers share an 8dp to 12dp radius to signal immediate interactivity.

## Components

### Buttons
- **Primary CTA (e.g., "Scan Networks", "Start WPS Pin"):** Filled surface in primary teal (`#80cbc4`), text and icon in `#111318` (`label-lg`, 600 weight). Minimum height 48dp.
- **Secondary / Outlined Button:** Transparent fill, 1dp border in `#43474e`, text in `#a8c7fa`.
- **Tonal Button:** Surface fill `#282c34`, text in `#80cbc4`. Used for auxiliary functions (e.g., "Clear Logs").

### Chips & Badges
- **Status Badges:** Non-clickable. Compact 6dp vertical, 10dp horizontal padding. Border 1dp. Contains icon (14dp) + monospace tag (`label-md`). Example: `[✓ WPA3-SAE]`, `[! WPS-LOCKED]`.
- **Filter Chips:** 32dp height, full pill radius. Inactive: `#1e2228` fill with `#43474e` stroke. Active: `#80cbc4` fill with `#111318` text and leading checkmark icon.

### Cards & Data Panels
- **Network Cell Card:** Background `#1e2228`, border radius 16dp. Left accent stripe indicates RSSI strength (Green: > -60 dBm, Amber: -61 to -75 dBm, Red: < -76 dBm). Contains SSID in `Roboto Flex` (Medium) and BSSID / Frequency / Channel in `JetBrains Mono` (`code-sm`).
- **Diagnostic Timeline Card:** Visual sequential tracker representing the WPS state machine:
  1. Driver Initialization
  2. Supplicant Binding
  3. Association
  4. M1-M8 Handshake
  5. IP Lease
  Completed steps render with `#4caf50` checks; active steps display an indeterminate `#80cbc4` pulse; failures render with `#ef5350` and an explicit error explanation.

### Input Fields
- Filled container style with background `#1e2228`, bottom indicator border 2dp. Focused stroke color `#80cbc4`. Floating labels in `Roboto Flex`, actual entered text (PIN codes, hex keys) in `JetBrains Mono`.

### Log Terminal & Console View
- Surface `#111318`, internal padding 12dp, corner radius 8dp. Text in `JetBrains Mono` (`code-sm`).
- Pre-tagged prefixes with dedicated semantic color highlights: `[INFO]` in `#64b5f6`, `[WARN]` in `#ffb74d`, `[ERR]` in `#ef5350`, timestamps in `#8e9199`.