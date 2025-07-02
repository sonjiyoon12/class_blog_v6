package com.tenco.blog.utils;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 날짜/시간 관련 유틸리티 클래스
 * static 메서드로 구성해서 객체 생성없이 바로 사용 가능하게 설계
 */
public class MyDateUtil {

    public static String timestampFormat(Timestamp time) {
        // Board 엔티티에 선언된 Timestamp를 Date 객체로 변환
        // getTime() 메서드를 호출해서 밀리초 단위로 시간을 받아 --> Date 객체 생성
        Date currnetDate = new Date(time.getTime());
        // 아파치 Commons 라이브러리 DateFormatUtils 클래스를 활용
        return DateFormatUtils.format(currnetDate, "yyyy-MM-dd HH:mm");
    }

}