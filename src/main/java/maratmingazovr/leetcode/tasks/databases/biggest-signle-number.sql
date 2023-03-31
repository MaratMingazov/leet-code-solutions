--https://leetcode.com/problems/biggest-single-number
SELECT MAX(num) as num
FROM (
    SELECT n.num
    FROM MyNumbers as n
    GROUP BY n.num
    HAVING COUNT(n.num) = 1
) as num