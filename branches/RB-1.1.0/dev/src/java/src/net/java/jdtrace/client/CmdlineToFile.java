/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author amit
 */
public class CmdlineToFile {
    private File file;
    private BufferedWriter bw = null;
    private final String nameProbePattern = "((([^\\s:]*:)?[^\\s:]*:)?[^\\s:]*:)?(\\S+)\\s*(\\/.*\\/)?\\s*(\\{[^\\}]*\\})";
    private static CmdlineToFile singleInstance = null;
    
    public static CmdlineToFile getInstance() {
        if (singleInstance == null) {
            try {
                singleInstance = new CmdlineToFile(File.createTempFile("jdtrace", ".d"));
            } catch (IOException ex) {
                Logger.getLogger(CmdlineToFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return singleInstance;
    }
    
    protected CmdlineToFile(File infile) {
        file = infile;
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(ScriptWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public File getFile() {
        return file;    
    }
    
    public void writeProbe(String cmdOption, String cmd) {
        if (bw == null) {
            try {
                bw = new BufferedWriter(new FileWriter(file, true));
            } catch (IOException ex) {
                Logger.getLogger(CmdlineToFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        switch (cmdOption) {
            case "-n":
                processNameProbes(cmd);
        }
    }
    
    public void flushFile() {
        try {
            bw.flush();
            bw.close();
            bw = null;
        } catch (IOException ex) {
            Logger.getLogger(CmdlineToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void writeLine(String line) {
        try {
            bw.write(line);
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(ScriptWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void processProbe(String description, String predicate, String action) {
        writeLine(description);
        if (predicate != null) {
            writeLine(predicate);
        }
        action.trim();
        // remove '{' and '}'
        action = action.substring(1, action.length() - 1);
        writeLine("{");
        writeLine(action);
        writeLine("}");
        writeLine("");
                
    } 
    
    void processNameProbes(String cmd) {
        Pattern pattern = Pattern.compile(nameProbePattern);
        Matcher matcher = pattern.matcher(cmd);

        System.out.println("cmd: " + cmd);
        while(matcher.find()) {
            String descriptor, predicate, action;
            if (matcher.group(1) != null) {
                descriptor = matcher.group(1);
            }
            else if (matcher.group(2) != null) {
                descriptor = matcher.group(2);
            }
            else {
                descriptor = matcher.group(3);
            }
            if (descriptor == null) descriptor = "";
            descriptor.trim();
            descriptor = descriptor + matcher.group(4);
            predicate = matcher.group(5);
            action = matcher.group(6);
            processProbe(descriptor, predicate, action);
            System.out.println("descriptor: " + descriptor);
            System.out.println("predicate: " + predicate);
            System.out.println("action: " + action);
        }
    }
}
