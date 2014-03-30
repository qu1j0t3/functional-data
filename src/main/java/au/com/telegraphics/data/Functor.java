package main.java.au.com.telegraphics.data;

import com.google.common.base.Function;

public interface Functor<T> {
   public <U> Option<U> map(Function<T,U> f);
}
