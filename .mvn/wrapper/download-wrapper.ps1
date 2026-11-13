param([Parameter(Mandatory = $true)][string]$ProjectBaseDir)

$ErrorActionPreference = 'Stop'
$wrapperDirectory = Join-Path $ProjectBaseDir '.mvn/wrapper'
$propertiesPath = Join-Path $wrapperDirectory 'maven-wrapper.properties'
$destination = Join-Path $wrapperDirectory 'maven-wrapper.jar'
$property = Get-Content -LiteralPath $propertiesPath |
    Where-Object { $_ -match '^wrapperUrl=' } |
    Select-Object -First 1
if (-not $property) { throw 'maven-wrapper.properties has no wrapperUrl' }

$url = $property.Substring('wrapperUrl='.Length).Trim()
if ($env:MVNW_REPOURL) {
    $artifactPath = ([uri]$url).AbsolutePath
    if ($artifactPath.StartsWith('/maven2/')) { $artifactPath = $artifactPath.Substring('/maven2'.Length) }
    $url = $env:MVNW_REPOURL.TrimEnd('/') + $artifactPath
}

$temporaryFile = $destination + '.' + [guid]::NewGuid().ToString('N') + '.tmp'
$client = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
    $client.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
try {
    $client.DownloadFile($url, $temporaryFile)
    Move-Item -LiteralPath $temporaryFile -Destination $destination -Force
} finally {
    $client.Dispose()
    if (Test-Path -LiteralPath $temporaryFile) { Remove-Item -LiteralPath $temporaryFile }
}
