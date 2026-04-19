import java.util.List;
import java.uril.ArrayList;

public class Profile {
    private String fullName;
    private String bio;
    private String profilePicture;
    private List<EverythingList> createdLists;

    public Profile(){
        this.fullName = "";
        this.bio = "";
        this.profilePicture = "";
        this.createdLists = new ArrayList<>();
    }

    public Profile(String fullName, String bio, String profilePicture){
        this.fullName = fullName;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.createdLists = new ArrayList<>();
    }

        public void displayCreatedLists() {
        if(createdLists == null || createdLists.isEmpty()) {
            System.out.println("  No lists created yet.");
        } else {
            for(EverythingList list : createdLists) {
                System.out.println("  - " + list.getTitle() + " (" + list.getItems().size() + " items)" +
                        (list.getAccess() ? " [Public]" : " [Private]"));
            }
        }
    }


    public String getFullName(){ return fullName; }
    public void setFullName(String fullName){ this.fullName = fullName; }
    public String getBio(){ return bio; }
    public void setBio(String bio){ this.bio = bio; }
    public String getProfilePicture(){ return profilePicture; }
    public void setProfilePicture(String profilePicture){ this.profilePicture = profilePicture; }
    public List<EverythingList> getCreatedLists(){ return createdLists;}
    public void setCreatedLists(List<EverythingList> createdLists){ this.createdLists = createdLists;}
    public List<Rankings> getCreatedRankings(){ return createdRankings;}
    public void setCreatedRankings(List<Rankings> createdRankings){ this.createdRankings = createdRankings; }

    public String displaySummary(){
        StringBuilder sb = new StringBuilder();
        sb.append("Full Name: ").append(fullName).append("\n");
        sb.append("Bio: ").append(bio).append("\n");
        sb.append("Lists created: ").append(createdLists.size()).append("\n");
        for (EverythingList list : createdLists) {
            sb.append(" - ").append(list.getTitle()).append("\n");
        }
        sb.append("Rankings created: ").append(createdRankings.size()).append("\n");
        for (Rankings ranking : createdRankings) {
            sb.append(" - ").append(ranking.getTitle()).append("\n");
        }
        return sb.toString();

    }
    
    @Override
    public String toString(){
        return "Profile{fullName='" + fullName + "', bio='" + bio + "'}";
    }
    public List<EverythingList> getCreatedLists() {
        return createdLists;
    }
    public void setCreatedLists(List<EverythingList> createdLists) {
        this.createdLists = createdLists;
    }
}
