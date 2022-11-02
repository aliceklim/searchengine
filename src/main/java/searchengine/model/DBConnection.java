package searchengine.model;

import java.sql.*;


public class DBConnection
{
    public static void main(String[]args) throws SQLException {
        DBConnection con = new DBConnection();
        addToDataBase("https://stackoverflow.com/questions/8645889/there-can-be-only-one-auto-column", 200, "content");
    }
    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3306/search_engine?allowPublicKeyRetrieval=true&useSSL=false";
    private static String dbUser = "user";
    private static String dbPass = "pass";
    private static StringBuilder insertQuery = new StringBuilder();
    public static Connection getConnection()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                System.out.println("Creating tables");
                connection.createStatement().execute("DROP TABLE IF EXISTS site");
                connection.createStatement().execute("CREATE TABLE site(" +
                        "id INT AUTO_INCREMENT primary key NOT NULL, " +
                        "status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL, " +
                        "status_time DATETIME NOT NULL, " +
                        "last_error TEXT, " +
                        "url VARCHAR(255) NOT NULL, " +
                        "name VARCHAR(255) NOT NULL)");
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "site_id INT NOT NULL, " +
                        "path TEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL, " +
                        "PRIMARY KEY(id), KEY(path(50)))");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT primary key, " +
                        "site_id INT NOT NULL, " +
                        "lemma VARCHAR(255) NOT NULL, " +
                        "frequency INT NOT NULL)");
                connection.createStatement().execute("DROP TABLE IF EXISTS `index`");
                connection.createStatement().execute("CREATE TABLE `index`(" +
                        "id INT NOT NULL AUTO_INCREMENT primary key, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "`rank` FLOAT NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static void addToDataBase(String path, int code, String content)throws SQLException{
        String sql = "INSERT INTO page (path, code, content) VALUES ('" + editUrl(path) + "',  '" + code + "',  '" + content + "')";
        DBConnection.getConnection().createStatement().execute(sql);
    }
    private static String editUrl(String url){
        String[] split = url.split("^.*?/(/[^/]*){1}");
        System.out.println(split[1]);
        return split[1];

    }

    public static void executeMultiInsert() throws SQLException{
        String sql = "INSERT INTO page(path, code, content) " + "VALUES" + insertQuery.toString();
        DBConnection.getConnection().createStatement().execute(sql);
    }

    public static void fillInStringBuilder(String path, int code, String content) throws SQLException
    {
        insertQuery.append((insertQuery.length() == 0 ? "" : ",") + "('" + editUrl(path) + "', '" + code + "','" + content +"')" );
    }
}



