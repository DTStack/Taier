insert into res
select
    t.cola
    ,t.indexa
    ,tt.class_id
    ,tt.score
from test t
LATERAL VIEW explode(map('1',100,'2',200)) tt as class_id ,score;