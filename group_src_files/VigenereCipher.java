package security;

public class VigenereCipher {

    public static char[] encode(String input, String key) {
        char[] inputArray = input.toUpperCase().toCharArray();
        char[] keyArray = key.toUpperCase().toCharArray();
        char[] outputArray = new char[inputArray.length];
        int keyIndex = 0;
        for (int i = 0; i < inputArray.length; i++) {
            outputArray[i] = (char) (((inputArray[i] + keyArray[keyIndex]) %26) + 'A');
            keyIndex = (keyIndex + 1) % keyArray.length;
        }
        return outputArray;
    }

    public static char[] decode(String input, String key) {
        char[] inputArray = input.toUpperCase().toCharArray();
        char[] keyArray = key.toUpperCase().toCharArray();
        char[] outputArray = new char[inputArray.length];
        int keyIndex = 0;
        for (int i = 0; i < inputArray.length; i++) {
            outputArray[i] = (char) (((inputArray[i] - keyArray[keyIndex]) %26) + 'A');
            keyIndex = (keyIndex + 1) % keyArray.length;
        }
        return outputArray;
    }
}
