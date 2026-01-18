@echo off
echo =============================================
echo    ЗАПУСК EXCEL ПАРСЕРА - ПРОСТОЙ СПОСОБ
echo =============================================
echo.

REM 1. Проверяем наличие файла Excel
if not exist "config_items.xlsx" (
    echo [ОШИБКА] Файл config_items.xlsx не найден!
    echo.
    echo Создайте Excel файл или переместите его в папку:
    echo %CD%
    echo.
    pause
    exit
)

echo [OK] Файл Excel найден
echo.

REM 2. Компилируем проект
echo [1/3] Компиляция проекта...
call gradlew compileJava --console=plain

if errorlevel 1 (
    echo [ОШИБКА] Не удалось скомпилировать проект
    pause
    exit
)

echo [OK] Проект скомпилирован
echo.

REM 3. Запускаем парсер
echo [2/3] Запуск парсера...
echo.

java -cp "build/classes/java/main;build/resources/main;C:\Users\zopalka\.gradle\caches\modules-2\files-2.1\com.google.code.gson\gson\2.8.9\*;C:\Users\zopalka\.gradle\caches\modules-2\files-2.1\org.apache.poi\poi\4.1.2\*;C:\Users\zopalka\.gradle\caches\modules-2\files-2.1\org.apache.poi\poi-ooxml\4.1.2\*" com.cheatmod.parser.ExcelConfigParser

echo.
echo [3/3] Готово!
echo.

REM 4. Проверяем результат
if exist "market.json" (
    echo [УСПЕХ] Файл market.json создан!
    echo.
    type market.json | more
) else (
    echo [ВНИМАНИЕ] Файл market.json не создан
)

pause