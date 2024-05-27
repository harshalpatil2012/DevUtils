SELECT
    id,
    country,
    LISTAGG(service, ',') WITHIN GROUP (ORDER BY service) AS services
FROM
    your_table
GROUP BY
    id, country;
