package net.java.jdtrace.agent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.instrument.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
//import org.openide.util.Exceptions;

/**
 *
 * @author ahurvitz
 */
public class VmDtracePluginAgent implements ClassFileTransformer, CommandExecutor {

    private static Pattern classPattern;
    private static boolean redefine;
    private static String dumpDir = "/tmp";
    private static ArrayList<String> methodNames = null;
    private static final String CLASS_FILE_NAME = "/tmp/.classesToInstrument";
    private static Class[] loadedClasses;
    private static Instrumentation inst;
    private static ClassFileTransformer cft = null;
    private static boolean undoInstrumentation = false;
    private static boolean justDumpClass = false;
    private static String jarDependencies[] = {
        "asm-5.0_BETA.jar",
        "asm-commons-5.0_BETA.jar",
        "asm-util-5.0_BETA.jar",
        "/usr/share/lib/java/dtrace.jar",
        "provider.jar"
    };
    //private static VvmJsdtProvider provider = null;
    private static int port = 5679;
    private static VvmJsdtProvider prov = null;
    private static String excludedClassPattern[] = {
        "java.lang.management.*",
        "java.lang.Class",
        "java.lang.System",
        "java.lang.Thread"
    };
    //private static String asmLibPath = "/export/home/amit/asm5/asm-5.0_BETA/lib";
    private static String asmLibPath = "/home/amit/javaOne/jdk8/agent/asm-lib";
    private static final Logger logger = Logger.getLogger(VmDtracePluginAgent.class.getName() );

    public void cleanAfterInstrumentation() {
        targetClasses.clear();
        //classDetailsMap.clear();
    }
    private static ArrayList<Class> targetClasses;
    private static ArrayList<Class> instrumentedClasses;
    private static HashMap<String, TargetClassDetails> classDetailsMap;
    private static Thread listener;

    public static void premain(String agentArgs, Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst_) {
        inst = inst_;
        logger.setLevel(Level.WARNING);
        //methodNames = new ArrayList(128);
        parseArgs(agentArgs);
        redefine = false; // true: redefing, false: retransform
        init(inst);
        final VmDtracePluginAgent agent = new VmDtracePluginAgent();

        
        for (String jarFileName : jarDependencies) {
            logger.info("dependency: " + jarFileName);
            try {
                if (jarFileName.startsWith("/")) {
                    // if absolute path - use as is
                }
                else {
                    jarFileName = asmLibPath + "/" + jarFileName;
                }
                inst.appendToBootstrapClassLoaderSearch(new JarFile(jarFileName));
            } catch (IOException ex) {
                Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        

        //org.amit.VvmJsdtProviderFactory.provider.test(17, "joseph", 18);
        
        
        //ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
            //Class pf = sysClassLoader.loadClass("VvmJsdtProviderFactory");
            
            
            // I do not perform this provider factory initialization  
            // because the later call to the class fails, no idea why
            /*
            logger.setLevel(Level.ALL);
            logger.info("try initialize VvmJsdtProviderFactory");
            logger.info("VvmJsdtProviderFactory was loaded by " + VvmJsdtProviderFactory.class.getClassLoader());
            logger.info("VvmJsdtProvider was loaded by " + VvmJsdtProvider.class.getClassLoader());
            logger.info("provider: " + VvmJsdtProviderFactory.provider);
            logger.setLevel(Level.WARNING);
            VvmJsdtProviderFactory.init();
            prov = VvmJsdtProviderFactory.provider;
            */
        
            /*
            try {
            logger.info("trying to load org.amit.VvmJsdtProviderFactory");
            Class cp = Class.forName("org.amit.VvmJsdtProvider");
            Class c = Class.forName("org.amit.VvmJsdtProviderFactory");
            logger.info("tried to load org.amit.VvmJsdtProviderFactory, with " + c.getClassLoader());
            logger.info("the provider itself was loaded by  " + cp.getClassLoader());
            } catch (ClassNotFoundException ex) {
            Logger.getLogger((Level.SEVERE, null, ex);
            }
            */
        
        

        //process();
        listener = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        CommandListener cl = new CommandListener((CommandExecutor) agent);
                        cl.setPort(port);
                        try {
                            cl.listen();
                        } catch (Exception ex) {
                            Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        listener.start();


        /*
         //inst.addTransformer(new InstrumentationAgent(), true);

         // by the time we are attached, the classes to be
         // dumped may have been loaded already. So, check
         // for candidates in the loaded classes.
         //Class[] classes = inst.getAllLoadedClasses();
         //loadedClasses = inst.getAllLoadedClasses();
         //List<Class> candidates = new ArrayList<Class>();
         //logger.info("classPattern: " + classPattern);
         //for (Class c : classes) {
         //logger.info("checking class " + c.getName());
         //    if (isCandidate(c.getName())) {
         //        logger.info("adding to candidates");
         //        candidates.add(c);
         //    }
         //}
            
         try {
         // if we have matching candidates, then
         // retransform those classes so that we
         // will get callback to transform.
         if (!candidates.isEmpty()) {
         if (!redefine) {
         inst.retransformClasses(candidates.toArray(new Class[0]));
         } else {
         logger.info("Going to Redefine");
         logger.info("candidate empty? " + candidates.isEmpty());
         int index = 0;
         ClassDefinition[] cds = new ClassDefinition[candidates.size()];
         for (Iterator<Class> itr = candidates.iterator(); itr.hasNext(); index++) {
         Class cls = itr.next();
        logger.info("Adding (to redefinition):" + cls.getName());
         byte[] bytes = ClassBytesHelper.getClassBytes(new File("/home/ahurvitz/javaOne/dump"), cls.getName());
         if (bytes == null) {
         logger.info("error read class" + cls.getName());
         // do something better here, maybe throw an exception, don't continue like this
         }
         cds[index] = new ClassDefinition(cls, bytes);
         }
         try {
         System.out.printf("calling redefineClasses");
         inst.redefineClasses(cds);
         } catch (ClassNotFoundException ex) {
         Logger.getLogger(InstrumentationAgent.class.getName()).log(Level.SEVERE, null, ex);
         }
         }
         }
         } catch (UnmodifiableClassException ucmethodnamese) {
         }
         */
    }

    private static void init(Instrumentation inst) {
        inst.addTransformer((cft = new VmDtracePluginAgent()), true);
        loadedClasses = inst.getAllLoadedClasses();
        targetClasses = new ArrayList();
        instrumentedClasses = new ArrayList();
        classDetailsMap = new HashMap();
    }

    public void deinstrumentAll() {
        undoInstrumentation = true;
        if (!instrumentedClasses.isEmpty()) {
            try {
                inst.retransformClasses(instrumentedClasses.toArray(new Class[0]));
            } catch (UnmodifiableClassException ex) {
                Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        boolean success = inst.removeTransformer(cft);
        instrumentedClasses.clear();
        logger.info("trasformer removed: " + success);
    }

    public void addMatchClasses(String classPattern, String methodPattern,
            String probeName, String probeId) {
        classPattern = classPattern.replace('/', '.');
        String ClassPatternRegexp = classPattern.replace("$", "\\$");
        logger.info("in addMatchClasses (8), " + classPattern + ":" + methodPattern);

        java.io.BufferedWriter bosty = null;
        try {
            bosty = new java.io.BufferedWriter(new java.io.FileWriter("/tmp/loadedClasses.txt"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Class c : loadedClasses) {
            //logger.info("class: " + c.getName());
            if (inExcludedClasses(c.getName())) {
                continue;
            }
            /*
            try {
                //logger.info("Going to write: " + c.getName() + " to bosty");
                bosty.write(c.getName());
                bosty.write("\n");
            } catch (IOException ex) {
                Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
            if (c.getName().matches(ClassPatternRegexp) && !inst.isModifiableClass(c)) {
                logger.info("class " + classPattern + " not modifiable");
            }
            if (c.getName().matches(ClassPatternRegexp) && inst.isModifiableClass(c)) {
                logger.info("adding: " + c.getName());
                targetClasses.add(c);
                // to do: if Class name already exists, add methodPattern, do not overide

                TargetClassDetails tcd = classDetailsMap.get(c.getName());
                if (tcd == null) {
                    tcd = new TargetClassDetails(c);
                    classDetailsMap.put(c.getName(), tcd);
                }
                tcd.addProbeCallPattern(methodPattern, probeName, probeId);
            }
        }
    }

    public void doRetransform() {
        undoInstrumentation = false;
        if (!(targetClasses.isEmpty())) {
            try {
                logger.info("retransformclasses...");
                inst.retransformClasses(targetClasses.toArray(new Class[0]));
            } catch (UnmodifiableClassException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            if (!justDumpClass) {
                instrumentedClasses.addAll(targetClasses);
            }
            justDumpClass = false;
        }
    }

    /*
     private void process() {
     // this invokes all preparations and transformation
     prepareTargetList();
     if (!(targetClasses.isEmpty())) {
     try {
     System.out.println("retransformclasses...");
     inst.retransformClasses(targetClasses.toArray(new Class[0]));
     } catch (UnmodifiableClassException ex) {
     ex.printStackTrace();
     }
     }
     }
     */

    /*
     public static String getListedMethodName(int idx) {
     return methodNames.get(idx);
     }

     public static synchronized int addListedMethodName(String name) {
     methodNames.add(name);
     return methodNames.size() - 1;
     }
     */
    @Override
    public byte[] transform(ClassLoader loader, String className,
            Class redefinedClass, ProtectionDomain protDomain,
            byte[] classBytes) {
        byte[] newBytes = null;
        //System.out.println("even before...");
        /*ClassLoader l = loader; 
         while (l.getParent(
         ) != null) {l = l.getParent();}
         try {
         System.out.println("before...");
         l.loadClass("org.amit.VvmJsdtProviderFactory");
         System.out.println("after...");
         //System.out.println("transform, before isCandidate(), class: " + className);
         //System.out.flush();
         //System.out.println("before...");
         //System.out.println("after...");
         //System.out.println("after...");
         } catch (ClassNotFoundException ex) {
         Logger.getLogger(VmDtracePluginAgent.class.getName()).log(Level.SEVERE, null, ex);
         }
         */

        // check and dump .class file
        //if (className.endsWith("NBTimeServer")) {
        //System.out.println("Found!!!! (" + className + ")");
        //}
        if (isCandidate(className)) {
            //if (className.equals("nbtimeserver/NBTimeServer")) {
            //doDumpClass(className, classBytes);
            logger.info("in transform() of class " + className);
            /*
             if (justDumpClass) {
             doDumpClass(className, classBytes);
             return null;
             }
             */
            TargetClassDetails dtls = classDetailsMap.get(className.replace('/', '.'));
            logger.info("ClassDetails: " + dtls.getProbeCallPattern());
            logger.info("about to call " + ClassBytesHelper.class.getName() + " with " + className + " and " + dtls.getProbeCallPattern());
            newBytes = ClassBytesHelper.addMethodEntryProbe(className, dtls, classBytes);
            //newBytes = classBytes;
            if (justDumpClass) {
                logger.info("about to dump class " + className);
                if (undoInstrumentation) {
                    doDumpClass(className, classBytes);
                } else {
                    doDumpClass(className, newBytes);
                }
            }
        }

        // we don't mess with .class file, just 
        // return null
        if (undoInstrumentation) {
            return null;
        }
        return newBytes;
    }

    private static boolean isCandidate(String className) {
        // ignore array classes
        if (className.charAt(0) == '[') {
            return false;
        }

        // convert the class name to external name
        className = className.replace('/', '.');
        // check for name pattern match
        //System.out.println("class: " + className + ", matches: " + classPattern.matcher(className).matches());
        //return classPattern.matcher(className).matches();
        return classDetailsMap.containsKey(className);
    }

    public void doDumpOnly() {
        justDumpClass = true;
    }

    private static void doDumpClass(String className, byte[] classBuf) {
        try {
            // create package directories if needed
            className = className.replace("/", File.separator);
            StringBuilder buf = new StringBuilder();
            buf.append(dumpDir);
            buf.append(File.separatorChar);
            int index = className.lastIndexOf(File.separatorChar);
            if (index != -1) {
                buf.append(className.substring(0, index));
            }
            String dir = buf.toString();
            new File(dir).mkdirs();

            // write .class file
            String fileName = dumpDir
                    + File.separator + className + ".class";
            logger.info("dumping class to " + fileName);
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(classBuf);
        } catch (IOException exp) {
        }
    }

    // parse agent args of the form arg1=value1,arg2=value2
    private static void parseArgs(String agentArgs) {
        logger.info("here, at parseArgs" + "args: " + agentArgs);
        if (agentArgs != null) {
            String[] args = agentArgs.split(" ");
            for (String arg : args) {
                String[] tmp = arg.split("=");
                if (tmp.length == 2) {
                    String name = tmp[0];
                    String value = tmp[1];
                    if (name.equals("port")) {
                        port = Integer.parseInt(value);
                    } else if (name.equals("libpath")) {
                        logger.info("libpath=" + tmp[1]);
                        asmLibPath = tmp[1];
                    }
                }
            }
        }

        if (classPattern == null) {
            classPattern = Pattern.compile(".*");
        }
        logger.info("classes: " + classPattern);
    }

    private boolean inExcludedClasses(String name) {
        for (String ptrn : excludedClassPattern) {
            if (name.matches(ptrn)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void closeAgent() {
    }
}
