package com.stark.video.chat.util;

/**
*
* Copyright 2003-2007 Jive Software.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* A collection of utility methods for String objects.
*/
public class StringUtils {
   private static final Logger LOGGER = Logger.getLogger(StringUtils.class.getName());

   public static final String QUOTE_ENCODE = "&quot;";
   public static final String APOS_ENCODE = "&apos;";
   public static final String AMP_ENCODE = "&amp;";
   public static final String LT_ENCODE = "&lt;";
   public static final String GT_ENCODE = "&gt;";

   /**
    * Escapes all necessary characters in the String so that it can be used
    * in an XML doc.
    *
    * @param string the string to escape.
    * @return the string with appropriate characters escaped.
    */
   public static CharSequence escapeForXML(final String string) {
       if (string == null) {
           return null;
       }
       final char[] input = string.toCharArray();
       final int len = input.length;
       final StringBuilder out = new StringBuilder((int)(len*1.3));
       CharSequence toAppend;
       char ch;
       int last = 0;
       int i = 0;
       while (i < len) {
           toAppend = null;
           ch = input[i];
           switch(ch) {
           case '<':
               toAppend = LT_ENCODE;
               break;
           case '>':
               toAppend = GT_ENCODE;
               break;
           case '&':
               toAppend = AMP_ENCODE;
               break;
           case '"':
               toAppend = QUOTE_ENCODE;
               break;
           case '\'':
               toAppend = APOS_ENCODE;
               break;
           default:
               break;
           }
           if (toAppend != null) {
               if (i > last) {
                   out.append(input, last, i - last);
               }
               out.append(toAppend);
               last = ++i;
           } else {
               i++;
           }
       }
       if (last == 0) {
           return string;
       }
       if (i > last) {
           out.append(input, last, i - last);
       }
       return out;
   }

   /**
    * Used by the hash method.
    */
   private static MessageDigest digest = null;

   /**
    * Hashes a String using the SHA-1 algorithm and returns the result as a
    * String of hexadecimal numbers. This method is synchronized to avoid
    * excessive MessageDigest object creation. If calling this method becomes
    * a bottleneck in your code, you may wish to maintain a pool of
    * MessageDigest objects instead of using this method.
    * <p>
    * A hash is a one-way function -- that is, given an
    * input, an output is easily computed. However, given the output, the
    * input is almost impossible to compute. This is useful for passwords
    * since we can store the hash and a hacker will then have a very hard time
    * determining the original password.
    *
    * @param data the String to compute the hash of.
    * @return a hashed version of the passed-in String
    */
   public synchronized static String hash(String data) {
       if (digest == null) {
           try {
               digest = MessageDigest.getInstance("SHA-1");
           }
           catch (NoSuchAlgorithmException nsae) {
               LOGGER.log(Level.SEVERE, "Failed to load the SHA-1 MessageDigest. Smack will be unable to function normally.", nsae);
           }
       }
       // Now, compute hash.
       try {
           digest.update(data.getBytes("UTF-8"));
       }
       catch (UnsupportedEncodingException e) {
           LOGGER.log(Level.SEVERE, "Error computing hash", e);
       }
       return encodeHex(digest.digest());
   }

   /**
    * Encodes an array of bytes as String representation of hexadecimal.
    *
    * @param bytes an array of bytes to convert to a hex string.
    * @return generated hex string.
    */
   public static String encodeHex(byte[] bytes) {
       StringBuilder hex = new StringBuilder(bytes.length * 2);

       for (byte aByte : bytes) {
           if (((int) aByte & 0xff) < 0x10) {
               hex.append("0");
           }
           hex.append(Integer.toString((int) aByte & 0xff, 16));
       }

       return hex.toString();
   }

   /**
    * Encodes a String as a base64 String.
    *
    * @param data a String to encode.
    * @return a base64 encoded String.
    */
   public static String encodeBase64(String data) {
       byte [] bytes = null;
       try {
           bytes = data.getBytes("ISO-8859-1");
       }
       catch (UnsupportedEncodingException uee) {
           throw new IllegalStateException(uee);
       }
       return encodeBase64(bytes);
   }

   /**
    * Encodes a byte array into a base64 String.
    *
    * @param data a byte array to encode.
    * @return a base64 encode String.
    */
   public static String encodeBase64(byte[] data) {
       return encodeBase64(data, false);
   }

   /**
    * Encodes a byte array into a bse64 String.
    *
    * @param data The byte arry to encode.
    * @param lineBreaks True if the encoding should contain line breaks and false if it should not.
    * @return A base64 encoded String.
    */
   public static String encodeBase64(byte[] data, boolean lineBreaks) {
       return encodeBase64(data, 0, data.length, lineBreaks);
   }

   /**
    * Encodes a byte array into a bse64 String.
    *
    * @param data The byte arry to encode.
    * @param offset the offset of the bytearray to begin encoding at.
    * @param len the length of bytes to encode.
    * @param lineBreaks True if the encoding should contain line breaks and false if it should not.
    * @return A base64 encoded String.
    */
   public static String encodeBase64(byte[] data, int offset, int len, boolean lineBreaks) {
       return Base64.encodeBytes(data, offset, len, (lineBreaks ?  Base64.NO_OPTIONS : Base64.DONT_BREAK_LINES));
   }

   /**
    * Decodes a base64 String.
    * Unlike Base64.decode() this method does not try to detect and decompress a gzip-compressed input.
    *
    * @param data a base64 encoded String to decode.
    * @return the decoded String.
    */
   public static byte[] decodeBase64(String data) {
       byte[] bytes;
       try {
           bytes = data.getBytes("UTF-8");
       } catch (java.io.UnsupportedEncodingException uee) {
           bytes = data.getBytes();
       }

       bytes = Base64.decode(bytes, 0, bytes.length, Base64.NO_OPTIONS);
       return bytes;
   }

   /**
    * Pseudo-random number generator object for use with randomString().
    * The Random class is not considered to be cryptographically secure, so
    * only use these random Strings for low to medium security applications.
    */
   private static Random randGen = new Random();

   /**
    * Array of numbers and letters of mixed case. Numbers appear in the list
    * twice so that there is a more equal chance that a number will be picked.
    * We can use the array to get a random number or letter by picking a random
    * array index.
    */
   private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                   "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

   /**
    * Returns a random String of numbers and letters (lower and upper case)
    * of the specified length. The method uses the Random class that is
    * built-in to Java which is suitable for low to medium grade security uses.
    * This means that the output is only pseudo random, i.e., each number is
    * mathematically generated so is not truly random.<p>
    *
    * The specified length must be at least one. If not, the method will return
    * null.
    *
    * @param length the desired length of the random String to return.
    * @return a random String of numbers and letters of the specified length.
    */
   public static String randomString(int length) {
       if (length < 1) {
           return null;
       }
       // Create a char buffer to put random letters and numbers in.
       char [] randBuffer = new char[length];
       for (int i=0; i<randBuffer.length; i++) {
           randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
       }
       return new String(randBuffer);
   }

   /**
    * Returns true if CharSequence is not null and is not empty, false otherwise
    * Examples:
    *    isNotEmpty(null) - false
    *    isNotEmpty("") - false
    *    isNotEmpty(" ") - true
    *    isNotEmpty("empty") - true
    *
    * @param cs checked CharSequence
    * @return true if string is not null and is not empty, false otherwise
    */
   public static boolean isNotEmpty(CharSequence cs) {
       return !isNullOrEmpty(cs);
   }

   /**
    * Returns true if the given CharSequence is not null or empty.
    *
    * @param cs
    * @return true if the given CharSequence is not null or empty
    */
   public static boolean isNullOrEmpty(CharSequence cs) {
       return cs == null || cs.length() == 0;
   }

   public static String collectionToString(Collection<String> collection) {
       StringBuilder sb = new StringBuilder();
       for (String s : collection) {
           sb.append(s);
           sb.append(" ");
       }
       String res = sb.toString();
       // Remove the trailing whitespace
       res = res.substring(0, res.length() - 1);
       return res;
   }
}
