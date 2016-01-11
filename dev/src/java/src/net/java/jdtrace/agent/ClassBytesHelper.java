package net.java.jdtrace.agent;

/*
 * To change this template, choose Tools | Templates
 * and open the telmplate in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 *
 * @author ahurvitz
 */
public class ClassBytesHelper {
    private static final Logger logger = Logger.getLogger( ClassBytesHelper.class.getName() );
    static {
        logger.setLevel(Level.WARNING);
    }
    
    public static byte[] getClassBytes(File path, String className) {
        FileInputStream is = null;
        byte[] bytes = null;
        try {
            is = new FileInputStream(path.getAbsolutePath() + File.separatorChar + className.replace('.', '/') + ".class");

            try {
                ClassReader cr = new ClassReader(is);
                ClassWriter cw = new ClassWriter(0);
                cr.accept(cw, 0);
                bytes = cw.toByteArray();

            } catch (IOException ex) {
                Logger.getLogger(ClassBytesHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassBytesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytes;
    }

    public static void dummyToRemove() {
        logger.info(" in dummyToRemove()");
    }

    public static byte[] addMethodEntryProbe(String className, TargetClassDetails classDetails, byte[] classBytes) {
        logger.info("starting addMethodEntryProbe");
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassInstrumentor ci = new ClassInstrumentor(cw, classDetails);

        //org.amit.VvmJsdtProviderFactory.provider.methodEntry((int)Thread.currentThread().getId(), "className", 9, "methodName", 10, "methodSignature", 15);
        // trying to call accept with EXPAND_FRAMES to overcome an issue
        //cr.accept(ci, 0);
        cr.accept(ci, ClassReader.EXPAND_FRAMES);
        logger.info("after accept()");
        //hread.dumpStack();

        
        /*
        // debug:
        logger.info("trying to write /tmp/" + className + ".class");

        try {
            FileOutputStream fos = new FileOutputStream(new File("/tmp/" + className + ".class"));
            fos.write(cw.toByteArray());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassBytesHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassBytesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        

        logger.info("currentThread name: " + Thread.currentThread().getName());

        return cw.toByteArray();
    }
}
