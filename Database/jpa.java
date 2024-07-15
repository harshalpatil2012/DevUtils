Dr
XMLAGG(XMLELEMENT(E, e.service || ',') ORDER BY e.service).EXTRACT('//text()')


@Transient
    private String features;

    // Getters and setters

    public List<String> getFeatureList() {
        return features != null ? Arrays.asList(features.split(",")) : new ArrayList<>();
    }

 @Query(value = "SELECT e.*, " +
           "LISTAGG(s.feature, ',') WITHIN GROUP (ORDER BY s.feature) AS features " +
           "FROM EnrolmentEntity e " +
           "LEFT JOIN Service s ON e.bnum = s.bnum " +
           "GROUP BY e.enrollementId, e.bnum, e.Table_ID1, e.Table_ID2, e.Table_ID3",
           countQuery = "SELECT COUNT(DISTINCT e.enrollementId) FROM EnrolmentEntity e",
           nativeQuery = true)
    Page<Object[]> findAllWithFeatures(Pageable pageable);

 public Page<EnrolmentEntity> findAllWithFeatures(Pageable pageable) {
        Page<Object[]> result = enrolmentRepository.findAllWithFeatures(pageable);
        
        List<EnrolmentEntity> enrollments = result.getContent().stream().map(row -> {
            EnrolmentEntity enrollment = (EnrolmentEntity) row[0];
            enrollment.setFeatures((String) row[1]);
            return enrollment;
        }).collect(Collectors.toList());

        return new PageImpl<>(enrollments, pageable, result.getTotalElements());
    }


           public void loadFeatures() {
        int pageNumber = 0;
        int pageSize = 10000;  // Main batch size
        int parallelBatchSize = 500; // Batch size for parallel feature fetching

        // Fetch the first page to enter the loop initially
        Page<EnrolmentEntity> enrolmentPage = enrolmentRepository.findAll(PageRequest.of(pageNumber, pageSize));
        
        do {
            // Process enrolments in parallel batches
            IntStream.range(0, (int) Math.ceil((double) enrolmentPage.getNumberOfElements() / parallelBatchSize))
                .parallel()
                .forEach(batchIndex -> {
                    int start = batchIndex * parallelBatchSize;
                    int end = Math.min(start + parallelBatchSize, enrolmentPage.getNumberOfElements());

                    List<EnrolmentEntity> enrolmentBatch = enrolmentPage.getContent().subList(start, end);
                    enrolmentBatch.parallelStream().forEach(enrolment -> {
                        Set<String> features = enrolmentRepository.findFeaturesByBnums(List.of(enrolment.getBnum()));
                        enrolment.setFeatures(features);
                    });
                });

            // ... Process or return the enrolmentPage with loaded features
            pageNumber++;

            // Fetch the next page within the loop
            if (enrolmentPage.hasNext()) {
                enrolmentPage = enrolmentRepository.findAll(PageRequest.of(pageNumber, pageSize));
            }
        } while (enrolmentPage.hasNext());
    }

 @Query("SELECT s.feature FROM Service s WHERE s.bnum IN :bnums")
    List<String> findFeaturesByBnums(@Param("bnums") List<String> bnums);




    public void loadFeatures() {
        int pageNumber = 0;
        int pageSize = 10000;
        int parallelBatchSize = 500; // Batch size for parallel enrolment processing
        int serviceBatchSize = 500;  // Batch size for fetching services

        Page<EnrolmentEntity> enrolmentPage;
        do {
            enrolmentPage = enrolmentRepository.findAll(PageRequest.of(pageNumber, pageSize));

            List<String> allBnums = enrolmentPage.getContent().stream()
                .map(EnrolmentEntity::getBnum)
                .distinct()
                .collect(Collectors.toList());

            Map<String, List<Service>> serviceMap = new HashMap<>();

            // Fetch services in batches
            for (int i = 0; i < allBnums.size(); i += serviceBatchSize) {
                int start = i;
                int end = Math.min(i + serviceBatchSize, allBnums.size());
                List<String> bnumBatch = allBnums.subList(start, end);

                List<Service> services = enrolmentRepository.findServicesByBnums(bnumBatch);
                serviceMap.putAll(services.stream().collect(Collectors.groupingBy(Service::getBnum)));
            }

            // Process enrolments in parallel and assign features
            enrolmentPage.getContent().parallelStream().forEach(enrolment -> {
                List<Service> enrolmentServices = serviceMap.get(enrolment.getBnum());
                if (enrolmentServices != null) {
                    Set<String> features = enrolmentServices.stream()
                        .map(Service::getFeature)
                        .collect(Collectors.toSet());
                    enrolment.setFeatures(features);
                } else {
                    enrolment.setFeatures(new HashSet<>());
                }
            });

            // ... Process or return the enrolmentPage with loaded features
            pageNumber++;
        } while (enrolmentPage.hasNext());
    }
}
