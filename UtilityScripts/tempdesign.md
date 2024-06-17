@startuml

package "Repository Layer" {
    interface OpsDataRepository {
        + findAllProjected(pageable: Pageable): List<DataProjection>
    }
    
    interface SdkDataRepository {
        + findAllProjected(pageable: Pageable): List<DataProjection>
    }
}

package "Service Layer" {
    class DataDTO {
        - Long id
        - String name
        - String value
        + DataDTO(Long id, String name, String value)
        + getId(): Long
        + getName(): String
        + getValue(): String
        + setId(Long id): void
        + setName(String name): void
        + setValue(String value): void
    }

    interface DataProjection {
        + getId(): Long
        + getName(): String
        + getValue(): String
    }

    class DataService {
        - OpsDataRepository opsDataRepository
        - SdkDataRepository sdkDataRepository
        + DataService(OpsDataRepository opsDataRepository, SdkDataRepository sdkDataRepository)
        + fetchOpsData(Pageable pageable): CompletableFuture<List<DataProjection>>
        + fetchSdkData(Pageable pageable): CompletableFuture<List<DataProjection>>
        + getCombinedData(Pageable pageable): Page<DataDTO>
    }

    DataService "1" *-- "1" OpsDataRepository
    DataService "1" *-- "1" SdkDataRepository
    DataService "1" *-- "1" DataDTO
    DataService "1" *-- "1" DataProjection
}

package "Controller Layer" {
    class DataController {
        - DataService dataService
        + getAllData(page: int, size: int): ResponseEntity<Page<DataDTO>>
    }

    DataController "1" *-- "1" DataService
}

@enduml



The getCombinedData method is responsible for fetching data from two different repositories (OpsDataRepository and SdkDataRepository), combining the results, and handling pagination across the combined dataset. Here’s a step-by-step explanation of the logic:

1. Parallel Data Fetching


CompletableFuture<List<DataProjection>> opsDataFuture = fetchOpsData(pageable);
CompletableFuture<List<DataProjection>> sdkDataFuture = fetchSdkData(pageable);
Parallel Execution: The method begins by making two asynchronous calls to fetch data from OpsDataRepository and SdkDataRepository. These calls are made in parallel using CompletableFuture.
2. Synchronization and Error Handling


List<DataProjection> opsDataList = new ArrayList<>();
List<DataProjection> sdkDataList = new ArrayList<>();

try {
    opsDataList = opsDataFuture.get();
    sdkDataList = sdkDataFuture.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
Fetching Results: The results of these asynchronous calls are obtained using the get() method of CompletableFuture. This method blocks until the data is available.
Error Handling: Any exceptions thrown during the data fetch are caught and logged. This ensures the method continues execution even if one of the fetch operations fails.
3. Calculating Total Records


int totalOpsData = opsDataList.size();
int totalSdkData = sdkDataList.size();
Total Count: The total number of records fetched from each repository is calculated. This helps in determining the overall size of the combined dataset.
4. Pagination Logic


List<DataDTO> combinedDataList = new ArrayList<>();

int start = pageable.getPageNumber() * pageable.getPageSize();
int end = Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), totalOpsData + totalSdkData);

for (int i = start; i < end; i++) {
    if (i < totalOpsData) {
        DataProjection opsData = opsDataList.get(i);
        combinedDataList.add(new DataDTO(opsData.getId(), opsData.getName(), opsData.getValue()));
    } else {
        DataProjection sdkData = sdkDataList.get(i - totalOpsData);
        combinedDataList.add(new DataDTO(sdkData.getId(), sdkData.getName(), sdkData.getValue()));
    }
}
Calculate Start and End Indexes: The start and end indexes for the current page are calculated based on the page number and page size.
start: The starting index for the current page.
end: The ending index, ensuring it does not exceed the total number of records.
Iterate and Combine Data:
The loop iterates from the start index to the end index.
If the index is within the range of opsDataList, it adds the corresponding DataProjection from opsDataList to the combined data list.
If the index exceeds the range of opsDataList, it fetches the corresponding DataProjection from sdkDataList.
5. Return Paginated Result


return new PageImpl<>(combinedDataList, pageable, totalOpsData + totalSdkData);
Create Page: A PageImpl object is created using the combined data list, the pageable object, and the total number of records.
Return Page: This PageImpl object is returned, representing the current page of the combined dataset.
Summary
Parallel Fetching: Data is fetched from both repositories in parallel using CompletableFuture and @Async.
Synchronization: The results are synchronized using the get() method of CompletableFuture.
Pagination: The combined dataset is paginated based on the requested page number and page size, ensuring seamless pagination across both datasets.
Error Handling: Exceptions during data fetching are caught and logged, ensuring robustness.



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DataService {

    private final OpsDataRepository opsDataRepository;
    private final SdkDataRepository sdkDataRepository;

    public DataService(OpsDataRepository opsDataRepository, SdkDataRepository sdkDataRepository) {
        this.opsDataRepository = opsDataRepository;
        this.sdkDataRepository = sdkDataRepository;
    }

    @Async
    public CompletableFuture<List<DataProjection>> fetchOpsData(Pageable pageable) {
        return CompletableFuture.completedFuture(opsDataRepository.findAllProjected(pageable));
    }

    @Async
    public CompletableFuture<List<DataProjection>> fetchSdkData(Pageable pageable) {
        return CompletableFuture.completedFuture(sdkDataRepository.findAllProjected(pageable));
    }

    public Page<DataDTO> getCombinedData(Pageable pageable) {
        CompletableFuture<List<DataProjection>> opsDataFuture = fetchOpsData(pageable);
        CompletableFuture<List<DataProjection>> sdkDataFuture = fetchSdkData(pageable);

        List<DataProjection> opsDataList = new ArrayList<>();
        List<DataProjection> sdkDataList = new ArrayList<>();

        try {
            opsDataList = opsDataFuture.get();
            sdkDataList = sdkDataFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        int totalOpsData = opsDataList.size();
        int totalSdkData = sdkDataList.size();

        List<DataDTO> combinedDataList = new ArrayList<>();

        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), totalOpsData + totalSdkData);

        for (int i = start; i < end; i++) {
            if (i < totalOpsData) {
                DataProjection opsData = opsDataList.get(i);
                combinedDataList.add(new DataDTO(opsData.getId(), opsData.getName(), opsData.getValue()));
            } else {
                DataProjection sdkData = sdkDataList.get(i - totalOpsData);
                combinedDataList.add(new DataDTO(sdkData.getId(), sdkData.getName(), sdkData.getValue()));
            }
        }

        return new PageImpl<>(combinedDataList, pageable, totalOpsData + totalSdkData);
    }
}


Test Scenarios for DataService
Pagination within OpsData only:

Test fetching a page where all results are from OpsData.
Pagination within SdkData only:

Test fetching a page where all results are from SdkData.
Pagination spanning both OpsData and SdkData:

Test fetching a page where results span from OpsData to SdkData.
Empty OpsData and filled SdkData:

Test fetching a page when OpsData is empty but SdkData contains data.
Empty SdkData and filled OpsData:

Test fetching a page when SdkData is empty but OpsData contains data.
Both OpsData and SdkData are empty:

Test fetching a page when both tables are empty.
Exact page size results from OpsData:

Test fetching a page where the number of results from OpsData exactly matches the page size.
Exact page size results from SdkData:

Test fetching a page where the number of results from SdkData exactly matches the page size.
Test Scenarios for DataController
Get data with default pagination parameters:

Test fetching data with default page and size parameters.
Get data with specific pagination parameters:

Test fetching data with specific page and size parameters.
Get data when OpsData is empty:

Test fetching data when there are no results in OpsData.
Get data when SdkData is empty:

Test fetching data when there are no results in SdkData.
Get data when both OpsData and SdkData are empty:

Test fetching data when both tables are empty.
Handle pagination spanning across OpsData and SdkData:

Test fetching data that spans across both tables to verify pagination logic.
Invalid pagination parameters:

Test handling of invalid pagination parameters, such as negative page numbers or page sizes.
