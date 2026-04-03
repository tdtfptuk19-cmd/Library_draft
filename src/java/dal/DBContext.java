/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

/**
 *
 * @author thien
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    public Connection getConnection() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;encrypt=false;";
            String username = "sa";
            String password = "123";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
            return null;
        }
    }
}
