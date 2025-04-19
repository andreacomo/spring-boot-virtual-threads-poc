package it.codingjam.virtualthreadspoc.utils;

import java.util.concurrent.Future;
import java.util.function.BiFunction;

public class Futures {
    public static <I1, I2, O> O join(Future<I1> f1, Future<I2> f2, BiFunction<I1, I2, O> fn) throws AsyncException {
        try {
            return fn.apply(f1.get(), f2.get());
        } catch (Exception e) {
            if (f1.state() == Future.State.FAILED) {
                f2.cancel(true);
                throw new AsyncException(f1.exceptionNow());
            } else if (f2.state() == Future.State.FAILED) {
                f1.cancel(true);
                throw new AsyncException(f2.exceptionNow());
            } else {
                throw new AsyncException(e);
            }
        }
    }

    public static class AsyncException extends RuntimeException {

        public AsyncException(Throwable cause) {
            super(cause);
        }
    }
}
