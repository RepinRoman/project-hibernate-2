package com.repin.dao;

import com.repin.domain.Film;
import org.hibernate.SessionFactory;

public class FilmDAO extends GenericDAO<Film> {
    public FilmDAO(SessionFactory sessionFactory) {
        super(Film.class, sessionFactory);
    }

    public Film getFirstAvailableFilmForRent() {
        return getCurrentSession()
                .createQuery("SELECT f FROM Film f WHERE f.id IN (select distinct i.film.id from Inventory i)", Film.class)
                .setMaxResults(1)
                .getSingleResult();
    }
}
