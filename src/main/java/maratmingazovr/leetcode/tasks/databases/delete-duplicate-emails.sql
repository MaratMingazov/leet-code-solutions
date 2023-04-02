--https://leetcode.com/problems/delete-duplicate-emails

DELETE FROM Person as pp
WHERE pp.id NOT IN (
  SELECT * FROM
    (
      SELECT MIN(p.id)
      FROM Person as p
      GROUP BY p.email
    ) as pp
)


delete p1 from person p1,person p2
where p1.email=p2.email and p1.id>p2.id;