/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
public class Utils {

    public static void sleep(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void waitUntilFileCreated(String filePath) {
        File file;
        do {
            file = new File(filePath);
            sleep(500);
        } while (!file.exists());
    }
    
    public static boolean waitUntilFileCreated(String filePath, int timeoutMillisec) {
        File file;
        int elapsed = 0;
        do {
            file = new File(filePath);
            sleep(500);
            elapsed += 500;
            if (elapsed >= timeoutMillisec) {return false;}
        } while (!file.exists());
        return true;
    }

    public static void waitUntilFileDeleted(String filePath) {
        File file;
        do {
            file = new File(filePath);
            sleep(500);
        } while (file.exists());
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static int findFreePort() {
        boolean portAvailable = false;
        int port = 6003;
        ServerSocket socket = null;
        while (! portAvailable) {
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                portAvailable = false;
            } finally {
                if (socket != null) {
                    portAvailable = true;
                    try {
                        socket.close();
                    } catch (IOException e) { /* e.printStackTrace(); */ }
                }
            }
            port++;
        }
        return port;
    }
    
    public static String getJdtraceHome() {
        return System.getenv("JDTRACE_HOME");
    }
    
    public java.io.File createTempFile() {
        java.io.File tmpfile = null;
        try {
            tmpfile = java.io.File.createTempFile("jdtrace", ".d");
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpfile;
    }
    
    public void removeTmpFile(java.io.File file) {
        file.delete();
    }
    
    public static String newScriptName(String scriptFile) {
        String newName;
        int suffixAt = scriptFile.lastIndexOf(".d");
        if (suffixAt == scriptFile.length() - 2) {
            newName = scriptFile.substring(0, suffixAt) + "_preprocessed.d";
        } 
        else {
            newName = scriptFile + "_preprocesed";
        }
        return newName;
    }
    
    public static String findReplaceScriptFile(String[] args) {
        String scriptFile = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                if (i + 1 == args.length) return null;
                scriptFile  = args[i + 1];
                args[i + 1] = newScriptName(scriptFile);
                break;
            }
            else if (args[i].equals("-n") || args[i].equals("-f") || args[i].equals("-m")) {
                scriptFile = CmdlineToFile.getInstance().getFile().getPath();
            }
        }
        return scriptFile;
    }
}
