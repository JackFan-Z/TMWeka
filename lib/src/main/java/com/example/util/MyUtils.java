package com.example.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by jackf on 2016/6/29.
 */
public class MyUtils {

    public static void Log(String tag, String msg) {
        System.out.println(tag + " - " + msg);
    }

    public static ArrayList<String> GetFileListEx(String targetFolder, String[] filter) {
        String [] targetFolders = new String[1];
        targetFolders[0] = targetFolder;
        return  GetFileListEx(targetFolders, filter);
    }

    public static ArrayList<String> GetFileListEx(String [] targetFolders, String[] filter) {
        ArrayList<String> ret = new ArrayList<String>();
        for (String targetFolder: targetFolders) {
            Log("Files", "Target Folder: " + targetFolder);

            File f = new File(targetFolder);
            File[] files = f.listFiles();
            if (files != null) {
                Log("Files", "Size: " + files.length);
            } else {
                Log("Files", "GetFileList get null");
                continue;
            }

            if (filter == null) {
                for (File file : files) {
                    ret.add(file.getPath());
                }
            } else {
                for (File file : files) {
                    for (String ext : filter) {
                        if (file.getName().endsWith(ext)) {
                            ret.add(file.getPath());
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static void makeDirs(String pathName) {
        File tempf = new File(pathName);
        File tempfdir = tempf.getParentFile();
        tempfdir.mkdirs();
    }

    public static String ReadFile(String filepath) {

        String ret = "";
        try {
            InputStream streamtext;
            streamtext = new FileInputStream(filepath);
            if (streamtext != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        streamtext);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                streamtext.close();
                bufferedReader.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log("Jack", "File not found: " + e.toString());
        } catch (IOException e) {
            Log("Jack", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static boolean WriteFile(String path, String data) {
        makeDirs(path);
        OutputStream streamtext;
        try {
            streamtext = new FileOutputStream(path);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    streamtext);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean WriteFile(String path, byte [] data) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        BufferedOutputStream bos = new BufferedOutputStream(out);
        try {
            bos.write(data);
            bos.flush();
            bos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
