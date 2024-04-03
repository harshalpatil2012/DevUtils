# Define input file and output folder paths
$inputFile = "C:\Logs\500000-Sales-Records.csv"
$outputFolder = "C:\updates"

# Record start time
$startTime = Get-Date

# Initialize counters
$totalRecords = 0
$totalUniqueRecords = 0
$totalDuplicateRecords = 0
$seen = @{}

# Function to process a single line
function ProcessLine {
    param (
        [string]$line
    )

    # Increment total records count
    $global:totalRecords += 1

    # Check if the line is unique
    if ($line -notin $seen.Keys) {
        # Increment unique records count
        $global:totalUniqueRecords += 1
        $seen[$line] = $true
        Add-Content -Path $outputFile -Value $line
    }
    else {
        # Increment duplicate records count
        $global:totalDuplicateRecords += 1
    }
}

# Function to process lines in parallel
function ProcessLinesInParallel {
    param (
        [array]$lines
    )

    foreach ($line in $lines) {
        Start-Job -ScriptBlock { param($line) ProcessLine $line } -ArgumentList $line | Out-Null
    }
}

# Define output file path
$outputFile = Join-Path -Path $outputFolder -ChildPath "$(Split-Path $inputFile -Leaf)_unique.txt"

# Read input file and process lines in parallel
$lines = Get-Content -Path $inputFile
$batchSize = 1000  # Adjust the batch size as needed
for ($i = 0; $i -lt $lines.Count; $i += $batchSize) {
    $batch = $lines[$i..($i + $batchSize - 1)]
    ProcessLinesInParallel $batch
}

# Wait for all background jobs to complete
Get-Job | Wait-Job | Out-Null

# Record end time
$endTime = Get-Date

# Print statistics
Write-Host "Total records: $totalRecords"
Write-Host "Total unique records: $totalUniqueRecords"
Write-Host "Total duplicate records: $totalDuplicateRecords"
Write-Host "Start time: $startTime"
Write-Host "End time: $endTime"

# Calculate and print execution time
$executionTime = New-TimeSpan -Start $startTime -End $endTime
Write-Host "Total execution time: $($executionTime.TotalSeconds) seconds"
