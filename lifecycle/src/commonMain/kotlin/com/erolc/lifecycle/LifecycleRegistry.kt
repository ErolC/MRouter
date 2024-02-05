package com.erolc.lifecycle

import androidx.annotation.VisibleForTesting

class LifecycleRegistry(private val lifecycleOwner: LifecycleOwner) : Lifecycle() {

    private val observerMap = mutableMapOf<LifecycleObserver, ObserverWithState>()
    private var handlingEvent = false
    private var addingObserverCounter = 0
    private var newEventOccurred = false
    private var parentStates = ArrayList<State>()

    /**
     * Current state
     */
    private var state: State = State.INITIALIZED


    override val currentState: State
        get() = state

    val observerCount: Int
        get() {
            return observerMap.size
        }

    fun handleLifecycleEvent(event: Event) {
        moveToState(event.targetState)
    }

    private fun moveToState(next: State) {
        if (state == next) {
            return
        }
        check(!(state == State.INITIALIZED && next == State.DESTROYED)) {
            "no event down from $state in component $lifecycleOwner"
        }
        state = next
        //判断是否正在同步或者是否正在添加观察者
        if (handlingEvent || addingObserverCounter != 0) {
            newEventOccurred = true
            // we will figure out what to do on upper level.
            return
        }
        handlingEvent = true
        sync()
        handlingEvent = false
        if (state == State.DESTROYED) {
            observerMap.clear()
        }
    }

    private fun <K, V> MutableMap<K, V>.putIfAbsent(key: K, value: V): V? {
        val entry = get(key)
        if (entry != null) {
            return entry
        }
        this[key] = value
        return null
    }

    override fun addObserver(observer: LifecycleObserver) {
        //获得初始状态
        val initialState = if (state == State.DESTROYED) State.DESTROYED else State.INITIALIZED
        val statefulObserver = ObserverWithState(observer, initialState)
        val previous = observerMap.putIfAbsent(observer, statefulObserver)
        if (previous != null) {
            return
        }
        val isReentrance = addingObserverCounter != 0 || handlingEvent
        var targetState = calculateTargetState(observer)
        addingObserverCounter++
        while (statefulObserver.state < targetState && observerMap.contains(observer)
        ) {
            pushParentState(statefulObserver.state)
            val event = Event.upFrom(statefulObserver.state)
                ?: throw IllegalStateException("no event up from ${statefulObserver.state}")
            statefulObserver.dispatchEvent(lifecycleOwner, event)
            popParentState()
            // mState / subling may have been changed recalculate
            targetState = calculateTargetState(observer)
        }
        if (!isReentrance) {
            // we do sync only on the top level.
            sync()
        }
        addingObserverCounter--
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observerMap.remove(observer)
    }

    private fun calculateTargetState(observer: LifecycleObserver): State {
        val value = observerMap[observer]
        val siblingState = value?.state
        val parentState =
            if (parentStates.isNotEmpty()) parentStates[parentStates.size - 1] else null
        return min(min(state, siblingState), parentState)
    }


    private val isSynced: Boolean
        get() {
            if (observerMap.isEmpty()) {
                return true
            }
            val eldestObserverState = observerMap.firstNotNullOf { it.value.state }
            val newestObserverState = observerMap.asIterable().last().value.state
            return eldestObserverState == newestObserverState && state == newestObserverState
        }

    private fun sync() {
        while (!isSynced) {
            newEventOccurred = false
            if (state < observerMap.firstNotNullOf { it.value.state }) {
                backwardPass(lifecycleOwner)
            }
            val newest = observerMap.asIterable().lastOrNull()
            if (!newEventOccurred && newest != null && state > newest.value.state) {
                forwardPass(lifecycleOwner)
            }
        }
        newEventOccurred = false
    }

    private fun forwardPass(lifecycleOwner: LifecycleOwner) {
        @Suppress()
        val ascendingIterator: Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> =
            observerMap.iterator()
        while (ascendingIterator.hasNext() && !newEventOccurred) {
            val (key, observer) = ascendingIterator.next()
            while (observer.state < state && !newEventOccurred && observerMap.contains(key)
            ) {
                pushParentState(observer.state)
                val event = Event.upFrom(observer.state)
                    ?: throw IllegalStateException("no event up from ${observer.state}")
                observer.dispatchEvent(lifecycleOwner, event)
                popParentState()
            }
        }
    }

    private fun backwardPass(lifecycleOwner: LifecycleOwner) {
        val descendingIterator = observerMap.asIterable().reversed()
        for ((key, observer) in descendingIterator) {
            if (!newEventOccurred) {
                while (observer.state > state && !newEventOccurred && observerMap.contains(key)
                ) {
                    val event = Event.downFrom(observer.state)
                        ?: throw IllegalStateException("no event down from ${observer.state}")
                    pushParentState(event.targetState)
                    observer.dispatchEvent(lifecycleOwner, event)
                    popParentState()
                }
            } else {
                break
            }
        }


    }

    private fun popParentState() {
        parentStates.removeAt(parentStates.size - 1)
    }

    private fun pushParentState(state: State) {
        parentStates.add(state)
    }


    internal class ObserverWithState(observer: LifecycleObserver, initialState: State) {
        var state: State
        var lifecycleObserver: LifecycleEventObserver

        init {
            lifecycleObserver = DefaultLifeCycleEventObserver(observer)
            state = initialState
        }

        fun dispatchEvent(owner: LifecycleOwner?, event: Event) {
            val newState = event.targetState
            state = min(state, newState)
            lifecycleObserver.onStateChanged(owner!!, event)
            state = newState
        }
    }

    companion object {
        /**
         * Creates a new LifecycleRegistry for the given provider, that doesn't check
         * that its methods are called on the threads other than main.
         *
         * LifecycleRegistry is not synchronized: if multiple threads access this `LifecycleRegistry`, it must be synchronized externally.
         *
         * Another possible use-case for this method is JVM testing, when main thread is not present.
         */
        @VisibleForTesting
        fun createUnsafe(owner: LifecycleOwner): LifecycleRegistry {
            return LifecycleRegistry(owner)
        }

        internal fun min(state1: State, state2: State?): State {
            return if ((state2 != null) && (state2 < state1)) state2 else state1
        }
    }

}