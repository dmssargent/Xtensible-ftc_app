@echo off
for /f "tokens=* delims=" %%a in ('dir "*.html" /s /b') do (
type %%a
)