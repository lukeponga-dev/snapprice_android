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

- [Privacy Policy](PRIVACY_POLICY.md)
- [Data Deletion Policy](DATA_DELETION_POLICY.md)
