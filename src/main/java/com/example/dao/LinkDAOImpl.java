package com.example.dao;

import com.example.entity.Link;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LinkDAOImpl implements LinkDAO {

    private final EntityManager entityManager;

    public LinkDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void saveLink(Link link) {
        Link newLink = entityManager.merge(link);
        link.setId(newLink.getId());
    }

    @Override
    public void deleteLink(String hash) {
        Query query = entityManager.createQuery("delete from Link " +
                "where hash = :hash")
                .setParameter("hash", hash);
        query.executeUpdate();
    }

    @Override
    public Link getLinkByHash(String hash) {
        try {
            Session session = entityManager.unwrap(Session.class);
            Query query = session.createQuery("from Link where hash = :hash")
                    .setParameter("hash", hash);
            return (Link) query.getSingleResult();
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List findAllLinks() {
        Query query = entityManager.createQuery("from Link");
        return query.getResultList();
    }
}
