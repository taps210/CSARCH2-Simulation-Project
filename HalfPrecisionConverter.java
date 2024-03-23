import java.util.Arrays;

public class HalfPrecisionConverter {

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
<<<<<<< HEAD

        // get sign bit
=======
    
        //get sign bit
>>>>>>> 9cb268de2c711dc95448f967956253c24286ba02
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
<<<<<<< HEAD
        // normalize to 1.f
=======
    
        //normalize to 1.f
>>>>>>> 9cb268de2c711dc95448f967956253c24286ba02
        normalized = normalize(binary);
    
        // get exponent representation

        expRep = getExpRep(normalized);
<<<<<<< HEAD

        // Adjust exponent if lower than -15
        // Check if the string contains "x2^-" and the array length is as expected
        if (normalized.contains("x2^-")) {
            String[] parts = normalized.split("x2\\^");

            if (parts.length >= 2) {
                try {
                    int exponent = Integer.parseInt(parts[1]);

                    if (exponent < -15) {
                        normalized = adjustExponent(normalized);
                        expRep = "00000";
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where the exponent is not a valid integer
                    System.err.println("Error: Invalid exponent format.");
                }
            }
        }

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

        // Nan
        if ("NaN".equals(input)) {
=======
    
        // get fraction representation
        if (normalized.indexOf('.') == -1) {
            fraction = "0000000000";
        }
        else {
            fraction = normalized.substring(normalized.indexOf('.') + 1, normalized.indexOf('x'));
            while (fraction.length() < 10) {
                fraction = fraction + "0";
            }
        }
    
        // special case: Zero
        if ("0".equals(input) || "Zero".equalsIgnoreCase(input) || "zero".equalsIgnoreCase(input) || "+0".equals(input)){
            signBit = '0';
            expRep = "00000";
            fraction = "0000000000";
        } else if ("-0".equals(input)){
            signBit = '1';
            expRep = "00000";
            fraction = "0000000000";
        }
    
        // Infinity
        if ((normalized.indexOf('^') + 1 > '1') || ((normalized.indexOf('^') + 1 == '1') && (normalized.indexOf('^') + 2 >= '5'))){
            expRep = "11111";
            fraction = "0000000000";
        }
    
        // Nan
        if ("NaN".equals(input)){
>>>>>>> 9cb268de2c711dc95448f967956253c24286ba02
            signBit = 'x';
            expRep = "11111";
            fraction = "01xxxxxxxx or 1xxxxxxxxx";
        }
<<<<<<< HEAD

        output = signBit + " " + expRep + " " + fraction;

        // answer that will appear in the GUI
=======
    
        output = signBit + " " + expRep + " " + fraction;
    
        //answer that will appear in the GUI
>>>>>>> 9cb268de2c711dc95448f967956253c24286ba02
        return output;
    }
    

    public static String adjustExponent(String input) {
        // Parse the input to extract the significand and exponent
        String[] parts = input.split("x2\\^-");
        String significand = parts[0];
        int exponent = Integer.parseInt(parts[1]);

        // Calculate the difference in exponents to reach -14
        int exponentDifference = exponent + (-14); // -18 + 4 = -14

        // Remove the decimal point from the significand
        StringBuilder adjustedSignificand = new StringBuilder(significand);
        int decimalPointIndex = adjustedSignificand.indexOf(".");
        if (decimalPointIndex != -1) {
            adjustedSignificand.deleteCharAt(decimalPointIndex);
        }

        // Prepend zeros to the significand based on the exponent difference
        for (int i = 0; i < exponentDifference; i++) {
            adjustedSignificand.insert(0, '0');
        }

        // Insert "." after the first zero
        int firstZeroIndex = adjustedSignificand.indexOf("0");
        if (firstZeroIndex != -1) {
            adjustedSignificand.insert(firstZeroIndex + 1, '.');
        }

        // Format the result with the adjusted exponent
        return adjustedSignificand + "x2^-14";
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