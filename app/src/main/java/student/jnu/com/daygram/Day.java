package student.jnu.com.daygram;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ASUS on 2018/11/16.
 */

public class Day implements Serializable{

    private int day;
    private int month;
    private String content;
    private int year;
    private boolean Empty;


    public Day(){
        this.day=0;
        this.month=0;
        this.content="";
        this.year=0;
        this.Empty=true;

    }

    public Day( int year,int month,int day,String content){
        this.year=year;
        this.month=month;
        this.content=content;
        this.day=day;
        this.Empty=false;
    }

    public Day(Context context, int year, int month, int day){
        read(context, year, month, day);
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public boolean Empty(){
        return this.Empty;
    }


    //写指定日期的日记， 保存该日记项
    public boolean write(Context context){
        String dirsrc = context.getFilesDir() + "/" + year + "/" + month;
        File dir = new File(dirsrc);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String src = context.getFilesDir() + "/" + year + "/" + month + "/" + day;
        File outputFile = new File(src);
        try {
            outputFile.createNewFile();
        } catch (IOException e) {

            return false;
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(outputFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);  //写入到文件
            oos.flush();
            return true;
        } catch (FileNotFoundException e) {

            return false;
        } catch (IOException e) {

            return false;
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {

                }
            }
        }

    }
    //读指定日期的日记
    public boolean read(Context context, int year, int month, int day){
        String src = context.getFilesDir() + "/" + year + "/" + month + "/" + day;
        File inputFile = new File(src);
        if(!inputFile.exists()) return false;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(inputFile);
            ois = new ObjectInputStream(fis);
            Day temp =  (Day)ois.readObject();
            this.year = temp.year;
            this.month = temp.month;
            this.day = temp.day;
            this.content = temp.content;
            this.Empty = temp.Empty;
            return true;

        } catch (FileNotFoundException e) {

            return false;
        } catch (IOException e) {

            return false;
        } catch (ClassNotFoundException e) {

            return false;
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {

                }
            }
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {

                }
            }
        }
    }

    //删除指定日期的日记
    public boolean delete(Context context) {
        String src = context.getFilesDir() + "/" + year + "/" + month + "/" + day;
        File openFile = new File(src);
        if (openFile.exists()) {
            openFile.delete();
            return true;
        }
        return false;
    }


}
