package net.java.jdtrace.agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class CommandListener {

    private static int port = 5679;
    private static CommandExecutor cmdExecutor = null;
    ServerSocket welcomeSocket = null;
    private static final Logger logger = Logger.getLogger(CommandListener.class.getName() );

    public CommandListener(CommandExecutor cmdExe) {
        cmdExecutor = cmdExe;
    }

    public void setPort(int newPort) {
        port = newPort;
    }

    public void listen() throws Exception {
        String clientSentence = null;
        String capitalizedSentence;
        welcomeSocket = new ServerSocket(port);
        boolean goOn = true;

        while (goOn) {
            logger.info("listening...");
            Socket connectionSocket = welcomeSocket.accept();
            logger.info("got something...");
            connectionSocket.setTcpNoDelay(true);
            connectionSocket.setPerformancePreferences(0, 2, 0);
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            logger.info("Received: " + clientSentence);
            goOn = parseAndExec(clientSentence);
            outToClient.writeBytes("completed");
            outToClient.flush();
            logger.info("content flushed");
            outToClient.close();
            logger.info("content closeed");
            connectionSocket.close();
        }
    }

    private boolean parseAndExec(String clientSentence) {
        String cmd, classPtrn, methodPtrn;
        boolean dumpOnly = false;
        String probeName = null;
        String probeId = null;
        StringTokenizer tknzr = new StringTokenizer(clientSentence);
        if (tknzr.hasMoreTokens()) {
            cmd = tknzr.nextToken();
            logger.info("first token: " + cmd);
        } else {
            return true;
        }
        switch (cmd) {
            case "dump":
                dumpOnly = true;
                cmdExecutor.doDumpOnly();
                logger.info("about to cascade to instrument");
            // cascade through instrument now
            case "instrument":
                while (tknzr.hasMoreTokens()) {
                    classPtrn = tknzr.nextToken();
                    logger.info("classPtrn: " + classPtrn);
                    // cascade through instr
                    if (!dumpOnly) {
                        if (tknzr.hasMoreTokens()) {

                            methodPtrn = tknzr.nextToken();
                            if (tknzr.hasMoreTokens()) {
                                probeName = tknzr.nextToken();
                            }
                            else {
                                return true;
                            }
                            if (tknzr.hasMoreTokens()) {
                                probeId = tknzr.nextToken();
                            }
                            else {
                                return true;
                            }

                        } else {
                            return true;
                        }
                    } else {
                        methodPtrn = "ignore";
                    }
                    cmdExecutor.addMatchClasses(classPtrn, methodPtrn, probeName, probeId);
                }
                cmdExecutor.doRetransform();
                logger.info("going to clean");
                cmdExecutor.cleanAfterInstrumentation();
                break;
            case "deinstrument_all":
                cmdExecutor.deinstrumentAll();
                break;
            case "close_agent":
                cmdExecutor.deinstrumentAll();
                try {
                    logger.info("Closing Listener server socket");
                    welcomeSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                cmdExecutor.closeAgent();
                return false;
        }
        return true;
    }
}
