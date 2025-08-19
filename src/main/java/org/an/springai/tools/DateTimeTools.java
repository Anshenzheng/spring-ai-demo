package org.an.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public  class DateTimeTools {
    @Tool(description = "获取当前时间")
    String getCurrentDateTime(){
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "获取用户的生日，入参是用户名，出参是出生年月日，如1995/05/06")
    String getBirthdayDate(String userName){
        if(userName.equalsIgnoreCase("Jim")){
            return "1992/11/21";
        } else if (userName.equalsIgnoreCase("John")) {
            return "1995/11/10";
        }else {
            return "not know";
        }
    }




}
