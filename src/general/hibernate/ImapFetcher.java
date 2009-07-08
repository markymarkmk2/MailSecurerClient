package dimm.general.hibernate;
// Generated 07.07.2009 13:49:31 by Hibernate Tools 3.2.1.GA



/**
 * ImapFetcher generated by hbm2java
 */
public class ImapFetcher  implements java.io.Serializable {


     private int id;
     private DiskArchive diskArchive;
     private Mandant mandant;
     private String server;
     private Integer port;
     private String username;
     private String password;
     private String flags;

    public ImapFetcher() {
    }

	
    public ImapFetcher(int id, DiskArchive diskArchive, Mandant mandant, String server) {
        this.id = id;
        this.diskArchive = diskArchive;
        this.mandant = mandant;
        this.server = server;
    }
    public ImapFetcher(int id, DiskArchive diskArchive, Mandant mandant, String server, Integer port, String username, String password, String flags) {
       this.id = id;
       this.diskArchive = diskArchive;
       this.mandant = mandant;
       this.server = server;
       this.port = port;
       this.username = username;
       this.password = password;
       this.flags = flags;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public DiskArchive getDiskArchive() {
        return this.diskArchive;
    }
    
    public void setDiskArchive(DiskArchive diskArchive) {
        this.diskArchive = diskArchive;
    }
    public Mandant getMandant() {
        return this.mandant;
    }
    
    public void setMandant(Mandant mandant) {
        this.mandant = mandant;
    }
    public String getServer() {
        return this.server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }
    public Integer getPort() {
        return this.port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFlags() {
        return this.flags;
    }
    
    public void setFlags(String flags) {
        this.flags = flags;
    }




}


