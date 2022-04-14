package com.example.service;


import com.example.entity.Link;

import java.util.List;

public interface LinkService {

    void saveLink(Link link);

    void deleteLink(String hash);

    Link getLinkByHash(String hash);

    List findAllLinks();
}
