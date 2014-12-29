/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.util.StringTokenizer;

/**
 *
 * @author amit
 */
class DescriptionConverter {
    String origProbeDescription;
    String pid, pidSubs;
    String classPattern, methodPattern;
    String probeName;
    ArgParser argParser;
    boolean endsWithComma = false;

    public DescriptionConverter(String line, ArgParser ap) {
        origProbeDescription = line;
        argParser = ap;
        parseProbeDescription();
    }

    String convert(String line) {
        String convertedName;
        if (probeName.equals("entry")) {
            convertedName = "method_entry";
        }
        else if (probeName.equals("return")) {
            convertedName = "method_return";
        }
        else {
            convertedName = ".*";
        }
        return "VvmJsdtProvider" + pidSubs + ":::" + convertedName;
    }
    
    public String getPid() {
        return pid;
    }
    
    public String getClassPattern() {
        return classPattern;
    }
    
    public String getMethodPattern() {
        return methodPattern;
    }

    public String getProbeName() {
        return probeName;
    }
    private void parseProbeDescription() {
        String [] tokens;
        tokens = origProbeDescription.split(":", 4);
        if (tokens.length < 4) return; // not good... should throw an exception
        if (tokens[0].matches("jsdt.*")) {
            pidSubs = tokens[0].substring(4);
            if (pidSubs.matches("\\$\\d+")) {
                pid = argParser.getFreeArg(new Integer(pidSubs.substring(1)).intValue());
            }
            else if (pidSubs.equals("$target")) 
                pid = argParser.getTarget();
        } else {
            return;
        }
        if (tokens[1].isEmpty())
            classPattern = ".*";
        else
            classPattern = tokens[1];
        if (tokens[2].isEmpty())
            methodPattern = ".*";
        else
            methodPattern = tokens[2];
        if (origProbeDescription.matches(".*,\\s*$")) {
            endsWithComma = true;
            tokens[3] = tokens[3].substring(0, tokens[3].indexOf(","));
        }
        if (tokens[3].isEmpty())
            probeName = ".*";
        else
            probeName = tokens[3];
    }
    
    public boolean commaAttheEnd() {
        return endsWithComma;
    }
    
}
