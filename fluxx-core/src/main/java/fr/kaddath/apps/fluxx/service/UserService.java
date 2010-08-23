package fr.kaddath.apps.fluxx.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.exception.FluxxerNotFoundException;

@Stateless
@LocalBean
public class UserService {

    @PersistenceContext
    EntityManager em;

    private CriteriaBuilder cb;

    @PostConstruct
    public void init() {
        cb = em.getCriteriaBuilder();
    }

    public void persist(Fluxxer user) {
        em.persist(user);
        em.flush();
    }

    public List<Fluxxer> findAll() {
        return em.createQuery("select fluxxer from Fluxxer fluxxer").getResultList();
    }

    public Fluxxer findByUsername(String username) {
        return em.find(Fluxxer.class, username);
    }

    public Fluxxer update(Fluxxer user) {
        return em.merge(user);
    }

    public List<Fluxxer> findByFeed(Feed feed) {
        List<Fluxxer> fluxxers = new ArrayList<Fluxxer>();
        String strQuery = "select * from AGGREGATEDFEED_FEED, FLUXXER, AGGREGATEDFEED where FLUXXER.USERNAME = AGGREGATEDFEED.FLUXXER_USERNAME and AGGREGATEDFEED_FEED.AGGREGATEDFEED_ID = AGGREGATEDFEED.ID and AGGREGATEDFEED_FEED.FEED_ID ="+feed.getId();
        Query query = em.createNativeQuery(strQuery, Fluxxer.class);
        List result = query.getResultList();
        if (result != null) {
            fluxxers.addAll(result);
        }
        return fluxxers;
    }

    public Long getNumFluxxers() {
        Query query = em.createQuery("select count(f) from Fluxxer f");
        return (Long)query.getSingleResult();
    }

    public Fluxxer findByTwitterAccount(String twitterAccount) throws FluxxerNotFoundException {
        Query query = em.createQuery("select f from Fluxxer f where f.twitterAccount = :twitterAccount", Fluxxer.class);
        query.setParameter("twitterAccount", twitterAccount);
        query.setMaxResults(1);
        List<Fluxxer> fluxxers = query.getResultList();
        if(!fluxxers.isEmpty()) {
            return fluxxers.get(0);
        } else {
            throw new FluxxerNotFoundException("No fluxxer with twitterAccount : "+twitterAccount);
        }
    }

    public Fluxxer findByMailAccount(String mailAccount) throws FluxxerNotFoundException {
        Query query = em.createQuery("select f from Fluxxer f where f.email = :mailAccount", Fluxxer.class);
        query.setMaxResults(1);
        query.setParameter("mailAccount", mailAccount);
        List<Fluxxer> fluxxers = query.getResultList();
        if(!fluxxers.isEmpty()) {
            return fluxxers.get(0);
        } else {
            throw new FluxxerNotFoundException("No fluxxer with mailAccount : "+mailAccount);
        }
    }
    
}
