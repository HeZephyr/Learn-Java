package example.concurrent.thread.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MyLock {
    // Inner class, extends AQS to implement an exclusive lock
    private static class Sync extends AbstractQueuedSynchronizer {
        // Checks if the lock is held exclusively
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // Attempts to acquire the lock
        @Override
        protected boolean tryAcquire(int acquires) {
            // Use CAS to set state from 0 to 1 if the lock is available
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread()); // Set the current thread as the owner
                return true;
            }
            return false; // Lock acquisition failed
        }

        // Attempts to release the lock
        @Override
        protected boolean tryRelease(int releases) {
            // If state is already 0, there is an error (lock not held)
            if (getState() == 0) throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null); // Clear the owner thread
            setState(0); // Release the lock by setting state back to 0
            return true;
        }
    }

    // Create a Sync object that will handle lock operations
    private final Sync sync = new Sync();

    // Public method to acquire the lock
    public void lock() {
        sync.acquire(1); // Calls AQS's acquire method
    }

    // Public method to release the lock
    public void unlock() {
        sync.release(1); // Calls AQS's release method
    }

    // Checks if the lock is currently held by any thread
    public boolean isLocked() {
        return sync.isHeldExclusively();
    }
}