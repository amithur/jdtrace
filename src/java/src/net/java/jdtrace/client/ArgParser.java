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

    String[] getDtraceArgs() {
        String[] dargs = dtraceArgs.toArray(new String[0]);
        return dargs;
    }

    String getFreeArg(int i) {
        i--; // parameters start from 1: $1, $2, $3...
        if (i < freeArgs.size()) {
            return freeArgs.get(i);
        }
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
                    if (i < args.length) {
                        port = Integer.parseInt(args[i]);
                    }
                    break;
                case "-s":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    if (i < args.length) {
                        scriptName = args[i];
                    }
                    break;
                case "-p":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    if (i < args.length) {
                        target = args[i];
                    }
                    break;
                case "-q":
                    dtraceArgs.add(args[i]);
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
                    String cmd;
                    dtraceArgs.add("-s");
                    i++;
                    cmd = args[i];
                    // if argument starts with a quote, then add next arguments to the command until
                    // the closing quote appears (at the end of the argument)
                    if (args[i].startsWith("'")) {
                        //System.out.println("args["+ i +"] = " + args[i]);
                        cmd = cmd.substring(1, cmd.length()); // chop leading "'"
                        if (!args[i].endsWith("'")) {
                            while (++i < args.length) {
                                cmd += (" " + args[i]);
                                //System.out.println("args["+ i +"] = " + args[i]);
                                if (args[i].endsWith("'")) {
                                    cmd = cmd.substring(0, cmd.length() - 1); // chop trailing "'"
                                    break;
                                }
                            }
                        }
                        else {
                            cmd = cmd.substring(0, cmd.length() - 2); // chop trailing "'"
                        }
                    }
                    
                    //System.out.println("CmdlineToFile cmd: " + cmd);

                    CmdlineToFile.getInstance().writeProbe("-n", cmd);
                    CmdlineToFile.getInstance().flushFile();
                    scriptName = CmdlineToFile.getInstance().getFile().getPath();
                    dtraceArgs.add(Utils.newScriptName(scriptName));
                    break;
                case "-i":
                    dtraceArgs.add(args[i]);
                    i++;
                    dtraceArgs.add(args[i]);
                    break;
                default:
                    if (!args[i].startsWith("-")) {
                        freeArgs.add(args[i]);
                        dtraceArgs.add(args[i]);
                    }
            }
        }
    }
}
