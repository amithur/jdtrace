/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.agent;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class TargetClassDetails {
    private Class classObj;
        private ArrayList<String> methodNamePatterns;
        private static final Logger logger = Logger.getLogger(TargetClassDetails.class.getName() );

        TargetClassDetails(Class c) {
            classObj = c;
            methodNamePatterns = new ArrayList();
            logger.setLevel(Level.WARNING);
        }

        public Class getClassObj() {
            return classObj;
        }
        
        public void addProbeCallPattern(String mp, String pn, String id) {
            methodNamePatterns.add(mp + " " + pn + " " + id);
        }

        public ArrayList<String> getProbeCallPattern() {
            return methodNamePatterns;
        }
        
        public ArrayList<Integer> getProbeIdListForMethodProbe(String methodName, String probeName) {
            ArrayList<Integer> probeIdList = new ArrayList();
            for (String pattern : methodNamePatterns) {
                StringTokenizer tknzr = new StringTokenizer(pattern);
                String methodPattern = tknzr.nextToken();
                String probePattern = tknzr.nextToken();
                String idPattern = tknzr.nextToken();
                if (methodName.matches(methodPattern) && probeName.equals(probePattern)) {
                    probeIdList.add(Integer.parseInt(idPattern));
                }
            }
            return probeIdList;
        }
        
        boolean methodNameMacthes(String methodName) {
            for (String pattern : methodNamePatterns) {
                StringTokenizer tknzr = new StringTokenizer(pattern);
                String methodPattern = tknzr.nextToken();
                String probePattern = tknzr.nextToken();
                String idPattern = tknzr.nextToken();
                logger.info("methodNameMatches(): pattern=" + methodPattern + ", name=" + methodName);
                if (methodName.matches(methodPattern)) {
                    logger.info("yes! matches");
                    return true;
                }
            }
            return false;
        }
}
