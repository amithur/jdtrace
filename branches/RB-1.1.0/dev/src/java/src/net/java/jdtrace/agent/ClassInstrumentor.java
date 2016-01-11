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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author ahurvitz
 */
public class ClassInstrumentor extends ClassVisitor {

    private String owner;
    private boolean isInterface;
    private boolean showArgs;
    TargetClassDetails classDetails;
    private static final Logger logger = Logger.getLogger(ClassInstrumentor.class.getName() );

    public ClassInstrumentor(ClassVisitor cv, TargetClassDetails cDetails) {
        super(Opcodes.ASM4, cv);
        classDetails = cDetails;
        logger.setLevel(Level.WARNING);
        logger.info("ClassInstrumentor constructor, " + classDetails.getProbeCallPattern());
        showArgs = false;
        logger.info("construntor of ClassInstrumentor");

    }

    @Override
    public void visit(int version, int access, String name,
            String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
    }
    
    private boolean  nameMatchesDetails(String name) {
        ArrayList<String> patterns = classDetails.getProbeCallPattern();
        for (String pattern : patterns) {
            StringTokenizer tknzr = new StringTokenizer(pattern);
            String methodPattern = tknzr.nextToken();
            if (name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name,
            String desc, String signature, String[] exceptions) {
        logger.info("here....(visitMethod)...., method name: " + name);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);
        if (!isInterface && mv != null && !name.equals("main") && !name.equals("<init>") && !name.equals("<cinit>")) {
            if (classDetails.methodNameMacthes(name)) {
                logger.info("instrumenting...---" + name + "---" + access + ", " + desc + ", " + mv + ", " + signature + ", " + owner);
                mv = new MethodEntryReturnInstrumentor(access, name, desc, mv, signature, owner, classDetails);
                //mv = new TEST_MethodEntryReturnInstrumentor(access, name, desc, mv, signature, owner);
                logger.info("instrumentor created");
            }

        }
        return mv;
    }
}
