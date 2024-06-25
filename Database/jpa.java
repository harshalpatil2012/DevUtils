@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    @Query(value = "SELECT e.num, e.country, " +
           "CAST(LISTAGG(e.service, ',') WITHIN GROUP (ORDER BY e.service) AS CLOB) as services " +
           "FROM enrollment e " +
           "GROUP BY e.num, e.country", 
           nativeQuery = true)
    List<Object[]> getEnrollmentsWithServices();
}

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    @Query(value = "SELECT e.num AS num, e.country AS country, " +
           "CAST(LISTAGG(e.service, ',') WITHIN GROUP (ORDER BY e.service) AS CLOB) AS services " +
           "FROM enrollment e " +
           "GROUP BY e.num, e.country", 
           nativeQuery = true)
    List<ProjectProjetViewDTO> getEnrollmentsWithServices();
}
// DTO for the aggregated result
public class AggregatedServiceDTO {
    private String num;
    private String country;
    private List<String> services;

    // Constructor, getters, etc.
}

@Repository
public interface YourEntityRepository extends JpaRepository<YourEntityName, Long> {

    @Query("SELECT new your.package.AggregatedServiceDTO(n.num, n.country, " +
           "       LISTAGG(n.service, ',') WITHIN GROUP (ORDER BY n.service)) " +
           "FROM YourEntityName n " +
           "GROUP BY n.num, n.country")
    List<AggregatedServiceDTO> findAggregatedServices();
}


@Repository
public interface YourEntityRepository extends JpaRepository<YourEntityName, Long> {

    @Query(value = "SELECT num, country, CAST(COLLECT(service) AS service_table) AS services " +
                   "FROM YourEntityName " +
                   "GROUP BY num, country", nativeQuery = true)
    List<Object[]> findAggregatedServicesRaw(); // Returns raw data

    default List<ProjView> findAggregatedServices() {
        return findAggregatedServicesRaw().stream()
            .map(result -> {
                String num = (String) result[0];
                String country = (String) result[1];
                // Extract and join the services from the nested table
                String[] servicesArray = (String[]) ((Array) result[2]).getArray();
                String services = String.join(",", servicesArray);

                return new ProjView(num, country, services);
            })
            .collect(Collectors.toList());
    }
}
