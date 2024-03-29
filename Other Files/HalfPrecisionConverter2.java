public class HalfPrecisionConverter2 {

    private static final int EXPONENT_BIAS = 15;

    public static String convertInput(String input) {
        int xIndex;
        double decimal;
        char signBit = '0';
        String expRep;
        String normalized;
        String binary = null;
        String fraction;
        String output;

        // get sign bit
        if (input.charAt(0) == '-') {
            signBit = '1';
            input = input.substring(1);
        }

        xIndex = input.indexOf('x');
        // if input is base-10, convert to binary value
        if (input.charAt(xIndex + 1) == '1') {
            decimal = base10ToDecimal(input, xIndex);
            binary = decimalToBinary(decimal);
        } else {
            binary = input;
        }

        // normalize to 1.f
        normalized = normalize(binary);
        System.out.println(normalized);

        // get exponent representation
        expRep = getExpRep(normalized);

        // get fraction representation
        if (normalized.indexOf('.') == -1) {
            fraction = "0000000000";
        } else {
            fraction = normalized.substring(normalized.indexOf('.') + 1, normalized.indexOf('x'));

            // Limit fraction to 10 digits
            if (fraction.length() > 10) {
                fraction = fraction.substring(0, 10);
            } else {
                while (fraction.length() < 10) {
                    fraction = fraction + "0";
                }
            }
        }

        // special case: Zero
        if ("0".equals(input) && signBit == '1') {
            expRep = "00000";
            fraction = "0000000000";

            System.out.println("hello");
            output = signBit + " " + expRep + " " + fraction;
            return output;
        } else if ("0".equals(input) || "Zero".equalsIgnoreCase(input) ||
                "zero".equalsIgnoreCase(input)
                || "+0".equals(input)) {
            signBit = '0';
            expRep = "00000";
            fraction = "0000000000";

            output = signBit + " " + expRep + " " + fraction;
            return output;
        }

        // Infinity
        if ((normalized.indexOf('^') + 1 > 1)
                || ((normalized.indexOf('^') + 1 == 1) && (normalized.indexOf('^') + 2 >= 5))) {
            expRep = "11111";
            fraction = "0000000000";
            System.out.println(fraction);
        }

        // Nan
        if ("NaN".equals(input)) {
            signBit = 'x';
            expRep = "11111";
            fraction = "01xxxxxxxx or 1xxxxxxxxx";
        }

        output = signBit + " " + expRep + " " + fraction;

        // answer that will appear in the GUI
        return output;
    }

    // Convert base-10 input to decimal value as double
    private static double base10ToDecimal(String input, int index) {
        String[] parts = input.split("x10\\^");
        double base = Double.parseDouble(parts[0]);
        int exponent = Integer.parseInt(parts[1]);

        return base * Math.pow(10, exponent);
        // double decimal = base * Math.pow(10, exponent);

        // return decimal;
    }

    // Convert decimal value to binary value as String
    private static String decimalToBinary(double input) {
        long intPart = (long) input;
        double fractionalPart = input - intPart;

        StringBuilder binaryIntPart = new StringBuilder(Long.toBinaryString(intPart));

        StringBuilder binaryFractionalPart = new StringBuilder(".");

        if (fractionalPart == 0) {
            return binaryIntPart.toString();
        }

        while (fractionalPart > 0) {
            if (binaryFractionalPart.length() > 32) {
                break;
            }
            fractionalPart *= 2;
            if (fractionalPart >= 1) {
                binaryFractionalPart.append(1);
                fractionalPart -= 1;
            } else {
                binaryFractionalPart.append(0);
            }
        }

        return binaryIntPart.toString() + binaryFractionalPart.toString();
    }

    // convert binary value to Hexadecimal (supposedly the input must contain the
    // concancated binary rep in a variable)
    public static String binaryToHex(String result) {
        result = result.trim().replace(" ", ""); // Remove any spaces and dots from the binary string
        // throws an error if its not binary
        if (!result.matches("[01]+")) {
            throw new IllegalArgumentException("Invalid binary input: " + result);
        }

        while (result.length() % 4 != 0) {
            result = "0" + result;
        }

        // Initialize StringBuilder to store the hexadecimal result
        StringBuilder hexBuilder = new StringBuilder();

        for (int i = 0; i < result.length(); i += 4) {
            String chunk = result.substring(i, i + 4);

            int decimalValue = Integer.parseInt(chunk, 2);
            String hexDigit = Integer.toHexString(decimalValue).toUpperCase();
            hexBuilder.append(hexDigit);
        }

        return hexBuilder.toString();
    }

    // Normalize binary to 1.f
    private static String normalize(String input) {
        int normalizedExponent;
        int digitsBeforePoint;
        int digitsBeforeOne;
        int exponentIndex = input.indexOf("x2^");
        int exponentPart = 0;
        String exponentSubstring;

        if (exponentIndex != -1) {
            exponentSubstring = input.substring(exponentIndex + 3);
            if (exponentSubstring.startsWith("-")) {
                exponentPart = -Integer.parseInt(exponentSubstring.substring(1));
            } else {
                exponentPart = Integer.parseInt(exponentSubstring);
            }
            input = input.substring(0, exponentIndex);
        }

        if (input.indexOf('.') == -1) {
            digitsBeforeOne = input.indexOf('1');
            normalizedExponent = input.length() - digitsBeforeOne + exponentPart - 1;
        } else {
            digitsBeforePoint = input.indexOf('.');
            digitsBeforeOne = input.indexOf('1');
            if (digitsBeforePoint > digitsBeforeOne) {
                input = input.replace(".", "");
                normalizedExponent = digitsBeforePoint - digitsBeforeOne + exponentPart - 1;
            } else {
                input = input.replace(".", "");
                input = input.substring(digitsBeforePoint);
                digitsBeforeOne = digitsBeforeOne - digitsBeforePoint;
                normalizedExponent = exponentPart - digitsBeforeOne;
            }
        }

        if ((input.indexOf('1') + 1) == input.length()) {
            return "1" + "x2^" + normalizedExponent;
        } else {
            return "1." + input.substring(input.indexOf('1') + 1) + "x2^" + normalizedExponent;
        }
    }

    // get binary exponent representation as String
    private static String getExpRep(String input) {
        String exponentSubstring;
        String expRep;
        int e;
        double ePrime;

        exponentSubstring = input.substring(input.indexOf('^') + 1);
        if (exponentSubstring.startsWith("-")) {
            e = -Integer.parseInt(exponentSubstring.substring(1));
        } else {
            e = Integer.parseInt(exponentSubstring);
        }

        ePrime = EXPONENT_BIAS + e;
        expRep = decimalToBinary(ePrime);

        while (expRep.length() < 5) {
            expRep = "0" + expRep;
        }

        return expRep;
    }
}