package com.repin;

import com.repin.dao.*;
import com.repin.domain.*;
import com.repin.util.Feature;
import com.repin.util.Rating;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Main {
    private final SessionFactory sessionFactory;

    private final ActorDAO actorDAO;
    private final AddressDAO addressDAO;
    private final CategoryDAO categoryDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final CustomerDAO customerDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;
    private final InventoryDAO inventoryDAO;
    private final LanguageDAO languageDAO;
    private final PaymentDAO paymentDAO;
    private final RentalDAO rentalDAO;
    private final StaffDAO staffDAO;
    private final StoreDAO storeDAO;

    public Main() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "H#1gqUFQInYp*E9kDe3b");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Film. class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .addProperties(properties)
                .buildSessionFactory();

        actorDAO = new ActorDAO(sessionFactory);
        addressDAO = new AddressDAO(sessionFactory);
        categoryDAO = new CategoryDAO(sessionFactory);
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        customerDAO = new CustomerDAO(sessionFactory);
        filmDAO = new FilmDAO(sessionFactory);
        filmTextDAO = new FilmTextDAO(sessionFactory);
        inventoryDAO = new InventoryDAO(sessionFactory);
        languageDAO = new LanguageDAO(sessionFactory);
        paymentDAO = new PaymentDAO(sessionFactory);
        rentalDAO = new RentalDAO(sessionFactory);
        staffDAO = new StaffDAO(sessionFactory);
        storeDAO = new StoreDAO(sessionFactory);
    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.customerReturnsRentedFilmToStore();
        main.customerRentFilm(customer);
        main.newFilmHasFilmedAndAvailableForRental();
    }

    private Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();

            Address address = new Address();
            address.setAddress("st. Pushkina, 18, apt. 37");
            address.setDistrict("Moscow district");
            address.setCity(cityDAO.getByName("Moscow"));
            address.setPhone("8-800-555-35-35");
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setFirstName("Roman");
            customer.setLastName("Repin");
            customer.setEmail("CustomerEmail@gmail.com");
            customer.setStore(storeDAO.getItems(0, 1).get(0));
            customer.setAddress(address);
            customer.setIsActive(true);
            customerDAO.save(customer);

            transaction.commit();
            return customer;
        }
    }

    private void customerReturnsRentedFilmToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();

            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());
            rentalDAO.save(rental);

            transaction.commit();
        }
    }

    private void customerRentFilm(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            Store store = storeDAO.getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(filmDAO.getFirstAvailableFilmForRent());
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(store.getStaff());
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setPaymentDate(LocalDateTime.now());
            payment.setRental(rental);
            payment.setCustomer(customer);
            payment.setAmount(BigDecimal.valueOf(20.24));
            payment.setStaff(store.getStaff());
            paymentDAO.save(payment);

            transaction.commit();
        }
    }

    private void newFilmHasFilmedAndAvailableForRental() {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            Language language = languageDAO.getItems(0, 10).stream().unordered().findAny().get();

            Film film = new Film();
            film.setLanguage(language);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categoryDAO.getItems(0, 5)));
            film.setActors(new HashSet<>(actorDAO.getItems(0, 10)));
            film.setRating(Rating.PG_13);
            film.setSpecialFeatures(Set.of(Feature.DELETED_SCENES, Feature.COMMENTARIES));
            film.setDescription("New comedy movie with Jim Carrey");
            film.setTitle("The Mask 3");
            film.setLength((short) 120);
            film.setRentalRate(BigDecimal.valueOf(3.99));
            film.setReplacementCost(BigDecimal.valueOf(15.99));
            film.setRentalDuration((byte) 48);
            film.setYear(Year.now());
            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setDescription(film.getDescription());
            filmText.setTitle(film.getTitle());
            filmTextDAO.save(filmText);

            transaction.commit();
        }
    }
}
