
public class Sha1 {

    private int A = 0x67452301;
    private int B = 0xEFCDAB89;
    private int C = 0x98BADCFE;
    private int D = 0x10325476;
    private int E = 0xC3D2E1F0;
    private int TEMP = 0;

    private int H0 = 0x67452301;
    private int H1 = 0xEFCDAB89;
    private int H2 = 0x98BADCFE;
    private int H3 = 0x10325476;
    private int H4 = 0xC3D2E1F0;

    private int K1 = 0x5A827999;
    private int K2 = 0x6ED9EBA1;
    private int K3 = 0x8F1BBCDC;
    private int K4 = 0xCA62C1D6;

    public static String encode(String input) {
        Sha1 sha = new Sha1();
        StringBuilder stringBuilder = sha.charToBinary(input.toCharArray());
        sha.padMessage(stringBuilder);
        String[] words = sha.splitBinaryNibble(stringBuilder.toString());
        int[] processedWords = sha.processMessage(words);

        sha.firstIteration(sha, processedWords);
        sha.secondIteration(sha, processedWords);

        sha.H0 = sha.H0 + sha.A;
        sha.H1 = sha.H1 + sha.B;
        sha.H2 = sha.H2 + sha.C;
        sha.H3 = sha.H3 + sha.D;
        sha.H4 = sha.H4 + sha.E;

        return sha.toHex(sha);
    }

    private String toHex(Sha1 sha) {
        int[] hashValues = { sha.H0, sha.H1, sha.H2, sha.H3, sha.H4 };

        StringBuilder hashResult = new StringBuilder();

        for (int value : hashValues) {
            String hex = Integer.toHexString(value);
            hashResult.append(hex);
        }

        return hashResult.toString();
    }

    private void secondIteration(Sha1 sha, int[] processedWords) {
        for (int i = 0; i < 80; i++) {
            if (i < 20) {
                sha.TEMP = sha.circularShiftOperation(sha.A, 5) + ((sha.B & sha.C) | ((~sha.B) & sha.D)) + sha.E + processedWords[i] + sha.K1 ;
            } else if (i < 40) {
                sha.TEMP = sha.circularShiftOperation(sha.A, 5) + (sha.B ^ sha.C ^ sha.D) + sha.E + processedWords[i] + sha.K2;
            } else if (i < 60) {
                sha.TEMP = sha.circularShiftOperation(sha.A, 5) + ((sha.B & sha.C) | (sha.B & sha.D) | (sha.C & sha.D)) + sha.E + processedWords[i] + sha.K3;
            } else {
                sha.TEMP = sha.circularShiftOperation(sha.A, 5) + (sha.B ^ sha.C ^ sha.D) + sha.E + processedWords[i] + sha.K4;
            }

            sha.E = sha.D;
            sha.D = sha.C;
            sha.C = sha.circularShiftOperation(sha.B, 30);
            sha.B = sha.A;
            sha.A = sha.TEMP;
        }
    }

    private void firstIteration(Sha1 sha, int[] processedWords) {
        for (int i = 0; i < 80; i++) {
            if (i < 20) {
                processedWords[i] = sha.circularShiftOperation(processedWords[i], 1);
            } else if (i < 40) {
                processedWords[i] = sha.circularShiftOperation(processedWords[i], 2);
            } else if (i < 60) {
                processedWords[i] = sha.circularShiftOperation(processedWords[i], 1);
            } else {
                processedWords[i] = sha.circularShiftOperation(processedWords[i], 2);
            }
        }
    }

    private void padMessage(StringBuilder stringBuilder) {

        int originalLength = stringBuilder.length();
        int paddingLength = (448 - (originalLength % 512)) % 512;

        stringBuilder.append("1");

        stringBuilder.append("0".repeat(Math.max(0, paddingLength)));

        String binaryLength = String.format("%64s", Long.toBinaryString(originalLength)).replace(' ', '0');
        stringBuilder.append(binaryLength);
    }

    private StringBuilder charToBinary(char[] inputToChars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : inputToChars) {
            for (int i = 7; i >= 0; i--) {
                char mask = (char) (1 << i);
                char result = ((c & mask) != 0 ? '1' : '0');
                stringBuilder.append(result);
            }
        }

        return stringBuilder;
    }

    private String[] splitBinaryNibble(String binaryString) {
        int numSubstrings = 16;
        int substringLength = 32;

        int paddingLength = numSubstrings * substringLength - binaryString.length();

        String[] substrings = new String[16];

        StringBuilder paddedBinary = new StringBuilder(binaryString);
        paddedBinary.append("0".repeat(Math.max(0, paddingLength)));

        for (int i = 0; i < numSubstrings; i++) {
            int startIndex = i * substringLength;
            int endIndex = startIndex + substringLength;
            substrings[i] = paddedBinary.substring(startIndex, endIndex);
        }

        return substrings;
    }

    public int[] processMessage(String[] messageWords) {
        int[] words = new int[80];

        for (int i = 0; i < 16; i++) {
            words[i] = Integer.parseInt(messageWords[i], 2);
        }

        for (int i = 16; i < 80; i++) {
            int temp = words[i - 3] ^ words[i - 8] ^ words[i - 14] ^ words[i - 16];
            words[i] = Integer.rotateLeft(temp, 1);
        }

        return words;
    }

    private int circularShiftOperation(int value, int n) {
        return (value << n) | (value >>> (32 - n));
    }
}
