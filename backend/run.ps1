# Arranca la API cargando backend/.env como variables de entorno.
# Uso: .\run.ps1
$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot ".env"
if (-not (Test-Path $envFile)) {
    Write-Host "No existe backend/.env - copia .env.example y completa tus credenciales de Supabase." -ForegroundColor Yellow
    exit 1
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { return }
    if ($line -match '^([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') {
        $key = $Matches[1]
        $value = $Matches[2].Trim().Trim('"').Trim("'")
        [Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  $key cargado" -ForegroundColor DarkGray
    }
}

Set-Location $PSScriptRoot
mvn spring-boot:run
