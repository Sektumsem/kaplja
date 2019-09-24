package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Properties;
import java.sql.SQLException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static com.company.Repaint.*;
import java.awt.Toolkit;

public class GameWindow extends JFrame {

    public static GameWindow game_window;
    public static long last_frame_time;
    public static Image sky;
    public static Image gameover;
    public static Image kap;
    public static Image restart;
    public static float kap_left=200;
    public static float kap_top=-100;
    public static float kap_v=200;
    public static int score=0;
    public static boolean end;
    public static float kap_width = 128;
    public static float kap_height = 128;
    public static boolean pause = false;
    public static float kap_speed_save;

    public static int direction;
    public static int onDirection(){
        int rand =(int) (Math.random()*2+1);
        if(rand == 2) direction = 1;
        else direction = -1;
        System.out.println(direction);
        return direction;
    }

    public static double mousecordX = 0;
    public static double mousecordY = 0;
    public static Entry nameEntry;
    public static Database db;
    public static boolean isRecorded = false;
    public static boolean drawRecords = false;
    public static ArrayList<String> recordsLast = new ArrayList<String>();





    public static void main(String[] args) throws IOException {
        db = new Database("jdbc:mysql://localhost/gamedrop?useLegacyDatetimeCode=false&serverTimezone=Europe/Helsinki", "root", "");
        db.init();

        sky = ImageIO.read(GameWindow.class.getResourceAsStream("background.png"));
        gameover = ImageIO.read(GameWindow.class.getResourceAsStream("game_over.png"));
        kap= ImageIO.read(GameWindow.class.getResourceAsStream("drop.png")).getScaledInstance((int) kap_width,(int) kap_height,Image.SCALE_DEFAULT);
        restart= ImageIO.read(GameWindow.class.getResourceAsStream("restart.png")).getScaledInstance(70,70, Image.SCALE_DEFAULT);
        game_window =new GameWindow();
        game_window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//после закрытия окна будет завершаться программа
        game_window.setLocation(20,100);
        game_window.setSize(906,478);
        game_window.setResizable(false);
        pause=false;

        last_frame_time=System.nanoTime();
        GameField game_field=new GameField();

        game_field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    if(pause) {
                        pause =false;
                        kap_v=kap_speed_save;
                    }
                    else{
                        kap_speed_save=kap_v;
                        kap_v=0;
                        mousecordX = MouseInfo.getPointerInfo().getLocation().getX();
                        mousecordY = MouseInfo.getPointerInfo().getLocation().getY();
                        pause=true;
                    }
                return;
                }
                if(pause) return;
                int x =e.getX();
                int y =e.getY();

                float kap_right= kap_left+kap.getWidth(null);
                float kap_bottom= kap_top+kap.getHeight(null);
                boolean is_drop = x >= kap_left && x <= kap_right && y >= kap_top && y<=kap_bottom;
                if(is_drop){
                    if(!(kap_height<=25 && kap_width<=50)){
                        kap_width=kap_width -1;
                        kap_height=kap_height -2;
                        try{
                            kapResize();
                        }
                        catch (IOException ioe){

                        }


                    }

                    kap_top=-100;
                    kap_left=(int)(Math.random() * (game_field.getWidth() - kap.getWidth(null)));
                    kap_v=kap_v+20;
                    score++;
                    onDirection();
                    game_window.setTitle("Score: "+score);
                }
                if(end){
                    boolean isRestart = x>=0 && x <=0 + restart.getWidth(null)&& y>=0 && y<=0 + restart.getHeight(null);
                    if(isRestart){
                        end=false;
                        score=0;
                        game_window.setTitle("Score: "+score);
                        kap_left=(int)(Math.random() * (game_field.getWidth() - kap.getWidth(null)));
                        kap_v=200;
                        kap_top=-100;
                        kap_width=128;
                        kap_height=128;
                        isRecorded = false;
                        drawRecords = false;
                    }
                }

            }
        });



        nameEntry = new Entry();
        game_window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("keyPress");
                nameEntry.keyPress(e);
                if (nameEntry.isActive && !isRecorded){
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        db.addRecord(nameEntry.text, score);
                        JOptionPane.showMessageDialog(null,"Ваш рекорд был добавлен в БД");
                        isRecorded = true;
                        recordsLast = db.getRecords();
                        drawRecords = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        game_window.add(game_field);
        game_window.setVisible(true);

    }


    private static class GameField extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            onRepaint(g);
            repaint();
        }
    }
}