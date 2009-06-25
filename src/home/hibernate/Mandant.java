package dimm.home.hibernate;
// Generated 22.06.2009 12:22:05 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * Mandant generated by hbm2java
 */
public class Mandant  implements java.io.Serializable {


     private int id;
     private String name;
     private String license;
     private Set<Hotfolder> hotfolders = new HashSet<Hotfolder>(0);
     private Set<ImapFetcher> imapFetchers = new HashSet<ImapFetcher>(0);
     private Set<Role> roles = new HashSet<Role>(0);
     private Set<Milter> milters = new HashSet<Milter>(0);
     private Set<Proxy> proxies = new HashSet<Proxy>(0);
     private Set<DiskArchive> diskArchives = new HashSet<DiskArchive>(0);
     private Set<Mail> mails = new HashSet<Mail>(0);
     private Set<AccountConnector> accountConnectors = new HashSet<AccountConnector>(0);

    public Mandant() {
    }

	
    public Mandant(int id, String name, String license) {
        this.id = id;
        this.name = name;
        this.license = license;
    }
    public Mandant(int id, String name, String license, Set<Hotfolder> hotfolders, Set<ImapFetcher> imapFetchers, Set<Role> roles, Set<Milter> milters, Set<Proxy> proxies, Set<DiskArchive> diskArchives, Set<Mail> mails, Set<AccountConnector> accountConnectors) {
       this.id = id;
       this.name = name;
       this.license = license;
       this.hotfolders = hotfolders;
       this.imapFetchers = imapFetchers;
       this.roles = roles;
       this.milters = milters;
       this.proxies = proxies;
       this.diskArchives = diskArchives;
       this.mails = mails;
       this.accountConnectors = accountConnectors;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getLicense() {
        return this.license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
    public Set<Hotfolder> getHotfolders() {
        return this.hotfolders;
    }
    
    public void setHotfolders(Set<Hotfolder> hotfolders) {
        this.hotfolders = hotfolders;
    }
    public Set<ImapFetcher> getImapFetchers() {
        return this.imapFetchers;
    }
    
    public void setImapFetchers(Set<ImapFetcher> imapFetchers) {
        this.imapFetchers = imapFetchers;
    }
    public Set<Role> getRoles() {
        return this.roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public Set<Milter> getMilters() {
        return this.milters;
    }
    
    public void setMilters(Set<Milter> milters) {
        this.milters = milters;
    }
    public Set<Proxy> getProxies() {
        return this.proxies;
    }
    
    public void setProxies(Set<Proxy> proxies) {
        this.proxies = proxies;
    }
    public Set<DiskArchive> getDiskArchives() {
        return this.diskArchives;
    }
    
    public void setDiskArchives(Set<DiskArchive> diskArchives) {
        this.diskArchives = diskArchives;
    }
    public Set<Mail> getMails() {
        return this.mails;
    }
    
    public void setMails(Set<Mail> mails) {
        this.mails = mails;
    }
    public Set<AccountConnector> getAccountConnectors() {
        return this.accountConnectors;
    }
    
    public void setAccountConnectors(Set<AccountConnector> accountConnectors) {
        this.accountConnectors = accountConnectors;
    }




}


