$apk = "app/build/outputs/apk/open/release/app-open-release.apk"
$buildToolsPaths = @(
    "$env:LOCALAPPDATA\Android\Sdk\build-tools",
    "C:\Android\Sdk\build-tools"
)
$apksigner = (Get-ChildItem -Path $buildToolsPaths -Filter "apksigner.bat" -Recurse -ErrorAction SilentlyContinue | Sort-Object FullName -Descending | Select-Object -First 1).FullName
Write-Host "Using apksigner: $apksigner"
Write-Host "APK target: $apk"
Write-Host "--- Signature Verification Output ---"
& $apksigner verify --verbose --print-certs $apk
Write-Host "--- APK File Details ---"
$hash = Get-FileHash $apk -Algorithm SHA256
Write-Host "APK SHA-256: $($hash.Hash)"
$item = Get-Item $apk
Write-Host "APK Size (bytes): $($item.Length)"
