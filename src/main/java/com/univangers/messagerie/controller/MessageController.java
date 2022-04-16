/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.univangers.messagerie.controller;

import com.univangers.messagerie.dto.AdresseDto;
import com.univangers.messagerie.dto.MessageDto;
import com.univangers.messagerie.services.AdresseService;
import com.univangers.messagerie.services.AdresseServiceInterface;
import com.univangers.messagerie.services.MessageServiceInterface;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author etud
 */
@RestController
@RequestMapping("/messagerie/message")
@Transactional
public class MessageController {

    @Autowired
    private MessageServiceInterface messageService;
    
    @Autowired
    private AdresseServiceInterface adresseService;

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello world !!!";
    }

    @PostMapping("/test-insert")
    public void testInsert() {
     /*   MessageDto mDto = new MessageDto();
        AdresseDto aDto = new AdresseDto();
        mDto.setObject("test");
        mDto.setDate(new Date());
        mDto.setBody("Test insertion Data");
        aDto = adresseService.findAdresseById("diya1003@gmail.com");
        mDto.setExpediteurDto(aDto);

        messageService.insertMessageDto(mDto);*/
    }
}
