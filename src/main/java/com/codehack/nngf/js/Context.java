package com.codehack.nngf.js;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private final List<String> dta = new ArrayList<>();

    public void AddProperty(String property) {
        dta.add(property);
    }

    public void AddMethod(String method, Object... args) {
        StringBuilder sb = new StringBuilder(method).append("(");
        for (Object ar : args) {
            String dt = ValueProcesser.convert(ar);
            if ((sb.lastIndexOf("(") + 1) != sb.length())
                sb.append(",");
            sb.append(dt);
        }
        sb.append(")");
        dta.add(sb.toString());
    }

    public void AddEnd() {
        dta.add(";");
    }

    public String getString() {
        StringBuilder b = new StringBuilder();
        for (String d : dta) {
            if (!b.isEmpty() && !d.equals(";")) {
                b.append(".");
            }
            b.append(d.strip());
        }
        return b.toString();
    }
}
