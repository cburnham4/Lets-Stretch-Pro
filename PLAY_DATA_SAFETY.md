# Google Play Data Safety Responses

**App:** Let's Stretch Pro (`letshangllc.stretchingroutinespro`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document provides responses for the Google Play Console Data Safety section.

---

## Overview

Let's Stretch Pro is a **paid, ad-free** stretching routines app. Stretch routines and optional user-selected photos are stored **locally on device** in a SQLite database. The app does **not** include AdMob, UMP, analytics, crash reporting, or other third-party tracking SDKs.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Photos / media (optional) | Yes (on-device only) | No | User-chosen routine/stretch images |
| Other personal info | No | No | — |
| App interactions / analytics | No | No | — |
| Advertising ID | No | No | No ads |
| Crash logs / diagnostics | No | No | No crash SDK |

---

## Detailed Data Types

### Photos and videos (optional)
- **Collected:** Yes — only if the user picks an image for a custom routine or stretch via the system document picker.
- **Shared:** No
- **Stored:** Locally in the app's SQLite database (as image bytes)
- **Purpose:** App functionality (displaying custom routines)
- **Optional:** Yes
- **Encrypted in transit:** N/A (no network upload)
- **Deletion:** User can delete custom routines in-app; uninstalling the app removes local data

### Network
This build does **not** declare `INTERNET` or network-related permissions. The app does not contact remote servers.

---

## Permissions

| Permission | Declared | Why / Play declaration notes |
|------------|----------|------------------------------|
| Storage / photos | **No** | Image pick uses `ACTION_GET_CONTENT` (system picker); no broad media/storage access |
| Internet / network | **No** | Ad-free Pro build; no ads or analytics SDKs |
| Notifications | **No** | Timer completion uses the device ringtone/alarm APIs in-process only |

If Play Console prompts for photo/media declarations: state that photos are optional, used only for in-app routine customization, stored on-device, and not shared.

---

## Third-Party SDKs

None that collect or share user data. Dependencies are limited to AndroidX / Material UI libraries and CircleImageView (UI only).

---

## Advertising

- **Ads:** None (paid Pro / ad-free)
- **UMP / consent:** Not applicable — no ads SDK
- **Advertising ID:** Not used

---

## Data Security

- **In transit:** No user data is transmitted over the network by this app
- **At rest:** Local SQLite / private app storage
- **Deletion:** Delete custom routines in-app, or clear app data / uninstall

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA). There is no advertising and no behavioral tracking SDK.

---

## Play Console Form Responses

### Does your app collect or share any of the required user data types?
**Yes** — optional photos selected by the user for custom routines (on-device only; not shared).

### Is all of the user data collected by your app encrypted in transit?
**N/A / Yes** — no user data is transmitted; there is no network data collection.

### Do you provide a way for users to request that their data is deleted?
**Yes** — users can delete custom routines in-app; uninstalling removes remaining local data.

### Data types to declare
1. **Photos and videos** (optional)
   - Collected: Yes (on device)
   - Shared: No
   - Ephemeral: No
   - Required: No
   - Purpose: App functionality

### Advertising ID
Declare that the app does **not** use advertising ID.

---

## Contact

For privacy inquiries, contact the developer via the email address listed on the Google Play Store listing.
