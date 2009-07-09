package dimm.general.hibernate;
// Generated 09.07.2009 10:42:43 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * MailHeaderVariable generated by hbm2java
 */
public class MailHeaderVariable  implements java.io.Serializable {


     private int id;
     private String varName;
     private Set<MailHeader> mailHeaders = new HashSet<MailHeader>(0);

    public MailHeaderVariable() {
    }

	
    public MailHeaderVariable(int id) {
        this.id = id;
    }
    public MailHeaderVariable(int id, String varName, Set<MailHeader> mailHeaders) {
       this.id = id;
       this.varName = varName;
       this.mailHeaders = mailHeaders;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public String getVarName() {
        return this.varName;
    }
    
    public void setVarName(String varName) {
        this.varName = varName;
    }
    public Set<MailHeader> getMailHeaders() {
        return this.mailHeaders;
    }
    
    public void setMailHeaders(Set<MailHeader> mailHeaders) {
        this.mailHeaders = mailHeaders;
    }




}


