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

import io.reactivex.Notification;
import io.reactivex.functions.BiConsumer;
import nucleus5.presenter.delivery.Delivery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DeliveryTest {

    private void testWithOnNextOnError(BiConsumer<BiConsumer, BiConsumer> test) throws Exception {
        BiConsumer onNext = mock(BiConsumer.class);
        BiConsumer onError = mock(BiConsumer.class);

        test.accept(onNext, onError);

        verifyNoMoreInteractions(onNext);
        verifyNoMoreInteractions(onError);
    }

    @Test
    public void testSplitOnNext() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                new Delivery(1, Notification.createOnNext(2)).split(onNext, onError);
                verify(onNext, times(1)).accept(1, 2);
            }
        });
    }

    @Test
    public void testSplitOnError() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                Throwable throwable = new Throwable();
                new Delivery(1, Notification.createOnError(throwable)).split(onNext, onError);
                verify(onError, times(1)).accept(1, throwable);
            }
        });
    }

    @Test
    public void testSplitOnComplete() throws Exception {
        testWithOnNextOnError(new BiConsumer<BiConsumer, BiConsumer>() {
            @Override
            public void accept(BiConsumer onNext, BiConsumer onError) throws Exception {
                new Delivery(1, Notification.createOnComplete()).split(onNext, onError);
            }
        });
    }
}
