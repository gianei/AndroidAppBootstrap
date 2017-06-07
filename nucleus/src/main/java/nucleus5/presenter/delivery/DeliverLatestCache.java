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

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import nucleus5.view.OptionalView;

public class DeliverLatestCache<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<OptionalView<View>> view;

    public DeliverLatestCache(Observable<OptionalView<View>> view) {
        this.view = view;
    }

    @Override
    public ObservableSource<Delivery<View, T>> apply(Observable<T> observable) {
        return Observable
            .combineLatest(
                view,
                observable
                    .materialize()
                    .filter(new Predicate<Notification<T>>() {
                        @Override
                        public boolean test(Notification<T> notification) throws Exception {
                            return !notification.isOnComplete();
                        }
                    }),
                new BiFunction<OptionalView<View>, Notification<T>, Object[]>() {
                    @Override
                    public Object[] apply(OptionalView<View> view, Notification<T> notification) throws Exception {
                        return new Object[]{view, notification};
                    }
                })
            .concatMap(new Function<Object[], ObservableSource<Delivery<View, T>>>() {
                @Override
                public ObservableSource<Delivery<View, T>> apply(Object[] pack) throws Exception {
                    return Delivery.validObservable((OptionalView<View>) pack[0], (Notification<T>) pack[1]);
                }
            });
    }
}
