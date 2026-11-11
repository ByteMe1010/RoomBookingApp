package org.example.Core.DatabaseModule;

/*
this is the main class that used to connect the database. fill the url, username, password and it will work. other two classes are examples
how to use this, database table names and database column names can be different in those.

Singletons
this pattern named Singleton, target is keeping a non static class like a static class with keeping only one object of that class alive.
first call creates a object of given class and other calls re uses that object.

when user calls .getInstance for the first time, it creates new DBConnection Object. and when they call it the next time, that object is reused.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection INSTANCE;

    //TODO: Fill those according to ports and paths before running the program
    public static String databaseURL= "jdbc:sqlserver://10.166.91.161:1433;databaseName=HOTEL_DB;encrypt=true;trustServerCertificate=true;";
    public static String databaseUsername = "sa";
    public static String databasePassword = "YourPassword123";
    private Connection connection;

    public static DBConnection getInstance(){
        if (INSTANCE == null || INSTANCE.connection == null){
            INSTANCE = new DBConnection();
        }
        return INSTANCE;
    }

    private DBConnection(){
        try {
            connection = DriverManager.getConnection(databaseURL,databaseUsername,databasePassword);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check does database URL,Username and password settted up correctly in DBConnection.java file\n and check does database is active in given port, also try adding trustServerCertificate=true to database URL properties list(Ask a AI how to)");
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
}
