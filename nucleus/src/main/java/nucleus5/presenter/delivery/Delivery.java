/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Konstantin Mikheev sirstripy-at-gmail-com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nucleus5.presenter.delivery;

import android.support.annotation.Nullable;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import nucleus5.view.OptionalView;

/**
 * A class that represents a couple of View and Data.
 *
 * @param <View>
 * @param <T>
 */
public final class Delivery<View, T> {

    private final View view;
    private final Notification<T> notification;

    public Delivery(View view, Notification<T> notification) {
        this.view = view;
        this.notification = notification;
    }

    public static boolean isValid(OptionalView<?> view, Notification<?> notification) {
        return view.view != null &&
            (notification.isOnNext() || notification.isOnError());
    }

    public static <View, T> Observable<Delivery<View, T>> validObservable(OptionalView<View> view, Notification<T> notification) {
        return isValid(view, notification) ?
            Observable.just(new Delivery<>(view.view, notification)) :
            Observable.<Delivery<View, T>>empty();
    }

    public void split(BiConsumer<View, T> onNext, @Nullable BiConsumer<View, Throwable> onError) throws Exception {
        if (notification.isOnNext())
            onNext.accept(view, notification.getValue());
        else if (onError != null && notification.isOnError())
            onError.accept(view, notification.getError());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delivery<?, ?> delivery = (Delivery<?, ?>)o;

        if (view != null ? !view.equals(delivery.view) : delivery.view != null) return false;
        return !(notification != null ? !notification.equals(delivery.notification) : delivery.notification != null);
    }

    @Override
    public int hashCode() {
        int result = view != null ? view.hashCode() : 0;
        result = 31 * result + (notification != null ? notification.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Delivery{" +
            "view=" + view +
            ", notification=" + notification +
            '}';
    }
}
