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


Test Scenarios:
Pagination within OpsData only:

Scenario: Fetch a page where all results are from OpsData.
Pagination within SdkData only:

Scenario: Fetch a page where all results are from SdkData.
Pagination spanning both OpsData and SdkData:

Scenario: Fetch a page where results span from OpsData to SdkData.
Empty OpsData and filled SdkData:

Scenario: Fetch a page when OpsData is empty but SdkData contains data.
Empty SdkData and filled OpsData:

Scenario: Fetch a page when SdkData is empty but OpsData contains data.
Both OpsData and SdkData are empty:

Scenario: Fetch a page when both tables are empty.
Exact page size results from OpsData:

Scenario: Fetch a page where the number of results from OpsData exactly matches the page size.
Exact page size results from SdkData:

Scenario: Fetch a page where the number of results from SdkData exactly matches the page size.
Invalid pagination parameters:

Scenario: Handle cases with invalid pagination parameters like negative page numbers or page sizes.


Introduction
The purpose of this document is to provide a high-level design for an API that fetches data from two different sources, combines the results, and supports pagination. The data is fetched from OpsDataRepository and SdkDataRepository, combined, and returned as a paginated list of DataDTO objects.

Requirements
Fetch Data from Two Repositories: The API should fetch data from both OpsDataRepository and SdkDataRepository.
Combine Data: The fetched data should be combined into a single list.
Support Pagination: The combined data should support pagination, allowing clients to request specific pages of data.
Parallel Data Fetching: Data fetching operations from both repositories should be executed in parallel to improve performance.
Error Handling: The API should handle any errors that occur during data fetching and combining.
Components
Controller Layer

DataController: Exposes the endpoint to retrieve the combined data with pagination support.
Service Layer

DataService: Handles the business logic of fetching data from both repositories, combining the results, and handling pagination.
Repository Layer

OpsDataRepository: Fetches data from the opsdata table.
SdkDataRepository: Fetches data from the sdkdata table.
DTOs and Projections

DataDTO: Data Transfer Object for returning combined data.
DataProjection: Interface for projecting data from the repositories.
