# План интеграции Anthropic API и превращения бота в AI-агента

## Фаза 1: Подготовка архитектуры (1-2 недели)

### 1.1 Рефакторинг существующей архитектуры
- **Создать интерфейс `MessageHandler`**
  ```java
  public interface MessageHandler {
      boolean canHandle(String message);
      String handle(String message, long chatId);
  }
  ```
- **Реализовать паттерн Chain of Responsibility** для обработки сообщений
- **Выделить классы-обработчики**:
  - `KeywordHandler` - для существующих ключевых слов
  - `WeatherHandler` - для запросов погоды
  - `AIHandler` - для AI-запросов (новый)
- **Создать `ConfigurationManager`** для централизованного управления настройками

### 1.2 Внедрение зависимостей
- **Добавить DI-фреймворк** (например, Google Guice или Spring Boot)
- **Создать модули конфигурации** для разных компонентов
- **Избавиться от статических методов** в классе `Telegram`

## Фаза 2: Интеграция Anthropic API (2-3 недели)

### 2.1 Создание клиента для Anthropic API
- **Добавить зависимости в pom.xml**:
  ```xml
  <dependency>
      <groupId>com.squareup.okhttp3</groupId>
      <artifactId>okhttp</artifactId>
      <version>4.12.0</version>
  </dependency>
  ```
- **Создать класс `AnthropicClient`**:
  - Методы для отправки запросов к API
  - Управление токенами и rate limiting
  - Обработка ответов и ошибок
  - Поддержка streaming ответов

### 2.2 Модель данных для AI
- **Создать классы для API**:
  - `AnthropicRequest` - запрос к API
  - `AnthropicResponse` - ответ от API
  - `ConversationContext` - контекст диалога
  - `Message` - модель сообщения
- **Реализовать сериализацию/десериализацию** с Jackson

### 2.3 Управление контекстом диалога
- **Создать `ConversationManager`**:
  - Хранение истории диалогов по chatId
  - Ограничение размера контекста
  - Очистка старых диалогов
- **Выбрать хранилище**:
  - In-memory с TTL для начала
  - Redis для production
  - PostgreSQL для долгосрочного хранения

## Фаза 3: AI функциональность (3-4 недели)

### 3.1 Базовые AI функции
- **Реализовать `AIHandler`**:
  - Определение AI-запросов (например, начинающихся с "@ai" или "?")
  - Формирование промптов
  - Обработка ответов Claude
  - Форматирование для Telegram
- **Добавить команды**:
  - `/ai` - включить AI режим
  - `/clear` - очистить контекст
  - `/model` - выбрать модель (claude-3-opus, claude-3-sonnet)

### 3.2 Продвинутые функции
- **Режимы работы AI**:
  - Ассистент (общие вопросы)
  - Кодер (помощь с программированием)
  - Аналитик (анализ данных)
  - Творческий (генерация контента)
- **Интеграция с существующими функциями**:
  - AI может вызывать погодный API
  - AI может искать на YouTube
  - AI может планировать напоминания

### 3.3 Обработка специальных случаев
- **Длинные ответы**: разбивка на несколько сообщений
- **Форматирование кода**: использование Telegram Markdown
- **Обработка изображений**: если пользователь отправляет фото
- **Голосовые сообщения**: транскрипция через Whisper API

## Фаза 4: Оптимизация и масштабирование (2-3 недели)

### 4.1 Производительность
- **Асинхронная обработка**:
  - Использовать CompletableFuture для API вызовов
  - Очередь задач для обработки сообщений
  - Thread pool для параллельной обработки
- **Кэширование**:
  - Кэш частых запросов
  - Кэш промптов и шаблонов

### 4.2 Мониторинг и аналитика
- **Метрики использования**:
  - Количество запросов к AI
  - Время ответа
  - Расход токенов
  - Популярные запросы
- **Интеграция с системами мониторинга**:
  - Prometheus + Grafana
  - Логирование в ELK стек

### 4.3 Безопасность
- **Фильтрация контента**:
  - Проверка входящих сообщений
  - Модерация AI ответов
- **Rate limiting**:
  - Ограничение запросов per user
  - Защита от спама
- **Шифрование**:
  - Хранение API ключей в зашифрованном виде
  - Шифрование истории диалогов

## Фаза 5: Дополнительные AI возможности (4+ недель)

### 5.1 RAG (Retrieval-Augmented Generation)
- **Векторная база данных** (Pinecone, Weaviate)
- **Загрузка документов** пользователями
- **Поиск по базе знаний** перед ответом AI

### 5.2 Мультимодальность
- **Анализ изображений** через Claude Vision
- **Генерация изображений** через DALL-E или Stable Diffusion
- **Работа с документами** (PDF, Word)

### 5.3 Агентские функции
- **Автономные действия**:
  - Планирование задач
  - Выполнение последовательности действий
  - Интеграция с внешними API
- **Персонализация**:
  - Обучение на основе предпочтений пользователя
  - Адаптивные ответы

## Технические требования

### Обновление зависимостей
```xml
<!-- Anthropic API интеграция -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- Async обработка -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.5.0</version>
</dependency>

<!-- Кэширование -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>

<!-- Redis для хранения контекста -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>5.0.0</version>
</dependency>
```

### Переменные окружения
```bash
# Anthropic API
ANTHROPIC_API_KEY=your_api_key
ANTHROPIC_MODEL=claude-3-opus-20240229
ANTHROPIC_MAX_TOKENS=4096
ANTHROPIC_TEMPERATURE=0.7

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=optional

# Лимиты
MAX_CONTEXT_LENGTH=10
REQUEST_LIMIT_PER_USER=100
REQUEST_WINDOW_HOURS=24
```

## Примерная структура проекта после обновления

```
telegram/
├── src/main/java/ru/tyatyushkin/telegram/
│   ├── core/
│   │   ├── Bot.java
│   │   ├── MessageRouter.java
│   │   └── Configuration.java
│   ├── handlers/
│   │   ├── MessageHandler.java
│   │   ├── AIHandler.java
│   │   ├── WeatherHandler.java
│   │   └── KeywordHandler.java
│   ├── ai/
│   │   ├── AnthropicClient.java
│   │   ├── ConversationManager.java
│   │   ├── PromptBuilder.java
│   │   └── models/
│   │       ├── AnthropicRequest.java
│   │       └── AnthropicResponse.java
│   ├── storage/
│   │   ├── ConversationStorage.java
│   │   ├── RedisStorage.java
│   │   └── InMemoryStorage.java
│   └── utils/
│       ├── RateLimiter.java
│       ├── MessageFormatter.java
│       └── MetricsCollector.java
```

## Ключевые метрики успеха

1. **Время ответа AI** < 3 секунды
2. **Точность ответов** > 90%
3. **Uptime** > 99.9%
4. **Стоимость per запрос** < $0.01
5. **Удовлетворенность пользователей** > 4.5/5

## Риски и митигация

1. **Высокая стоимость API**
   - Митигация: кэширование, умные промпты, выбор подходящей модели

2. **Безопасность данных**
   - Митигация: шифрование, аудит, соответствие GDPR

3. **Масштабируемость**
   - Митигация: микросервисная архитектура, горизонтальное масштабирование

4. **Зависимость от внешних API**
   - Митигация: fallback механизмы, локальные модели как backup