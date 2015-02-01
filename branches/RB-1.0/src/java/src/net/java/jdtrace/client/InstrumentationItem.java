/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

/**
 *
 * @author amit
 */
class InstrumentationItem {
    String pid;
    String classPattern;
    String methodPattern;
    String probeName;

    public String getProbeName() {
        return probeName;
    }

    public int getProbeId() {
        return probeId;
    }
    int probeId;

    InstrumentationItem(String p, String c, String m, String pn, int id) {
        pid = p;
        classPattern = c;
        methodPattern = m;
        probeName = pn;
        probeId = id;
    }

    String getPid() {
        return pid;
    }

    String getClassPattern() {
        return classPattern;
    }

    String getMethodPatttern() {
        return methodPattern;
    }
    
}
