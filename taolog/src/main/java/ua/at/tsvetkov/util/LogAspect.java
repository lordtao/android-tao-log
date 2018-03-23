package ua.at.tsvetkov.util;

import android.os.Build;
import android.os.Trace;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.TimeUnit;

@Aspect
public class LogAspect {

    @Pointcut("within(@ua.at.tsvetkov.annotations.ToLog *)")
    public void withinAnnotatedClass() {}

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {}

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {}

    @Pointcut("execution(@ua.at.tsvetkov.annotations.ToLog * *(..)) || methodInsideAnnotatedType()")
    public void method() {}

    @Pointcut("execution(@ua.at.tsvetkov.annotations.ToLog *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {}

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        enterMethod(joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        exitMethod(joinPoint, result, lengthMillis);

        return result;
    }

    private static void enterMethod(JoinPoint joinPoint) {
        if (ua.at.tsvetkov.util.Log.isDisabled()) return;

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder("\u21E2 ");

        checkForClass(cls.getSimpleName(), methodName, builder);

        builder.append(" (");
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
//            if(methodName.contains("<init>")){
//
//            } else {
            builder.append(parameterNames[i]).append('=');
            builder.append(ObjectFormatter.toString(parameterValues[i]));
//            }
        }
        builder.append(')');

        StringBuilder sb = new StringBuilder();
        Format.fillTag(cls.getName(), Thread.currentThread().getStackTrace(), sb);

        android.util.Log.v(sb.toString(), Format.getFormattedMessage(builder.toString()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            Trace.beginSection(section);
        }
    }

    private static void exitMethod(JoinPoint joinPoint, Object result, long lengthMillis) {
        if (ua.at.tsvetkov.util.Log.isDisabled()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }

        Signature signature = joinPoint.getSignature();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        StringBuilder builder = new StringBuilder("\u21E0 ");

        checkForClass(cls.getSimpleName(), methodName, builder);

        builder.append(" [")
                .append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(ObjectFormatter.toString(result));
        }

        StringBuilder sb = new StringBuilder();
        Format.fillTag(cls.getName(), Thread.currentThread().getStackTrace(), sb);

        android.util.Log.v(sb.toString(), Format.getFormattedMessage(builder.toString()));
    }

    private static void checkForClass(String className, String methodName, StringBuilder builder) {
        if (methodName.contains("<init>")) {
            builder.append("<init ")
                    .append(className)
                    .append('>');
        } else {
            builder.append(methodName);
        }
    }

}
