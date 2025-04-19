package it.codingjam.virtualthreadspoc.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class Futures {
    public static <I1, I2, O> O join(Future<I1> f1, Future<I2> f2, long timeOutSeconds, BiFunction<I1, I2, O> fn) throws AsyncException {
        try {
            return fn.apply(f1.get(timeOutSeconds, TimeUnit.SECONDS), f2.get(timeOutSeconds, TimeUnit.SECONDS));
        } catch (Exception e) {
            f1.cancel(true);
            f2.cancel(true);
            throw new AsyncException(e);
        }
    }

    public static <I1, I2, O> O join(Future<I1> f1, Future<I2> f2, BiFunction<I1, I2, O> fn) throws AsyncException {
        return join(f1, f2, 1, fn);
    }

    public static class AsyncException extends RuntimeException {

        public AsyncException(Throwable cause) {
            super(cause);
        }
    }
}
