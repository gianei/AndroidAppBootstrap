/*
 * Copyright (c) 2017 Gianei Leandro Sebastiany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.glsebastiany.appbootstrap.domain.repository.firebase


import android.content.Context
import com.androidhuman.rxfirebase2.database.ChildEvent
import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase
import com.glsebastiany.appbootstrap.domain.repository.SimpleDataRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import javax.inject.Inject

class SimpleDataFirebaseRepository @Inject
constructor(private val context: Context) : SimpleDataRepository {

    init {
        FirebaseApp.initializeApp(context)
    }

    override fun listen(): Observable<ChildEvent> {
        return RxFirebaseDatabase
                .childEvents(FirebaseDatabase.getInstance().getReference("simpleData"))
//                .delay(1, TimeUnit.SECONDS)
    }
}
