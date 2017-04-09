package com.epriest.game.CanvasGL.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by darka on 2016-10-31.
 */

public class gameLog {
    private static final boolean LOG = false;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault());
    private static String PREFIX = ">>";
    private static String LOCATOR = ":at ";

    public static void println(int priority, SourceInfo info, String log) {
        if (log.length() < 3600) {
            android.util.Log.println(priority, info.cls_method, PREFIX + log + info.locator);
        } else {
            android.util.Log.println(priority, info.cls_method, PREFIX + info.locator);
            StringTokenizer st = new StringTokenizer(log, "\n", true);
            while (st.hasMoreTokens())
                android.util.Log.println(priority, "", st.nextToken());
        }
    }

    public static void l2(Object... args) {
        if (!LOG)
            return;
        final SourceInfo info = getSourceInfo(2, new Exception());
        final String log = _MESSAGE(args);
        println(android.util.Log.ERROR, info, log);
    }

    public static void ln(int n, Object... args) {
        if (!LOG)
            return;
        final SourceInfo info = getSourceInfo(n, new Exception());
        final String log = _MESSAGE(args);

        println(android.util.Log.ERROR, info, log);
    }
    public static void l() {
        if (!LOG)
            return;

        final SourceInfo info = getSourceInfo(1, new Exception());
        println(android.util.Log.ERROR, info, "");
    }
    public static void l(Object... args) {
        if (!LOG)
            return;

        final SourceInfo info = getSourceInfo(1, new Exception());
        final String log = _MESSAGE(args);
        println(android.util.Log.ERROR, info, log);
    }

    private static long last_filter;
    public static void l_filter(long sec, Object... args) {
        if (!LOG)
            return;

        if (last_filter < System.currentTimeMillis() - sec)
            return;

        last_filter = System.currentTimeMillis();
        final SourceInfo info = getSourceInfo(1, new Exception());
        final String log = _MESSAGE(args);

        println(android.util.Log.ERROR, info, log);
    }
    public static void a(Object... args) {
        if (!LOG)
            return;
        Exception e = new Exception();
        final SourceInfo info = getSourceInfo(1, e);
        final String log = _MESSAGE(args);
        println(android.util.Log.ASSERT, info, log);
    }
    public static void e(Object... args) {
        if (!LOG)
            return;
        Exception e = new Exception();
        final SourceInfo info = getSourceInfo(1, e);
        final String log = _MESSAGE(args);
        println(android.util.Log.ERROR, info, log);
    }
    public static void w(Object... args) {
        if (!LOG)
            return;
        Exception e = new Exception();
        final SourceInfo info = getSourceInfo(1, e);
        final String log = _MESSAGE(args);
        println(android.util.Log.WARN, info, log);
    }
    public static void i(Object... args) {
        if (!LOG)
            return;
        final SourceInfo info = getSourceInfo(1, new Exception());
        final String log = _MESSAGE(args);
        println(android.util.Log.INFO, info, log);
    }
    public static void d(Object... args) {
        if (!LOG)
            return;
        final SourceInfo info = getSourceInfo(1, new Exception());
        final String log = _MESSAGE(args);
        println(android.util.Log.DEBUG, info, log);
    }

    public static void v(Object... args) {
        if (!LOG)
            return;
        final SourceInfo info = getSourceInfo(1, new Exception());
        final String log = _MESSAGE(args);
        println(android.util.Log.VERBOSE, info, log);
    }
    public static class SourceInfo {
        public String pkg;
        public String cls;
        public String method;
        public String java_line;

        public String cls_method_java_line;
        public String cls_method;
        public String locator;
        public SourceInfo(String pkg, String cls, String method, String javaline) {
            this.pkg = pkg;
            this.cls = cls;
            this.method = method;
            this.java_line = javaline;

            this.cls_method = cls + "." + method;
            this.cls_method_java_line = cls_method + javaline;
            this.locator = LOCATOR + javaline;
        }
    }
    public static SourceInfo getSourceInfo(int n, final Exception e) {
        final String funcStack = e.getStackTrace()[n].toString();
        int posJava = funcStack.lastIndexOf("(");
        int posFunc = funcStack.lastIndexOf(".", posJava - 1);
        int posClass = funcStack.lastIndexOf(".", posFunc - 1);

        String pkg = funcStack.substring(0, posClass);
        String cls = funcStack.substring(posClass + 1, posFunc);
        String method = funcStack.substring(posFunc + 1, posJava);
        String javaline = funcStack.substring(posJava);

        return new SourceInfo(pkg, cls, method, javaline);
    }
    public static void j(String json) {
        if (!LOG)
            return;
        gameLog.l2(_DUMP_JSON(json));
    }
    public static String _DUMP_JSON(String json) {
        if (json == null || json.length() <= 0)
            return "";
        final String INDENT = "    ";
        StringBuilder sb = new StringBuilder();
        int indentDepth = 0;
        String ch = null;
        boolean inString = false;
        for (int i = 0; i < json.length(); i++) {
            ch = json.substring(i, i + 1);
            if (ch.equals("\""))
                inString = !inString;

            if (inString) {
                sb.append(ch);
            } else if (ch.equals("{") || ch.equals("[")) {
                sb.append(ch).append("\n");
                indentDepth++;
                for (int j = 0; j < indentDepth; j++) {
                    sb.append(INDENT);
                }
            } else if (ch.equals("}") || ch.equals("]")) {
                sb.append("\n");
                indentDepth--;
                for (int j = 0; j < indentDepth; j++) {
                    sb.append(INDENT);
                }
                sb.append(ch);
            } else if (ch.equals(",")) {
                sb.append(ch);
                sb.append("\n");
                for (int j = 0; j < indentDepth; j++) {
                    sb.append(INDENT);
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    public static String _MESSAGE(Object... args) {
        if (args == null)
            return "null[]";

        StringBuilder sb = new StringBuilder();
        for (Object object : args) {
            //@S
            if (object ==         null     )  sb.append(",").append("null"                              );
            else if (object instanceof Class    )  sb.append(",").append( _DUMP((Class<?> ) object          ));
            else if (object instanceof Cursor)  sb.append(",").append( _DUMP((Cursor   ) object          ));
            else if (object instanceof Intent)  sb.append(",").append( _DUMP((Intent   ) object          ));
            else if (object instanceof Bundle)  sb.append(",").append( _DUMP((Bundle   ) object          ));
            else if (object instanceof Uri)  sb.append(",").append( _DUMP((Uri      ) object          ));
            else if (object instanceof byte[]   )  sb.append(",").append( _DUMP((byte[]   ) object          ));
            else                                   sb.append(",").append(                   object.toString());
            //@E
        }
        return sb.toString();
    }

    private static String _DUMP(Cursor c) {
        if (c == null)
            return "null_Cursor";

        StringBuilder sb = new StringBuilder();
        int count = c.getCount();
        sb.append("<" + count + ">");

        try {
            String[] columns = c.getColumnNames();
            sb.append(Arrays.toString(columns));
            sb.append("\n");
        } catch (Exception e) {
        }

        int countColumns = c.getColumnCount();
        if (!c.isBeforeFirst()) {
            for (int i = 0; i < countColumns; i++) {
                try {
                    sb.append(c.getString(i) + ",");
                } catch (Exception e) {
                    sb.append("BLOB,");
                }
            }
        } else {
            int org_pos = c.getPosition();
            while (c.moveToNext()) {
                for (int i = 0; i < countColumns; i++) {
                    try {
                        sb.append(c.getString(i) + ",");
                    } catch (Exception e) {
                        sb.append("BLOB,");
                    }
                }
                sb.append("\n");
            }
            c.moveToPosition(org_pos);
        }
        return sb.toString();
    }

    public static void _provider(Context context, Uri uri) {
        if (context == null || uri == null) {
            gameLog.l2("context==null || uri == null");
            return;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        if (c == null)
            return;
        gameLog.l2(c);
        c.close();
    }

    private static String _DUMP(Bundle bundle) {
        if (bundle == null)
            return "null_Bundle";

        StringBuffer sb = new StringBuffer();

        final Set<String> keys = bundle.keySet();
        String type = null;
        String value = null;
        for (String key : keys) {
            final Object o = bundle.get(key);
            if (o == null) {
                type = "null";
                value = "null";
            } else if (o.getClass().isArray()) {
                type = o.getClass().getSimpleName();
                value = Arrays.toString((Object[]) o);
            } else {
                type = o.getClass().getSimpleName();
                value = o.toString();
            }

            sb.append(key + "," + type + "," + value).append("\n");
        }

        return sb.toString();
    }

    private static String _DUMP(Class<?> cls) {
        if (cls == null)
            return "null_Class<?>";
        return cls.getSimpleName() + ((cls.getSuperclass() != null) ? (">>" + cls.getSuperclass().getSimpleName()) : "");
    }

    private static String _DUMP(Uri uri) {
        if (uri == null)
            return "null_Uri";

//		return uri.toString();
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n Uri                       ");
        sb.append(uri.toString());
        sb.append("\r\n Scheme                    ");
        sb.append(uri.getScheme() != null ? uri.getScheme().toString() : "null");
        sb.append("\r\n Host                      ");
        sb.append(uri.getHost() != null ? uri.getHost().toString() : "null");
        sb.append("\r\n Port                      ");
        sb.append(uri.getPort());
        sb.append("\r\n Path                      ");
        sb.append(uri.getPath() != null ? uri.getPath().toString() : "null");
        sb.append("\r\n Query                     ");
        sb.append(uri.getQuery() != null ? uri.getQuery().toString() : "null");
        sb.append("\r\n");
        sb.append("\r\n Fragment                  ");
        sb.append(uri.getFragment() != null ? uri.getFragment().toString() : "null");
        sb.append("\r\n LastPathSegment           ");
        sb.append(uri.getLastPathSegment() != null ? uri.getLastPathSegment().toString() : "null");
        sb.append("\r\n SchemeSpecificPart        ");
        sb.append(uri.getSchemeSpecificPart() != null ? uri.getSchemeSpecificPart().toString() : "null");
        sb.append("\r\n UserInfo                  ");
        sb.append(uri.getUserInfo() != null ? uri.getUserInfo().toString() : "null");
        sb.append("\r\n PathSegments              ");
        sb.append(uri.getPathSegments() != null ? uri.getPathSegments().toString() : "null");
        sb.append("\r\n Authority                 ");
        sb.append(uri.getAuthority() != null ? uri.getAuthority().toString() : "null");
        sb.append("\r\n");
        sb.append("\r\n EncodedAuthority          ");
        sb.append(uri.getEncodedAuthority() != null ? uri.getEncodedAuthority().toString() : "null");
        sb.append("\r\n EncodedPath               ");
        sb.append(uri.getEncodedPath() != null ? uri.getEncodedPath().toString() : "null");
        sb.append("\r\n EncodedQuery              ");
        sb.append(uri.getEncodedQuery() != null ? uri.getEncodedQuery().toString() : "null");
        sb.append("\r\n EncodedFragment           ");
        sb.append(uri.getEncodedFragment() != null ? uri.getEncodedFragment().toString() : "null");
        sb.append("\r\n EncodedSchemeSpecificPart ");
        sb.append(uri.getEncodedSchemeSpecificPart() != null ? uri.getEncodedSchemeSpecificPart().toString() : "null");
        sb.append("\r\n EncodedUserInfo           ");
        sb.append(uri.getEncodedUserInfo() != null ? uri.getEncodedUserInfo().toString() : "null");
        sb.append("\r\n");
        return sb.toString();
    }

    private static String _DUMP(Intent intent) {
        if (intent == null)
            return "null_Intent";
        StringBuffer sb = new StringBuffer();
        //@S
        sb.append(intent.getAction    () != null ? "Action     "+intent.getAction    ().toString() + "\n" : "");
        sb.append(intent.getData      () != null ? "Data       "+intent.getData      ().toString() + "\n" : "");
        sb.append(intent.getCategories() != null ? "Categories "+intent.getCategories().toString() + "\n" : "");
        sb.append(intent.getType      () != null ? "Type       "+intent.getType      ().toString() + "\n" : "");
        sb.append(intent.getScheme    () != null ? "Scheme     "+intent.getScheme    ().toString() + "\n" : "");
        sb.append(intent.getPackage   () != null ? "Package    "+intent.getPackage   ().toString() + "\n" : "");
        sb.append(intent.getComponent () != null ? "Component  "+intent.getComponent ().toString() + "\n" : "");
        sb.append(intent.getFlags     () != 0x00 ? "Flags      "+Integer.toHexString(intent.getFlags())+ "\n" : "");
        //@E

        if (intent.getExtras() != null)
            sb.append(_DUMP(intent.getExtras()));

        return sb.toString();
    }

    public static void milliseconds(long milliseconds) {
        gameLog.l2(_DUMP_milliseconds(milliseconds));
    }
    public static void milliseconds_elapsed(long milliseconds) {
        gameLog.l2(_DUMP_elapsed(milliseconds));
    }

    public static String _DUMP_milliseconds() {
        return _DUMP_milliseconds(System.currentTimeMillis());
    }
    public static String _DUMP_milliseconds(long milliseconds) {
        return "<" + sf.format(new Date(milliseconds)) + "," + SystemClock.elapsedRealtime() + ">";
    }
    public static String _DUMP_elapsed(long elapsedRealtime) {
        return _DUMP_milliseconds(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - elapsedRealtime));
    }

    public static void t(Context context, Object... args) {
        if (!LOG)
            return;
        if (context == null)
            return;
        gameLog.l2(args);
        Toast.makeText(context, _MESSAGE(args), Toast.LENGTH_SHORT).show();
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static String _DUMP(byte[] bytearray) {
        if (bytearray == null)
            return "null_bytearray";
        try {
            char[] chars = new char[2 * bytearray.length];
            for (int i = 0; i < bytearray.length; ++i) {
                chars[2 * i] = HEX_CHARS[(bytearray[i] & 0xF0) >>> 4];
                chars[2 * i + 1] = HEX_CHARS[bytearray[i] & 0x0F];
            }
            return new String(chars);
        } catch (Exception e) {
            return "!!byte[]";
        }
    }

    public static String _h2s(byte[] bytearray) {
        return _DUMP(bytearray);
    }

    public static byte[] _s2h(String string_hex_format) {
        byte[] bytes = new byte[string_hex_format.length() / 2];
        try {
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(string_hex_format.substring(2 * i, 2 * i + 2), 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void _object(Object o) {
        gameLog.l2(_DUMP_object(o));
    }

    public static String _DUMP_throwable(Throwable th) {
        String stacktrace = "";
        PrintWriter printWriter = null;
        try {
            Writer result = new StringWriter();
            printWriter = new PrintWriter(result);
            Throwable cause = th;
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            stacktrace = result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
        return stacktrace;
    }

    public static String _DUMP_object(Object o) {
        if (o == null)
            return "null_Object";
        try {
            StringBuilder sb = new StringBuilder();
            for (Field f : o.getClass().getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    final String key = f.getName();
                    final Object value = f.get(o);

                    sb.append(key);
                    sb.append(":");
                    sb.append(value == null ? "null" : value.toString());
                    sb.append("\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "!!_DUMP_O";
        }

    }

    // Line Logger
    public static class Line1Logger {
        private static StringBuilder LOGGER;
        public static void append(Object... args) {
            if (LOGGER == null || LOGGER.length() > 1024 * 4)
                LOGGER = new StringBuilder(1024);
            LOGGER.append(_MESSAGE(args));
        }
        public static String pop() {
            final String log = LOGGER.toString();
            LOGGER = null;
            return log;
        }
    }

    // String Logger
    public static class LineNLogger {
        private static StringBuilder LOGGER = new StringBuilder(1024);
        public static void insert(Object... args) {
            LOGGER.insert(0, _MESSAGE(args)).append("\n");
            LOGGER.setLength(1024 * 4);
        }
        public static String get() {
            return LOGGER.toString();
        }
        public static void clear() {
            LOGGER.delete(0, LOGGER.length());
        }
    }

    // LIST_LOGGER
    public static class ListLogger {
        private static ArrayList<String> LOGGER = new ArrayList<String>(100);
        public static void insert(Object... args) {
            LOGGER.add(0, _MESSAGE(args));
            while (LOGGER.size() > 1024)
                LOGGER.remove(1024);
        }
        public static ArrayList<String> pop() {
            final ArrayList<String> result = peek();
            clear();
            return result;
        }
        public static ArrayList<String> get() {
            return peek();
        }
        public static ArrayList<String> peek() {
            return LOGGER;
        }
        public static void clear() {
            LOGGER.clear();
        }
    }

    // LIST_LOGGER
    public static class TicLogger {
        static long s = 0;
        private static long e;
        public static void start() {
            s = System.currentTimeMillis();
            e = s;
            gameLog.l2(String.format("%20d%20d%20d", s, e, e - s));
        }

        public static void stop() {
            e = System.currentTimeMillis();
            gameLog.l2(String.format("%20d%20d%20d", s, e, e - s));
            s = e;
        }

        public static void stop(String log) {
            e = System.currentTimeMillis();
            gameLog.l2(log, String.format("%20d%20d%20d", s, e, e - s));
            s = e;
        }

        public static void tic() {
            e = System.currentTimeMillis();
            gameLog.l2(String.format("%20d%20d%20d", s, e, e - s));
        }
        public static void tic(String log) {
            e = System.currentTimeMillis();
            gameLog.l2(log, String.format("%20d%20d%20d", s, e, e - s));
        }
    }

    public static void _cls(Class<?> cls) {
        gameLog.l2(cls);
        gameLog.i("getName", cls.getName());
        gameLog.i("getPackage", cls.getPackage());
        gameLog.i("getCanonicalName", cls.getCanonicalName());
        gameLog.i("getDeclaredClasses", Arrays.toString(cls.getDeclaredClasses()));
        gameLog.i("getClasses", Arrays.toString(cls.getClasses()));
        gameLog.i("getSigners", Arrays.toString(cls.getSigners()));
        gameLog.i("getEnumConstants", Arrays.toString(cls.getEnumConstants()));
        gameLog.i("getTypeParameters", Arrays.toString(cls.getTypeParameters()));
        gameLog.i("getGenericInterfaces", Arrays.toString(cls.getGenericInterfaces()));
        gameLog.i("getInterfaces", Arrays.toString(cls.getInterfaces()));
        //@S
        if (cls.isAnnotation()) gameLog.i("classinfo",cls.isAnnotation(),"isAnnotation");
        if (cls.isAnonymousClass()) gameLog.i(cls.isAnonymousClass(),"isAnonymousClass");
        if (cls.isArray()) gameLog.i(cls.isArray(),"isArray");
        if (cls.isEnum()) gameLog.i(cls.isEnum(),"isEnum");
        if (cls.isInstance(CharSequence.class)) gameLog.i(cls.isInstance(CharSequence.class),"isInstance");
        if (cls.isAssignableFrom(CharSequence.class)) gameLog.i(cls.isAssignableFrom(CharSequence.class),"isAssignableFrom");
        if (cls.isInterface()) gameLog.i(cls.isInterface(),"isInterface");
        if (cls.isLocalClass()) gameLog.i(cls.isLocalClass(),"isLocalClass");
        if (cls.isMemberClass()) gameLog.i(cls.isMemberClass(),"isMemberClass");
        if (cls.isPrimitive()) gameLog.i(cls.isPrimitive(),"isPrimitive");
        if (cls.isSynthetic()) gameLog.i(cls.isSynthetic(),"isSynthetic");
        //@E
    }

    public static String getLogerText(Object... args) {
        return _DUMP_milliseconds() + _MESSAGE(args) + getSourceInfo(1, new Exception()).cls_method_java_line;
    }

    public static void flog(Context context, Object... args) {
        if (!LOG)
            return;
        fileLog(context.getPackageName(), args);
    }
    public static void fflog(String prefix, Object... args) {
        if (!LOG)
            return;
        fileLog(prefix, args);
    }
    private static void fileLog(String prefix, Object... args) {
        if (!LOG)
            return;
//		Log.ln(3, prefix, _MESSAGE(args));
        final File dirPath = new File(Environment.getExternalStorageDirectory(), "/_flog");
        if (!dirPath.exists())
            dirPath.mkdirs();

        String yyyymmdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
        final File logFile = new File(dirPath, prefix + "_" + yyyymmdd + ".log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8"));
            buf.append(_DUMP_milliseconds() + _MESSAGE(args) + getSourceInfo(1, new Exception()).cls_method_java_line);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
        }
    }

    public static Loger _LOGER = new Loger();
    public static Loger _loger() {
        return _LOGER;
    }

    public static class Loger {
        private StringBuilder sb = new StringBuilder();
        private long start;
        private long end;

        public void log(Object... args) {
            if (!LOG)
                return;

            long now = System.currentTimeMillis();

            if (sb == null) {
                sb = new StringBuilder();
                start = now;
                sb.append("start").append(",");
                sb.append(_DUMP_milliseconds(now)).append(",");
                sb.append("\n");
            }

            sb.append(_DUMP_milliseconds(now)).append(",");
            sb.append(System.currentTimeMillis() - end).append(",");
            sb.append(_MESSAGE(args)).append(",");
            sb.append(getSourceInfo(1, new Exception()).cls_method_java_line).append("\n");
            end = now;
        }
        public void print() {
            if (!LOG)
                return;

            long now = System.currentTimeMillis();
            sb.append("end").append(",");
            sb.append(_DUMP_milliseconds(now)).append(",");
            sb.append("length : " + (now - start)).append("\n");
            gameLog.l(sb.toString());
            sb = null;
        }
    }
}
