# Interactive Motion & Animation Specification: Startup Transition

**Document Type:** Motion & Interaction Engineering Specification  
**Application:** WPS Android App (WPS Network Utility)  
**Spec Alignment:** §7 (Startup Flow), §8 (Splash Screen), §41 (Session Animation), §91–97 (Animation System), §149–150 (Visual Character)  
**Motion Framework:** Jetpack Compose Animation (`core:animation`, `AnimatedVisibility`, `animate*AsState`, `updateTransition`)  

---

## 1. Executive Motion Philosophy (§91, §150)

The startup transition must embody **precision, technical reliability, and instant responsiveness**.
Per §91 and §150:
- **No decorative delay**: The splash exists only during real cold-boot initialization (§8).
- **No fake progress spinners**: Non-blocking asynchronous checks (§7, §2.2).
- **No playful or bouncy physics**: Avoid oversized spring damping, confetti, 3D flips, or neon glows. Use crisp Material 3 motion curves with standard easing (`FastOutSlowInEasing` / `LinearOutSlowInEasing`).

---

## 2. Startup Timeline & Choreography Architecture (§7, §8, §91)

```
Time (t)      0ms ------------ 200ms ------------ 400ms ------------ 650ms ------------ 850ms
Icon Scale    0.92 ───────> 1.0 (250ms)
Icon Pulse    Radar waves emit: 1st wave (300ms) ────────> 2nd wave (600ms)
Title Alpha   0.0 ───────> 1.0 (200ms delay, 200ms duration)
Status Bar    0% ───────────────────────────────> 100% (Driven by async init StateFlow)
Screen Exit   Home Screen Shared Element crossfade & status container slide (300ms)
```

### Stage Breakdown:

| Stage | Trigger / Precondition | Visual Change | Easing & Duration |
| :--- | :--- | :--- | :--- |
| **Stage 1: App Icon Entrance** | `onCreate()` / Activity launch | Central radar emblem scales `0.92f → 1.0f`, alpha `0f → 1f` | `FastOutSlowInEasing`, **220ms** |
| **Stage 2: Telemetry Reveal** | Icon entrance finishes | App title and version chip slide up `+12dp → 0dp`, alpha `0f → 1f` | `LinearOutSlowInEasing`, **200ms** |
| **Stage 3: Subsystem Init Indicator** | Async workers launch (§7) | Progress bar animates to loaded checkpoint (`ENV` & `CONFIG`); radar emits subtle ambient ripples | `FastOutLinearInEasing`, **180ms per tick** |
| **Stage 4: Asynchronous Decoupling** | Minimum environment validated | Subsys checks move to background thread (`SUBSYS: ASYNC §7`) | Non-blocking transition |
| **Stage 5: Exit & Home Reveal** | Navigation to Home (`NavHost`) | Splash contents fade out `1f → 0f` with `0.98f` subtle scale down; Home `StatusCard` slides up `+24dp → 0dp` | `FastOutSlowInEasing`, **280ms** |

---

## 3. Detailed Parameter & Token Matrix

### 3.1 Jetpack Compose Timing Tokens

```kotlin
object StartupMotionDefaults {
    const val DURATION_ICON_ENTER = 220
    const val DURATION_TEXT_REVEAL = 200
    const val DURATION_RADAR_PULSE = 1200
    const val DURATION_TRANSITION_HOME = 280
    const val DURATION_SUBSYS_FADE = 160

    val CubicEaseOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)     // Decelerate
    val CubicEaseIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)      // Accelerate
    val CubicStandard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)    // Standard Material 3
}
```

### 3.2 Radar Pulse Wave Shader / Canvas Parameters
- **Wave Ring Count:** 2 concentric circles.
- **Max Radius:** Expanding from `56dp` to `120dp`.
- **Stroke Width:** Decaying linearly from `1.5dp` to `0.5dp`.
- **Alpha:** Decaying exponentially from `0.45` to `0.00`.
- **Color:** `#80CBC4` (Teal 200 Primary accent token).

---

## 4. Compose Implementation Architecture

### 4.1 Production State Representation

```kotlin
enum class StartupPhase {
    ICON_ENTRANCE,
    TELEMETRY_REVEAL,
    SUBSYSTEMS_BOOTSTRAP,
    COMPLETED
}

data class StartupTelemetryState(
    val phase: StartupPhase = StartupPhase.ICON_ENTRANCE,
    val envReady: Boolean = false,
    val configLoaded: Boolean = false,
    val isReadyToNavigate: Boolean = false
)
```

### 4.2 Transition Orchestration Code Snippet

```kotlin
@Composable
fun SplashScreenTransition(
    telemetryState: StartupTelemetryState,
    onNavigateHome: () -> Unit
) {
    val transition = updateTransition(targetState = telemetryState.phase, label = "StartupTransition")

    // 1. Icon Scale & Alpha
    val iconScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 220, easing = FastOutSlowInEasing) },
        label = "IconScale"
    ) { phase ->
        if (phase == StartupPhase.ICON_ENTRANCE) 0.92f else 1.0f
    }

    val iconAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 180, easing = LinearEasing) },
        label = "IconAlpha"
    ) { phase ->
        if (phase == StartupPhase.ICON_ENTRANCE) 0.0f else 1.0f
    }

    // 2. Subsystem Telemetry Card Slide
    val telemetryOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 240, easing = FastOutSlowInEasing) },
        label = "TelemetryOffset"
    ) { phase ->
        when (phase) {
            StartupPhase.ICON_ENTRANCE -> 24.dp
            StartupPhase.TELEMETRY_REVEAL,
            StartupPhase.SUBSYSTEMS_BOOTSTRAP,
            StartupPhase.COMPLETED -> 0.dp
        }
    }

    // 3. Navigation trigger when ready
    LaunchedEffect(telemetryState.isReadyToNavigate) {
        if (telemetryState.isReadyToNavigate) {
            onNavigateHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SplashContent(
            iconScale = iconScale,
            iconAlpha = iconAlpha,
            telemetryOffset = telemetryOffset,
            telemetryState = telemetryState
        )
    }
}
```

---

## 5. Navigation Transition to Home Screen (§5, §92)

When navigating from `Splash` to `Home`:

1. **Shared Element Coordinate Mapping:**
   - The central radar emblem transitions seamlessly into the Home screen's top app bar leading icon (`32dp` size) using `SharedTransitionLayout` (or a crossfade in standard Compose 1.6+).
2. **Container Exit:**
   - Splash surface exits with `slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(280, easing = CubicStandard))`.
3. **Home Entry:**
   - Home screen `EnvironmentStatusCard` enters with `fadeIn(animationSpec = tween(240, delayMillis = 60)) + slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up, initialOffset = { it / 6 })`.

---

## 6. Accessibility & Reduced Motion Protocol (§97, §149)

Per **Section §97 (Reduced Motion)**:
When `LocalAccessibilityManager.current` detects reduced motion preferences or system developer option `Animator duration scale == 0`:
- **Disable radar ripple canvas drawing**: The expanding rings are completely suppressed.
- **Collapse durations**: Set transition durations to `0ms` (instantaneous switch) or maximum `80ms` linear opacity crossfade.
- **Maintain semantic state announcements**: TalkBack will announce:
  - *"WPS Network Utility started. Environment ready, moving to Scanner."*
