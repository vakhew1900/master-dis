param (
    [Parameter(Mandatory=$true)]
    [string]$StudentPath,
    [Parameter(Mandatory=$true)]
    [string]$ReferencePath,
    [string]$OutputPath = "report.html"
)

# Resolve absolute paths
$StudentAbsPath = [System.IO.Path]::GetFullPath($StudentPath)
$ReferenceAbsPath = [System.IO.Path]::GetFullPath($ReferencePath)

Write-Host "Comparing student repo at: $StudentAbsPath" -ForegroundColor Cyan
Write-Host "Against reference repo at: $ReferenceAbsPath" -ForegroundColor Cyan
Write-Host "Output will be: $OutputPath" -ForegroundColor Yellow

mvn compile exec:java -Dexec.mainClass="org.master.diploma.git.GitGraphComparisonApp" -Dexec.args="`"$StudentAbsPath`" `"$ReferenceAbsPath`" `"$OutputPath`""
