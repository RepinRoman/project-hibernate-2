package com.repin.dao;

import com.repin.domain.City;
import org.hibernate.SessionFactory;

public class CityDAO extends GenericDAO<City> {
    public CityDAO(SessionFactory sessionFactory) {
        super(City.class, sessionFactory);
    }

    public City getByName(String name) {
        return getCurrentSession()
                .createQuery("SELECT c FROM City c WHERE c.city = :name", City.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getSingleResult();
    }
}
