import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EverythingRankings {
    private int id;
    private String title;
    private String authorID;
    private Date pubDate;
    private Date update;
    private boolean access;     // true = public, false = private
    private List<String> items;

    public EverythingRankings(String title, String authorID, boolean access){
        this.id = -1; // assigned by DB
        this.title = title;
        this.authorID = authorID;
        this.access = access;
        this.items = new ArrayList<>();
    }

    public EverythingRankings(int id, String title, String authorID, boolean access){
        this.id = id;
        this.title = title;
        this.authorID = authorID;
        this.access = access;
        this.pubDate = new Date();
        this.update = new Date();
        this.items = new ArrayList<>();
    }

    public EverythingRankings(int id, String title, String authorID, boolean access, long pubDateMs, long updateMs){
        this.id = id;
        this.title = title;
        this.authorID = authorID;
        this.access = access;
        this.pubDate = new Date(pubDateMs);
        this.update = new Date(updateMs);
        this.items = new ArrayList<>();
    }

    public int getID(){ return id; }
    public void setID(int id){ this.id = id; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public String getAuthorID(){ return authorID; }
    public boolean getAccess(){ return access; }
    public void setAccess(boolean access){ this.access = access; }
    public List<String> getItems(){ return items; }
    public void setItems(List<String> items){ this.items = items; }
    public Date getPubDate(){ return pubDate; }
    public Date getUpdate(){ return update; }
    public void setUpdate(Date update){ this.update = update; }

    public void addItem(String itemName){
        items.add(itemName);
    }

    public boolean hasItem(String itemName){
        for(String item : items){
            if(item.equalsIgnoreCase(itemName)) return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return "EverythingRankings{id=" + id + ", title='" + title + "', author='" + authorID + "', pubDate=" + pubDate + ", update=" + update + ", access=" + access + ", items=" + items + "}";
    }
}
