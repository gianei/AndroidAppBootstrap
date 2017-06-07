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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import mocks.BundleMock;
import nucleus5.factory.ReflectionPresenterFactory;
import nucleus5.factory.RequiresPresenter;
import nucleus5.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NucleusFragmentTest.TestView.class, PresenterLifecycleDelegate.class, ReflectionPresenterFactory.class})
public class NucleusFragmentTest {

    public static final Class<?> BASE_VIEW_CLASS = Fragment.class;
    public static final Class<TestView> VIEW_CLASS = TestView.class;

    public static class TestPresenter extends Presenter {
    }

    @RequiresPresenter(TestPresenter.class)
    public static class TestView extends NucleusFragment {
    }

    public void setUpIsFinishing(boolean b) {
        Activity activity = mock(Activity.class);
        when(activity.isFinishing()).thenReturn(b);
        when(activity.isChangingConfigurations()).thenReturn(!b);
        stub(method(BASE_VIEW_CLASS, "getActivity")).toReturn(activity);
    }

    private TestPresenter mockPresenter;
    private PresenterLifecycleDelegate mockDelegate;
    private ReflectionPresenterFactory mockFactory;
    private TestView tested;

    private void setUpPresenter() throws Exception {
        mockPresenter = mock(TestPresenter.class);

        mockDelegate = mock(PresenterLifecycleDelegate.class);
        PowerMockito.whenNew(PresenterLifecycleDelegate.class).withAnyArguments().thenReturn(mockDelegate);
        when(mockDelegate.getPresenter()).thenReturn(mockPresenter);

        mockFactory = mock(ReflectionPresenterFactory.class);
        when(mockFactory.createPresenter()).thenReturn(mockPresenter);

        PowerMockito.mockStatic(ReflectionPresenterFactory.class);
        when(ReflectionPresenterFactory.fromViewClass(any(Class.class))).thenReturn(mockFactory);
    }

    @Before
    public void setUp() throws Exception {
        setUpPresenter();

        tested = spy(VIEW_CLASS);
        suppress(method(BASE_VIEW_CLASS, "onCreate", Bundle.class));
        suppress(method(BASE_VIEW_CLASS, "onSaveInstanceState", Bundle.class));
        suppress(method(BASE_VIEW_CLASS, "onResume"));
        suppress(method(BASE_VIEW_CLASS, "onPause"));
        suppress(method(BASE_VIEW_CLASS, "onDestroyView"));
        suppress(method(BASE_VIEW_CLASS, "onDestroy"));

        setUpIsFinishing(false);
    }

    @Test
    public void testCreation() throws Exception {
        tested.onCreate(null);
        assertEquals(mockPresenter, tested.getPresenter());
        PowerMockito.verifyStatic(times(1));
        ReflectionPresenterFactory.fromViewClass(argThat(new ArgumentMatcher<Class<?>>() {
            @Override
            public boolean matches(Object argument) {
                return TestView.class.isAssignableFrom((Class) argument);
            }
        }));
        verify(mockDelegate, times(1)).getPresenter();
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testLifecycle() throws Exception {
        tested.onCreate(null);
        tested.onResume();
        verify(mockDelegate, times(1)).onResume(tested);
        tested.onPause();
        tested.onDestroyView();
        verify(mockDelegate, times(1)).onDropView();
        tested.onSaveInstanceState(BundleMock.mock());
        verify(mockDelegate, times(1)).onSaveInstanceState();
        tested.onDestroy();
        verify(mockDelegate, times(1)).onDestroy(false);
        verifyNoMoreInteractions(mockPresenter, mockDelegate, mockFactory);
    }

    @Test
    public void testSaveRestore() throws Exception {
        Bundle presenterBundle = BundleMock.mock();
        when(mockDelegate.onSaveInstanceState()).thenReturn(presenterBundle);

        tested.onCreate(null);

        Bundle state = BundleMock.mock();
        tested.onSaveInstanceState(state);

        tested = spy(TestView.class);
        tested.onCreate(state);
        verify(mockDelegate).onRestoreInstanceState(presenterBundle);
    }

    @Test
    public void testDestroy() throws Exception {
        tested.onCreate(null);
        setUpIsFinishing(true);
        tested.onPause();
        tested.onDestroy();
        verify(mockDelegate, times(1)).onDestroy(true);
    }
}
