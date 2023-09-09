package com.example.blog_system.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTools {
   public static String removeMarkdownKeywords(String markdownText) {

      String[] regex = {"#"};
      String text = markdownText;
      for(String i:regex)
      {
         text = text.replaceAll(i,"");
      }
      return text;
   }
}
