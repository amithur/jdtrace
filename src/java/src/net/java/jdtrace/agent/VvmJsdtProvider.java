package net.java.jdtrace.agent;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.tracing.Provider;

/**
 *
 * @author root
 */
public interface VvmJsdtProvider extends Provider {

    public void method_entry(int threadId, String className, int classNameLength,
                            String methodName, int methodNameLength,
                            String methodSignature, int methodSignatureLength,
                            int probeId);
    //public void startMethod(String methodName, String str, int i);

    public void method_return(int threadId, String className, int classNameLength,
                            String methodName, int methodNameLength,
                            String methodSignature, int methodSignatureLength,
                            int probeId);
    public void test(int l, String str, int strlen);
}
