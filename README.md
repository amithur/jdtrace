# jdtrace
jdtrace - a DTrace based tool for Java
= Efficient DTrace Java profiling tool Wiki =


== General Description
----
JDTrace is a DTrace wraparound written in Java, which adds Java code
tracing probes. JDTrace adds the jdtrace DTrace provider. JDTrace
provider probes are implemented by Java Statically Defined Tracing
(JSDT) and use the Java attach and instrumentation API to dynamically add
probes once activated and dynamically remove them once deactivated.

The jdtrace provider in JDTrace 1.1.0 includes 2 probes:
 '''jdtrace'''''pid'':''java-class-full-path-name'':''java-method-name'':'''entry'''
and
 '''jdtrace'''''pid'':''java-class-full-path-name'':''java-method-name'':'''return'''

Next updates will add
 '''jdtrace'''''pid'':''java-class-full-path-name'':''java-method-name'':''line-number''

Regular expressions (conforms to Java/Perl regular expressions) can be used for java-class-full-path-name and java-class-full-path-name. An empty name will match all classes or all methods, approriately.
JDTrace probes adds two predefined variables: 
self->classname and
self->methodname
both contain the appropriate class and method names for the Java method in context 

== Download and Install
----
#Download jdtrace_1.1.0.tar.gz from [https://java.net/projects/jdtrace/downloads] (jdtrace tool for Java 8)
#Extract the content to your prefered location
#Add jdtrace_1.1.0-installation-directory/bin directory to your PATH
#In jdtrace_1.0-installation-directory/jdtrace.properties:
##Set JDTRACE_HOME to jdtrace_1.1.0-installation-directory
##Set JAVA_HOME to your Java 8 JDK
#You are done

== How to Run
----
See next section for setting DTrace permission for a user. JDTrace will not be able to execute without setting DTrace permission to the user who runs the tool.
Run the same as dtrace. See current limitation below, though.
For example
 # jdtrace -s ''jdtrace-script-name''
 ''jtrace-script-name'' is a dtrace script which uses the jdtrace provider and JDTrace extensions
And one liner:
 # jdtrace -n 'jdtrace234:java.lang.String:replaceAll:entry {@=count();}'


== Grant DTrace permission for a user
----
As root user, run:
 # usermod -K defaultpriv=basic,dtrace_proc,dtrace_user,dtrace_kernel ''login_id''


== Current Limitations
----
#jdtrace 1.1.0 currently implements a naive D language parser with these limitations:
#probe description of jdtrace provider does not support comma separated list of probes yet
#predicates of jdtrace provider probes must (if exist) appear in a separate line

