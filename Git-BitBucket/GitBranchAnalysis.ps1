param (
    [string]$REPO_URL = "https://bitbucket.org/yourusername/yourrepo.git",
    [string]$LOG_PATH = "C:\logs"
)

function CloneRepository($repoUrl, $repoPath) {
    try {
        git clone $repoUrl $repoPath
        return $true
    } catch {
        Write-Host "Error cloning repository: $_"
        return $false
    }
}

function GetBranchesWithNoCommits() {
    git for-each-ref --format '%(refname:short)' refs/heads/ | ForEach-Object {
        if (git rev-list --count $_ 2>$null -eq 0) {
            $_ | Out-File -Append -FilePath $branchNoCommitLog
        }
    }
}

function GetMergedFeatureBranches() {
    git branch -r --merged release/ | Select-String -Pattern '^  origin/(.*)$' | ForEach-Object {
        $branch = $_.Matches[0].Groups[1].Value
        $lastCommitDate = git log -n 1 --format="%at" $branch 2>$null
        if ($lastCommitDate -and ($lastCommitDate -lt (Get-Date).AddDays(-90))) {
            $branch | Out-File -Append -FilePath $mergedFeatureBranchLog
        }
    }
}

# Set log file paths
$branchNoCommitLog = Join-Path $LOG_PATH "branchwithnocommit.log"
$mergedFeatureBranchLog = Join-Path $LOG_PATH "mergedfeaturebranches.log"

# Clone the repository
$REPO_PATH = Join-Path $env:TEMP "BitbucketRepo"
if (-not (Test-Path $REPO_PATH)) {
    Write-Host "Cloning the repository..."
    if (-not (CloneRepository $REPO_URL $REPO_PATH)) {
        Write-Host "Error cloning repository. Exiting."
        Exit
    }
}

# Navigate to the repository directory
Set-Location $REPO_PATH

# Get branches with no commits
Write-Host "Getting branches with no commits..."
GetBranchesWithNoCommits
Write-Host "Branches with no commits logged to: $branchNoCommitLog"

# Get merged feature branches older than 90 days
Write-Host "Getting merged feature branches older than 90 days..."
GetMergedFeatureBranches
Write-Host "Merged feature branches older than 90 days logged to: $mergedFeatureBranchLog"

Write-Host "Process completed. Check $branchNoCommitLog and $mergedFeatureBranchLog for the results."
