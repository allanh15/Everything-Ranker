public class Profile {
    private String fullName;
    private String bio;
    private String profilePicture;

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

    @Override
    public String toString(){
        return "Profile{fullName='" + fullName + "', bio='" + bio + "'}";
    }
}
