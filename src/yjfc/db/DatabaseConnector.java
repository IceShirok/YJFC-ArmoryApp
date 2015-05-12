package yjfc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    public final static String DB_CLASS = "org.sqlite.JDBC";
    public final static String DB_DRIVER = "jdbc:sqlite:";
    public final static String DB_NAME = "blah.db";
    public final static String DB_CHECKOUTITEM_TYPE = "type";
    public final static String DB_CHECKOUTITEM_NUM = "num";
    public final static String DB_CHECKOUTITEM_SIZE = "size";
    public final static String DB_CHECKOUTITEM_HAND = "hand";
    public final static String DB_CHECKOUTITEM_PERSON = "person";
    public final static String DB_CHECKOUTITEM_DATE = "checkout_date";

    static {
        try {
            DatabaseConnector.executeUpdateInDatabase("drop table if exists checkout_item");
            DatabaseConnector.executeUpdateInDatabase("create table checkout_item ("
                    + DB_CHECKOUTITEM_TYPE + " text,"
                    + DB_CHECKOUTITEM_NUM + " int,"
                    + DB_CHECKOUTITEM_SIZE + " text,"
                    + DB_CHECKOUTITEM_HAND + " text,"
                    + DB_CHECKOUTITEM_PERSON + " text,"
                    + DB_CHECKOUTITEM_DATE + " date,"
                    + "PRIMARY KEY (" + DB_CHECKOUTITEM_TYPE + "," + DB_CHECKOUTITEM_NUM + ")"
                    + ");");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getTypesInDatabase()
            throws ClassNotFoundException {
        Class.forName(DB_CLASS);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_DRIVER + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("select type from checkout_item;");
            List<String> aList = new ArrayList<>();

            while(rs.next()) {
                aList.add(rs.getString(DB_CHECKOUTITEM_TYPE));
            }
            return aList;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
    public static List<CheckoutItemPOJO> getCheckoutItemInDatabase(String query)
            throws ClassNotFoundException {
        Class.forName(DB_CLASS);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_DRIVER + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(query);
            List<CheckoutItemPOJO> aList = new ArrayList<>();

            while(rs.next()) {
                CheckoutItemPOJO item = new CheckoutItemPOJO();
                item.setType(rs.getString(DB_CHECKOUTITEM_TYPE));
                item.setNum(rs.getInt(DB_CHECKOUTITEM_NUM));
                item.setSize(rs.getString(DB_CHECKOUTITEM_SIZE));
                item.setHanded(rs.getString(DB_CHECKOUTITEM_HAND));
                item.setPerson(rs.getString(DB_CHECKOUTITEM_PERSON));
                //item.setCheckoutDate(rs.getDate(DB_CHECKOUTITEM_DATE));
                aList.add(item);
            }
            return aList;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public static void executeUpdateInDatabase(String query)
            throws ClassNotFoundException {
        Class.forName(DB_CLASS);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_DRIVER + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

}
