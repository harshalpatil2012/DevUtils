param (
  [Parameter(Mandatory = $true)]
  [string] $RootPath,
  [Parameter(Mandatory = $true)]
  [string] $LogPath
)

function GetBranchesWithNoCommits($repoPath, $repoName) {
  $branches = git for-each-ref --format '%(refname:short)' refs/heads/ -Path $repoPath
  if (!$branches) {
    Write-Verbose "No branches found in $repoName."
    return
  }

  foreach ($branch in $branches) {
    if (!(git rev-parse --verify $branch -Path $repoPath)) {
      $branch | Out-File -Append -FilePath Join-Path $LogPath "branchwithnocommit.log-$repoName"
    }
  }
}

function GetMergedFeatureBranches($repoPath, $repoName) {
  $mergedBranches = git branch -r --merged --contains "release/" -Path <span class="math-inline">repoPath \| Select\-String \-Pattern '^ origin/\(\.\*\)</span>'
  if (!$mergedBranches) {
    Write-Verbose "No merged branches found in $repoName."
    return
  }

  foreach ($branchMatch in $mergedBranches.Matches) {
    $branch = $branchMatch.Groups[1].Value
    $lastCommitDate = git log -n 1 --format="%at" $branch -Path $repoPath 2>$null
    if ($lastCommitDate -and ($lastCommitDate -lt (Get-Date).AddDays(-90))) {
      $branch | Out-File -Append -FilePath Join-Path $LogPath "mergedfeaturebranches.log-$repoName"
    }
  }
}

# Set log file paths
$branchNoCommitLog = Join-Path $LogPath "branchwithnocommit.log"
$mergedFeatureBranchLog = Join-Path $LogPath "mergedfeaturebranches.log"

# Get a list of child folders (repositories) within the root folder
$repoFolders = Get-ChildItem -Path $RootPath -Directory

foreach ($repoFolder in $repoFolders) {
  # Set the current repository path
  $repoPath = Join-Path $RootPath $repoFolder.Name

  # Check if it's a Git repository
  if (!(Test-Path (Join-Path $repoPath ".git"))) {
    Write-Verbose "Skipping $repoFolder.Name - Not a Git repository."
    continue
  }

  # Extract repository name from the folder
  $repoName = $repoFolder.Name

  # Get latest updates for the repository
  Write-Verbose "Updating repository $repoName..."

  try {
    git pull -Path $repoPath
  } catch {
    Write-Error "Error updating repository $repoName: $_"
    continue
  }

  # Get branches with no commits
  Write-Verbose "Getting branches with no commits for $repoName..."
  GetBranchesWithNoCommits $repoPath $repoName

  # Get merged feature branches older than 90 days
  Write-Verbose "Getting merged feature branches older than 90 days for $repoName..."
  GetMergedFeatureBranches
