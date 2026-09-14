if (!(Test-Path "apk_artifacts")) {
    New-Item -ItemType Directory -Path "apk_artifacts"
}

$src = "app/build/outputs/apk/open/release/app-open-release.apk"
$dest = "apk_artifacts/WPS-A-Tester-v1.0.0.apk"
Copy-Item -Force $src $dest

$hash = (Get-FileHash $dest -Algorithm SHA256).Hash
$checksumLine = "$hash  WPS-A-Tester-v1.0.0.apk"
Set-Content -Path "apk_artifacts/SHA256SUMS.txt" -Value $checksumLine

Write-Host "Artifact ready: $dest"
Write-Host "Checksum line in SHA256SUMS.txt: $checksumLine"
$item = Get-Item $dest
Write-Host "Size: $($item.Length) bytes"
