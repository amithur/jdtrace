/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.agent;

/**
 *
 * @author root
 */
public interface CommandExecutor {
    public void doDumpOnly();
    public void addMatchClasses(String classPattern, String methodPattern,
                                String probeName, String probeId);
    public void doRetransform();
    public void cleanAfterInstrumentation();
    public void deinstrumentAll();

    public void closeAgent();
}
