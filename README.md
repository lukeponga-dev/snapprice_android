# PriceSnap 📸

PriceSnap is an AI-powered price scanner built for op shop staff and individuals to value thrift finds, resale items, clothing, sneakers, collectibles, and secondhand goods. Point your camera at an item to get new and used price ranges, sold-comp signals, and confidence notes in seconds.

## 🚀 Key Features

- **Instant AI Appraisal**: Capture or upload photos to identify items and estimate market value using high-precision vision models.
- **Condition Intelligence**: Numeric condition scoring (e.g., 8/10) paired with automated defect detection (e.g., "minor scuffing", "fading").
- **Market Comparison Matrix**: View localized resale pricing (NZD) with low, median, and high signals from platforms like Trade Me.
- **Scan History**: Securely save and review your appraisal history locally with offline-first support.
- **Premium Design**: A high-contrast Material 3 interface optimized for dark mode with emerald accents and fluid navigation.

## 🛠 Tech Stack

- **UI**: Jetpack Compose with Material 3 components.
- **Navigation**: Type-safe Navigation Compose with a 4-tab bottom navigation container.
- **Architecture**: MVVM (Model-View-ViewModel) for clean state management.
- **Hardware**: CameraX for live viewfinder, frame-alignment, and high-resolution capture.
- **Networking**: Retrofit & OkHttp with GSON serialization for backend communication.
- **Persistence**: Room Database for secure, local history storage.
- **Concurrency**: Kotlin Coroutines & Flow for asynchronous appraisal workflows.

## 📂 Project Structure

```text
dev.lukeponga.pricesnap
├── camera         # CameraX implementation & image processing
├── history        # Room DB entities, DAOs, and Repositories
├── model          # Data transfer objects & UI models
├── network        # Retrofit API services & NetworkClient
└── ui             # Jetpack Compose screens, themes, and ViewModels
    ├── components # Shared UI elements
    ├── screens    # Primary app destinations (Home, Scan, etc.)
    └── theme      # Material 3 color schemes & typography
```

## 🚥 Getting Started

1. **Scan**: Point the camera at any item or upload from your gallery.
2. **Analyze**: The app identifies the item and checks live market comps.
3. **Appraise**: Review the condition grade and recommended resale price.
4. **Save**: Bookmark the appraisal to your local history for later reference.

---
Built with ❤️ using **Google AI Studio** & **Jetpack Compose**.

## Privacy and data deletion

PriceSnap sends appraisal photos over HTTPS to its Vercel API, which uses Google Gemini to analyse them. Firebase Authentication processes account details when sign-in is used. Scan history and thumbnails are stored locally on the device, and Android backup is disabled.

For the full details, including provider processing, retention, security, and your rights, see:

- [Privacy Policy](PRIVACY_POLICY.md)
- [Data Deletion Policy](DATA_DELETION_POLICY.md)

## Internal backend integration (Option A)

The app uses `https://pricesnap-server.vercel.app/` and sends
`POST /api/valuate` with `imageBase64` and the real `mimeType`. Gemini runs
inside [pricesnap-backend](https://github.com/lukeponga-dev/pricesnap-backend);
no Gemini key belongs in the APK. The unused `/api/analyze` client call has
been removed because this backend does not expose it.

Deploy [backend PR #2](https://github.com/lukeponga-dev/pricesnap-backend/pull/2)
and configure its server-side `GEMINI_API_KEY` before testing this Android branch.
The app checks `/api/connection` for internal-engine configuration. This does
not prove the provider key is valid or that quota is available.

To use another deployment, build with
`-PpricesnapBaseUrl=https://your-backend.example/` or set `PRICESNAP_BASE_URL`.
Keep the trailing slash required by Retrofit. The default already targets the
PriceSnap server. Read timeout is 120 seconds, total call timeout 150 seconds,
and automatic connection retries are disabled to avoid duplicate valuation work.

Results use fractional confidence (0–1) and condition scores out of 100.
`insufficient_evidence` and missing/invalid canonical prices display as unpriced,
with no zero-dollar fallback. Unpriced results are not added to priced history,
and their Save Result button is disabled. Server warnings are shown on the result
screen. Positive estimates are labelled market estimates, not verified sale prices.

Validation commands (Android SDK and project-compatible JDK required):

```bash
bash gradlew :app:testDebugUnitTest --tests 'dev.lukeponga.pricesnap.model.ValuationResponseTest' --tests 'dev.lukeponga.pricesnap.network.AppraisalRepositoryTest'
bash gradlew :app:assembleDebug
```

Manual acceptance: scan a real item against the configured deployment; inspect
NZD price/range, confidence and condition. Also exercise an insufficient-evidence
result and confirm it shows no price and creates no priced history entry.
