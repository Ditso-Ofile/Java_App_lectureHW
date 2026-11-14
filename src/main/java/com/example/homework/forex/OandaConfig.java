package com.example.homework.forex;

import com.oanda.v20.Context;
import com.oanda.v20.account.AccountID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Oanda settings, read from application.properties (oanda.url / oanda.account-id / oanda.token). */
@Component
public class OandaConfig {

    private final String url;
    private final String accountId;
    private final String token;

    public OandaConfig(@Value("${oanda.url}") String url,
                       @Value("${oanda.account-id}") String accountId,
                       @Value("${oanda.token}") String token) {
        this.url = url;
        this.accountId = accountId;
        this.token = token;
    }

    public Context context() {
        return new Context(url, token);
    }

    public AccountID accountId() {
        return new AccountID(accountId);
    }
}
