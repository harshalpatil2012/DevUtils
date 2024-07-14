
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
