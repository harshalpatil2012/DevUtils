@RestController 
public class DataSyncController {

    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @GetMapping("/loadAll")
    public Mono<Void> triggerDataSync() {
        dataSyncService.loadAllBooksIntoRedis();
        return Mono.empty();
    }
}


@Service
@EnableScheduling
public class DataSyncService {

    private final BookRepository bookRepository;
    private final ReactiveRedisTemplate<String, Book> redisTemplate;

    public DataSyncService(BookRepository bookRepository, ReactiveRedisTemplate<String, Book> redisTemplate) {
        this.bookRepository = bookRepository;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void loadAllBooksIntoRedis() {
        Flux.defer(() -> calculateTotalPages(50000))
                .flatMapMany(page -> bookRepository.findAllBy(PageRequest.of(page, 50000))
                        .flatMap(book -> redisTemplate.opsForValue().set("book:" + book.getId(), book)
                                .then(redisTemplate.opsForSet().add("book:ids", "book:" + book.getId()))))
                .then(syncRemovedBooks())
                .subscribe();
    }

    private Flux<Integer> calculateTotalPages(int pageSize) {
        return bookRepository.count()
                .map(total -> (int) Math.ceil((double) total / pageSize));
    }

    private Mono<Void> syncRemovedBooks() {
        return redisTemplate.opsForSet()
                .members("book:ids")
                .collectList()
                .flatMapMany(existingIds -> {
                    return bookRepository.findAll()
                            .map(book -> "book:" + book.getId())
                            .collect(Collectors.toSet())
                            .map(currentIds -> {
                                existingIds.removeAll(currentIds);
                                return existingIds;
                            })
                            .flatMapIterable(keysToDelete -> keysToDelete)
                            .flatMap(redisTemplate.opsForValue()::delete);
                });
    }
}


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}


@Entity
@Table(name = "books")
public class Book {
    @Id
    private Long id;
    private String title;
    private String author;
    private String status; // ... other fields

    // Getters and setters
}

Explanation of the Consolidated Code for very large number of Book Data Synchronization from Oracle DB to Redis Instance
This code addresses the scenario where you want to periodically synchronize a large dataset (200 million books) from a database (represented by JPA and a Book entity) to Redis in batches (50k) while considering deleted books.

Here's a breakdown of the code and its functionalities:

1. Book Entity (Book.java)

This file defines the Book entity class with its properties (id, title, author, status, etc.). This class represents the data structure for your book records.

2. Book Repository (BookRepository.java)

This file defines the BookRepository interface that extends JpaRepository<Book, Long>. This leverages Spring Data JPA to provide methods for interacting with the database table containing book information.

3. Data Synchronization Service (DataSyncService.java)

This file contains the core logic for data synchronization:

@Scheduled Annotation: Schedules the loadAllBooksIntoRedis method to run periodically (e.g., every minute in this case).

loadAllBooksIntoRedis Method:

Flux.defer(() -> calculateTotalPages(50000)): Optimizes by calculating the total number of pages needed only once using calculateTotalPages.
flatMapMany: Processes a stream of page numbers and fetches data in batches using bookRepository.findAllBy(PageRequest.of(page, 50000)).
Processing Individual Books: Iterates over each book and performs the following actions:
Sets the book data in Redis with the key "book:" + book.getId().
Adds the key to the Redis set "book:ids" to track all book IDs currently in Redis.
then(syncRemovedBooks()): After processing all pages, calls the syncRemovedBooks method to identify and delete entries from Redis that no longer exist in the database.
calculateTotalPages Method: Calculates the total number of pages required to retrieve all books in batches of 50k.

syncRemovedBooks Method:

Fetches all existing book IDs from the Redis set "book:ids".
Retrieves all current book IDs from the database and collects them into a set.
Identifies keys present in Redis but missing from the current database data (deleted books).
Deletes those identified keys from Redis using redisTemplate.opsForValue()::delete.
4. Data Synchronization Controller (Optional - DataSyncController.java)

This file (optional) provides a REST endpoint (/loadAll) to manually trigger the data synchronization process using the dataSyncService.

Key Points

Reactive Programming: The code utilizes Project Reactor's Flux and Mono for asynchronous and non-blocking data processing.
Pagination: It retrieves data from the database in batches to handle the large dataset efficiently.
Deleted Book Handling: It identifies and removes books from Redis that have been marked as deleted in the database.
Error Handling: While not explicitly shown here, implementing robust error handling for database and Redis operations is crucial for a production environment.
