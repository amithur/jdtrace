/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
class AgentController {

    List<InstrumentationItem> toInstrumentList = null;
    HashMap<String, ArrayList<String>> patternsListPerPid = null;
    List<VirtualMachine> vms = null;
    private static HashSet<String> agentStartedMap;
    private static String agentPath = "/export/home/amit/javaOne/Instrumentationagent.jar";
    private static String agentLibraryPath = null;
    private static int portToUse = 6003;
    private static List<String> zones;
    private static List<String> zonesPath;
    private HashMap<String, Integer> ports;
    private static String fileToWaitFor = null; 
    private static String findTargetUtil = null;

    public void setInitialPort(int p) {
        portToUse = p;
    }
    
    private void loadAgentProperties() {
        Properties props = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(Utils.getJdtraceHome() + "/" + "agent-properties");
            props.load(in);
            String path = props.getProperty("agent-path");
            if (path != null && !path.isEmpty()) {
                agentPath = Utils.getJdtraceHome() + "/" + path;
            }
            String str = props.getProperty("zones");
            if (str != null) {
                String[] zonesProp = props.getProperty("zones").split(" ");
                if (zonesProp.length > 0) {
                    zones = Arrays.asList(zonesProp);
                }
            }
            str = props.getProperty("zones_path");
            if (str != null) {
                String[] zonesPathProp = str.split(" ");
                if (zonesPathProp.length > 0) {
                    zonesPath = Arrays.asList(zonesPathProp);
                }
            }
            str = props.getProperty("agent-library-path");
            if (str != null && !str.isEmpty()) {
                agentLibraryPath = str;
            }
            
            findTargetUtil = props.getProperty("get-process-host-util");
            fileToWaitFor = props.getProperty("file-to-wait-for");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void delegateToZone(String zone, HashMap<String, HashSet<String>> patternsPerPidSubset) {
         String pathToWrite = zonesPath.get(zones.indexOf(zone));
         // create a file to write per pid patterns.
    }
    
    public String getFileToWaitFor() {
        return fileToWaitFor;
    }

    static final class COMMANDS {

        public static String INSTRUMENT = "instrument";
        public static String DEINSTRUMENT = "deinstrument_all";
        public static String DUMP = "dump";
        public static String CLOSE = "close_agent";
    }

    AgentController(List<InstrumentationItem> instrumenationList) {
        toInstrumentList = instrumenationList;
        patternsListPerPid = new HashMap();
        agentStartedMap = new HashSet();
        ports = new HashMap();
        vms = new ArrayList();
        loadAgentProperties();
    }

    void attachAndInstrumentAll() {
        preprocessList();
        boolean areZonePids = false;
        for (String pid : patternsListPerPid.keySet()) {
            //System.out.println("calling getTargetHost(" + pid +")");
            String host = getTargetHost(pid);
            Logger.getLogger(this.getClass().getName()).info("ignoring zone type");
            if (true) {
            //if (host.equals("localhost")) {
                attachAndInitAgent(pid);
                sendInstrumentCommand(pid, patternsListPerPid.get(pid));
            } else {
                areZonePids = true;
            }
        }
        if (areZonePids) {
            delegateToZones();
        }
    }

    void delegateToZones() {
        /* Currently do nothing
        HashMap<String, HashSet<String>> patternsPerPidSubset = new HashMap();
        for (String zone : zones) {
            for (String pid : patternsSetPerPid.keySet()) {
                if (getTargetHost(pid).equals(zone)) {
                    HashSet patternsToAdd = patternsSetPerPid.get(pid);
                    patternsPerPidSubset.put(pid, patternsToAdd);
                }
            }
            delegateToZone(zone, patternsPerPidSubset);
        }
        */
    }

    void deinstrumentAll() {
        for (String pid : patternsListPerPid.keySet()) {
            sendDeInstrumentAllCommand(pid);
        }
    }

    void detachAll() {
        for (VirtualMachine vm : vms) {
            try {
                vm.detach();
            } catch (IOException ex) {
                Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void closeAgent() {
        for (String pid : agentStartedMap) {
            try {
                sendCommand(pid, COMMANDS.CLOSE);
            } catch (Exception ex) {
                //Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void preprocessList() {
        for (InstrumentationItem item : toInstrumentList) {
            String pid = item.getPid();
            String classPattern = item.getClassPattern();
            String methodPattern = item.getMethodPatttern();
            int probeId = item.getProbeId();
            String probeName = item.getProbeName();

            boolean doAdd = false;
            ArrayList existingPatterns;
            String patternToAdd = classPattern + " " + methodPattern + " " +
                    probeName + " " + probeId;
            if (patternsListPerPid.containsKey(pid)) {
                existingPatterns = patternsListPerPid.get(pid);
                if (!existingPatterns.contains(patternToAdd)) {
                    doAdd = true;
                }
            } else {
                existingPatterns = new ArrayList<String>();
                doAdd = true;
            }
            if (doAdd) {
                existingPatterns.add(patternToAdd);
                patternsListPerPid.put(pid, existingPatterns);
            }
        }
    }

    private void attachAndInitAgent(String pid) {
        try {
            String vmId = pid;
            VirtualMachine vm = VirtualMachine.attach(vmId);
            Logger.getLogger(this.getClass().getName()).info("vm attached");
            vms.add(vm);
            int port = findPortFor(pid);
            String args = "port=" + port;
            if (agentLibraryPath != null) {
                args += " libpath=" + Utils.getJdtraceHome() + "/" + agentLibraryPath;
            }
            vm.loadAgent(agentPath, args);
            Logger.getLogger(this.getClass().getName()).info("agent loaded");
            
            agentStartedMap.add(pid);
            Thread.sleep(10); // let the agent load complete
            return;

        } catch (AttachNotSupportedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (AgentLoadException ex) {
            ex.printStackTrace();
        } catch (AgentInitializationException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void sendInstrumentCommand(String pid, ArrayList<String> patterns) {
        String cmd = COMMANDS.INSTRUMENT;
        if (patterns.isEmpty()) {
            return;
        }
        for (String pattern : patterns) {
            cmd += (" " + pattern);
        }
        try {
            sendCommand(pid, cmd);
            //System.out.println("AgentConroller, sent command to " + pid +": " + cmd);
            //sendCommand(pid, "dump");
        } catch (Exception ex) {
            Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendDeInstrumentAllCommand(String pid) {
        String cmd = COMMANDS.DEINSTRUMENT;
        try {
            sendCommand(pid, cmd);
        } catch (Exception ex) {
            // don't output currently - target process might have already finifshed
            // and we currently don't want this exception to be reported
            //Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int findPortFor(String pid) {
        // a naive impelemtation. Need to improve - look for a really free port
        if (ports.containsKey(pid)) {
            return ports.get(pid);
        }
        //int port = Utils.findFreePort();
        //ports.put(pid, port);
        //return port;
        ports.put(pid, portToUse);
        return portToUse++;
    }

    public String sendCommand(String pid, String cmd) throws Exception {
        String answer;
        int destinationPort;

        // first make sure agent is on
        destinationPort = findPortFor(pid);
        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(getTargetHost(pid), destinationPort);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes(cmd + '\n');
        //System.out.println("sent: " + cmd);
        answer = inFromServer.readLine();
        //System.out.println("answer: " + answer);

        outToServer.close();
        inFromServer.close();
        clientSocket.close();
        return answer;
    }

    String getTargetHost(String pid) {
        String host = "localhost";
        
        /*
        * only local host is currently supported
        
        String cmd = Utils.getJdtraceHome() + "/gettargethost " + pid;
        if (findTargetUtil != null)
            cmd = findTargetUtil + " " + pid;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            //System.out.println("Going to exec: " + cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.waitFor();
            int est = p.exitValue();
            int elapsed;
            for (elapsed = 0; elapsed < 2000; elapsed += 10) {
                if (br.ready()) {
                    break;
                }
                else {
                    Utils.sleep(10);
                }
            }
            if (elapsed >= 2000) {
                // should throw an exception
                System.err.println("jdtrace: timeout: pid " + pid + " not found");
                System.exit(1);
            }
            String cmdOutput = br.readLine();
            if (!cmdOutput.equalsIgnoreCase("global")) {
                host = cmdOutput;
            }
        } catch (IOException ex) {
            Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Logger.getLogger(this.getClass().getName()).info("ignoring host type");
        host = "localhost";
        */

        return host;
    }
}
