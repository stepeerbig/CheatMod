@echo off
echo Обновление репозитория на GitHub...
echo.

echo 1. Проверяем статус изменений...
git status

echo.
echo 2. Добавляем ВСЕ изменения...
git add .

echo.
echo 3. Фиксируем изменения...
set /p commit_msg="Введите описание изменений: "
git commit -m "%commit_msg%"

echo.
echo 4. Отправляем на GitHub...
git push origin main

echo.
echo ✓ Изменения успешно отправлены на GitHub!
echo.
pause