package lab8;

import java.util.regex.Pattern;

public class Parsers {

  public static boolean parseInt(String str) {
    try {
      Integer.parseInt(str);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  public static boolean parseIsDigit(String str) {
    if (str.length() == 0) {
      return false;
    }
    for (char ch : str.toCharArray()) {
      if (!Character.isDigit(ch)) {
        return false;
      }
    }
    return true;
  }

  private static final Pattern PATTERN = Pattern.compile("^\\d+$");

  public static boolean parseRegex(String str){
    return PATTERN.matcher(str).matches();
  }

  public static boolean parseAnotherRegex(String str) {
    return str.matches("^\\d+$");
  }
}