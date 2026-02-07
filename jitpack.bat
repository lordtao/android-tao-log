@echo off
setlocal
chcp 65001 > nul

set "VERSION=2.3.0"

echo Текущая версия для тега: %VERSION%
set /p "confirmation=Это правильная версия? (y/n): "

if /i "%confirmation%"=="y" (
    echo.
    echo Создание git-тега %VERSION%
    git tag %VERSION%
    if errorlevel 1 (
        echo.
        echo Ошибка при создании тега. Возможно, тег %VERSION% уже существует.
        pause
        exit /b 1
    )
    echo.
    echo Отправка тега %VERSION% на origin
    git push origin %VERSION%
    if errorlevel 1 (
        echo.
        echo Ошибка при отправке тега на origin.
        pause
        exit /b 1
    )
    echo.
    echo Тег %VERSION% успешно создан и отправлен.
) else (
    echo.
    echo Операция отменена пользователем.
)

echo.
pause
