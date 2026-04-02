# Configuration JDK 17 pour ce projet uniquement
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Write-Host "JAVA_HOME set to $env:JAVA_HOME" -ForegroundColor Green

