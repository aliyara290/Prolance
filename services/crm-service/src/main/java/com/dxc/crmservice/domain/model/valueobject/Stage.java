package com.dxc.crmservice.domain.model.valueobject;

import java.util.Set;

public enum Stage {

    PROSPECTING,
    QUALIFICATION,
    PROPOSAL,
    NEGOTIATION,
    WON,
    LOST;

    public boolean canMoveTo(Stage target) {
        return switch (this) {
            case PROSPECTING -> Set.of(QUALIFICATION, LOST).contains(target);
            case QUALIFICATION -> Set.of(PROPOSAL, LOST).contains(target);
            case PROPOSAL -> Set.of(NEGOTIATION, LOST).contains(target);
            case NEGOTIATION -> Set.of(WON, LOST).contains(target);
            case WON, LOST -> false;
        };
    }

    public boolean isClosed() {
        return this == WON || this == LOST;
    }
}