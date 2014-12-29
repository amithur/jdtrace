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

    private static String findReplaceScriptFile(String[] args) {
        String scriptFile = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                if (i + 1 == args.length) return null;
                scriptFile  = args[i + 1];
                args[i + 1] = newScriptName(scriptFile);
                break;
            }
        }
        return scriptFile;
    }

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        String scriptFile = findReplaceScriptFile(args);
        String newScriptFile = newScriptName(scriptFile);
        PreProcessor preProcessor = new PreProcessor(scriptFile, newScriptFile, args);
        ArgParser argParser = preProcessor.getArgParser();
        ScriptRunner scriptRunner = new ScriptRunner();
        List<InstrumentationItem> instrumenationList = preProcessor.process();
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

    private static String newScriptName(String scriptFile) {
        String newName;
        int suffixAt = scriptFile.lastIndexOf(".d");
        if (suffixAt == scriptFile.length() - 2) {
            newName = scriptFile.substring(0, suffixAt) + "_preprocessed.d";
        } 
        else {
            newName = scriptFile + "_preprocesed";
        }
        return newName;
    }

}
