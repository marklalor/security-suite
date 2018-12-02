package edu.cwru.eecs444.fall2018.implementations;

public class VigenereCipher {

    public static char[] encode(char[] key, char[] input) {
        char[] outputArray = new char[input.length];
        int keyIndex = 0;
        for (int i = 0; i < input.length; i++) {
            outputArray[i] = (char) (((input[i] + key[keyIndex]) %26) + 'A');
            keyIndex = (keyIndex + 1) % key.length;
        }
        return outputArray;
    }

    public static char[] decode(char[] key, char[] input) {
        char[] outputArray = new char[input.length];
        int keyIndex = 0;
        for (int i = 0; i < input.length; i++) {
            outputArray[i] = (char) (((input[i] - key[keyIndex] + 26) %26) + 'A');
            keyIndex = (keyIndex + 1) % key.length;
        }
        return outputArray;
    }

    public static void main(String[] args) {
        char[] key = "KEY".toCharArray();
        char[] input = "THISISASENTENCE".toCharArray();
        System.out.println(new String(decode(key, encode(key, input)))); // prints :H/9I9AS+NT+NC+
    }
}
