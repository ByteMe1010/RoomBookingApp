package com.codehack.nngf.Core;

import org.unbescape.javascript.JavaScriptEscape;

public class Js {

    public static Expr el() {
        return new Expr("element");
    }

    public static Expr evt() {
        return new Expr("e");
    }

    public static Expr id(String id) {
        return new Expr("document.getElementById('" + JavaScriptEscape.escapeJavaScript(id) + "')");
    }

    public static Expr queryAll(Object selector) {
        StringBuilder sb = new StringBuilder("document.querySelectorAll(");
        appendArgStatic(sb, selector);
        sb.append(")");
        return new Expr(sb.toString());
    }

    public static Expr expr() {
        return new Expr("");
    }

    public static Expr global(String name) {
        return new Expr(name);
    }

    public static Arg raw(String js) {
        return new Arg(js);
    }

    private static void appendArgStatic(StringBuilder sb, Object arg) {
        if (arg instanceof Arg) {
            sb.append(((Arg) arg).js);
        } else if (arg instanceof String) {
            sb.append("'").append(JavaScriptEscape.escapeJavaScript((String) arg)).append("'");
        } else {
            sb.append(arg);
        }
    }

    public static class Expr {
        private final StringBuilder prefix;
        private final StringBuilder chain = new StringBuilder();
        private boolean hasReturn = false;

        Expr(String prefix) {
            this.prefix = new StringBuilder(prefix);
        }

        public Expr call(String name, Object... args) {
            chain.append(".");
            chain.append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) chain.append(", ");
                appendArg(args[i]);
            }
            chain.append(")");
            return this;
        }

        public Expr method(String name, Object... args) {
            chain.append(name).append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) chain.append(", ");
                appendArg(args[i]);
            }
            chain.append(")");
            return this;
        }

        public Expr prop(String name, Object value) {
            chain.append(".").append(name).append(" = ");
            appendArg(value);
            return this;
        }

        public Expr propAppend(String name, Object value) {
            chain.append(".").append(name).append(" += ");
            appendArg(value);
            return this;
        }

        public Expr get(String name) {
            chain.append(".").append(name);
            return this;
        }

        public Expr ret() {
            hasReturn = true;
            return this;
        }

        public String toExpression() {
            return prefix.toString() + chain;
        }

        public String end() {
            StringBuilder sb = new StringBuilder();
            if (hasReturn) {
                sb.append("return ");
            }
            sb.append(prefix).append(chain).append(";");
            return sb.toString();
        }

        private void appendArg(Object arg) {
            appendArgStatic(chain, arg);
        }
    }

    public static class Arg {
        final String js;

        Arg(String js) {
            this.js = js;
        }
    }
}
