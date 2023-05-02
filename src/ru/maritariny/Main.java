package ru.maritariny;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static Map<Character, Integer> mapChar = new HashMap();
    private static Map<Integer, Character> mapInteger = new HashMap();

    public static void main(String[] args) throws IOException {
        fillMap();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] textArray = reader.readLine().split(" ");
        long n = Long.parseLong(reader.readLine());
        List<String> message = new ArrayList<>();
        for (long i = 0; i < n; i++) {
            message.add(reader.readLine());
        }
        reader.close();

        decode(textArray, message);
//        List<String> result2 = decode(textArray, message);
//        for (String t: result2) {
//            System.out.println(t);
//        }
//        System.out.println();
//        List<String> result = decodeFast(textArray, message);
//        for (String t: result) {
//            System.out.println(t);
//        }

    }

    public static void decode (String[] textArray, List<String> message) {
        Map<String, String> match = new HashMap<>();
        for (String word : textArray) {
            match.put(convertWord(word), word);
        }
        StringBuilder sb = new StringBuilder();
        for (String word : message) {
            String tmp = convertWord(word);
            sb.append(match.get(tmp));
            sb.append("\n");
        }
        System.out.println(sb);
    }

    private static String convertWord(String word) {
        char[] charArray = word.toCharArray();
        int k = 1 - mapChar.get(charArray[0]);
        if (k == 0) {
            return word;
        }
        StringBuilder sb = new StringBuilder();
        for (char ch : charArray) {
            int n = mapChar.get(ch) + k;
            if (n <= 0) {
                n += 26;
            }
            sb.append(mapInteger.get(n));
        }
        return sb.toString();
    }

    private static void fillMap() {
        int i = 1;
        for(char c = 'a'; c <='z'; c++) {
            mapChar.put(c, i);
            mapInteger.put(i, c);
            i++;
        }
    }

    public static ArrayList<String> decodeFast (String[] textArray, List<String> message) {
        ArrayList<String> result = new ArrayList<>();

        Map<Integer, Map<Integer, List<String>>> mainMap = new HashMap<>();
        String shortWord = null;
        for (String word : textArray) {
            Integer length = word.length();

            if (shortWord == null && length == 1) {
                shortWord = word;
                continue;
            }
            Map<Integer, List<String>> allWordsMap = mainMap.get(length);
            int checkSum = getCheckSum(word);

            if (allWordsMap == null) {
                allWordsMap = new HashMap<>();
                List<String> list = new ArrayList<>();
                list.add(word);
                allWordsMap.put(checkSum, list);
                mainMap.put(length, allWordsMap);
            } else {
                List<String> list = allWordsMap.get(checkSum);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(word);
                allWordsMap.put(checkSum, list);
                mainMap.put(length, allWordsMap);
            }
        }
        for (String word : message) {
            int length = word.length();
            if (length == 1) {
                result.add(shortWord);
                continue;
            }

            String wordIn = "";
            int checkSum = getCheckSum(word);
            Map<Integer, List<String>> allWordsMap = mainMap.get(length);
            List<String> list = allWordsMap.get(checkSum);
            if (list.size() == 1) {
                wordIn = list.get(0);
            } else {
                wordIn = decryptWord(length, word, list);
            }
            result.add(wordIn);
        }
        return result;
    }

    private static int getCheckSum(String word) {
        int result = 0;
        for (int i = 1; i < word.length(); i++) {
            char pch = word.charAt(i - 1);
            char ch = word.charAt(i);

            result += (mapChar.get(pch) - mapChar.get(ch));
        }
        return (result < 0) ? result + 26 : result;
    }

    private static String decryptWord(int key, String outWord, List<String> in) {
        String result = "";
        int[] outNumbers = new int[key];
        for (int i = 0; i < key; i++) {
            char ch = outWord.charAt(i);
            outNumbers[i] = mapChar.get(ch);
        }

        for (String inWord : in) {
            boolean ok = true;
            int[] inNumbers = new int[key];
            for (int i = 0; i < key; i++) {
                char ch = inWord.charAt(i);
                inNumbers[i] = mapChar.get(ch);
                if (i != 0) {
                    int outDif = outNumbers[i] - outNumbers[i - 1];
                    if (outDif < 0 ) {
                        outDif += 26;
                    }
                    int inDif = inNumbers[i] - inNumbers[i-1];
                    if (inDif < 0 ) {
                        inDif += 26;
                    }
                    if (outDif != inDif) {
                        ok = false;
                        break;
                    }
                }
            }
            if (!ok) {
                continue;
            }
            result = inWord;
        }
        return result;
    }
}
