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
import android.support.annotation.Nullable;

import org.junit.Test;

import java.util.ArrayList;

import nucleus5.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PresenterTest {

    static class TestPresenter extends Presenter<Object> {

        ArrayList<Bundle> onCreate = new ArrayList<>();
        ArrayList<Bundle> onSave = new ArrayList<>();
        ArrayList<Object> onTakeView = new ArrayList<>();
        int onDestroy, onDropView;

        @Override
        protected void onCreate(@Nullable Bundle savedState) {
            onCreate.add(savedState);
        }

        @Override
        protected void onDestroy() {
            onDestroy++;
        }

        @Override
        protected void onSave(Bundle state) {
            onSave.add(state);
        }

        @Override
        protected void onTakeView(Object o) {
            onTakeView.add(o);
        }

        @Override
        protected void onDropView() {
            onDropView++;
        }
    }

    @Test
    public void testLifecycle() throws Exception {
        TestPresenter presenter = new TestPresenter();
        Bundle bundle = mock(Bundle.class);

        presenter.create(bundle);
        assertEquals(bundle, presenter.onCreate.get(0));

        presenter.save(bundle);
        assertEquals(bundle, presenter.onSave.get(0));

        Object view = 1;
        presenter.takeView(view);
        assertEquals(view, presenter.onTakeView.get(0));

        presenter.dropView();
        assertEquals(1, presenter.onDropView);

        presenter.destroy();
        assertEquals(1, presenter.onDestroy);


        assertEquals(1, presenter.onCreate.size());
        assertEquals(1, presenter.onSave.size());
        assertEquals(1, presenter.onTakeView.size());
        assertEquals(1, presenter.onDropView);
        assertEquals(1, presenter.onDestroy);
    }

    @Test
    public void testOnDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.destroy();
        verify(listener, times(1)).onDestroy();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnNoDestroy() throws Exception {
        Presenter.OnDestroyListener listener = mock(Presenter.OnDestroyListener.class);
        Presenter presenter = new Presenter();
        presenter.create(null);
        presenter.addOnDestroyListener(listener);
        presenter.removeOnDestroyListener(listener);
        presenter.destroy();
        verifyNoMoreInteractions(listener);
    }
}