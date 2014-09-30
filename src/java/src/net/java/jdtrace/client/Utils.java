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
        System.out.println("port found: " + port);
        return port;
    }
}
