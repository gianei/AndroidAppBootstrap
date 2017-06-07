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

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Notification;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import nucleus5.presenter.delivery.DeliverLatestCache;
import nucleus5.presenter.delivery.Delivery;
import nucleus5.view.OptionalView;

import static org.junit.Assert.assertFalse;

public class DeliverCacheTest {

    @Test
    public void testCache() throws Exception {
        PublishSubject<OptionalView<Object>> view = PublishSubject.create();
        final TestObserver<Delivery<Object, Integer>> testObserver = new TestObserver<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverLatestCache<Object, Integer> restartable = new DeliverLatestCache<>(view);
        LambdaObserver<Delivery<Object, Integer>> observer = new LambdaObserver<>(new Consumer<Delivery<Object, Integer>>() {
            @Override
            public void accept(Delivery<Object, Integer> delivery) throws Exception {
                delivery.split(
                    new BiConsumer<Object, Integer>() {
                        @Override
                        public void accept(Object o, Integer integer) throws Exception {
                            testObserver.onNext(new Delivery<>(o, Notification.createOnNext(integer)));
                        }
                    },
                    new BiConsumer<Object, Throwable>() {
                        @Override
                        public void accept(Object o, Throwable throwable) throws Exception {
                            testObserver.onNext(new Delivery<>(o, Notification.<Integer>createOnError(throwable)));
                        }
                    }
                );
            }
        }, Functions.<Throwable>emptyConsumer(), Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        restartable.apply(subject)
            .subscribe(observer);

        // only latest value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        view.onNext(new OptionalView<Object>(100));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(3)));

        testObserver.assertValueCount(1);
        testObserver.assertNotComplete();

        // no values delivered if a view has been detached
        view.onNext(new OptionalView<>(null));

        testObserver.assertValueCount(1);
        testObserver.assertNotComplete();

        // the latest value will be delivered to the new view
        view.onNext(new OptionalView<Object>(101));
        deliveries.add(new Delivery<Object, Integer>(101, Notification.createOnNext(3)));

        testObserver.assertValueCount(2);
        testObserver.assertNotComplete();

        // a throwable will be delivered as well
        Throwable throwable = new Throwable();
        subject.onError(throwable);
        deliveries.add(new Delivery<Object, Integer>(101, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(3);
        testObserver.assertNotComplete();

        // the throwable will be delivered after a new view is attached
        view.onNext(new OptionalView<Object>(102));
        deliveries.add(new Delivery<Object, Integer>(102, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(4);
        testObserver.assertNotComplete();

        // final checks
        testObserver.assertValueSequence(deliveries);

        observer.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }
}
