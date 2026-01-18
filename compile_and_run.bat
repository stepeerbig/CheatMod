@echo off
echo Компиляция проекта...

REM Компилируем только наши классы, используя классовый путь из зависимостей Gradle
gradlew compileJava

if errorlevel 1 (
    echo Ошибка компиляции.
    pause
    exit /b 1
)

echo Запуск парсера...

REM Запускаем парсер, используя классовый путь, собранный Gradle
java -cp "build/classes/java/main;build/resources/main;%USERPROFILE%\.gradle\caches\modules-2\files-2.1\*" com.cheatmod.parser.ExcelConfigParser

pause