--https://leetcode.com/problems/duplicate-emails/
SELECT p.email
FROM Person as p
GROUP BY p.email
HAVING count(p.email) > 1