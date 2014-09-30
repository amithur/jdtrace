/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java.jdtrace.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author amit
 */
class PreProcessor {

    String originalFile, newFile;
    ArgParser argParser = null;
    private HashSet<String> proxiesGenerated;
    private int curProbeId;

    PreProcessor(String scriptFile, String newScriptFile, String[] args) {
        originalFile = scriptFile;
        newFile = newScriptFile;
        proxiesGenerated = new HashSet();
        argParser = new ArgParser(args);
        curProbeId = 0;
    }

    ArgParser getArgParser() {
        return argParser;
    }

    List<InstrumentationItem> process() {
        ScriptReader scriptReader = new ScriptReader(originalFile);
        ScriptWriter scriptWriter = new ScriptWriter(newFile);
        List<InstrumentationItem> list = new ArrayList();
        String line;
        boolean prevLineIsJsdtDesc = false;
        ArrayList<String> linesBuffer = new ArrayList();
        while ((line = scriptReader.nextLine()) != null) {
            if (isJsdtProbeDescription(line)) {
                DescriptionConverter descriptionConverter = new DescriptionConverter(line, argParser);
                line = descriptionConverter.convert(line);
                if (!proxyProbeAddedFor(line)) {
                    scriptWriter.addProxyProbe(line);
                    this.markProxtGeneratedFor(line);
                }
                /*
                 if (!proxyProbeAddedFor(descriptionConverter.getPid(),
                 descriptionConverter.getClassPattern(),
                 descriptionConverter.getMethodPattern(),
                 descriptionConverter.getProbeName())) {
                 scriptWriter.addProxyProbe(line);
                 this.markProxtGeneratedFor(descriptionConverter.getPid(),
                 descriptionConverter.getClassPattern(),
                 descriptionConverter.getMethodPattern(),
                 descriptionConverter.getProbeName());
                 }
                 */
                if (descriptionConverter.commaAttheEnd()) {
                    linesBuffer.add(line);
                } else {
                    if (!linesBuffer.isEmpty()) {
                        for (String l : linesBuffer) {
                            // write all lines, separates with commas
                            scriptWriter.writeLine(l + ",");
                        }
                        linesBuffer.clear();
                    }
                    scriptWriter.writeLine(line);
                }
                if (!prevLineIsJsdtDesc) {
                    curProbeId++;
                }
                list.add(new InstrumentationItem(descriptionConverter.getPid(),
                        descriptionConverter.getClassPattern(),
                        descriptionConverter.getMethodPattern(),
                        descriptionConverter.getProbeName(),
                        curProbeId
                        ));
                prevLineIsJsdtDesc = true;
            } else {
                if (prevLineIsJsdtDesc) {
                    scriptWriter.addBeforeOrMergePredicate(line, curProbeId);
                }
                else {
                    scriptWriter.writeLine(line);
                }
                prevLineIsJsdtDesc = false;
            }
        }
        scriptWriter.flushAndclose();
        return list;
    }

    private boolean isJsdtProbeDescription(String line) {
        return line.matches("^\\s*jsdt.*:.*");
    }

    private void markProxtGeneratedFor(String pid, String cl, String m, String name) {
        if (name.equals(".*")) {
            markProxtGeneratedFor(pid, cl, m, "entry");
            markProxtGeneratedFor(pid, cl, m, "return");
        }
        proxiesGenerated.add(pid + ":" + cl + ":" + m + ":" + name);
    }

    private void markProxtGeneratedFor(String probeLine) {
        proxiesGenerated.add(probeLine);
    }

    private boolean proxyProbeAddedFor(String probeLine) {
        return proxiesGenerated.contains(probeLine);
    }

    private boolean proxyProbeAddedFor(String pid, String cl, String m, String name) {
        if (name.equals(".*")) {
            return proxyProbeAddedFor(pid, cl, m, "entry")
                    && proxyProbeAddedFor(pid, cl, m, "return");
        }
        return proxiesGenerated.contains(pid + ":" + cl + ":" + m + ":" + name);
    }
}
