/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.univangers.messagerie.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author etud
 */
public class FichierDto {
    
      @Getter @Setter
    private Integer id;
      
        @Getter @Setter
    private String filetype;
        
          @Getter @Setter
    private String filename;
       
          
            @Getter @Setter
    private String filepath;
            
            
              @Getter @Setter
    private MessageDto  messageDto;

    public FichierDto() {
    }

    public FichierDto(Integer id, String filetype, String filename, String filepath, MessageDto messageDto) {
        this.id = id;
        this.filetype = filetype;
        this.filename = filename;
        this.filepath = filepath;
        this.messageDto = messageDto;
    }

   
       
       
       
    
    
}
