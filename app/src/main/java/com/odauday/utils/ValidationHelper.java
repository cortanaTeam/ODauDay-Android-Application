package com.odauday.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by infamouSs on 3/24/18.
 */

public class ValidationHelper {
    
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+([.][_A-Za-z0-9-]+)*@[A-Za-z0-9]+([.][A-Za-z0-9]+)*([.][A-Za-z]{2,})$";
    public static final String PHONE_PATTERN = "[0-9+-]+";
    
    
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }
    
    public static boolean isNull(Object obj) {
        return obj == null;
    }
    
    public static boolean isEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        
        return matcher.matches();
    }
    
    public static boolean isEqual(String str1, String str2) {
        return str1.equals(str2);
    }
    
    public static boolean isPhoneNumber(String phone) {
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
    
        return matcher.matches();
    }
    public static <T> boolean isEmptyList(List<T> list){
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }
}
