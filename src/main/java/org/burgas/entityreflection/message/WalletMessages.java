package org.burgas.entityreflection.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletMessages {

    SAME_SENDER_AND_RECEIVER_WALLET("Same sender and receiver wallets"),
    NOT_ENOUGH_WALLET_BALANCE("Not enough wallet balance"),
    WALLET_NOT_FOUND("Wallet not found");

    private final String message;
}
