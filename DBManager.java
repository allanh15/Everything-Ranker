import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    Connection c = null;
    Statement stmt = null;

    public DBManager(){
        try{
            //Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
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
                  " ACCESS         INT    NOT NULL DEFAULT 0)";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS ITEMS " +
                  "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                  " NAME           TEXT   NOT NULL," +
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
                  " ACCESS         INT    NOT NULL DEFAULT 0)";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS ITEMS " +
                  "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                  " NAME           TEXT   NOT NULL," +
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
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public int addUser(User user){
        try{
            stmt = c.createStatement();
            String sql = "INSERT INTO USERS (ID,USERNAME,PASSWORD,EMAIL) " +
                         "VALUES (" +
                         "'" + user.getID() + "',  " +
                         "'" + user.getUserName() + "',  " +
                         "'" + user.getPassword() + "',  " +
                         "'" + user.getEmail() + "');";
            stmt.executeUpdate(sql);
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    public User getUser(String userName){
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS WHERE USERNAME = '" + userName + "';");
            String id = rs.getString("ID");
            String retUserName = rs.getString("USERNAME");
            String password = rs.getString("PASSWORD");
            String email = rs.getString("EMAIL");
            rs.close();
            return new User(id,retUserName,password,email);
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    // ===== LIST OPERATIONS =====

    public int addList(EverythingList list){
        try{
            stmt = c.createStatement();
            int accessInt = list.getAccess() ? 1 : 0;
            String sql = "INSERT INTO LISTS (TITLE, AUTHOR_ID, ACCESS) " +
                         "VALUES (" +
                         "'" + list.getTitle() + "',  " +
                         "'" + list.getAuthorID() + "',  " +
                         accessInt + ");";
            stmt.executeUpdate(sql);

            // Get the auto-generated ID
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid();");
            int newId = rs.getInt(1);
            rs.close();
            return newId;
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
    }

    public EverythingList getList(int listID){
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM LISTS WHERE ID = " + listID + ";");
            if(!rs.next()){
                rs.close();
                return null;
            }
            int id = rs.getInt("ID");
            String title = rs.getString("TITLE");
            String authorID = rs.getString("AUTHOR_ID");
            boolean access = rs.getInt("ACCESS") == 1;
            rs.close();

            EverythingList list = new EverythingList(id, title, authorID, access);

            // Load items for this list
            Statement stmt2 = c.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT NAME FROM ITEMS WHERE LIST_ID = " + listID + ";");
            while(rs2.next()){
                list.addItem(rs2.getString("NAME"));
            }
            rs2.close();
            return list;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public String getAuthor(int listID){
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT AUTHOR_ID FROM LISTS WHERE ID = " + listID + ";");
            if(!rs.next()){
                rs.close();
                return null;
            }
            String author = rs.getString("AUTHOR_ID");
            rs.close();
            return author;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public int addItem(int listID, String itemName){
        try{
            stmt = c.createStatement();
            String sql = "INSERT INTO ITEMS (NAME, LIST_ID) " +
                         "VALUES ('" + itemName + "', " + listID + ");";
            stmt.executeUpdate(sql);
            return 1;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public boolean itemExistsInList(int listID, String itemName){
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM ITEMS WHERE LIST_ID = " + listID +
                " AND LOWER(NAME) = LOWER('" + itemName + "');");
            int count = rs.getInt(1);
            rs.close();
            return count > 0;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public List<String> getItemsForList(int listID){
        List<String> items = new ArrayList<>();
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME FROM ITEMS WHERE LIST_ID = " + listID + ";");
            while(rs.next()){
                items.add(rs.getString("NAME"));
            }
            rs.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return items;
    }

    // ===== RANKING OPERATIONS =====

    public int addRanking(int listID, String authorID, String itemName, int rankPosition){
        try{
            stmt = c.createStatement();
            String sql = "INSERT INTO RANKINGS (LIST_ID, AUTHOR_ID, ITEM_NAME, RANK_POSITION) " +
                         "VALUES (" + listID + ", " +
                         "'" + authorID + "', " +
                         "'" + itemName + "', " +
                         rankPosition + ");";
            stmt.executeUpdate(sql);
            return 1;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public List<String> getRankings(int listID, String authorID){
        List<String> ranked = new ArrayList<>();
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT ITEM_NAME FROM RANKINGS WHERE LIST_ID = " + listID +
                " AND AUTHOR_ID = '" + authorID + "' ORDER BY RANK_POSITION ASC;");
            while(rs.next()){
                ranked.add(rs.getString("ITEM_NAME"));
            }
            rs.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return ranked;
    }

    public int deleteRankings(int listID, String authorID){
        try{
            stmt = c.createStatement();
            String sql = "DELETE FROM RANKINGS WHERE LIST_ID = " + listID +
                         " AND AUTHOR_ID = '" + authorID + "';";
            int rows = stmt.executeUpdate(sql);
            return rows;
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public boolean rankingExists(int listID, String authorID){
        try{
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM RANKINGS WHERE LIST_ID = " + listID +
                " AND AUTHOR_ID = '" + authorID + "';");
            int count = rs.getInt(1);
            rs.close();
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
