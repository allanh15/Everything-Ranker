public class Item {
    private String name;
    private String description;
    private String photo;
    private int rank;

    public Item(String name){
        this.name = name;
        this.description = "";
        this.photo = null;
        this.rank = 0;
    }

    public Item(String name, String description, String photo, int rank){
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.rank = rank;
    }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public String getPhoto(){ return photo; }
    public void setPhoto(String photo){ this.photo = photo; }
    public int getRank(){ return rank; }
    public void setRank(int rank){ this.rank = rank; }

    @Override
    public String toString(){
        return "Item{name='" + name + "', description='" + description + "', rank=" + rank + "}";
    }
}
