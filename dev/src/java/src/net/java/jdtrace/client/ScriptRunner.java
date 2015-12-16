/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
class ScriptRunner {

    private String fileNameToWaitFor;
    private boolean waitForJdtraceProvider;

    public ScriptRunner(boolean waitJdtraceProvider) {
        fileNameToWaitFor = null;
        waitForJdtraceProvider = waitJdtraceProvider;
    }

    void runScript(String[] args) {
        boolean success;
        try {
            String cmd[] = new String[args.length + 1];
            if (waitForJdtraceProvider) {
                Runtime.getRuntime().exec(Utils.getJdtraceHome() + "/check_for_dtrace_provider.sh");
                success = Utils.waitUntilFileCreated(fileNameToWaitFor, 8000);
                if (! success) {
                    // should throw an exception!
                    System.err.println("DTrace provider creation timeout");
                    System.exit(1);
                }
            }
            cmd[0] = "/usr/sbin/dtrace";
            System.arraycopy(args, 0, cmd, 1, args.length);
            Process p = Runtime.getRuntime().exec(cmd);
            String line;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = err.readLine()) != null) {
                System.err.println(line);
            }
            in.close();
            err.close();
            p.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(ScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFileToWaitFor(String filePath) {
        fileNameToWaitFor = filePath;
    }

}