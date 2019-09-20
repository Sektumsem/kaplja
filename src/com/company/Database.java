package com.company;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public String host, user, password;
    public Connection connection;

    public Database(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public void addRecord(String name, int score) {
        try {
            String sql = String.format("INSERT INTO users(Name, Score) VALUES ('%s',%d)", name, score);
            Statement st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ArrayList<String> getRecords() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM users");
            while (res.next()) {
                int score = res.getInt(3);
                String name = res.getString(2);

                result.add(name + ": " + score);
            }
            res.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void init() {

            String url = "jdbc:mysql://localhost/gamedrop?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki";
            String username = "root";
            String password = "";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connection to Store DB succesfull!");
            } catch (Exception ex) {
                System.out.println("Connection failed...");
            }
        }
    }
