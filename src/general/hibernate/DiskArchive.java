package dimm.general.hibernate;
// Generated 09.07.2009 10:42:43 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * DiskArchive generated by hbm2java
 */
public class DiskArchive  implements java.io.Serializable {


     private int id;
     private Mandant mandant;
     private String name;
     private String flags;
     private Set<DiskSpace> diskSpaces = new HashSet<DiskSpace>(0);
     private Set<ImapFetcher> imapFetchers = new HashSet<ImapFetcher>(0);
     private Set<Milter> milters = new HashSet<Milter>(0);
     private Set<Proxy> proxies = new HashSet<Proxy>(0);
     private Set<Hotfolder> hotfolders = new HashSet<Hotfolder>(0);

    public DiskArchive() {
    }

	
    public DiskArchive(int id) {
        this.id = id;
    }
    public DiskArchive(int id, Mandant mandant, String name, String flags, Set<DiskSpace> diskSpaces, Set<ImapFetcher> imapFetchers, Set<Milter> milters, Set<Proxy> proxies, Set<Hotfolder> hotfolders) {
       this.id = id;
       this.mandant = mandant;
       this.name = name;
       this.flags = flags;
       this.diskSpaces = diskSpaces;
       this.imapFetchers = imapFetchers;
       this.milters = milters;
       this.proxies = proxies;
       this.hotfolders = hotfolders;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Mandant getMandant() {
        return this.mandant;
    }
    
    public void setMandant(Mandant mandant) {
        this.mandant = mandant;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getFlags() {
        return this.flags;
    }
    
    public void setFlags(String flags) {
        this.flags = flags;
    }
    public Set<DiskSpace> getDiskSpaces() {
        return this.diskSpaces;
    }
    
    public void setDiskSpaces(Set<DiskSpace> diskSpaces) {
        this.diskSpaces = diskSpaces;
    }
    public Set<ImapFetcher> getImapFetchers() {
        return this.imapFetchers;
    }
    
    public void setImapFetchers(Set<ImapFetcher> imapFetchers) {
        this.imapFetchers = imapFetchers;
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
    public Set<Hotfolder> getHotfolders() {
        return this.hotfolders;
    }
    
    public void setHotfolders(Set<Hotfolder> hotfolders) {
        this.hotfolders = hotfolders;
    }




}


