package fr.kaddath.apps.fluxx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String humaniseCamelCase(String word) {
        Pattern pattern = Pattern.compile("([A-Z]|[a-z])[a-z]*");

        List<String> tokens = new ArrayList<String>();
        Matcher matcher = pattern.matcher(word);
        String acronym = "";
        while (matcher.find()) {
            String found = matcher.group();
            if (found.matches("^[A-Z]$")) {
                acronym += found;
            } else {
                if (acronym.length() > 0) {
                    //we have an acronym to add before we continue
                    tokens.add(acronym);
                    acronym = "";
                }
                tokens.add(found.toLowerCase());
            }
        }
        if (acronym.length() > 0) {
            tokens.add(acronym);
        }
        if (tokens.size() > 0) {
            String humanisedString = capitaliseFirstLetter(tokens.remove(0));
            for (String s : tokens) {
                humanisedString += " " + s;
            }
            return humanisedString;
        }

        return word;
    }

    private static String capitaliseFirstLetter(String string) {
        String newString = "";
        newString+=string.charAt(0);
        newString = newString.toUpperCase();
        newString += string.substring(1);
        return newString;
    }
}
