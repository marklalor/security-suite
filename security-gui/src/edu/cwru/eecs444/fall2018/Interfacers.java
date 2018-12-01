package edu.cwru.eecs444.fall2018;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Interfacers {
    public static final int MAX_MESSAGE_BYTES = 65536;
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static final Map<String, Object> INTERFACERS = new HashMap<>();

    static {
        INTERFACERS.put("Binary Python", new PythonBinaryInterfacer("../python-template/program.py"));
        INTERFACERS.put("Text Python", new PythonTextInterfacer("../python-template/program_text.py"));
    }

    public static class PythonTextInterfacer implements TextInterfacer {

        private final String programPath;

        public PythonTextInterfacer(String programPath) {
            this.programPath = programPath;
        }

        private char[] runProgram(String action, char[] key, char[] plaintext) throws IOException {
            // Provide hex-encoded binary data to the python program
            ProcessBuilder builder= new ProcessBuilder("python", programPath,
                    action, String.valueOf(key), String.valueOf(plaintext));
            Process p = builder.start();

            // Read characters from stdout of program as response
            InputStream is = p.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[MAX_MESSAGE_BYTES];

            int readByte;
            while ((readByte = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readByte);
            }

            return new String(buffer.toByteArray(), Interfacers.UTF8_CHARSET).toCharArray();
        }

        @Override
        public char[] encipher(char[] key, char[] plaintext) throws IOException {

            return runProgram("encipher", key, plaintext);
        }

        @Override
        public char[] decipher(char[] key, char[] ciphertext) throws IOException {
            return runProgram("decipher", key, ciphertext);
        }
    }

    public static class PythonBinaryInterfacer implements BinaryInterfacer {

        private final String programPath;

        public PythonBinaryInterfacer(String programPath) {
            this.programPath = programPath;
        }

        private byte[] runProgram(String action, byte[] key, byte[] plaintext) throws IOException {
            // Provide hex-encoded binary data to the python program
            ProcessBuilder builder= new ProcessBuilder("python", programPath,
                    action, Utilities.bytesToHex(key), Utilities.bytesToHex(plaintext));
            Process p = builder.start();

            // Read bytes from stdout of program as response
            InputStream is = p.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[MAX_MESSAGE_BYTES];

            int readByte;
            while ((readByte = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readByte);
            }

            return buffer.toByteArray();
        }

        @Override
        public byte[] encipher(byte[] key, byte[] plaintext) throws IOException {
            return runProgram("encipher", key, plaintext);
        }

        @Override
        public byte[] decipher(byte[] key, byte[] ciphertext) throws IOException {
            return runProgram("decipher", key, ciphertext);
        }
    }

    @FunctionalInterface
    public interface BinaryAction {
        byte[] doAction(byte[] key, byte[] text) throws Exception;
    }

    @FunctionalInterface
    public interface TextAction {
        char[] doAction(char[] key, char[] text) throws Exception;
    }

    public interface BinaryInterfacer {
        byte[] encipher(byte[] key, byte[] plaintext) throws Exception;
        byte[] decipher(byte[] key, byte[] ciphertext) throws Exception;
    }

    public interface TextInterfacer {
        char[] encipher(char[] key, char[] plaintext) throws Exception;
        char[] decipher(char[] key, char[] ciphertext) throws Exception;
    }

    public static class KeyPair {
        public final byte[] privateKey;
        public final byte[] publicKey;

        public KeyPair(byte[] privateKey, byte[] publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    public interface RSAInterfacer {
        KeyPair keygen();
        byte[] encipher(byte[] key, byte[] plaintext) throws Exception;
        byte[] decipher(byte[] key, byte[] ciphertext) throws Exception;
    }
}
