package dimm.general.hibernate;
// Generated 25.06.2009 14:21:36 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * Mail generated by hbm2java
 */
public class Mail  implements java.io.Serializable {


     private long id;
     private Integer mid;
     private String from;
     private Integer rfc822Size;
     private String subject;
     private String location;
     private Integer locationId;
     private String flags;
     private Integer rcvTimestamp;
     private Set<MailHeader> mailHeaders = new HashSet<MailHeader>(0);
     private Set<MailRecipientLink> mailRecipientLinks = new HashSet<MailRecipientLink>(0);

    public Mail() {
    }

	
    public Mail(long id) {
        this.id = id;
    }
    public Mail(long id, Integer mid, String from, Integer rfc822Size, String subject, String location, Integer locationId, String flags, Integer rcvTimestamp, Set<MailHeader> mailHeaders, Set<MailRecipientLink> mailRecipientLinks) {
       this.id = id;
       this.mid = mid;
       this.from = from;
       this.rfc822Size = rfc822Size;
       this.subject = subject;
       this.location = location;
       this.locationId = locationId;
       this.flags = flags;
       this.rcvTimestamp = rcvTimestamp;
       this.mailHeaders = mailHeaders;
       this.mailRecipientLinks = mailRecipientLinks;
    }
   
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    public Integer getMid() {
        return this.mid;
    }
    
    public void setMid(Integer mid) {
        this.mid = mid;
    }
    public String getFrom() {
        return this.from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    public Integer getRfc822Size() {
        return this.rfc822Size;
    }
    
    public void setRfc822Size(Integer rfc822Size) {
        this.rfc822Size = rfc822Size;
    }
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    public String getFlags() {
        return this.flags;
    }
    
    public void setFlags(String flags) {
        this.flags = flags;
    }
    public Integer getRcvTimestamp() {
        return this.rcvTimestamp;
    }
    
    public void setRcvTimestamp(Integer rcvTimestamp) {
        this.rcvTimestamp = rcvTimestamp;
    }
    public Set<MailHeader> getMailHeaders() {
        return this.mailHeaders;
    }
    
    public void setMailHeaders(Set<MailHeader> mailHeaders) {
        this.mailHeaders = mailHeaders;
    }
    public Set<MailRecipientLink> getMailRecipientLinks() {
        return this.mailRecipientLinks;
    }
    
    public void setMailRecipientLinks(Set<MailRecipientLink> mailRecipientLinks) {
        this.mailRecipientLinks = mailRecipientLinks;
    }




}


