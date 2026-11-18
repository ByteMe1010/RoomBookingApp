package com.codehack.nngf.Core.styleQuery;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NComponet;

import java.util.List;

public class DataPacket {
    public final NComponet component;
    private final List<String> tokens;
    private int pointer = 0;
    
    public final StyleBlock styleBuffer = StyleBlock.of();

    public DataPacket(String query, NComponet component) {
        this.component = component;
        this.tokens = QueryTokenizer.tokenize(query);
    }

    public boolean hasNext() {
        return pointer < tokens.size();
    }

    public String next() {
        if (pointer < tokens.size()) {
            return tokens.get(pointer++);
        }
        return "";
    }

    public String peek() {
        if (pointer < tokens.size()) {
            return tokens.get(pointer);
        }
        return "";
    }

    public void apply() {
        if (component != null && !styleBuffer.styles.isEmpty()) {
            component.styles.addStyle(styleBuffer);
        }
    }
}
