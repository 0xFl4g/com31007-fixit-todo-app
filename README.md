# FixIt - Android Task Manager

A feature-rich task management app built with Kotlin and Jetpack Compose, following the MVVM architecture pattern. Developed as part of the COM31007 module at the University of Sheffield.

## Features

- **Task Management** -- Create, edit, view, and delete tasks with titles, due dates, priorities (0-5), notes, and image attachments
- **Task Lists** -- Organise tasks into named lists with sorting by name, creation date, or last updated
- **My Day** -- Quick view of tasks due today
- **Template Tasks** -- Save and reuse task templates for recurring work
- **Geolocation** -- Tag tasks with GPS coordinates, see distance from your current location, and get notified when you're near a task's location
- **Notifications** -- Scheduled reminders via AlarmManager with broadcast receiver, re-scheduled automatically after reboot
- **Settings** -- Toggle notification preferences and manage app data
- **Theming** -- Material Design 3 with dynamic colour support (Android 12+), light and dark modes

## Architecture

```
group.project.fixitapp/
├── data/
│   ├── entities/        # Room database entities (Task, List, Setting, Template)
│   ├── dao/             # Data access objects
│   ├── AppDatabase.kt   # Room database singleton with seed data
│   └── Converters.kt    # LocalDateTime type converters
├── services/
│   ├── GeoLocationService.kt      # Location tracking singleton
│   ├── NotificationReceiver.kt    # Broadcast receiver for scheduled notifications
│   └── BootReceiver.kt            # Re-schedules reminders after device reboot
├── utils/
│   └── NotificationUtils.kt       # Notification channel, scheduling, and display helpers
├── ui/
│   ├── pages/           # Composable screens (Home, TaskList, NewTask, EditTask, ViewTask, Settings, etc.)
│   ├── viewmodel/       # ViewModels for each screen
│   └── theme/           # Colour palette, typography, and dimensions
└── MainActivity.kt      # Entry point, navigation host, permission handling
```

**Pattern:** MVVM with `StateFlow` for reactive state, Room for persistence, Jetpack Compose Navigation for routing.

## Tech Stack

| Component | Version |
|-----------|---------|
| Kotlin | 2.1.10 |
| Jetpack Compose BOM | 2025.01.01 |
| Room | 2.7.1 |
| Navigation Compose | 2.8.9 |
| Lifecycle / ViewModel | 2.8.7 |
| Material 3 | Latest via BOM |
| Min SDK | 30 (Android 11) |
| Target SDK | 35 (Android 15) |

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 21
- Android SDK 35

### Build & Run

```bash
# Clone the repository
git clone https://github.com/0xFl4g/com31007-fixit-todo-app.git
cd com31007-fixit-todo-app

# Build the project
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug

# Run the unit tests
./gradlew testDebugUnitTest
```

Or open the project in Android Studio and run it directly.

## Database

Room with 4 tables:

| Table | Purpose |
|-------|---------|
| `task` | Tasks with title, due date, priority, location, image, reminder, and list association |
| `list` | Named task lists |
| `setting` | App preferences (notification toggles) |
| `template_task` | Reusable task templates |

The database is pre-seeded with default notification settings and 5 sample templates on first launch.

## Permissions

| Permission | Purpose |
|------------|---------|
| `ACCESS_FINE_LOCATION` | GPS-based task location tagging |
| `ACCESS_COARSE_LOCATION` | Network-based location fallback |
| `POST_NOTIFICATIONS` | Display reminder notifications (Android 13+) |
| `RECEIVE_BOOT_COMPLETED` | Re-schedule reminder alarms after a reboot |
| `SCHEDULE_EXACT_ALARM` | Schedule precise reminder alarms |

## License

This project is licensed under the MIT License -- see the [LICENSE](LICENSE) file for details.
