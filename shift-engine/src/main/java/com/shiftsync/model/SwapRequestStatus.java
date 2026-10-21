package com.shiftsync.model;

// This one is made for the status in the middle of the trade
public enum SwapRequestStatus {

    // 1: Initiator sends the request, waiting for the other party to respond
    PENDING_PEER,

    // 2: Colleague accepted the trade, goes straight to automated hours checking
    PEER_ACCEPTED,

    // 3: Legality is confirmed, waiting for superior's approval
    PENDING_MANAGER,

    // 4.1: Approved - Trade successful and noted in the database
    APPROVED,

    // 4.2: Denied - Trade unsuccessful because of the colleague, the superior or the automated system
    REJECTED
}
