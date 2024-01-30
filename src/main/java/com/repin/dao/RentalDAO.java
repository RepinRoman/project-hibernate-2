package com.repin.dao;

import com.repin.domain.Rental;
import org.hibernate.SessionFactory;

public class RentalDAO extends GenericDAO<Rental> {
    public RentalDAO(SessionFactory sessionFactory) {
        super(Rental.class, sessionFactory);
    }

    public Rental getAnyUnreturnedRental() {
        return getCurrentSession()
                .createQuery("SELECT r FROM Rental r WHERE r.returnDate IS NULL", Rental.class)
                .setMaxResults(1)
                .getSingleResult();
    }
}
