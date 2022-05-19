/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.univangers.messagerie.dao;

import com.univangers.messagerie.model.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author etud
 */
@Repository
@Transactional
public class MessageDao implements MessageDaoInterface {

    @PersistenceContext
    private transient EntityManager em;

    @Override
    public void insertMessage(Message message) {
        em.persist(message); // Fait le INSERT
        em.flush();
    }

    @Override
    public Message findMessageById(Integer idMessage) {

        Message m = em.find(Message.class, idMessage);
        return m;
    }

    @Override
    public List<Message> findAllMessage() {
        List<Message> messageList = em.createQuery("SELECT m FROM Message m").getResultList();

        return messageList;
    }

    @Override
    public void updateMessage(Message message) {
        em.merge(message);
    }

    @Override
    public void deleteMessage(Integer idMessage) {
        Message mToDelete = findMessageById(idMessage);
        if (mToDelete != null) {
            em.remove(mToDelete);
        }
    }

    @Override
    public Integer countMessage() {
        Integer count = 0;
        Object object = em.createQuery("SELECT COUNT(m)  FROM Message m ").getSingleResult();
        if (object != null) {
            count = (int) (long) object;
        }
        return count;
    }

    @Override
    public List<Message> findMessageBySender(String idAdresse) {
        List<Message> messageList;
        try {
            messageList = em.createQuery("SELECT m FROM Message m WHERE LOWER(m.sender.idADRESSE) LIKE LOWER(CONCAT('%',:idAdresse,'%'))")
                    .setParameter("idAdresse", idAdresse).getResultList();
        } catch (NoResultException nre) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }

    @Override
    public List<Message> findMessageBySubject(String keyWord) {
        List<Message> messageList;
        try {
            messageList = em.createQuery("SELECT m FROM Message m WHERE LOWER(m.subject) LIKE LOWER(CONCAT( '%',:keyWord,'%'))")
                    .setParameter("keyWord", keyWord).getResultList();
        } catch (NoResultException nre) {
            messageList = new ArrayList<>();
        }
        return messageList;
    }

    @Override
    public List<Message> findMessageByDestinataire(String keyWord) {
        List<Message> messageList;
        try {
            messageList = em.createQuery("SELECT m FROM Message m WHERE m.destinataires=:keyWord").setParameter("keyWord", keyWord).getResultList();
        } catch (NoResultException nre) {
            messageList = new ArrayList<>();

        }
        return messageList;
    }

    @Override
    public Integer countMessagesBetweenDates(Date startDate, Date endDate) {
        Integer count = 0;

        try {
            Object object = em.createQuery("SELECT COUNT(m) FROM Message m WHERE m.sentdate BETWEEN :starDate  AND :endDate")
                    .setParameter("starDate", startDate)
                    .setParameter("endDate", endDate).getSingleResult();
            if (object != null) {
                count = (int) (long) object;
            }
        } catch (NoResultException nre) {
            count = 0;

        }
        return count;
    }

    @Override
    public Integer countMessageById(Integer idMessage) {
        Integer count = 0;
        try {
            Object object = em.createQuery("SELECT COUNT(m) FROM Message m WHERE m.idMESSAGE=:idMessage").setParameter("idMessage", idMessage).getSingleResult();
            if (object != null) {
                count = (Integer) object;
            }

        } catch (NoResultException nre) {
            count = 0;

        }
        return count;
    }
    
    @Override
    public List<Message> findMessagesBetweenDates(Date startDate, Date endDate) {
        List<Message> messageList;
        try {
            messageList = em.createQuery("SELECT m FROM Message m WHERE m.sentdate BETWEEN :starDate  AND :endDate")
                    .setParameter("starDate", startDate)
                    .setParameter("endDate", endDate).getResultList();
        } catch (NoResultException nre) {
            messageList = new ArrayList<>();

        }
        return messageList;
    }

}
