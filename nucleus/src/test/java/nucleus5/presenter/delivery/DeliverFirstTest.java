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
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import nucleus5.presenter.delivery.DeliverFirst;
import nucleus5.presenter.delivery.Delivery;
import nucleus5.view.OptionalView;

import static org.junit.Assert.assertFalse;

public class DeliverFirstTest {

    @Test
    public void testOnce() throws Exception {
        PublishSubject<OptionalView<Object>> view = PublishSubject.create();
        final TestObserver<Delivery<Object, Integer>> testObserver = new TestObserver<>();
        ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverFirst<Object, Integer> restartable = new DeliverFirst<>(view);
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
        }, Functions.ERROR_CONSUMER, Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        restartable.apply(subject)
            .subscribe(observer);

        // only first value is delivered
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        view.onNext(new OptionalView<Object>(100));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.createOnNext(1)));

        testObserver.assertValueCount(1);

        // no values delivered if a view has been detached
        view.onNext(null);

        testObserver.assertValueCount(1);

        // the latest value will not be delivered to the new view
        view.onNext(new OptionalView<Object>(101));

        testObserver.assertValueCount(1);

        // successive values will be ignored
        subject.onNext(4);

        testObserver.assertValueCount(1);

        // final checks
        testObserver.assertValueSequence(deliveries);

        observer.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    @Test
    public void testOnceThrowable() throws Exception {
        PublishSubject<OptionalView<Object>> view = PublishSubject.create();
        final TestObserver<Delivery<Object, Integer>> testObserver = new TestObserver<>();
        final ArrayList<Delivery<Object, Integer>> deliveries = new ArrayList<>();

        final PublishSubject<Integer> subject = PublishSubject.create();
        DeliverFirst<Object, Integer> restartable = new DeliverFirst<>(view);
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
        }, Functions.ERROR_CONSUMER, Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        restartable.apply(subject)
            .subscribe(observer);

        // only first value is delivered
        Throwable throwable = new Throwable();
        subject.onError(throwable);

        testObserver.assertNotComplete();
        testObserver.assertNoValues();

        view.onNext(new OptionalView<Object>(100));
        deliveries.add(new Delivery<Object, Integer>(100, Notification.<Integer>createOnError(throwable)));

        testObserver.assertValueCount(1);

        // final checks
        testObserver.assertValueSequence(deliveries);

        observer.dispose();
        assertFalse(subject.hasObservers());
        assertFalse(view.hasObservers());
    }

    //  https://github.com/ReactiveX/RxJava/issues/3182
    @Test(expected = Exception.class)
    public void testThrowDuringOnNext() throws Exception {

//        Observable
//            .just(1)
//            .filter(new Func1<Integer, Boolean>() {
//                @Override
//                public Boolean call(Integer integer) {
//                    return true;
//                }
//            })
//            .first()
//            .subscribe(new Action1<Integer>() {
//                @Override
//                public void call(Integer integer) {
//                    throw new RuntimeException();
//                }
//            });

        PublishSubject<OptionalView<Object>> view = PublishSubject.create();

        final PublishSubject<Integer> subject = PublishSubject.create();
        new DeliverFirst<Object, Integer>(view)
            .apply(subject)
            .subscribe(new Observer<Delivery<Object, Integer>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Delivery<Object, Integer> delivery) {
                    try {
                        delivery.split(
                            new BiConsumer<Object, Integer>() {
                                @Override
                                public void accept(Object o, Integer integer) throws Exception {
                                    throw new RuntimeException();
                                }
                            },
                            null
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        subject.onNext(3);
        view.onNext(new OptionalView<Object>(100));
    }
}
