package org.suai.analyser;

import org.suai.Example;
import org.suai.parser.ParseException;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyser {

    class Result {
        int size;
        int maxNesting;
        String code;

        public Result(int size, int maxNesting, String code) {
            this.size = size;
            this.maxNesting = maxNesting;
            this.code = code;
        }
    }

    public Example analyse (Example ex, String funcName) {
//        System.out.println("\t" + ex.getSource());
        Example exampleAnalysed;
        if (ex.getSource().contains("cplusplus.com") ||
                ex.getSource().contains("cppreference.com")) {
            exampleAnalysed = new Example(ex);
            exampleAnalysed.setRating(1000);
            return exampleAnalysed;
        } else if (ex.getSource().contains("stackoverflow.com")) {
            return new Example(ex);
        } else { // for searchcode (github, gitlab, bitbucket and etc.)
            ex = deleteComments(ex);
            Result[] res = new Result[0];
            try {
                res = extractFunction(ex, funcName);
            } catch (ParseException e) {
                return new Example(ex.getSource(), null, 0);
            }
//            System.out.println("Found functions: " + res.length);
            exampleAnalysed = new Example(ex.getSource());
            if (res.length > 0) {
                exampleAnalysed = countRating(res, 0.95, 0.7, ex.getSource());
            }
            return exampleAnalysed;
        }
    }

    private Example deleteComments (Example ex) {
        String code = ex.getCode().replaceAll("(?s:\\/\\*.*?\\*\\/)|\\/\\/.*", "");
        ex.setCode(code);
        return ex;
    }

    private Result[] extractFunction(Example ex, String funcName) throws ParseException {
        // .*\w+\s*\(.*\)\s*\{
        // \n((?!if|for|while|do|switch)([\w\*]+( )*?){2,}\(([^!@#$+%^;]+?)\)(?!\s*;))
        // ^([\w\*]+( )*?){2,}\(([^!@#$+%^;]+?)\)(?!\s*;)  // it's work
        // \n?(([\w\*]+( )*?){2,}\(([^!@#$+%^;]+?)\)(?!\s*;))
        // \n(([\w*]+( )*?){2,}\(([^!@#$+%^;]+?)\)(?!\s*;))\s*\{  // \s*\{  --- with open bracket in the end (don't work)
        String[] lines = ex.getCode().split("\\r?\\n");

        Pattern beginPattern = Pattern.compile("\\n(([\\w*]+( )*?){2,}\\(([^!@#$+%^;]*?)\\)(?!\\s*;)[^;])");
        Matcher beginMatcher = beginPattern.matcher(ex.getCode());
        ArrayList<String> funcDefinitionsHead = new ArrayList<>();
        while (beginMatcher.find()) {
            String line = beginMatcher.group(1);
            if (!line.contains(";")) { // to avoid function declaration (regexp for some reason does not handle it.)
                funcDefinitionsHead.add(line);
            }
        }
        if (funcDefinitionsHead.isEmpty()) {
            throw new ParseException("Can't find function definition!");
        }
        ////////////////////////
        /*System.out.println("FUNCTIONS: _____________________");
        for (String def: funcDefinitionsHead) {
            System.out.println(def);
        }
        System.out.println("_________________________________");*/
        ///////////////////////
        // for search first string in multiline function definitions
        String[] beginsOfDefinitions = new String[funcDefinitionsHead.size()];
        for (int i = 0; i < funcDefinitionsHead.size(); i++) {
            String[] defLines = funcDefinitionsHead.get(i).split("\\r?\\n");
            String beginDef = defLines[0];
            beginsOfDefinitions[i] = beginDef;
        }

        int funcNumber = 0;
        int curNesting;
        int maxNesting;
        boolean funcFound;

        boolean[] funcNameOnLine = new boolean[beginsOfDefinitions.length];
        int[] beginFuncIndexes = new int[beginsOfDefinitions.length]; // for line numbers of functions beginnings 'f() { ...'
        int[] endFuncIndexes = new int[beginsOfDefinitions.length]; // for line numbers of functions endings '... }'
        int[] maxNestings = new int[beginsOfDefinitions.length];
        int[] funcSize = new int[beginsOfDefinitions.length];
        ArrayList<ArrayList<Integer>> notFuncIndexes = new ArrayList<>(funcDefinitionsHead.size()); // for #include and others
        ArrayList<Integer> notFuncInd = new ArrayList<>(); // for one function
        for (int i = 0; i < lines.length; i++) {
            curNesting = 0;
            maxNesting = 0;
            funcFound = false;
            if (lines[i].contains(beginsOfDefinitions[funcNumber])) {
                beginFuncIndexes[funcNumber] = i;
                funcFound = true;
                while (!lines[i].contains("{")) {
                    i++;
                    if (i >= lines.length - 1)
                        break;
                }
                i++; // pass first open bracket of function definition
                do {
                    if (i >= lines.length - 1)
                        break;

                    if (lines[i].contains("{")) {
                        curNesting++;
                    }
                    if (curNesting > maxNesting) {
                        maxNesting = curNesting;
                    }

                    if (lines[i].contains("}")) {
                        curNesting--;
                    }
                    if (Pattern.compile("\\b" + funcName + "\\s*\\(").matcher(lines[i]).find()) {
                        funcNameOnLine[funcNumber] = true;
                    }
                    i++;
                    if (i >= lines.length - 1)
                        break;
                } while (curNesting >= 0);
            }
            if (funcFound) {
                endFuncIndexes[funcNumber] = i;
                funcSize[funcNumber] = endFuncIndexes[funcNumber] - beginFuncIndexes[funcNumber];
                notFuncIndexes.add(new ArrayList<>(notFuncInd));
                notFuncInd.clear();
                maxNestings[funcNumber] = maxNesting;
                funcNumber++;
                if (funcNumber >= funcDefinitionsHead.size()) {
                    break;
                }
            } else {
                notFuncInd.add(i);
            }
        }

        ArrayList<String> funcCodes = new ArrayList<>();
        int foundFunctions = funcDefinitionsHead.size();
        ArrayList<Integer> foundFuncIndexes = new ArrayList<>();
        for (int i = 0; i < funcDefinitionsHead.size(); i++) {
            if (!funcNameOnLine[i]) {
                foundFunctions--;
                continue; // if this func don't contain funcName in body
            } else {
                foundFuncIndexes.add(i);
            }
            ArrayList<Integer> funcLines = new ArrayList<>();
            for (int j = beginFuncIndexes[i]; j <= endFuncIndexes[i]; j++) {
                funcLines.add(j);
            }
            // to remove all copies
            Set<Integer> set = new TreeSet<>(notFuncIndexes.get(i));
            if (i != 0) { // to save all #include and others, that in beginning of file in 2nd, 3rd , ... functions
                set.addAll(notFuncIndexes.get(0));
            }
            set.addAll(funcLines);
            funcSize[i] = set.size();
            StringBuffer sb = new StringBuffer();
            int setI = 0;
            for (Integer lineN: set) {
                String line = lines[lineN];
                if (line.equals("") || line.matches("\\s*")) { // if line is empty than not append it in funcCode
                    if (setI >= beginFuncIndexes[i] && setI <= endFuncIndexes[i]) { // if it's line from function body
                        funcSize[i]--; // decrease size
                    }
                } else {
                    sb.append(lines[lineN]).append("\n");
                }
                setI++;
            }
            funcCodes.add(sb.toString());
        }
        if (foundFunctions <= 0) {
            throw new ParseException("Can't find functions that using " + funcName);
        }
        Result[] results = new Result[foundFunctions];
        for (int i = 0; i < results.length; i++) {
            results[i] = new Result(funcSize[foundFuncIndexes.get(i)], maxNestings[foundFuncIndexes.get(i)], funcCodes.get(i));
        }

        return results;
    }

    private Example countRating (Result[] results, double sizeCoef, double nestingCoef, String source) {
        ArrayList<Double> rating = new ArrayList<>(results.length);
        for (Result res: results) {
            int n = 0;
            if (res.size < 15) {
                n = 15 - res.size;
            } else if (res.size > 25) {
                n = res.size - 25;
            }
            double sizeRating = 500 * Math.pow(sizeCoef, n);

            int k = 0;
            if (res.maxNesting > 2) {
                k = res.maxNesting - 2;
            }
            double nestingRating = 500 * Math.pow(nestingCoef, k);
            double scaleCoef = 0.7; // parts of code from GitHub, GitLab and other can't be better examples, than code
            // from cplusplus, cppreference and StackOverflow cites. So rating of examples from GitHub-like cites
            // should be scaled down.
            rating.add((sizeRating + nestingRating) * scaleCoef);
        }
        double maxRating = 0;
        int indexOfMaxRatingFunc = 0;
        for (int i = 0; i < rating.size(); i++) {
            if (rating.get(i) > maxRating) {
                maxRating = rating.get(i);
                indexOfMaxRatingFunc = i;
            }
        }

        return new Example(source, results[indexOfMaxRatingFunc].code, (int) Math.round(rating.get(indexOfMaxRatingFunc)));
    }
}