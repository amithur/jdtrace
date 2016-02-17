/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author amit
 */
public class Jdtrace {

    /**
     * @param args the command line arguments
     */
    
    private static void setLoggingLevel() {
        String [] classesToSetLevel = {AgentController.class.getName(),
                                       ArgParser.class.getName(),
                                       Jdtrace.class.getName(),
                                       CmdlineToFile.class.getName()};
        for (int i = 0; i < classesToSetLevel.length; i++) {
            java.util.logging.Logger.getLogger(classesToSetLevel[i]).setLevel(Level.FINEST);
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        setLoggingLevel();
        String scriptFile = Utils.findReplaceScriptFile(args);
        String newScriptFile = Utils.newScriptName(scriptFile);
        java.util.logging.Logger.getLogger("Jdtrace").info("Preprocessing...");
        PreProcessor preProcessor = new PreProcessor(scriptFile, newScriptFile, args);
        ArgParser argParser = preProcessor.getArgParser();
        java.util.logging.Logger.getLogger("Jdtrace").info("Pre Instrumentation...");
        List<InstrumentationItem> instrumenationList = preProcessor.process();
        ScriptRunner scriptRunner = new ScriptRunner(preProcessor.areJdtraceProbesToProcess());
        AgentController agentController = new AgentController(instrumenationList);
        if (argParser.getPort() != 0)
            agentController.setInitialPort(argParser.getPort());
        java.util.logging.Logger.getLogger("Jdtrace").info("Attaching and instrumenting...");
        agentController.attachAndInstrumentAll();
        if (argParser.isInstrumentOnly() || argParser.isNoRun()) {
            Utils.waitUntilFileCreated(agentController.getFileToWaitFor());
            Utils.deleteFile(agentController.getFileToWaitFor());
        }
        else {
            scriptRunner.setFileToWaitFor(agentController.getFileToWaitFor());
            Utils.deleteFile(agentController.getFileToWaitFor());
            java.util.logging.Logger.getLogger("Jdtrace").info("Running...");
            scriptRunner.runScript(argParser.getDtraceArgs());
        }
        agentController.closeAgent(); // closeAgent() includes deinstrument_all()
        agentController.detachAll();
    }
}
