package com.repin.dao;

import com.repin.domain.Address;
import org.hibernate.SessionFactory;

public class AddressDAO extends GenericDAO<Address> {
    public AddressDAO(SessionFactory sessionFactory) {
        super(Address.class, sessionFactory);
    }
}
