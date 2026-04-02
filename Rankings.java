import java.util.ArrayList;
import java.util.List;

public class Rankings {
    private String author;
    private int listID;
    private int rankID;
    private List<Item> rankOrder;
    private boolean access;

    public Rankings(String author, int listID, int rankID, boolean access){
        this.author = author;
        this.listID = listID;
        this.rankID = rankID;
        this.rankOrder = new ArrayList<>();
        this.access = access;
    }

    public String getAuthor(){ return author; }
    public void setAuthor(String author){ this.author = author; }
    public int getListID(){ return listID; }
    public void setListID(int listID){ this.listID = listID; }
    public int getRankID(){ return rankID; }
    public void setRankID(int rankID){ this.rankID = rankID; }
    public List<Item> getRankOrder(){ return rankOrder; }
    public void setRankOrder(List<Item> rankOrder){ this.rankOrder = rankOrder; }
    public boolean getAccess(){ return access; }
    public void setAccess(boolean access){ this.access = access; }

    public void addRankedItem(Item item){
        rankOrder.add(item);
    }

    @Override
    public String toString(){
        return "Rankings{author='" + author + "', listID=" + listID + ", rankID=" + rankID + ", items=" + rankOrder.size() + ", access=" + access + "}";
    }
}
