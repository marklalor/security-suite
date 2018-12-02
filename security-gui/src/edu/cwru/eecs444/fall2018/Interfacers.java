package edu.cwru.eecs444.fall2018;

import edu.cwru.eecs444.fall2018.implementations.Rsa;
import edu.cwru.eecs444.fall2018.implementations.VigenereCipher;

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
        INTERFACERS.put("Vigenere Cipher", new TextInterfacer() {
            @Override
            public String encipher(String key, String plaintext) {
                return new String(VigenereCipher.encode(key.toUpperCase().toCharArray(), plaintext.toUpperCase().toCharArray()));
            }

            @Override
            public String decipher(String key, String ciphertext) {
                return new String(VigenereCipher.decode(key.toUpperCase().toCharArray(), ciphertext.toUpperCase().toCharArray()));
            }
        });

        INTERFACERS.put("RSA Keygen", (KeypairGenInterfacer) () -> Rsa.generateKeyPair(Rsa.DEFAULT_KEY_SIZE));

        INTERFACERS.put("Binary Python", new PythonBinaryInterfacer("../python-template/program.py"));
        INTERFACERS.put("Text Python", new PythonTextInterfacer("../python-template/program_text.py"));
    }

    public static class PythonTextInterfacer implements TextInterfacer {

        private final String programPath;

        public PythonTextInterfacer(String programPath) {
            this.programPath = programPath;
        }

        private String runProgram(String action, String key, String plaintext) throws IOException {
            // Provide hex-encoded binary data to the python program
            ProcessBuilder builder= new ProcessBuilder("python", programPath, action, key, plaintext);
            Process p = builder.start();

            // Read characters from stdout of program as response
            InputStream is = p.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[MAX_MESSAGE_BYTES];

            int readByte;
            while ((readByte = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readByte);
            }

            return new String(buffer.toByteArray(), Interfacers.UTF8_CHARSET);
        }

        @Override
        public String encipher(String key, String plaintext) throws IOException {

            return runProgram("encipher", key, plaintext);
        }

        @Override
        public String decipher(String key, String ciphertext) throws IOException {
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
        String doAction(String key, String text) throws Exception;
    }

    public interface BinaryInterfacer {
        byte[] encipher(byte[] key, byte[] plaintext) throws Exception;
        byte[] decipher(byte[] key, byte[] ciphertext) throws Exception;
    }

    public interface TextInterfacer {
        String encipher(String key, String plaintext) throws Exception;
        String decipher(String key, String ciphertext) throws Exception;
    }

    public interface KeypairGenInterfacer {
        Rsa.KeyPair generateKeyPair();
    }
}
