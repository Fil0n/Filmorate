with l as (select l.film_id, count(l.film_id)
           from film f
    join likes l on f.id = l.film_id group by film_id)
select * from film f
    join l on f.id = l.film_id
limit 10