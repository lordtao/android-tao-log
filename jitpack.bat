@echo off
setlocal
chcp 65001 > nul

set "MAJOR=2"
set "MINOR=3"
set "PATCH=14"
set "VERSION=%MAJOR%.%MINOR%.%PATCH%"

echo Текущая версия для тега: %VERSION%
set /p "confirmation=Это правильная версия? (y/n): "

if /i not "%confirmation%"=="y" goto :cancel

echo.
set "commit_msg=Update version to %VERSION%"
echo Введите описание коммита (нажмите Enter для сообщения по умолчанию "%commit_msg%"):
set /p "user_msg=> "
if not "%user_msg%"=="" set "commit_msg=%user_msg%"

echo.
echo Обновление версии в библиотечном build.gradle.kts...

rem Используем PowerShell для замены значений в файле
powershell -Command "$path = 'logger/build.gradle.kts'; (Get-Content $path) -replace 'val versionMajor = \d+', 'val versionMajor = %MAJOR%' -replace 'val versionMinor = \d+', 'val versionMinor = %MINOR%' -replace 'val versionPatch = \d+', 'val versionPatch = %PATCH%' | Set-Content $path -Encoding UTF8"

echo.
echo Фиксация изменений в git...
git add .
git commit -m "%commit_msg%"

echo.
echo Создание git-тега %VERSION%
git tag %VERSION%
if errorlevel 1 (
    echo.
    echo Ошибка при создании тега. Возможно, тег %VERSION% уже существует.
    goto :end
)

echo.
echo Отправка тега %VERSION% и коммита на origin
git push origin %VERSION%
git push
if errorlevel 1 (
    echo.
    echo Ошибка при отправке данных на origin.
    goto :end
)

echo.
echo Тег %VERSION% и изменения успешно отправлены.
goto :end

:cancel
echo.
echo Операция отменена пользователем.

:end
echo.
pause
