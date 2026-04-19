import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String fullName;
    private String bio;
    private String profilePicture;
    private List<EverythingList> createdLists = new ArrayList<>();
    private List<Rankings> createdRankings = new ArrayList<>();

    public Profile(){
        this.fullName = "";
        this.bio = "";
        this.profilePicture = "";
    }

    public Profile(String fullName, String bio, String profilePicture){
        this.fullName = fullName;
        this.bio = bio;
        this.profilePicture = profilePicture;
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
}
