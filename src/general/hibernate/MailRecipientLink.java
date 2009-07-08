package dimm.general.hibernate;
// Generated 07.07.2009 13:49:31 by Hibernate Tools 3.2.1.GA



/**
 * MailRecipientLink generated by hbm2java
 */
public class MailRecipientLink  implements java.io.Serializable {


     private int id;
     private Mail mail;
     private MailRecipient mailRecipient;
     private String type;

    public MailRecipientLink() {
    }

	
    public MailRecipientLink(int id) {
        this.id = id;
    }
    public MailRecipientLink(int id, Mail mail, MailRecipient mailRecipient, String type) {
       this.id = id;
       this.mail = mail;
       this.mailRecipient = mailRecipient;
       this.type = type;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Mail getMail() {
        return this.mail;
    }
    
    public void setMail(Mail mail) {
        this.mail = mail;
    }
    public MailRecipient getMailRecipient() {
        return this.mailRecipient;
    }
    
    public void setMailRecipient(MailRecipient mailRecipient) {
        this.mailRecipient = mailRecipient;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }




}


