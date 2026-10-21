package com.shiftsync.model;

// Here, I will implement the "state machine" for a shift in the hospital
public enum ShiftStatus {

    // This one is allocated to a medical staff with respect to the schedule
    SCHEDULED,

    // The member suddenly cannot come to its shift, so the shift needs to be taken fast by somebody else
    VACANT_EMERGENCY,

    // The shift is temporarily blocked because two members negotiate a trade on it
    SWAP_REQUESTED,

    // The shift is done and the hours have been clocked
    COMPLETED
}
