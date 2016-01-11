/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
class ScriptReader {
    
    private BufferedReader br  = null;

    ScriptReader(String originalFile) {
        try {
            br = new BufferedReader(new FileReader(originalFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String nextLine() {
        if (br == null) return null;
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ScriptReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return line;
    }
    
}
