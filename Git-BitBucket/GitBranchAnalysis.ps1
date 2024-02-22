param (
    [string[]]$REPO_URLS = @("https://bitbucket.org/yourusername/repo1.git", "https://bitbucket.org/yourusername/repo2.git"),
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

function GetBranchesWithNoCommits($repoName) {
    git for-each-ref --format '%(refname:short)' refs/heads/ | ForEach-Object {
        if (git rev-list --count $_ 2>$null -eq 0) {
            $_ | Out-File -Append -FilePath "$branchNoCommitLog-$repoName"
        }
    }
}

function GetMergedFeatureBranches($repoName) {
    git branch -r --merged release/ | Select-String -Pattern '^  origin/(.*)$' | ForEach-Object {
        $branch = $_.Matches[0].Groups[1].Value
        $lastCommitDate = git log -n 1 --format="%at" $branch 2>$null
        if ($lastCommitDate -and ($lastCommitDate -lt (Get-Date).AddDays(-90))) {
            $branch | Out-File -Append -FilePath "$mergedFeatureBranchLog-$repoName"
        }
    }
}

foreach ($repoUrl in $REPO_URLS) {
    # Extract repository name from the URL
    $repoName = ($repoUrl -split '/')[3]

    # Set log file paths
    $branchNoCommitLog = Join-Path $LOG_PATH "branchwithnocommit.log"
    $mergedFeatureBranchLog = Join-Path $LOG_PATH "mergedfeaturebranches.log"

    # Clone the repository
    $REPO_PATH = Join-Path $env:TEMP $repoName
    if (-not (Test-Path $REPO_PATH)) {
        Write-Host "Cloning the repository $repoName..."
        if (-not (CloneRepository $repoUrl $REPO_PATH)) {
            Write-Host "Error cloning repository $repoName. Skipping to the next repository."
            continue
        }
    }

    # Navigate to the repository directory
    Set-Location $REPO_PATH

    # Get branches with no commits
    Write-Host "Getting branches with no commits for $repoName..."
    GetBranchesWithNoCommits $repoName
    Write-Host "Branches with no commits logged to: $branchNoCommitLog-$repoName"

    # Get merged feature branches older than 90 days
    Write-Host "Getting merged feature branches older than 90 days for $repoName..."
    GetMergedFeatureBranches $repoName
    Write-Host "Merged feature branches older than 90 days logged to: $mergedFeatureBranchLog-$repoName"

    Write-Host "Process completed for $repoName. Check $branchNoCommitLog-$repoName and $mergedFeatureBranchLog-$repoName for the results."
}
