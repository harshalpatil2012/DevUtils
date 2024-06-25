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
