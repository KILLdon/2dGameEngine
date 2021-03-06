package com.notjuststudio.engine2dgame.control;

import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Georgy on 31.03.2017.
 */
public class PyEngine {

    public final static PrintStream out;
    public final static PrintStream err;

    private static final Queue<String> cmds = new LinkedList<>();
    private static final List<String> lastCmd = new ArrayList<>();

    public static final String
            PREFIX = ">>> ",
            PREFIX_NEW = "... ";

    private static PythonInterpreter pyInterpreter;
    private static PythonInterpreter tmpInterpreter;

    static {
        out = System.out;
        err = System.err;
    }

    static void init() {
//        tmpInterpreter = new PythonInterpreter();
        pyInterpreter = new PythonInterpreter();
        pyInterpreter.setOut(System.out);
        pyInterpreter.setErr(System.err);
        loadClasses(Main.class);
        exec("import math");
        exec("from java.awt import Color");
        exec("from java.lang import Float");
        exec("from java.lang import String");
        exec("exit = quit = Game.closeRequest");

        exec("class List(object):" +
                "    pass");

        exec("class __objs__(object):\n" +
                "    pass");


        exec("class __rooms__(object):\n" +
                "    pass");

        exec("def instance_create(x, y, obj):\n" +
                "  return __obj__()");
    }

    static void exec(String cmd) {
        pyInterpreter.exec(cmd);
    }

    static void exec(PyCode script) {
        pyInterpreter.exec(script);
    }

    static PyObject eval(String cmd) {
        return pyInterpreter.eval(cmd);
    }

    static PyObject get(String name) {
        return pyInterpreter.get(name);
    }

    static void put(String name, Object value) {
        pyInterpreter.set(name, value);
    }

    static <T> T get(String name, Class<T> javaClass) {
        return pyInterpreter.get(name, javaClass);
    }

    static PyCode compile(String script) {
        return pyInterpreter.compile(script);
    }

    static PythonInterpreter getTmpInterpreter() {
        tmpInterpreter.cleanup();
        return tmpInterpreter;
    }

    static void loadClasses(Class mClass) {
        String pack = mClass.getPackage().toString().split(" ")[1];
        String packageDir = pack.replaceAll("[.]", "/");
        URL url = mClass.getProtectionDomain().getCodeSource().getLocation();
        try {
            if (new File(url.getFile()).isFile()) {
                printAllClass(packageDir, url);
            } else {
                printAllClass(0, packageDir, mClass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printAllClass(String pack, URL url) throws IOException {
        ZipInputStream zip = new ZipInputStream(url.openStream());
        while(true) {
            ZipEntry e = zip.getNextEntry();
            if (e == null)
                break;
            String name = e.getName();
            if (name.startsWith(pack) && name.endsWith(".class") && !name.contains("$") && !name.contains("colladaConverter")) {
                pyInterpreter.exec("from " + name.substring(0, name.lastIndexOf('/')).replaceAll("[/]", ".") + " import " + name.substring(name.lastIndexOf('/') + 1, name.length() - 6));
            }
        }
    }

    private static void printAllClass(int level, String pack, Class mClass) throws IOException {
        ClassLoader cl = mClass.getClassLoader();
        URL upackage = cl.getResource(pack);
        InputStream in = (InputStream) upackage.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("$") || line.contains("colladaConverter") || line.endsWith("glsl") || line.endsWith("png")  || line.endsWith("fnt"))
                continue;
            if (line.endsWith(".class")) {
                pyInterpreter.exec("from " + pack.replaceAll("[/]", ".") + " import " + line.substring(0, line.length() - 6));
                continue;
            }
            try {
                printAllClass(level + 1,pack + "/" + line, mClass);
            } catch (NullPointerException e) {}
        }
    }

    static void execConsole() {
        Queue<String> cmdsTMP = new LinkedList<>(cmds);
        cmds.clear();

        while (true) {
            String cmd;
            try {
                cmd = cmdsTMP.remove();
                String[] lines = cmd.split("\n");
                System.out.println(PREFIX + lines[0]);
                for (String line : Arrays.asList(lines).subList(1, lines.length )) {
                    System.out.println(PREFIX_NEW + line);
                }
            } catch (NoSuchElementException e) {
                break;
            }
            try {
                PyObject result = eval(cmd);
                if (!result.getType().fastGetName().equals("NoneType")) {
                    System.out.println(result);
                }
            } catch (Exception e) {
                try {
                    exec(cmd);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
