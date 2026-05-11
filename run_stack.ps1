$ErrorActionPreference = 'Stop'
# 1️⃣ Clean Docker storage
.\prune_docker.ps1

# 2️⃣ Build & start the stack (detached)
Write-Host "Building and starting Docker stack..."
docker compose up --build -d

# 3️⃣ Wait for health checks (max 2 minutes)
$services = @('tool95-postgres','tool95-redis','tool95-backend','tool95-ai-service','tool95-frontend')
$deadline = (Get-Date).AddMinutes(2)
while((Get-Date) -lt $deadline){
    $allHealthy = $true
    foreach($svc in $services){
        $status = docker inspect -f '{{.State.Health.Status}}' $svc 2>$null
        if($status -ne 'healthy' -and $status -ne $null){ $allHealthy = $false }
    }
    if($allHealthy){ break }
    Start-Sleep -Seconds 5
}
if($allHealthy){
    Write-Host "All services are healthy."
} else {
    Write-Host "Some services did not become healthy. Check logs with 'docker compose logs <service>'."
}
