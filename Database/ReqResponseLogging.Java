SELECT
    id,
    country,
    LISTAGG(service, ',') WITHIN GROUP (ORDER BY service) AS services
FROM
    your_table
GROUP BY
    id, country;


SELECT id, countryid, country, services
FROM (
    SELECT id,
           countryid,
           country,
           LISTAGG(service, ',') WITHIN GROUP (ORDER BY service) AS services,
           ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM your_entity
    GROUP BY id, countryid, country
)
WHERE rn > :offset
FETCH NEXT :pageSize ROWS ONLY;

