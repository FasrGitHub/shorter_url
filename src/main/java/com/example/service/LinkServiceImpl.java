package com.example.service;

import com.example.dao.LinkDAO;
import com.example.entity.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkDAO linkDAO;

    public LinkServiceImpl(LinkDAO linkDAO) {
        this.linkDAO = linkDAO;
    }


    @Override
    @Transactional
    public void saveLink(Link link) {
        linkDAO.saveLink(link);
    }

    @Override
    @Transactional
    public void deleteLink(String hash) {
        linkDAO.deleteLink(hash);
    }

    @Override
    @Transactional
    public Link getLinkByHash(String hash) {
        return linkDAO.getLinkByHash(hash);
    }

    @Override
    @Transactional
    public List findAllLinks() {
        return linkDAO.findAllLinks();
    }
}
