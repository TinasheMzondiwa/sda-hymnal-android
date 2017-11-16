/*
 * Copyright (c) 2017. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tinashe.sdah.util

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.Observable


/**
 * Created by tinashe on 2017/11/15.
 */
object RxFirebase {

    enum class EventType {
        CHILD_ADDED, CHILD_CHANGED, CHILD_REMOVED, CHILD_MOVED
    }

    class FirebaseChildEvent constructor(var snapshot: DataSnapshot, var eventType: EventType, var prevName: String)

    fun observeChildren(ref: Query): Observable<FirebaseChildEvent> {

        return Observable.create {

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevName: String) {
                    it.onNext(FirebaseChildEvent(dataSnapshot, EventType.CHILD_ADDED, prevName))
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, prevName: String) {
                    it.onNext(FirebaseChildEvent(dataSnapshot, EventType.CHILD_CHANGED, prevName))
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    it.onNext(FirebaseChildEvent(dataSnapshot, EventType.CHILD_REMOVED, ""))
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, prevName: String) {
                    it.onNext(FirebaseChildEvent(dataSnapshot, EventType.CHILD_MOVED, prevName))
                }

                override fun onCancelled(error: DatabaseError) {
                    // Notify Subscriber
                    it.onError(error.toException())
                    ref.removeEventListener(this)
                }
            })

        }
    }


    fun observe(ref: Query): Observable<DataSnapshot> {

        return Observable.create {

            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    it.onNext(dataSnapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Notify Subscriber
                    it.onError(error.toException())
                    ref.removeEventListener(this)
                }
            })
        }
    }

    fun observe(dbRef: DatabaseReference): Observable<DataSnapshot> {

        return Observable.create {

            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    it.onNext(dataSnapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Notify Subscriber
                    it.onError(error.toException())
                    dbRef.removeEventListener(this)
                }
            })
        }
    }

    fun observeSingle(ref: Query): Observable<DataSnapshot> {
        return Observable.create {
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                    ref.removeEventListener(this)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    it.onNext(dataSnapshot)
                }
            })
        }
    }

    fun observePush(dbRef: DatabaseReference, value: Any): Observable<Task<Void>> {
        return Observable.create { e ->
            dbRef.push().setValue(value)
                    .addOnCompleteListener { e.onNext(it) }
                    .addOnFailureListener { e.onError(it) }
        }
    }

    fun observeUpdate(dbRef: DatabaseReference, value: Any): Observable<Task<Void>> {
        return Observable.create { e ->
            dbRef.setValue(value)
                    .addOnCompleteListener { e.onNext(it) }
                    .addOnFailureListener { e.onError(it) }
        }
    }

}