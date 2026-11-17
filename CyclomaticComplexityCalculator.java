package com.parabank.whitebox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiny static analyser: V(G) = (number of decision predicates) + 1.
 * Counts if / for / while / case / catch, short-circuit && and ||, and ternary ?.
 * Comments and string literals are stripped first so they cannot skew the count.
 */
public final class CyclomaticComplexityCalculator {

    private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENT = Pattern.compile("//[^\\n]*");
    private static final Pattern STRING_LITERAL = Pattern.compile("\"(\\\\.|[^\"\\\\])*\"");
    private static final Pattern KEYWORDS = Pattern.compile("\\b(if|for|while|case|catch)\\b");
    private static final Pattern SHORT_CIRCUIT = Pattern.compile("&&|\\|\\|");
    private static final Pattern TERNARY = Pattern.compile("\\?");

    private CyclomaticComplexityCalculator() {}

    public static int predicates(String javaSource) {
        String s = BLOCK_COMMENT.matcher(javaSource).replaceAll(" ");
        s = LINE_COMMENT.matcher(s).replaceAll(" ");
        s = STRING_LITERAL.matcher(s).replaceAll("\"\"");
        return count(KEYWORDS, s) + count(SHORT_CIRCUIT, s) + count(TERNARY, s);
    }

    public static int complexity(String javaSource) {
        return predicates(javaSource) + 1;
    }

    public static int complexityOfFile(Path javaFile) throws IOException {
        return complexity(Files.readString(javaFile));
    }

    private static int count(Pattern p, String s) {
        Matcher m = p.matcher(s);
        int n = 0;
        while (m.find()) {
            n++;
        }
        return n;
    }
}
