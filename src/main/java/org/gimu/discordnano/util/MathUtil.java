/*
 *  Copyright 2016 Son Nguyen <mail@gimu.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.gimu.discordnano.util;

public class MathUtil {

    private final String exp;
    int pos = -1;
    int ch;

    public MathUtil(String exp)
    {
        this.exp = exp;
    }

    public void nextChar()
    {
        ch = (++pos < exp.length()) ? exp.charAt(pos) : -1;
    }

    public boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    public double eval() {
        nextChar();
        double x = parseExpression();
        if (pos < exp.length()) throw new RuntimeException("Unexpected: " + (char) ch);
        return x;
    }

    double parseExpression() {
        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor
        // factor = `+` factor | `-` factor | `(` expression `)`
        //        | number | functionName factor | factor `^` factor
        double x = parseTerm();
        while (true) {
            if (eat('+')) x += parseTerm(); // Addition
            else if (eat('-')) x -= parseTerm(); // Subtraction
            else return x;
        }
    }

    double parseTerm() {
        double x = parseFactor();
        while (true) {
            if (eat('*')) x *= parseFactor(); // Multiplication
            else if (eat('/')) x /= parseFactor(); // Division
            else return x;
        }
    }

    double parseFactor() {
        if (eat('+')) return parseFactor(); // Unary plus
        if (eat('-')) return -parseFactor(); // Unary minus

        double x;
        int startPos = this.pos;
        if (eat('(')) { // parentheses
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(exp.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // functions
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = exp.substring(startPos, this.pos);
            x = parseFactor();
            switch (func) {
                case "sqrt":
                    x = Math.sqrt(x);
                    break;
                case "sin":
                    x = Math.sin(x);
                    break;
                case "cos":
                    x = Math.cos(x);
                    break;
                case "tan":
                    x = Math.tan(x);
                    break;
                case "asin":
                    x = Math.asin(x);
                    break;
                case "acos":
                    x = Math.acos(x);
                    break;
                case "atan":
                    x = Math.atan(x);
                    break;
                case "log":
                    x = Math.log10(x);
                    break;
                case "ln":
                    x = Math.log(x);
                    break;
                case "sinh":
                    x = Math.sinh(x);
                    break;
                case "cosh":
                    x = Math.cosh(x);
                    break;
                case "tanh":
                    x = Math.tanh(x);
                    break;
                default:
                    throw new RuntimeException("Unknown function: " + func);
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^')) x = Math.pow(x, parseFactor()); // Exponentiation

        return x;
    }
}
