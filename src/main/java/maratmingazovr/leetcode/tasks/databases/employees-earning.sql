--https://leetcode.com/problems/employees-earning-more-than-their-managers/
SELECT e.name as Employee
FROM Employee as e
LEFT JOIN Employee as m
ON e.managerId = m.id
WHERE e.salary > m.salary