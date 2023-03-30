--https://leetcode.com/problems/customers-who-never-order
SELECT c.name AS Customers
FROM Customers as c
LEFT JOIN Orders as o
ON c.id = o.customerId
WHERE o.id IS NULL