public class  HalfPrecisionConverter {

    private static final int EXPONENT_BIAS = 15;

    public static String convertInput(String input) {
        int xIndex;
        double decimal;
        char signBit = '0';
        //String input = "-32.5x10^0"; // Example input string
        String expRep;
        String normalized;
        String binary = null;
        String fraction;
        String output;

        //get sign bit
        if (input.charAt(0) == '-') {
            signBit = '1';
            input = input.substring(1);
        }

        xIndex = input.indexOf('x');
        //if input is base-10, convert to binary value
        if (input.charAt(xIndex + 1) == '1') {
            decimal = base10ToDecimal(input, xIndex);
            binary = decimalToBinary(decimal);
        }
        else {
            binary = input;
        }

        //normalize to 1.f
        normalized = normalize(binary);

        // get exponent representation
        expRep = getExpRep(normalized);

        //denormalized
        /* if((normalized.indexOf('.') + 1) == '-' && (((normalized.indexOf('.') + 2) == '1' && (normalized.indexOf('.') + 3) > '5') || 
            ((normalized.indexOf('.') + 2) > '1' && (normalized.indexOf('.') + 3) != 'x')) ) {
            expRep = "00000";
        } */
        
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

        //special case: Zero
        if (input == "0" || input == "Zero" || input == "zero" || input == "+0"){
            signBit = '0';
            expRep = "00000";
            fraction = "0000000000";
        } else if (input == "-0"){
            signBit = '1';
            expRep = "00000";
            fraction = "0000000000";
        }



        

        output = signBit + " " + expRep + " " + fraction;

        //answer that will appear in the GUI
        return output;
    }



    //Convert base-10 input to decimal value as double
    private static double base10ToDecimal(String input, int index) {
        String[] parts = input.split("x10\\^");
        double base = Double.parseDouble(parts[0]);
        int exponent = Integer.parseInt(parts[1]);
        
        return base * Math.pow(10, exponent);
        //double decimal = base * Math.pow(10, exponent);

        //return decimal;
    }

    //Convert decimal value to binary value as String
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

    //convert binary value to Hexadecimal (supposedly the input must contain the concancated binary rep in a variable)
    private static String binaryToHex(String result){
        result=result.trim();
        //throws an error if its not binary
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
            }
            else {
                exponentPart = Integer.parseInt(exponentSubstring);
            }
            input = input.substring(0, exponentIndex);
        }

        if (input.indexOf('.') == -1) {
            digitsBeforeOne = input.indexOf('1');
            normalizedExponent = input.length() - digitsBeforeOne + exponentPart - 1;
        }
        else {
            digitsBeforePoint = input.indexOf('.');
            digitsBeforeOne = input.indexOf('1');
            if (digitsBeforePoint > digitsBeforeOne) {
                input = input.replace(".", "");
                normalizedExponent = digitsBeforePoint - digitsBeforeOne + exponentPart - 1;
            }
            else {
                input = input.replace(".", "");
                input = input.substring(digitsBeforePoint);
                digitsBeforeOne = digitsBeforeOne - digitsBeforePoint;
                normalizedExponent = exponentPart - digitsBeforeOne;
            }
        }

        if (input.indexOf('1') == -1) {
            return "zero";
        }
        else if ((input.indexOf('1') + 1) == input.length()) {
            return "1" + "x2^" + normalizedExponent;
        }
        else {
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
        }
        else {
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