General Description
-------------------
JDTrace is a DTrace wraparound written in Java, which adds Java code
tracing probes. JDTrace adds the jdtrace DTrace provider. JDTrace
provider probes are implemented by Java Statically Defined Tracing
(JSDT) and use the Java attach and instrumentation API to dynamically add
probes once activated and dynamically remove them once desctivated.

The jdtrace provider in JDTrace 1.0 includes 2 probes:
jdtrace<pid>:<java-class-full-path-name>:<java-method-name>:entry
and
jdtrace<pid>:<java-class-full-path-name>:<java-method-name>:return

Next updates will add
jdtrace<pid>:<java-class-full-path-name>:<java-method-name>:<line-number>

The jdtrace tool takes same parameters as dtrace. 

Regular expressions (conforms to Java/Perl regular expressions) can be used for java-class-full-path-name and java-class-full-path-name. An empty name will match all classes or all methods, approriately.
JDTrace probes adds two predefined variables: 
self->classname
self->methodname
both contain the appropriate class and method names for the Java method in context 

Download and Install
--------------------

How to Run
----------
Run the same as dtrace. See current limitation below, though.
For example
# jdtrace -s <jdtrace-script-name>
<jtrace-script-name> is a dtrace script which uses the jdtrace provider and JDTrace extensions

Current Limitations
-------------------
1. jdtrace 1.0 currently implements a naive D language parser with these limitations:
1.1 probe description of jdtrace provider does not support comma separated list of probes yet
1.2 predicates of jdtrace provider probes must (if exist) appear in a separate line
2. jdtrace 1.0 support does not support oneliners (-n option). Onliners are planned to be supported in jdtrace 1.1


