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

package nucleus5.presenter;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import mocks.BundleMock;
import nucleus5.presenter.Factory;
import nucleus5.presenter.RxPresenter;
import nucleus5.view.OptionalView;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RxPresenterTest {

    @Test
    public void testAdd()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Disposable mock = Mockito.mock(Disposable.class);
        when(mock.isDisposed()).thenReturn(false);
        presenter.add(mock);
        presenter.onDestroy();
        verify(mock, times(1)).dispose();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testAddRemove()  throws Exception {
        RxPresenter presenter = new RxPresenter();
        Disposable mock = Mockito.mock(Disposable.class);
        when(mock.isDisposed()).thenReturn(false);
        presenter.add(mock);
        presenter.remove(mock);
        verify(mock, times(1)).dispose();
        presenter.onDestroy();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).create();
        verifyNoMoreInteractions(restartable);

        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.create(bundle);
        presenter.restartable(1, restartable);

        verify(restartable, times(2)).create();
        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testStopRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).create();
        verifyNoMoreInteractions(restartable);

        presenter.stop(1);

        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);

        verify(restartable, times(1)).create();
        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testCompletedRestartable() throws Exception {
        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);

        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(true);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);
    }

    @Test
    public void testCompletedRestartableDoesNoRestart() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(false);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);

        presenter.start(1);

        verify(restartable, times(1)).create();
        verifyNoMoreInteractions(restartable);

        when(disposable.isDisposed()).thenReturn(true);
        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);

        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);

        verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testRestartableIsDisposed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(false);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
    }

    @Test
    public void testStartedRestartableIsNotDisposed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(false);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
        presenter.start(1);
        assertFalse(presenter.isDisposed(1));
    }

    @Test
    public void testCompletedRestartableIsDisposed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);

        Factory<Disposable> restartable = mock(Factory.class);
        Disposable disposable = mock(Disposable.class);
        when(restartable.create()).thenReturn(disposable);
        when(disposable.isDisposed()).thenReturn(true);

        presenter.restartable(1, restartable);
        assertTrue(presenter.isDisposed(1));
        presenter.start(1);
        assertTrue(presenter.isDisposed(1));
    }
    
    @Test
    public void testViewObservable() {
        RxPresenter<Integer> presenter = new RxPresenter<>();
        presenter.onCreate(null);

        TestObserver<OptionalView<Integer>> testObserver = new TestObserver<>();
        presenter.view().subscribe(testObserver);
        testObserver.assertValueCount(0);

        List<OptionalView<Integer>> values = new ArrayList<>();

        presenter.onTakeView(1);
        values.add(new OptionalView<>(1));
        assertValues(values, testObserver);

        presenter.onDropView();
        values.add(new OptionalView<Integer>(null));
        assertValues(values, testObserver);

        presenter.onTakeView(2);
        values.add(new OptionalView<>(2));
        assertValues(values, testObserver);

        presenter.onDestroy();
        assertValues(values, testObserver);
        testObserver.assertComplete();
    }

    private void assertValues(List<OptionalView<Integer>> values, TestObserver<OptionalView<Integer>> observer) {
        observer.assertValues(values.toArray(new OptionalView[values.size()]));
    }
}
