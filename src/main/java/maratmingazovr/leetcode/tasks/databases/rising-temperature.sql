--https://leetcode.com/problems/rising-temperature
SELECT w.id as Id
FROM Weather as w
LEFT JOIN Weather w2
ON DATEDIFF(w.recordDate, w2.recordDate) = 1
WHERE w.temperature > w2.temperature