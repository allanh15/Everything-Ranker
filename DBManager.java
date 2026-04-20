import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    Connection c = null;
    Statement stmt = null;

    public DBManager(){
        try{
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            initTables();
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    // Constructor that accepts a custom db path (used for testing)
    public DBManager(String dbPath){
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            initTables();
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private void initTables() throws SQLException {
        stmt = c.createStatement();

        String sql = "CREATE TABLE IF NOT EXISTS USERS " +
                        "(ID INT PRIMARY KEY    NOT NULL," +
                        " USERNAME       TEXT   NOT NULL," +
                        " PASSWORD       TEXT   NOT NULL," +
                        " EMAIL          TEXT)";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS LISTS " +
              "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
              " TITLE          TEXT   NOT NULL," +
              " AUTHOR_ID      TEXT   NOT NULL," +
              " PUB_DATE       BIGINT NOT NULL," +
              " UPDATE_DATE    BIGINT NOT NULL," +
              " ACCESS         INT    NOT NULL DEFAULT 0)";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS ITEMS " +
              "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
              " NAME           TEXT   NOT NULL," +
              " DESCRIPTION    TEXT   DEFAULT ''," +
              " PHOTO          TEXT," +
              " RANK           INT    DEFAULT 0," +
              " LIST_ID        INT    NOT NULL," +
              " FOREIGN KEY(LIST_ID) REFERENCES LISTS(ID))";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS RANKINGS " +
              "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
              " LIST_ID        INT    NOT NULL," +
              " AUTHOR_ID      TEXT   NOT NULL," +
              " ITEM_NAME      TEXT   NOT NULL," +
              " RANK_POSITION  INT    NOT NULL," +
              " FOREIGN KEY(LIST_ID) REFERENCES LISTS(ID))";
        stmt.executeUpdate(sql);
    }

    // ===== GENERIC DCD METHODS =====

    /**
     * DCD method: addObject(Object o): boolean
     * Adds an object to the appropriate table based on its type.
     */
    public boolean addObject(Object o){
        if(o instanceof User){
            return addUser((User)o) == 1;
        } else if(o instanceof EverythingList){
            return addList((EverythingList)o) > 0;
        } else if(o instanceof Item){
            Item item = (Item)o;
            return false; // items need a listID, use insertItem instead
        }
        return false;
    }

    /**
     * DCD method: getObject(int ID): Object
     * Retrieves an object by ID. Tries lists first.
     */
    public Object getObject(int ID){
        EverythingList list = getList(ID);
        if(list != null) return list;
        return null;
    }

    /**
     * DCD method: deleteObject(Object o): boolean
     * Deletes an object from the appropriate table.
     */
    public boolean deleteObject(Object o){
        if(o instanceof EverythingList){
            EverythingList list = (EverythingList)o;
            return deleteList(list.getID());
        }
        return false;
    }

    /**
     * DCD method: insertItem(item): boolean
     * Inserts an Item object into a list.
     */
    public boolean insertItem(int listID, Item item){
        try{
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO ITEMS (NAME, DESCRIPTION, PHOTO, RANK, LIST_ID) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getPhoto());
            ps.setInt(4, item.getRank());
            ps.setInt(5, listID);
            ps.executeUpdate();
            ps.close();
            return true;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    // ===== USER OPERATIONS =====

    public int addUser(User user){
        try{
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO USERS (ID, USERNAME, PASSWORD, EMAIL) VALUES (?, ?, ?, ?)");
            ps.setString(1, user.getID());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.executeUpdate();
            ps.close();
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }
    public boolean userExists(String userName){
        if(getUser(userName) != null){
            return true;
        }else return false;
    }
    
    public User getUser(String userName){
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM USERS WHERE USERNAME = ?");
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                rs.close();
                ps.close();
                return null;
            }
            String id = rs.getString("ID");
            String retUserName = rs.getString("USERNAME");
            String password = rs.getString("PASSWORD");
            String email = rs.getString("EMAIL");
            rs.close();
            ps.close();
            return new User(id, retUserName, password, email);
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    // ===== LIST OPERATIONS =====

    public int addList(EverythingList list){
        try{
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO LISTS (TITLE, AUTHOR_ID, PUB_DATE, UPDATE_DATE, ACCESS) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, list.getTitle());
            ps.setString(2, list.getAuthorID());
            ps.setLong(3, list.getPubDate().getTime());
            ps.setLong(4, list.getUpdate().getTime());
            ps.setInt(5, list.getAccess() ? 1 : 0);
            ps.executeUpdate();
            ps.close();

            // Get the auto-generated ID
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT last_insert_rowid();");
            int newId = rs.getInt(1);
            rs.close();
            s.close();
            return newId;
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
    }

    public EverythingList getList(int listID){
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM LISTS WHERE ID = ?");
            ps.setInt(1, listID);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                rs.close();
                ps.close();
                return null;
            }
            int id = rs.getInt("ID");
            String title = rs.getString("TITLE");
            String authorID = rs.getString("AUTHOR_ID");
            long pubDateMs = rs.getLong("PUB_DATE");
            long updateMs = rs.getLong("UPDATE_DATE");
            boolean access = rs.getInt("ACCESS") == 1;
            rs.close();
            ps.close();

            EverythingList list = new EverythingList(id, title, authorID, access, pubDateMs, updateMs);

            // Load items for this list
            PreparedStatement ps2 = c.prepareStatement(
                "SELECT NAME FROM ITEMS WHERE LIST_ID = ?");
            ps2.setInt(1, listID);
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()){
                list.addItem(rs2.getString("NAME"));
            }
            rs2.close();
            ps2.close();
            return list;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    
     public List<EverythingList> getListsByUser(String userID) {
        List<EverythingList> userLists = new ArrayList<>();
        try {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT ID FROM LISTS WHERE AUTHOR_ID = ?");
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int listID = rs.getInt("ID");
                EverythingList list = getList(listID);
                if(list != null) {
                    userLists.add(list);
                }
            }
            rs.close();
            ps.close();
        } catch(Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return userLists;
    }

    public List<EverythingRankings> getRankingsByUser(String userID) {
        List<EverythingRankings> userRankings = new ArrayList<>();
        try {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT DISTINCT LIST_ID FROM RANKINGS WHERE AUTHOR_ID = ?");
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int listID = rs.getInt("LIST_ID");
                EverythingList list = getList(listID);
                if(list != null) {
                    EverythingRankings ranking = new EverythingRankings(list.getID(), list.getTitle(), list.getAuthorID(), list.getAccess(), list.getPubDate().getTime(), list.getUpdate().getTime());
                    ranking.setItems(getRankings(listID, userID));
                    userRankings.add(ranking);
                }
            }
            rs.close();
            ps.close();
        } catch(Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return userRankings;
    }

    public String getAuthor(int listID){
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT AUTHOR_ID FROM LISTS WHERE ID = ?");
            ps.setInt(1, listID);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                rs.close();
                ps.close();
                return null;
            }
            String author = rs.getString("AUTHOR_ID");
            rs.close();
            ps.close();
            return author;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a list and its associated items from the database.
     */
    public boolean deleteList(int listID){
        try{
            // Delete items first
            PreparedStatement ps1 = c.prepareStatement(
                "DELETE FROM ITEMS WHERE LIST_ID = ?");
            ps1.setInt(1, listID);
            ps1.executeUpdate();
            ps1.close();

            // Delete rankings for this list
            PreparedStatement ps2 = c.prepareStatement(
                "DELETE FROM RANKINGS WHERE LIST_ID = ?");
            ps2.setInt(1, listID);
            ps2.executeUpdate();
            ps2.close();

            // Delete the list
            PreparedStatement ps3 = c.prepareStatement(
                "DELETE FROM LISTS WHERE ID = ?");
            ps3.setInt(1, listID);
            int rows = ps3.executeUpdate();
            ps3.close();
            return rows > 0;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public int addItem(int listID, String itemName){
        try{
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO ITEMS (NAME, LIST_ID) VALUES (?, ?)");
            ps.setString(1, itemName);
            ps.setInt(2, listID);
            ps.executeUpdate();
            ps.close();
            return 1;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public boolean itemExistsInList(int listID, String itemName){
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM ITEMS WHERE LIST_ID = ? AND LOWER(NAME) = LOWER(?)");
            ps.setInt(1, listID);
            ps.setString(2, itemName);
            ResultSet rs = ps.executeQuery();
            int count = rs.getInt(1);
            rs.close();
            ps.close();
            return count > 0;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves list items for a given list.
     * Referenced in Rank List DSD: retrieveListItems(listID)
     */
    public List<String> retrieveListItems(int listID){
        return getItemsForList(listID);
    }

    public List<String> getItemsForList(int listID){
        List<String> items = new ArrayList<>();
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT NAME FROM ITEMS WHERE LIST_ID = ?");
            ps.setInt(1, listID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                items.add(rs.getString("NAME"));
            }
            rs.close();
            ps.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return items;
    }

    // ===== RANKING OPERATIONS =====

    public int addRanking(int listID, String authorID, String itemName, int rankPosition){
        try{
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO RANKINGS (LIST_ID, AUTHOR_ID, ITEM_NAME, RANK_POSITION) VALUES (?, ?, ?, ?)");
            ps.setInt(1, listID);
            ps.setString(2, authorID);
            ps.setString(3, itemName);
            ps.setInt(4, rankPosition);
            ps.executeUpdate();
            ps.close();
            return 1;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public List<String> getRankings(int listID, String authorID){
        List<String> ranked = new ArrayList<>();
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT ITEM_NAME FROM RANKINGS WHERE LIST_ID = ? AND AUTHOR_ID = ? ORDER BY RANK_POSITION ASC");
            ps.setInt(1, listID);
            ps.setString(2, authorID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ranked.add(rs.getString("ITEM_NAME"));
            }
            rs.close();
            ps.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return ranked;
    }

    public int deleteRankings(int listID, String authorID){
        try{
            PreparedStatement ps = c.prepareStatement(
                "DELETE FROM RANKINGS WHERE LIST_ID = ? AND AUTHOR_ID = ?");
            ps.setInt(1, listID);
            ps.setString(2, authorID);
            int rows = ps.executeUpdate();
            ps.close();
            return rows;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Updates the ranked list data in the database.
     * Referenced in Rank List DSD: updateRankedList(listID, rankedData)
     */
    public boolean updateRankedList(int listID, String authorID, List<String> rankedData){
        deleteRankings(listID, authorID);
        for(int i = 0; i < rankedData.size(); i++){
            int result = addRanking(listID, authorID, rankedData.get(i), i + 1);
            if(result == 0) return false;
        }
        return true;
    }

    public boolean rankingExists(int listID, String authorID){
        try{
            PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM RANKINGS WHERE LIST_ID = ? AND AUTHOR_ID = ?");
            ps.setInt(1, listID);
            ps.setString(2, authorID);
            ResultSet rs = ps.executeQuery();
            int count = rs.getInt(1);
            rs.close();
            ps.close();
            return count > 0;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    // Cleanup for testing
    public void close(){
        try{
            if(c != null) c.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
