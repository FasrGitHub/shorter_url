package com.example.controller;

import com.example.entity.ErrorMassage;
import com.example.helpers.CodeGenerator;
import com.example.entity.Link;
import com.example.service.LinkService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping()
public class LinkController {

    private static final Logger log = LogManager.getLogger();
    private final CodeGenerator codeGenerator;
    private final LinkService linkService;
    private final Semaphore semaphore;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
        this.codeGenerator = new CodeGenerator();
        this.semaphore = new Semaphore(100);
    }

    @PostMapping("/")
    public ResponseEntity createShortUrl(@RequestBody Link link) {
        log.info("Method {} class {} started working.", "createShortUrl" ,this.getClass().getSimpleName());
        String originalUrl = link.getOriginalUrl();

        try {
            new URL(originalUrl).toURI();
            semaphore.acquire();

            String hash = codeGenerator.generate(6);
            Link result = new Link(null, hash, originalUrl, ZonedDateTime.now());
            linkService.saveLink(result);

            semaphore.release();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (URISyntaxException | MalformedURLException exception) {
            log.warn("Method {} class {} invalid URL specified!", "createShortUrl", this.getClass().getSimpleName());
            ErrorMassage error = new ErrorMassage("Invalid URL specified!");
            return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (InterruptedException e) {
            semaphore.release();
            log.error("Queue error!" + e);
            ErrorMassage error = new ErrorMassage("Queue error!");
            return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{hash}")
    public ResponseEntity redirectShorter(@PathVariable("hash") String hash){
        log.info("The method for issuing a link has been launched. Link = '{}'", hash);
        Link link = linkService.getLinkByHash(hash);
        System.out.println(2/0);

        if (link != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", link.getOriginalUrl());

            return new ResponseEntity(headers, HttpStatus.FOUND);
        } else {
            log.warn("This link = {} does not exist!", hash);
            ErrorMassage error = new ErrorMassage("This link = " + hash +"does not exist!");
            return new ResponseEntity(error,HttpStatus.NOT_FOUND);
        }
    }

    @Scheduled(cron ="0 * * * * *")
    public void deleteLinkAfterTimes() {
        List<Link> links = linkService.findAllLinks();
        for (Link link: links) {
            if(link.getCreatedAt().isBefore(ZonedDateTime.now().minusMinutes(10))){
                linkService.deleteLink(link.getHash());
            }
        }
    }
}
