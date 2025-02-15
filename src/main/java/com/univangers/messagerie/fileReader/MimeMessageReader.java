/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.univangers.messagerie.fileReader;

import com.univangers.messagerie.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author etud
 */
@Component
public class MimeMessageReader {

    private static String ATTACHS_FILE_DIR;

    @Value("${message.attachs.directory}")
    public void setNameStatic(String attachsFilesDir) {
        MimeMessageReader.ATTACHS_FILE_DIR = attachsFilesDir;
    }

    public static MailObject readMessageFile(String filePath) throws FileNotFoundException, MessagingException, IOException {
        InputStream inputStream = new FileInputStream(filePath);
        MailObject mailObject = readMessageInputStream(inputStream);
        return mailObject;
    }

    public static MailObject readMessageInputStream(InputStream inputStream) throws FileNotFoundException, MessagingException, IOException {
        Properties props = new Properties();
        props.setProperty("mail.mime.address.strict", "false");
        props.setProperty("mail.mime.decodetext.strict", "false");
        Session mailSession = Session.getDefaultInstance(props, null);
        //InputStream inputStream = new FileInputStream(filePath);
        MimeMessage message = new MimeMessage(mailSession, inputStream);

        MailObject mailObject = new MailObject();

        InternetAddress senderAddress = (InternetAddress) message.getFrom()[0];

        if (senderAddress == null || message.getSentDate() == null) {
            return null;
        }
        InfoPersonne infoSender = new InfoPersonne(Utils.getClearString(senderAddress.getAddress().toLowerCase()));
        if (senderAddress.getPersonal() != null) {
            String[] info = senderAddress.getPersonal().split(" ");
            if (info.length == 2) {
                infoSender.setLastName(info[0]);
                infoSender.setFirstName(info[1]);
            } else {
                infoSender.setLastName(senderAddress.getPersonal());
            }
        }
        mailObject.setFrom(infoSender);
        mailObject.setSentDate(message.getSentDate());
        mailObject.setReceivedDate(message.getReceivedDate());
        mailObject.setSubject(message.getSubject());
        mailObject.setContent(getTextFromMessage(message));

        //FONCTION
        String fonction = getFonctionFromString(message.getSubject());
        mailObject.setFonction(fonction);

        //DESTINATAIRES
        InternetAddress[] toList = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
        if (toList != null) {
            for (InternetAddress adr : toList) {
                if (Utils.isValidInternetAddress(adr)) { //Vérifie la validité de l'adresse
                    InfoPersonne infoDestTo = new InfoPersonne(Utils.getClearString(adr.getAddress().toLowerCase()));
                    // Dans certain fichier le nom de la personne est mal encodé ou trop long ==> Ignoré ces adresses
                    if (adr.getPersonal() != null && adr.getPersonal().length() < 40) {
                        String[] info = adr.getPersonal().split(" ");
                        if (info.length == 2) {
                            infoDestTo.setLastName(info[0]);
                            infoDestTo.setFirstName(info[1]);
                        } else {
                            infoDestTo.setLastName(adr.getPersonal());
                        }

                    }
                    if (!mailObject.getTo().contains(infoDestTo)) {
                        mailObject.getTo().add(infoDestTo);
                    }
                }
            }
        }

        //DESTINATAIRES CC
        InternetAddress[] ccList = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
        if (ccList != null) {
            for (InternetAddress adr : ccList) {
                InfoPersonne infoDestCc = new InfoPersonne(Utils.getClearString(adr.getAddress().toLowerCase()));
                if (adr.getPersonal() != null) {
                    String[] info = adr.getPersonal().split(" ");
                    if (info.length == 2) {
                        infoDestCc.setLastName(info[0]);
                        infoDestCc.setFirstName(info[1]);
                    } else {
                        infoDestCc.setLastName(adr.getPersonal());
                    }
                }

                if (!mailObject.getCc().contains(infoDestCc)) {
                    mailObject.getCc().add(infoDestCc);
                }
            }
        }

        //DESTINATAIRES BCC
        /* Address[] bccList = message.getRecipients(Message.RecipientType.BCC);
        if (bccList != null) {
            for (Address adr : message.getRecipients(Message.RecipientType.BCC)) {
                String mailAdr = getMailFromString(adr.toString());
                mailObject.getBcc().add(mailAdr);
            }
        }*/
        //FICHIER
        List<AttachFile> fileList = extractAttachements(message);
        if (fileList != null) {
            mailObject.setFileList(fileList);
        }
        return mailObject;
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";

        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // Sans le break le même texte apparaît 3 fois ???
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                //result = result + "\n" + Jsoup.parse(html).text();
                result = result + "\n" + html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    private static String getMailFromString(String str) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(str);
        String mail = null;
        if (m.find()) {
            mail = m.group();
        }
        return mail;
    }

    private static String getFonctionFromString(String str) {
        Matcher m = Pattern.compile("[\\[\\w*]+]").matcher(str);
        String fonction = null;
        if (m.find()) {
            fonction = m.group();
            fonction = fonction.replace("[", "");
            fonction = fonction.replace("]", "");
        }
        return fonction;
    }

    private static Boolean getMailTransfert(String subject) throws MessagingException {
        Boolean resultat = false;
        Matcher fwd = Pattern.compile("fwd:").matcher(subject);
        Matcher tr = Pattern.compile("TR:").matcher(subject);
        if (fwd.find() || tr.find()) {
            return true;
        }
        return resultat;
    }

    /**
     *
     * @param message
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private static List<AttachFile> extractAttachements(Message message) throws IOException, MessagingException {

        List<AttachFile> fileList = new ArrayList<>();

        File attachsDir = new File(ATTACHS_FILE_DIR);
        if (!attachsDir.exists()) {
            attachsDir.mkdirs();
        }

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                        && StringUtils.isBlank(bodyPart.getFileName())) {
                    continue; // dealing with attachments only
                }
                InputStream is = bodyPart.getInputStream();
                String fileName = bodyPart.getFileName();
                if (fileName != null) {
                    fileName = MimeUtility.decodeText(fileName);

                    File f = new File(ATTACHS_FILE_DIR + File.separator + fileName);

                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] buf = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buf)) != -1) {
                        fos.write(buf, 0, bytesRead);
                    }
                    AttachFile attachFile = new AttachFile();
                    String ext = FilenameUtils.getExtension(f.getName());
                    if (ext.length() > 8) {
                        ext = "UKNW";
                    }
                    attachFile.setFilename(f.getName());
                    attachFile.setFilepath(f.getAbsolutePath());
                    attachFile.setFiletype(ext);
                    fileList.add(attachFile);
                    fos.close();

                }

            }
        }
        return fileList;
    }

    public static Boolean isValidEmailAddress(String emailStr) {
        Boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(emailStr);
            emailAddr.validate();
        } catch (AddressException aex) {
            result = false;
        }
        return result;
    }

}
