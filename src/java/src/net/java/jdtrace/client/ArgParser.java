/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amit
 */
class ArgParser {

    String[] args;
    List<String> freeArgs, dtraceArgs;
    private String scriptName = null;
    private String target = null;
    private boolean instrumentOnly = false;
    private boolean norun = false;
    private int port = 0;
    
    public ArgParser(String[] a) {
        args = a;
        freeArgs = new ArrayList();
        dtraceArgs = new ArrayList();
        parse();
    }
    
    public int getPort() {
        return port;
    }

    List<String> getFreeArgs() {
        return freeArgs;
    }
    
    String [] getDtraceArgs() {
        String [] dargs = dtraceArgs.toArray(new String[0]);
        return dargs;
    }
    
    String getFreeArg(int i) {
        i--; // parameters start from 1: $1, $2, $3...
        if (i < freeArgs.size())
            return freeArgs.get(i);
        return null;
    }
    
    String getScriptName() {
        return scriptName;
    }
    
    String getTarget() {
        return target;
    }
    
        public boolean isInstrumentOnly() {
        return instrumentOnly;
    }
    
    public boolean isNoRun() {
        return norun;
    }

    private void parse() {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-instrument-only":
                    instrumentOnly = true;
                    norun = true;
                    break;
                case "-norun":
                    norun = true;
                    break;
                case "-port":
                    i++;
                    if (i < args.length)
                        port = Integer.parseInt(args[i]);
                    break;
                case "-s":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    if (i < args.length)
                        scriptName = args[i];
                    break;
                case "-p":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    if (i < args.length)
                        target = args[i];
                    break;
                case "-D":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-b":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-c":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-U":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-x":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-X":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-P":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-m":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-f":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-n":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                case "-i":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                default:
                    if (! args[i].startsWith("-")) {
                        freeArgs.add(args[i]);
                        dtraceArgs.add(args[i]);
                    }
            }
        }
    }
}
