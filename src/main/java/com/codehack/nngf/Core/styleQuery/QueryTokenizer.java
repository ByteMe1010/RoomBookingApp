package com.codehack.nngf.Core.styleQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTokenizer {

    public static List<String> tokenize(String query) {
        List<String> tokens = new ArrayList<>();
        if (query == null || query.isEmpty()) return tokens;

        // 1. Preprocessing: Replace : ; \n \t with spaces
        String clean = query.replaceAll("[:;\\n\\t]", " ");

        // 2. Collapse repeating whitespace
        clean = clean.replaceAll("\\s+", " ").trim();

        // 3. Tokenize by numbers, letters, and special chars.
        // We match:
        // - Colors: #[a-zA-Z0-9]+
        // - Numbers with units: -?\d+(?:\.\d+)?(?:px|%|em|rem|vh|vw)?
        // - Words: [a-zA-Z]+
        // - Symbols: [^a-zA-Z0-9\s] (This captures commas, &, $, . etc separately)
        Matcher m = Pattern.compile("(#[a-zA-Z0-9]+)|(-?\\d+(?:\\.\\d+)?(?:px|%|em|rem|vh|vw)?)|([a-zA-Z]+)|([^a-zA-Z0-9\\s])").matcher(clean);
        while (m.find()) {
            tokens.add(m.group());
        }

        return tokens;
    }
}
