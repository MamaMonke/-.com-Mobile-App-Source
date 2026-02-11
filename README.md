# ИТД - Android приложение

Нативное Android-приложение для социальной сети [ИТД.com](https://итд.com/) на **Kotlin + Jetpack Compose**.

## Функционал

- **Авторизация** — вход и регистрация с выбором emoji-клана
- **Лента** — популярные посты и подписки с бесконечной прокруткой
- **Топ кланов** — рейтинг emoji-кланов по количеству участников
- **Создание постов** — текст, прикрепление изображений
- **Лайки, репосты, комментарии**
- **Поиск** — пользователей и хэштегов, трендовые хэштеги
- **Уведомления** — подписки, комментарии, лайки, упоминания
- **Профиль** — просмотр и редактирование, подписка/отписка
- **Настройки** — профиль, оформление, безопасность, приватность

## Технический стек

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Hilt** — Dependency Injection
- **Retrofit** + **OkHttp** — сетевой слой
- **Coil** — загрузка изображений
- **DataStore** — хранение токенов
- **Navigation Compose** — навигация
- **MVVM** архитектура

## Структура проекта

```
app/src/main/java/com/itd/app/
├── ITDApplication.kt          # Application class (Hilt)
├── MainActivity.kt            # Entry point
├── MainViewModel.kt           # Auth state + notifications
├── data/
│   ├── api/
│   │   ├── ITDApiService.kt   # Retrofit API endpoints
│   │   ├── AuthInterceptor.kt # JWT Bearer token interceptor
│   │   └── TokenManager.kt    # DataStore token storage
│   ├── model/
│   │   ├── Auth.kt            # Login/Register/Profile models
│   │   ├── Post.kt            # Post, Attachment models
│   │   ├── User.kt            # User, Clan, Suggestion models
│   │   └── Notification.kt    # Notification, Hashtag models
│   └── repository/
│       ├── AuthRepository.kt
│       ├── PostRepository.kt
│       ├── UserRepository.kt
│       └── NotificationRepository.kt
├── di/
│   └── AppModule.kt           # Hilt DI module
└── ui/
    ├── theme/                  # Dark theme (Color, Type, Theme)
    ├── navigation/             # NavHost + Bottom navigation
    ├── components/             # PostCard, TopClans, CreatePostBar
    └── screens/
        ├── auth/               # Login, Register
        ├── feed/               # Feed (Popular/Following)
        ├── explore/            # Search + Trending hashtags
        ├── notifications/      # Notifications
        ├── profile/            # User profile
        ├── post/               # Post detail + comments
        └── settings/           # Settings
```

## Как собрать

1. Откройте проект в **Android Studio Hedgehog** или новее
2. Синхронизируйте Gradle
3. Подключите устройство или эмулятор (API 26+)
4. Запустите `app`

## API

Приложение работает с API `https://итд.com/api/`:
- Авторизация через JWT токены
- Base URL: `https://xn--d1ah4a.com/`

## Требования

- Android 8.0+ (API 26)
- Android Studio Hedgehog+
- JDK 17

## Оригинальный автор
https://sovadev.space/
