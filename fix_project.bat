@echo off
echo Исправление структуры проекта CheatMod...
echo.

echo 1. Создаем правильную структуру пакетов...
mkdir src\main\java\com\cheatmod 2>nul
if exist "src\main\java\CheatMod.java" (
    move "src\main\java\CheatMod.java" "src\main\java\com\cheatmod\"
    echo Файл CheatMod.java перемещен
)

echo 2. Обновляем build.gradle...
curl -s -o build.gradle https://raw.githubusercontent.com/kdjdev/forge-mod-template/main/build.gradle

echo 3. Обновляем mods.toml...
(
echo modLoader="javafml"
echo loaderVersion="[36,)"
echo license="MIT"
echo.
echo [[mods]]
echo modId="cheatmod"
echo version="1.0.0"
echo displayName="Cheat Mod"
echo description="Auto-buy mod for Mineblaze"
echo authors="stepeerbig"
echo logoFile=""
echo.
echo [[dependencies.cheatmod]]
echo     modId="forge"
echo     mandatory=true
echo     versionRange="[36,)"
echo     ordering="NONE"
echo     side="BOTH"
echo.
echo [[dependencies.cheatmod]]
echo     modId="minecraft"
echo     mandatory=true
echo     versionRange="[1.16.5]"
echo     ordering="NONE"
echo     side="BOTH"
) > src\main\resources\META-INF\mods.toml

echo 4. Создаем Gradle Wrapper...
gradle wrapper --gradle-version 7.5.1 --distribution-type bin

echo 5. Удаляем проблемные файлы...
del cheatmod.refmap.json 2>nul
if exist "src\main\java\com\cheatmod\mixin\NetworkManagerMixin.java" (
    echo Проверь NetworkManagerMixin.java - возможно он пустой
)

echo.
echo =========================================
echo Исправления применены!
echo Теперь выполни:
echo   1. gradlew clean
echo   2. gradlew build
echo   3. gradlew runClient
echo =========================================

pause