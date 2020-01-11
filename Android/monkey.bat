echo Pin screen first
pause
adb shell monkey -p es.unizar.eina.notepadvT -v 5000
adb shell am task lock stop