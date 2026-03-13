package org.astroEngine.Constants;

public enum vSyncState {
    ENABLE(0),
    DISABLE(1),
    ENABLE_IF_SUPPORTS(-1);

    private int value;

    vSyncState(int value) {
        this.value = value;
    }
}
