  package net.java.jdtrace.agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 *
 * @author ahurvitz
 */
public class MethodEntryReturnInstrumentor extends AdviceAdapter {

    String methodName = null;
    String className = null;
    String methodSignature = null;
    TargetClassDetails tcd;
    private static final Logger logger = Logger.getLogger(MethodEntryReturnInstrumentor.class.getName() );

    public MethodEntryReturnInstrumentor(int access, String name, String desc,
            MethodVisitor mv, String signature, String classOwner,
            TargetClassDetails t) {
        super(ASM4, mv, access, name, desc);
        logger.setLevel(Level.WARNING);
        logger.info("MethodEntryReturnInstrumetor constructor");
        methodName = name;
        methodSignature = signature == null ? "null" : signature;
        logger.info("signature: " + methodSignature);
        className = classOwner == null ? "null" : classOwner;
        tcd = t;
    }

    @Override
    protected void onMethodEnter() {
        logger.info("method entry starts: " + methodName);

        ArrayList<Integer> probeIdList = tcd.getProbeIdListForMethodProbe(methodName, "entry");
        logger.info("onMethodEnter, before the loop");
        for (Integer probeId : probeIdList) {
            logger.info("onMethodEnter: " + probeId);
            // insert the bottom code per probe id
            // Hotspot DTrace compliant method entry
            // and comment out this until the "}" bracket
            mv.visitFieldInsn(GETSTATIC, "net/java/jdtrace/agent/VvmJsdtProviderFactory", "provider", "Lnet/java/jdtrace/agent/VvmJsdtProvider;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J");
            mv.visitInsn(L2I);
            
            mv.visitLdcInsn(className);
             mv.visitIntInsn(BIPUSH, className.length());
             mv.visitLdcInsn(methodName);
             mv.visitIntInsn(BIPUSH, methodName.length());
             mv.visitLdcInsn(methodSignature);
             mv.visitIntInsn(BIPUSH, methodSignature.length());
             mv.visitIntInsn(BIPUSH, probeId);
             
            mv.visitMethodInsn(INVOKEINTERFACE, "net/java/jdtrace/agent/VvmJsdtProvider", "method_entry", "(ILjava/lang/String;ILjava/lang/String;ILjava/lang/String;II)V");

        }

        
        /*
         // test probe
         mv.visitFieldInsn(GETSTATIC, "net/java/jdtrace/agent/VvmJsdtProviderFactory", "provider", "Lnet/java/jdtrace/agent/VvmJsdtProvider;");
         //mv.visitFieldInsn(GETSTATIC, "VvmJsdtProviderFactory", "provider", "LVvmJsdtProvider;");
         mv.visitIntInsn(BIPUSH, 17);
         mv.visitLdcInsn("just a string");
         mv.visitIntInsn(BIPUSH, 13);
         mv.visitMethodInsn(INVOKEINTERFACE, "net/java/jdtrace/agent/VvmJsdtProvider", "test", "(ILjava/lang/String;I)V");
         //mv.visitMethodInsn(INVOKEINTERFACE, "VvmJsdtProvider", "test", "(ILjava/lang/String;I)V");
         */

        /*      
         // test start ---------------------------
         mv.visitFieldInsn(GETSTATIC, "org/amit/VvmJsdtProviderFactory", "provider", "Lorg/amit/VvmJsdtProvider;");
         mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
         mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J");
         mv.visitInsn(L2I);
         mv.visitLdcInsn("className");
         mv.visitIntInsn(BIPUSH, 7);
         mv.visitLdcInsn("methodName");
         mv.visitIntInsn(BIPUSH, 14);
         mv.visitLdcInsn("methodSignature");
         mv.visitIntInsn(BIPUSH, 21);
         mv.visitMethodInsn(INVOKEINTERFACE, "org/amit/VvmJsdtProvider", "methodEntry", "(ILjava/lang/String;ILjava/lang/String;ILjava/lang/String;I)V");
         mv.visitFieldInsn(GETSTATIC, "org/amit/VvmJsdtProviderFactory", "provider", "Lorg/amit/VvmJsdtProvider;");
         mv.visitIntInsn(BIPUSH, 17);
         mv.visitLdcInsn("just a string");
         mv.visitIntInsn(BIPUSH, 13);
         mv.visitMethodInsn(INVOKEINTERFACE, "org/amit/VvmJsdtProvider", "test", "(ILjava/lang/String;I)V");
         // test end -----------------------------
         */
        //logger.info("method entry ends: " + methodName);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
    }

    @Override
    protected void onMethodExit(int opcode) {

        // logger.info("method exit starts: " + methodName);
        // Hotspot DTrace compliant method entry    
        ArrayList<Integer> probeIdList = tcd.getProbeIdListForMethodProbe(methodName, "return");
        for (Integer probeId : probeIdList) {
            mv.visitFieldInsn(GETSTATIC, "net/java/jdtrace/agent/VvmJsdtProviderFactory", "provider", "Lnet/java/jdtrace/agent/VvmJsdtProvider;");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J");
            mv.visitInsn(L2I);
            
            mv.visitLdcInsn(className);
             mv.visitIntInsn(BIPUSH, className.length());
             mv.visitLdcInsn(methodName);
             mv.visitIntInsn(BIPUSH, methodName.length());
             mv.visitLdcInsn(methodSignature);
             mv.visitIntInsn(BIPUSH, methodSignature.length());
             mv.visitIntInsn(BIPUSH, probeId);
            mv.visitMethodInsn(INVOKEINTERFACE, "net/java/jdtrace/agent/VvmJsdtProvider", "method_return", "(ILjava/lang/String;ILjava/lang/String;ILjava/lang/String;II)V");
            
//mv.visitMethodInsn(INVOKEINTERFACE, "VvmJsdtProvider", "methodReturn", "(ILjava/lang/String;ILjava/lang/String;ILjava/lang/String;I)V");
        }
        
        /*
         mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
         mv.visitLdcInsn("this is instrumented code at exit of " + methodName);
         mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
         */
        
        logger.info("method exit ends: " + methodName);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 9, maxLocals);
        logger.info("visitMax: " + methodName);
    }
}
