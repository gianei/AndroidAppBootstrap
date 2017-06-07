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

package nucleus5.view;

import android.os.Bundle;
import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import mocks.BundleMock;
import mocks.ParcelMock;
import nucleus5.factory.PresenterFactory;
import nucleus5.factory.PresenterStorage;
import nucleus5.presenter.Presenter;
import nucleus5.view.ParcelFn;
import nucleus5.view.PresenterLifecycleDelegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PresenterLifecycleDelegate.class, PresenterStorage.class, ParcelFn.class, Parcel.class})
public class PresenterLifecycleDelegateTest {

    PresenterStorage storage;
    PresenterFactory<Presenter> factory;
    ArrayList<Presenter> presenters = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(Bundle.class).withNoArguments().thenAnswer(new Answer<Bundle>() {
            @Override
            public Bundle answer(InvocationOnMock invocation) throws Throwable {
                return BundleMock.mock();
            }
        });

        storage = mockStorage();

        factory = mock(PresenterFactory.class);
        when(factory.createPresenter()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mockPresenter();
            }
        });

        mockStatic(Parcel.class);
        Parcel parcel = ParcelMock.mock();
        PowerMockito.when(Parcel.obtain()).thenReturn(parcel);
    }

    Presenter mockPresenter() {
        Presenter presenter = mock(Presenter.class);

        final ArrayList<Presenter.OnDestroyListener> onDestroyListeners = new ArrayList<>();

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                onDestroyListeners.add((Presenter.OnDestroyListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(presenter).addOnDestroyListener(any(Presenter.OnDestroyListener.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                for (Presenter.OnDestroyListener listener : onDestroyListeners)
                    listener.onDestroy();
                return null;
            }
        }).when(presenter).destroy();
        return presenter;
    }

    // PresenterStorage should be prepared with @PrepareForTest
    public PresenterStorage mockStorage() {
        PresenterStorage storage = mock(PresenterStorage.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                presenters.add((Presenter) invocation.getArguments()[0]);
                return null;
            }
        }).when(storage).add(any(Presenter.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return "" + presenters.indexOf(invocation.getArguments()[0]);
            }
        }).when(storage).getId(any(Presenter.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return presenters.get(Integer.parseInt((String) invocation.getArguments()[0]));
            }
        }).when(storage).getPresenter(anyString());
        Whitebox.setInternalState(PresenterStorage.class, "INSTANCE", storage);
        return storage;
    }

    @Test
    public void testWithNullFactory() throws Exception {
        PresenterLifecycleDelegate<?> delegate = new PresenterLifecycleDelegate<>(null);
        delegate.onRestoreInstanceState(BundleMock.mock());
        assertNull(delegate.getPresenterFactory());
        assertNull(delegate.getPresenter());
        assertNotNull(delegate.onSaveInstanceState());
        delegate.setPresenterFactory(null);
        delegate.onResume(1);
        delegate.onDropView();
        delegate.onDestroy(false);
        delegate.onDestroy(true);
    }

    @Test
    public void twoWaysOfFactoryInjection() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        Presenter presenter = delegate.getPresenter();
        assertEquals(presenters.get(0), presenter);

        delegate = new PresenterLifecycleDelegate<>(null);
        delegate.setPresenterFactory(factory);
        presenter = delegate.getPresenter();
        assertEquals(presenters.get(1), presenter);
    }

    @Test
    public void saveRestore() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onResume(1);
        Bundle bundle = delegate.onSaveInstanceState();

        delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onRestoreInstanceState(bundle);
        assertEquals(presenters.get(0), delegate.getPresenter());

        delegate = new PresenterLifecycleDelegate<>(factory);
        assertNotEquals(presenters.get(0), delegate.getPresenter());
    }

    @Test
    public void saveNoResume() throws Exception {
        PresenterLifecycleDelegate<Presenter> delegate = new PresenterLifecycleDelegate<>(factory);
        Bundle bundle = delegate.onSaveInstanceState();

        delegate = new PresenterLifecycleDelegate<>(factory);
        delegate.onRestoreInstanceState(bundle);
        assertEquals(presenters.get(0), delegate.getPresenter());

        delegate = new PresenterLifecycleDelegate<>(factory);
        assertNotEquals(presenters.get(0), delegate.getPresenter());
    }
}
