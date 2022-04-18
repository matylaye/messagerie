/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.univangers.messagerie.services;

import com.univangers.messagerie.dao.ListeDaoInterface;
import com.univangers.messagerie.dto.ListeDto;
import com.univangers.messagerie.model.Liste;

/**
 *
 * @author etud
 */
public class ListeService {
     private ListeDaoInterface listeDao;

     public ListeDto findListeDtoById(String idlist) {
        Liste liste = listeDao.findListeById(idlist);
        return convertToDto(liste);
    }
     
      private Liste convertToEntity(ListeDto listeDto) {
        Liste liste = new Liste();
       liste.setIdLISTE(listeDto.getId());
       liste.setLibelle(listeDto.getLibelle());
       
        return liste;
      }
    private ListeDto convertToDto(Liste liste) {
       ListeDto listeDto= new ListeDto();
        listeDto.setId(liste.getIdLISTE());
        liste.setLibelle(liste.getLibelle());
        return listeDto;
}
}