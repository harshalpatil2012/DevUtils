param (
    [string]$REPO_PATH = "C:\path\to\your\repo",
    [string]$LOG_PATH = "C:\path\to\your\logs"
)

# Function to check if a branch has no commits
function HasNoCommits($branch) {
    $commitCount = git rev-list --count $branch 2>$null
    return $commitCount -eq 0
}

# Function to check if a branch is merged to release/ and is older than 1 year
function IsMergedAndOld($branch) {
    $lastCommitDate = git log -n 1 --format="%at" $branch 2>$null
    $currentDate = Get-Date -Format "yyyy-MM-ddTHH:mm:ss"
    $oneYearAgo = (Get-Date).AddYears(-1).ToString("yyyy-MM-ddTHH:mm:ss")
    return ($lastCommitDate -lt $oneYearAgo) -and ($lastCommitDate -ne $null)
}

# Set log file paths
$branchNoCommitLog = Join-Path $LOG_PATH "branchwithnocommit.log"
$mergedFeatureBranchLog = Join-Path $LOG_PATH "mergedfeaturebranches.log"

# Get branches with no commits
Write-Host "Getting branches with no commits..."
git for-each-ref --format '%(refname:short)' refs/heads/ | ForEach-Object {
    if (HasNoCommits $_) {
        $_ | Out-File -Append -FilePath $branchNoCommitLog
    }
}
Write-Host "Branches with no commits logged to: $branchNoCommitLog"

# Get merged feature branches older than 1 year
Write-Host "Getting merged feature branches older than 1 year..."
git branch -r --merged release/ | Select-String -Pattern '^  origin/(.*)$' | ForEach-Object {
    $branch = $_.Matches[0].Groups[1].Value
    if (IsMergedAndOld $branch) {
        $branch | Out-File -Append -FilePath $mergedFeatureBranchLog
    }
}
Write-Host "Merged feature branches older than 1 year logged to: $mergedFeatureBranchLog"

Write-Host "Process completed. Check $branchNoCommitLog and $mergedFeatureBranchLog for the results."
