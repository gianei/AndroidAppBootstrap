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

package nucleus5.factory;

import android.os.Bundle;

import org.junit.Test;

import nucleus5.factory.PresenterFactory;
import nucleus5.factory.ReflectionPresenterFactory;
import nucleus5.factory.RequiresPresenter;
import nucleus5.presenter.Presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

public class ReflectionPresenterFactoryTest {


    public static class ViewNoPresenter {

    }


    public static class TestPresenter extends Presenter {

        public int value;

        @Override
        public void onCreate(Bundle savedState) {
            super.onCreate(savedState);
            if (savedState != null)
                value = savedState.getInt("1");
        }

        @Override
        public void onSave(Bundle state) {
            super.onSave(state);
            state.putInt("1", 1);
        }
    }

    @RequiresPresenter(TestPresenter.class)
    public static class ViewWithPresenter extends Presenter {

    }

    @Test
    public void testNoPresenter() throws Exception {
        assertNull(ReflectionPresenterFactory.fromViewClass(ViewNoPresenter.class));
    }

    @Test
    public void testProvidePresenter() throws Exception {
        assertNotNull(new ReflectionPresenterFactory<>(TestPresenter.class).createPresenter());

        PresenterFactory<Presenter> factory = ReflectionPresenterFactory.fromViewClass(ViewWithPresenter.class);
        assertNotNull(factory);
        assertTrue(factory.createPresenter() instanceof TestPresenter);
    }
}
