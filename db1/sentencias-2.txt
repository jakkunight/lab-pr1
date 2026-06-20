-- 1.

select count(*) as c, pers_id_pais, pers_id_ciudad
from personas
group by pers_id_pais, pers_id_ciudad
having c > 5;

-- 2.
select count(*) as c, pers_apellido
from personas
group by pers_apellido
having c > 2
order by c desc;

-- 3.
select count(*) as c, pers_sexo
from personas
group by pers_sexo
having c > 10
order by pers_sexo asc;

-- 4.
select pers_serie_ci, pers_estado_civil, count(*) as n
from personas
where pers_serie_ci = 'B1'
group by pers_serie_ci, pers_estado_civil
having n > 100;

-- 5.
select
    extract(year from pers_fecha_nacimiento) as year,
    count(*) as n
from personas
where pers_fecha_nacimiento is not null
group by extract(year from pers_fecha_nacimiento)
having count(*) > 50
order by year desc;

