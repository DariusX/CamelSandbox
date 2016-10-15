package com.zerses.camelsandbox.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Independent of Camel.... just us JPA to set up some data etc.
 * Uncomment the method you wish to run
 * @author dcooper
 *
 */
public class JpaDataSetup {

    public static void main(String[] args) {

        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("test1jpa");

        EntityManager em = emfactory.createEntityManager();
        em.getTransaction().begin();

        //testCreate(em); 
        //testUpdate(em);
        testFind(em);
        testNamedQuery(em);

        em.getTransaction().commit();
        em.close();
        emfactory.close();
    }

    private static void testCreate(EntityManager em) {
        Person person = new Person();
        person.setName("Mata Hari");
        person.setAge(57);

        em.persist(person);

        person = new Person();
        person.setName("Gen. Patton");
        person.setAge(57);

        em.persist(person);

    }

    private static void testUpdate(EntityManager em) {

        Person person = em.find(Person.class, 51);
        person.setName("Boris Spassky");
        person.setAge(60);

    }

    private static void testNamedQuery(EntityManager em) {

        TypedQuery<Person> query = em.createNamedQuery("Person.findByAgeRange", Person.class);
        query.setParameter("age1", 60);
        query.setParameter("age2", 80);
        List<Person> resultList = query.getResultList();
        for (Person nxtPerson : resultList) {
            System.out.println("--- --- ---------> " + nxtPerson.getPersonId() + " " + nxtPerson.getName());
        }
    }

    private static void testFind(EntityManager em) {

        Person personKey = new Person();

        Person personFound = em.find(Person.class, new Integer(151));

        System.out.println("--- --- ---------> " + personFound.getPersonId() + " " + personFound.getName());

    }

}
