package net.java.jdtrace.agent;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author ahurvitz
 */
public class TEST_MethodEntryReturnInstrumentor extends AdviceAdapter {

    String methodName = null;
    //String className = null;
    //String methodSignature = null;

    public TEST_MethodEntryReturnInstrumentor(int access, String name, String desc,
            MethodVisitor mv, String signature, String classOwner) {
        super(ASM4, mv, access, name, desc);
        //System.out.println("TEST_MethodEntryReturnInstrumetor constructor");
        //methodName = name;
        System.out.println("version 227");
        //methodSignature = signature == null ? "null" : signature;
        //System.out.println("signature: " + methodSignature);
        //className = classOwner == null ? "null" : classOwner;
    }

    
    @Override
    protected void onMethodEnter() {
    }

    @Override
    protected void onMethodExit(int opcode) {
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 6, maxLocals);
        System.out.println("visitMax: " + methodName);
       
    }
    
}
