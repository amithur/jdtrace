/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
class ScriptWriter {

    private BufferedWriter bw = null;
    
    public ScriptWriter(String newFile) {
        try {
            bw = new BufferedWriter(new FileWriter(newFile));
        } catch (IOException ex) {
            Logger.getLogger(ScriptWriter.class.getName()).log(Level.SEVERE, null, ex);
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

    void addProxyProbe(String line) {
        writeLine("");
        writeLine(line);
        writeLine("{");
        writeLine("    self->strPtr = (char *)copyin(arg1, args[2]+1);");
        writeLine("    self->strPtr[(int)args[2]] = '\\0';");
        writeLine("    self->classStr = (string)self->strPtr;");
        writeLine("    self->strPtr = (char *)copyin(arg3, (int)args[4]+1);");
        writeLine("    self->strPtr[(int)args[4]] = '\\0';");
        writeLine("    self->methodStr = (string)self->strPtr;");
        writeLine("    self->probeclass = self->classStr;");
        writeLine("    self->probemethod = self->methodStr;");
        writeLine("}");
        writeLine("");
    }
    
    public void flushAndclose() {
        try {
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ScriptWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void addBeforeOrMergePredicate(String line, int curProbeId) {
        if (isPredicate(line)) {
            mergeProbeIdToPredicate(line, curProbeId);
        }
        else {
            addProbeIdPredicate(line, curProbeId);
            writeLine(line);
        }
    }

    private boolean isPredicate(String line) {
        return line.matches("^\\s*\\/.*\\/\\s*$");
    }

    private void addProbeIdPredicate(String line, int probeId) {
        String predicateLine = "/args[7] == " + probeId + "/";
        writeLine(predicateLine);
    }

    private void mergeProbeIdToPredicate(String line, int probeId) {
        int start = line.indexOf('/');
        int end = line.lastIndexOf('/');
        String newLine = line.substring(0, start);
        newLine += "/(args[7] == " + probeId + ") && (";
        newLine += line.substring(start + 1, end);
        newLine += ")";
        newLine += line.substring(end);
        writeLine(newLine);
    }
    
}
