# Industrial Evolution

Самостоятельный технологический мод для **Minecraft Java 1.20.1 / Forge 47.3.0 / Java 17**.
Mod ID: `industrialevolution`. Текущая версия: **0.1.0, этап 1 — основа проекта**.

## Реализовано

- Отдельная творческая вкладка Industrial Evolution.
- Резина (`industrialevolution:rubber`): слизь + сушёная ламинария → 2 предмета.
- Корпус механизма (`industrialevolution:machine_casing`): 8 железных слитков вокруг резины → 1 блок.
- Установка и добыча корпуса каменной киркой или лучше, выпадение блока, рецепты в книге рецептов.
- Оригинальные текстуры 16×16, русская и английская локализации, теги Forge.
- Общий код регистрации для клиента и выделенного сервера.
- Отдельный source set `gametest`, исключённый из распространяемого JAR.

Корпус — конструкционный блок. Энергетика, машины, реакторы и остальные системы
будут добавляться последовательно согласно [плану](docs/ROADMAP.ru.md).
JEI и Jade пока не являются зависимостями: специализированные интеграции появятся
вместе с интерфейсами и собственными рецептами механизмов.

Проверено: сборка JAR и три серверных GameTest прошли; конфигурации IntelliJ
созданы. Графический клиент и подключение игроков пока не проверялись.

## Сборка

Установите **JDK 17**, задайте `JAVA_HOME` на каталог JDK и проверьте `java -version`.
Gradle скачивается через включённый Wrapper; отдельная установка Gradle не нужна.

```bash
./gradlew --version
./gradlew build
./gradlew runGameTestServer
```

Windows PowerShell: используйте `.\gradlew.bat` вместо `./gradlew`.
Готовый мод: `build/libs/industrialevolution-0.1.0.jar`.
Установите Forge 47.3.0 для Minecraft 1.20.1 и поместите JAR в `mods/` клиента и сервера.
Для первой сборки нужен Интернет: Forge/Maven, Gradle и официальные ресурсы Mojang.

`build` компилирует и собирает мод; игровые проверки запускаются **отдельно**
командой `runGameTestServer`. Обычный Gradle `test` без тестов не подтверждает
работоспособность мода. GameTest использует собственный временный мир в
`run-gametest/`; результаты и точный состав проверок описаны в
[VALIDATION.ru.md](docs/VALIDATION.ru.md).

## IntelliJ IDEA

1. Откройте корень репозитория как Gradle-проект.
2. В **Project Structure → Project SDK** выберите JDK 17.
3. В **Settings → Build Tools → Gradle → Gradle JVM** также выберите JDK 17;
   источник Gradle — Wrapper.
4. Дождитесь импорта зависимостей и выполните `./gradlew genIntellijRuns`.
5. Перезагрузите Gradle-проект. Запустите конфигурацию `runClient` для одиночной
   игры либо `runServer` для выделенного сервера. Те же задачи доступны через
   терминал: `./gradlew runClient`, `./gradlew runServer`.
6. Для обычного сервера прочитайте Minecraft EULA по ссылке, которую выводит
   сервер. После согласия измените созданный `run/eula.txt`, затем повторите запуск.
   GameTest-сервер предназначен для автоматических проверок.

Игровой клиент требует графической среды и поддержки OpenGL. На облачной машине
проверяется серверный путь; визуальный осмотр проводится локально.

## Ручная проверка клиента

Создайте творческий мир. Найдите вкладку Industrial Evolution и оба объекта,
проверьте русскую и английскую локализации и отсутствие отсутствующих текстур.
Поставьте корпус, переключитесь в выживание и проверьте добычу каменной киркой.
Соберите оба рецепта на верстаке; деревянная кирка не должна давать корпус.
Перезапустите мир и убедитесь, что поставленный блок сохранился.

## Структура и дальнейшая разработка

```text
.
├── build.gradle, settings.gradle, gradle.properties
├── gradlew, gradlew.bat, gradle/wrapper/
├── src/main/java/com/industrialevolution/
│   ├── IndustrialEvolution.java
│   └── registry/{ModBlocks,ModItems,ModCreativeTabs}.java
├── src/main/resources/
│   ├── META-INF/mods.toml, pack.mcmeta
│   ├── assets/industrialevolution/{blockstates,models,textures,lang}/
│   └── data/{industrialevolution,forge,minecraft}/
├── src/gametest/{java,resources}/
├── tools/generate_assets.py
└── docs/
    ├── ARCHITECTURE.ru.md
    ├── ROADMAP.ru.md
    ├── SPECIFICATION.ru.md
    └── VALIDATION.ru.md
```

[Архитектура](docs/ARCHITECTURE.ru.md) · [План этапов](docs/ROADMAP.ru.md) ·
[Полное исходное задание](docs/SPECIFICATION.ru.md)

Ресурсы воспроизводятся командой `python3 tools/generate_assets.py` (Python 3,
без сторонних библиотек). Меняйте генератор вместе с генерируемыми файлами.
Весь код первого этапа находится в репозитории; пустые реализации будущих модулей
не включены. Следующий этап: EU API, генератор, BatBox и медный кабель.

Права на собственный код и ресурсы: All Rights Reserved до выбора автором лицензии.
Условия использования Gradle Wrapper приведены в `licenses/GRADLE-LICENSE.txt`.
