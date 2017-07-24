create view  "BlockedRowUnLabeled" AS
  select b.* from "BlockedRow" b
  left join (select distinct id1, id2 from "Label") l on b.id1 = l.id1 and b.id2 = l.id2
  where l.id1 is null
;
