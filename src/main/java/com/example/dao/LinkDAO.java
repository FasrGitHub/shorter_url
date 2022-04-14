package com.example.dao;

import com.example.entity.Link;

import java.util.List;

public interface LinkDAO {

    void saveLink(Link link);

    void deleteLink(String hash);

    Link getLinkByHash(String hash);

    List findAllLinks();
}
