$apk = "app/build/outputs/apk/open/release/app-open-release.apk"
$buildTools = "C:\Users\temp\AppData\Local\Android\Sdk\build-tools\36.0.0\aapt2.exe"
& $buildTools dump badging $apk
