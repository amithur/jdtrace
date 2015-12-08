/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.util.List;

/**
 *
 * @author amit
 */
public class Jdtrace {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        String scriptFile = Utils.findReplaceScriptFile(args);
        String newScriptFile = Utils.newScriptName(scriptFile);
        PreProcessor preProcessor = new PreProcessor(scriptFile, newScriptFile, args);
        ArgParser argParser = preProcessor.getArgParser();
        List<InstrumentationItem> instrumenationList = preProcessor.process();
        ScriptRunner scriptRunner = new ScriptRunner(preProcessor.areJdtraceProbesToProcess());
        AgentController agentController = new AgentController(instrumenationList);
        if (argParser.getPort() != 0)
            agentController.setInitialPort(argParser.getPort());
        agentController.attachAndInstrumentAll();
        if (argParser.isInstrumentOnly() || argParser.isNoRun()) {
            Utils.waitUntilFileCreated(agentController.getFileToWaitFor());
            Utils.deleteFile(agentController.getFileToWaitFor());
        }
        else {
            scriptRunner.setFileToWaitFor(agentController.getFileToWaitFor());
            Utils.deleteFile(agentController.getFileToWaitFor());
            scriptRunner.runScript(argParser.getDtraceArgs());
        }
        agentController.closeAgent(); // closeAgent() includes deinstrument_all()
        agentController.detachAll();
    }
}
