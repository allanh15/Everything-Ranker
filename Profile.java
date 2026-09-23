import java.util.List;
import java.util.ArrayList;

public class Profile {
    private String fullName;
    private String bio;
    private String profilePicture;
    private List<EverythingList> createdLists;
    private List<EverythingRankings> createdRankings;

    public Profile(){
        this.fullName = "";
        this.bio = "";
        this.profilePicture = "";
        this.createdLists = new ArrayList<>();
        this.createdRankings = new ArrayList<>();
    }

    public Profile(String fullName, String bio, String profilePicture){
        this.fullName = fullName;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.createdLists = new ArrayList<>();
        this.createdRankings = new ArrayList<>();
    }

    public String displayCreatedLists() {
        String output = "";
        if(createdLists == null || createdLists.isEmpty()) {
            output = "  No lists created yet.";
            System.out.println(output);
        } else {
            for(EverythingList list : createdLists) {
                output += "  - " + list.getTitle() + " (" + list.getItems().size() + " items)" +
                        (list.getAccess() ? " [Public]" : " [Private]") + "\n";
                System.out.println("  - " + list.getTitle() + " (" + list.getItems().size() + " items)" +
                        (list.getAccess() ? " [Public]" : " [Private]"));
            }
        }
        return output;
    }

    public void displayCreatedRankings() {
        if(createdRankings == null || createdRankings.isEmpty()) {
            System.out.println("  No rankings created yet.");
        } else {
            for(EverythingRankings ranking : createdRankings) {
                System.out.println("  - " + ranking.getTitle() + " (" + ranking.getItems().size() + " items)" +
                        (ranking.getAccess() ? " [Public]" : " [Private]"));
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
    public List<EverythingRankings> getCreatedRankings(){ return createdRankings;}
    public void setCreatedRankings(List<EverythingRankings> createdRankings){ this.createdRankings = createdRankings; }

    public String displaySummary(){
        StringBuilder sb = new StringBuilder();
        sb.append("Full Name: ").append(fullName).append("\n");
        sb.append("Bio: ").append(bio).append("\n");
        sb.append("Lists created: ").append(createdLists.size()).append("\n");
        for (EverythingList list : createdLists) {
            sb.append(" - ").append(list.getTitle()).append("\n");
        }
        sb.append("Rankings created: ").append(createdRankings.size()).append("\n");
        for (EverythingRankings ranking : createdRankings) {
            sb.append(" - ").append(ranking.getTitle()).append("\n");
        }
        return sb.toString();

    }
    
    @Override
    public String toString(){
        return "Profile{fullName='" + fullName + "', bio='" + bio + "'}";
    }
}
