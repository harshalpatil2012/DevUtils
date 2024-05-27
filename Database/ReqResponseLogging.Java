SELECT
    id,
    country,
    LISTAGG(service, ',') WITHIN GROUP (ORDER BY service) AS services
FROM
    your_table
GROUP BY
    id, country;


SELECT id, country, services
FROM (
    SELECT id,
           country,
           LISTAGG(service, ',') WITHIN GROUP (ORDER BY service) AS services,
           ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM your_table
    GROUP BY id, country
)
WHERE rn > :offset
FETCH NEXT :pageSize ROWS ONLY;

