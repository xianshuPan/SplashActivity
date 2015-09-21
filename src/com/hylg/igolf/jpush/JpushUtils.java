package com.hylg.igolf.jpush;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JpushUtils {
    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
