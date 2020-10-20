package com.bcit.comp7082.group1.aspects;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Aspect
public class FindPhotosLogging {
    private static final String POINTCUT_METHOD = "execution(@com.bcit.comp7082.group1.aspects.FindPhotosLoggingBehaviour * * (..))";

    @Pointcut(POINTCUT_METHOD)
    public void onFindPhotos() {

    }

    @Around("onFindPhotos()")
    public Object onFindPhotosAround(ProceedingJoinPoint point) throws Throwable {
        Log.d("PhotoAdvice: ", "starting advice");

        Object[] args = point.getArgs();
        Date startTimestamp = (Date) args[0];
        Date endTimestamp = (Date) args[1];
        String keywords = (String) args[2];
        double[] latRange = (double[]) args[3];
        double[] lonRange = (double[]) args[4];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        builder.append("Start time: ");
        builder.append(dateFormat.format(startTimestamp));
        builder.append("/nEnd time: ");
        builder.append(dateFormat.format(endTimestamp));
        builder.append("/nKeywords: ");
        builder.append(keywords);
        Log.d("Photo search terms: ", builder.toString());
        return point.proceed(args);
    }
}

