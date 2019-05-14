package com.sudoku.checknet;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sudoku.checknet.annotation.CheckNet;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class SectionAspect {

    private static final String POINTCUT_METHOD =
            "execution(@com.sudoku.checknet.annotation.CheckNet * *(..))";
    /**
     * 切点
     */
    @Pointcut(POINTCUT_METHOD)
    public void checkNetPoint() {}

    /**
     * 处理切面
     */
    @Around("checkNetPoint()")
    public Object checkNetAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e("SectionAspect", "checkNetAround");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法
        Method method = signature.getMethod();
        //是否使用了该注解
        boolean annotationPresent = method.isAnnotationPresent(CheckNet.class);
        if(annotationPresent){
            //获取注解信息
            CheckNet checkNet = method.getAnnotation(CheckNet.class);
            if (checkNet == null) {
                return joinPoint.proceed();
            }
            //获取当前切点所在的类信息
            Object object = joinPoint.getThis();
            //获取上下文信息
            Context context = getContext(object);
            if (context == null) {
                return joinPoint.proceed();
            }
            //是否有连接到了网络
            if (isNetworkAvailable(context)) {
                return joinPoint.proceed();
            }
            //没有网络连接
            Toast.makeText(context, "请检查您的网络", Toast.LENGTH_LONG).show();
            return null;
        }
        return joinPoint.proceed();
    }

    /**
     * 通过对象获取上下文
     */
    private Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof View) {
            View view = (View) object;
            return view.getContext();
        }
        return null;
    }

    /**
     * 检查当前网络是否可用
     */
    private static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (NetworkInfo aNetworkInfo : networkInfo) {
                    // 判断当前网络状态是否为连接状态
                    if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
