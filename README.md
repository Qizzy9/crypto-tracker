# 📈 CryptoTracker

> Android-приложение для отслеживания криптовалют в реальном времени с конвертером и избранным.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue?logo=android)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-green)
![License](https://img.shields.io/badge/license-MIT-green)

---

## Возможности

- **Список монет** — топ-50 по капитализации с ценой и изменением за 24ч
- **Поиск** — фильтрация по названию и тикеру
- **График** — 7-дневный спарклайн, нарисованный вручную через Canvas
- **Конвертер** — конвертация между любыми монетами по актуальному курсу
- **Избранное** — сохраняется локально, работает без интернета
- **Offline-first** — данные кэшируются в Room, приложение работает без сети
- **Pull-to-refresh** — обновление списка свайпом вниз
- **Shimmer-эффект** — анимация загрузки вместо пустого экрана

## Скриншоты

| Markets | Converter | Detail |
|---------|-----------|--------|
| Список монет с графиками | Конвертер с BottomSheet | График + избранное |

## Стек

| | |
|---|---|
| Язык | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Архитектура | MVVM + Clean Architecture |
| DI | Koin 3.5 |
| Сеть | Retrofit 2 + OkHttp |
| База данных | Room (offline-first кэш) |
| Изображения | Coil |
| Навигация | Navigation Compose |
| API | [CoinGecko](https://www.coingecko.com/en/api) (бесплатный) |

## Архитектура

```
app/
├── data/
│   ├── local/          # Room: Database, DAO, Entity
│   ├── remote/         # Retrofit: API, DTO
│   └── repository/     # Offline-first реализация
├── domain/
│   ├── model/          # Доменные модели
│   ├── repository/     # Интерфейсы
│   └── util/           # Resource sealed class
├── di/                 # Koin модули
└── ui/
    ├── components/     # SparklineChart (Canvas), Shimmer
    ├── navigation/     # NavGraph, Screen
    ├── screens/
    │   ├── list/       # Список монет
    │   ├── detail/     # Детальный экран
    │   ├── convert/    # Конвертер
    │   └── favorites/  # Избранное
    └── theme/          # Тёмная тема
```

## Запуск

1. Клонируй репозиторий
```bash
git clone https://github.com/Qizzy9/crypto-tracker.git
```

2. Открой в **Android Studio**

3. Нажми **Run** — API ключ не нужен, CoinGecko работает бесплатно

## Лицензия

MIT
