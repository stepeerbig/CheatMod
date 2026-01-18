@echo off
echo Запуск CheatMod для тестирования...
echo.

REM Компилируем проект
call gradlew build

REM Копируем конфиги если нужно
if not exist "config/cheatmod" mkdir config\cheatmod
if not exist "config/cheatmod/market.json" (
    echo Создаю market.json...
    echo {} > config\cheatmod\market.json
)

if not exist "config/cheatmod/telegram.json" (
    echo Создаю telegram.json...
    echo {"botToken":"7813109373:AAHrdMxnKylyBPZIEKGNfSq2B7ZhYw36tQE","chatId":"-5180834846","enabled":true} > config\cheatmod\telegram.json
)

echo.
echo Готово! Запускайте Minecraft с установленным модом.
echo Управление:
echo - Правый Shift: открыть GUI управления
echo - M: вкл/выкл Market AutoBuy
echo - P: пауза
pause