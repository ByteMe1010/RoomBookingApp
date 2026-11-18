package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table extends NComponet {
    private final List<String> columns = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public Table(String id) {
        super(id, "Table");
    }

    public Table(String id, NWindow window) {
        super(id, "Table", window);
    }

    public static Table of(String id, NWindow window) {
        return new Table(id, window);
    }

    public void addColumn(String name) {
        columns.add(name);
    }

    public void addRow(String... values) {
        List<String> row = new ArrayList<>();
        Collections.addAll(row, values);
        rows.add(row);
    }

    public void clearRows() {
        rows.clear();
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    @Override
    public String buildHtml(String childHtml) {
        StringBuilder html = new StringBuilder();

        html.append("<div class=\"NNGF-Table-header\">");
        for (String col : columns) {
            html.append("<div class=\"NNGF-Table-cell\">").append(col).append("</div>");
        }
        html.append("</div>");

        html.append("<div class=\"NNGF-Table-body\">");
        for (List<String> row : rows) {
            html.append("<div class=\"NNGF-Table-row\">");
            for (String cell : row) {
                html.append("<div class=\"NNGF-Table-cell\">").append(cell).append("</div>");
            }
            html.append("</div>");
        }
        html.append("</div>");

        return html.toString();
    }
}
