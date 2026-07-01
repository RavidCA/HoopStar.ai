<div align="center">

# 🏀 HoopStar.ai

### Court Intelligence. Elevated.

**Turn any basketball game video into personal highlight reels for every player — right from your phone.**

*Kotlin · Jetpack Compose · Material 3 · MVVM + Clean Architecture · Hilt*

</div>

---

## Overview

HoopStar.ai is a coach‑facing Android app that transforms a raw game video into
per‑player highlight reels. The Python/FastAPI backend already exists and runs;
this repository is the **Android client**, built from scratch against its HTTP API.

**The flow in one line:** a game video goes in → the AI detects players, reads
jersey numbers and recognizes actions → the coach maps detections to the roster →
picks a player → gets a finished highlight reel ready to watch, download, and
share on WhatsApp.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [User Flow](#user-flow)
5. [Screens](#screens)
6. [Key Features](#key-features)
7. [Setup & Run](#setup--run)
8. [Test Credentials](#test-credentials)
9. [API Contract](#api-contract)
10. [Notes & Gotchas](#notes--gotchas)

---

## Tech Stack

| Area | Technology |
|------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 (dark‑first) |
| Architecture | MVVM + Clean Architecture (data / domain / ui) |
| Dependency Injection | Hilt |
| Async | Coroutines + Flow / StateFlow |
| Networking | Retrofit 2 + OkHttp + Moshi |
| Image loading | Coil |
| Video playback | Media3 / ExoPlayer |
| Secure token storage | EncryptedSharedPreferences |
| Local persistence | Room (hidden teams + saved reels) |
| Background + notifications | Foreground Service + NotificationCompat |
| Navigation | Navigation‑Compose |
| Min SDK | 26 · **Target** SDK latest |

---

## Architecture

One‑way data flow with clearly separated layers:

```
UI (Compose) → ViewModel (StateFlow) → UseCase → Repository → API / Room
```

**Principles**
- ViewModels expose state via `StateFlow<UiState>` (Loading / Success / Empty / Error).
- Repositories return `DataResult<T>` (Success / Error) — Retrofit types never leak to the UI.
- All network calls are `suspend` functions on `Dispatchers.IO`.
- FastAPI errors are parsed defensively: the `detail` field may be a **string or a validation array**.

---

## Project Structure

```
core/            Base helpers: DataResult, UiState, ColorUtils, FormatUtils,
                 TeamLogos (team‑logo gallery)
data/
  local/         TokenStore (EncryptedSharedPreferences)
  local/db/      Room: HoopStarDatabase, DAO, HiddenTeamEntity, SavedReelEntity
  remote/        Retrofit APIs + DTOs + AuthInterceptor + NgrokInterceptor
  remote/        SafeApiCall, ApiErrorParser
  repository/    Repository implementations (Auth, Teams, Videos, Mapping,
                 Highlights, SavedReels)
domain/
  model/         Domain models: Coach, Team, Player, Job, Mapping, Highlight
  repository/    Repository interfaces
  usecase/       Use cases (Login, CreateTeam, UploadVideo, AutoMap, ExtractReel…)
service/         ReelGenerationService (Foreground) + ReelNotifications
ui/
  theme/         Material 3 theme (Color, Type, Theme)
  navigation/    NavGraph + Routes
  splash/        Animated splash + StarField
  auth/          Login / Register + AuthViewModel
  teams/         Team list + Roster + ViewModels
  upload/        Upload + job status + polling
  mapping/       Player mapping
  highlights/    Player picker + background reel generation
  playercard/    Player card with saved reels
  player/        ExoPlayer screen + DownloadHelper
  components/     Reusable composables (buttons, fields, shimmer, empty/error)
di/              Hilt modules (Network, Api, Repository, Database)
```

---

## User Flow

```
Splash (animated logo)
  → Login / Register            [store encrypted JWT]
  → My Teams                    [list, create, remove teams]
  → Roster                      [add / edit / delete players]
  → Games                       [upload a game video]
  → Job Status                  [live AI processing — polling]
  → Player Mapping              [map detected jerseys to roster, confidence]
  → Highlights                  [pick a player → generate reel in background]
  → (notification when ready) → Reel Player   [ExoPlayer + download + share]
  → Player Card                 [all reels saved for a player]
```

---

## Screens

**Splash** — Vector basketball logo (drawn in Compose Canvas, crisp at any
resolution) on a twinkling star field, with a pulsing/bouncing ball, a loading
bar, and a tagline. Fades smoothly into Login/Teams. Validates whether the stored
JWT is still valid against `/api/auth/me`.

**Login / Register** — Centered forms, show/hide password, local validation plus
friendly server errors (401 / 409 / 422). On success, stores the encrypted token
and routes to Teams.

**My Teams** — The coach's teams as bold cards (color stripe, logo, name, season,
player count). A FAB opens a centered dialog to create a team (name, season, color
palette, logo gallery). Each card has a trash button to remove the team (local hide).

**Roster** — Players sorted by jersey number: colored jersey chip, avatar, name,
birth year. Add / edit / delete (owner coach only). A "Games & Highlights" button
leads to uploads. Tapping a player opens their Player Card.

**Games (Upload)** — Pick a video from the device, multipart upload, and a game
history list with a status pill for each job.

**Job Status** — Animated progress ring (percentage, frames, FPS, duration),
polling every 4 seconds until terminal, a cancel button, and a "Continue to
Mapping" button when complete.

**Player Mapping** — Runs auto‑mapping and, for each detected jersey, shows the
suggested roster player with a confidence badge (High / Medium / Low / None) plus
a counts summary.

**Highlights** — Pick a player → "Generate reel". Generation runs in a Foreground
Service in the background; the screen shows *"Generating in the background — you
can leave the app."*

**Reel Player** — Full‑screen ExoPlayer with controls, the ngrok header (critical
for loading the MP4), and a download button via the system download manager.

**Player Card** — Every reel saved for the player (locally in Room): clip count,
duration, date, plus Watch (player) and Share (WhatsApp) buttons for each, with
delete support.

---

## Key Features

**Branding & logo** — Vector basketball logo drawn in Compose Canvas (glowing ring
+ seam lines); app launcher icon in every mipmap density plus an adaptive icon.

**Team logos** — A gallery of 10 selectable icons, stored in the backend's
`logo_url` field (as `"logo:xxx"`), shown on the card and in the Roster title.

**Background analysis + notifications** — Reel generation (extract → compose, two
slow synchronous calls) runs in a Foreground Service. An ongoing notification shows
progress; a final high‑priority notification with sound fires when ready — tapping
it opens the player. Works even after leaving the app.

**Local reel saving** — Every generated reel is stored in Room and appears on the
Player Card, surviving app restarts.

**WhatsApp sharing** — A button that sends a link to the reel straight to WhatsApp
(falls back to a general share sheet if WhatsApp isn't installed).

**Design** — Material 3 dark‑first, dynamic accent from the team color, slide/fade
transitions between screens, skeleton shimmer while loading, and consistent
Empty/Error states on every screen. The interface is in English and locked to LTR
and portrait orientation.

---

## Setup & Run

The **backend base URL** is defined in `app/build.gradle.kts` under `defaultConfig`:

```kotlin
buildConfigField("String", "BASE_URL",
    "\"https://thrift-fraying-plentiful.ngrok-free.dev/\"")
```

To move to production, change only this value and sync. No other code depends on
the address.

**Run:**
1. Open in Android Studio → Sync Project with Gradle Files.
2. Build → Rebuild Project (if KSP/Hilt complains after new additions, do Clean + Rebuild).
3. Run on an emulator/device with Android 8.0+ (API 26+).
4. On Android 13+, grant the notification permission prompt on the Highlights screen.

---

## Test Credentials

**Test coach** (owns a team — write endpoints testable immediately):
- email: `android.tester@starhoop.ai`
- password: `androidtest123`
- coach_id 16 · team_id 15 "Android Test Team" · players #7 (id 44), #12 (id 45)

**Existing processed game** (test mapping/highlights without the ~25‑min wait):
- team_id 13 "Bremen" · job_id 22 (completed)
- players with clips: 39 (Raylon #22), 40 (Colton #10), 36 (Preston #23)
- player with **no** clips (422 case): 42 (Ian #15)

---

## API Contract

All JSON is snake_case. Auth: JWT (HS256, 30‑day expiry), header
`Authorization: Bearer <token>`. No refresh tokens — when expired, log in again.

**Auth**
- `POST /api/auth/register` · `POST /api/auth/login` · `GET /api/auth/me`

**Teams** (reads are open; writes require auth + ownership)
- `GET /api/teams` · `POST /api/teams` · `GET /api/teams/{id}`
- No update/delete‑team endpoint (create‑only) → client‑side removal = local hide.

**Players**
- `GET / POST / PUT / DELETE /api/teams/{teamId}/players[/{playerId}]`

**Video**
- `POST /api/videos/upload` (multipart, `team_id` as a form field)
- `GET /api/videos/{jobId}` · `GET /api/videos` · `POST /api/videos/{jobId}/cancel`

**Player Mapping**
- `POST /api/videos/{jobId}/player_mapping/auto?team_id=X`
  (underscore in path, `team_id` as a query param)

**Highlights**
- `POST /api/videos/{jobId}/highlights/extract?player_id=X`
  ("no clips" case = **422 with no reel_id**)
- `POST /api/videos/{jobId}/highlights/{reelId}/compose` (synchronous, slow)
- `GET .../download` → binary MP4

---

## Notes & Gotchas

- **ngrok:** the backend is exposed via a tunnel on the backend developer's
  machine. If `/docs` is unreachable, the tunnel is down — not an app bug. Every
  request (including ExoPlayer) sends `ngrok-skip-browser-warning: true`.
- **Timeouts:** extract/compose are synchronous and take minutes → `readTimeout`
  is set to 1200 seconds.
- **Nullable reel_id:** the "no clips" case returns 422 → handled as a friendly
  message/notification.
- **Error `detail`:** may be a string or an array — parsed by `ApiErrorParser`.
- **Team deletion:** no server endpoint → the team is hidden locally (Room) but
  remains in the backend DB.
- **Saved reels:** stored locally (Room) because there's no endpoint that returns
  a player's saved reels.
- **KSP/Hilt:** an `error.NonExistentClass` error almost always means a Clean +
  Rebuild is needed after adding a repository/binding.

---

<div align="center">

*Built from scratch, milestone by milestone, against a live backend the whole way.* 🏀

</div>
