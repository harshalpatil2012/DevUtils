param (
    [string]$ROOT_PATH = "C:\path\to\root\folder",
    [string]$LOG_PATH = "C:\logs"
)

function GetBranchesWithNoCommits($repoName) {
    $branches = git for-each-ref --format '%(refname:short)' refs/heads/
    if ($branches -eq $null) {
        Write-Host "No branches found in $repoName."
        return
    }

    foreach ($branch in $branches) {
        if (git rev-list --count $branch 2>$null -eq 0) {
            $branch | Out-File -Append -FilePath "$branchNoCommitLog-$repoName"
        }
    }
}

function GetMergedFeatureBranches($repoName) {
    $mergedBranches = git branch -r --merged release/ | Select-String -Pattern '^  origin/(.*)$'
    if ($mergedBranches -eq $null) {
        Write-Host "No merged branches found in $repoName."
        return
    }

    foreach ($branchMatch in $mergedBranches.Matches) {
        $branch = $branchMatch.Groups[1].Value
        $lastCommitDate = git log -n 1 --format="%at" $branch 2>$null
        if ($lastCommitDate -and ($lastCommitDate -lt (Get-Date).AddDays(-90))) {
            $branch | Out-File -Append -FilePath "$mergedFeatureBranchLog-$repoName"
        }
    }
}

# Set log file paths
$branchNoCommitLog = Join-Path $LOG_PATH "branchwithnocommit.log"
$mergedFeatureBranchLog = Join-Path $LOG_PATH "mergedfeaturebranches.log"

# Get a list of child folders (repositories) within the root folder
$repoFolders = Get-ChildItem -Path $ROOT_PATH -Directory

foreach ($repoFolder in $repoFolders) {
    # Set the current repository path
    $REPO_PATH = Join-Path $ROOT_PATH $repoFolder.Name
    Set-Location $REPO_PATH

    # Check if it's a Git repository
    if (-not (Test-Path (Join-Path $REPO_PATH ".git"))) {
        Write-Host "Skipping $repoFolder.Name - Not a Git repository."
        continue
    }

    # Extract repository name from the folder
    $repoName = $repoFolder.Name

    # Get latest updates for the repository
    Write-Host "Updating repository $repoName..."
    git pull

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
