package com.mycorp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycorp.support.Ticket;
import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Collections;

import static com.mycorp.ZendeskFactory.*;
import static com.mycorp.ZendeskHelper.complete;
import static com.mycorp.ZendeskLogger.submit;
import static com.mycorp.ZendeskUtils.createMapper;

public class Zendesk implements Closeable {
    private static final String JSON = "application/json; charset=UTF-8";
    private final boolean closeClient;
    private final AsyncHttpClient client;
    private final Realm realm;
    private final String url;
    private final String oauthToken;
    private final ObjectMapper mapper;
    private final Logger logger;
    private boolean closed = false;


    protected Zendesk(AsyncHttpClient client, String url, String username, String password) {
        this.logger = LoggerFactory.getLogger(Zendesk.class);
        this.closeClient = client == null;
        this.oauthToken = null;
        this.client = client == null ? new AsyncHttpClient() : client;
        this.url = url.endsWith("/") ? url + "api/v2" : url + "/api/v2";
        if (username != null) {
            this.realm = newRealm(username, password);
        } else {
            if (password != null) {
                throw new IllegalStateException("Cannot specify token or password without specifying username");
            }
            this.realm = null;
        }
        this.mapper = createMapper();
    }

    public Ticket createTicket(Ticket ticket) {
        return complete(
                    submit(
                        this.logger,
                        this.client,
                        req(this.realm,
                            this.oauthToken,
                            "POST",
                            cnst(this.url, "/tickets.json"),
                            JSON,
                            json(this.mapper, Collections.singletonMap("ticket", ticket))),
                        handle(Ticket.class, "ticket")));
    }

  protected <T> ZendeskAsyncCompletionHandler<T> handle(final Class<T> clazz, final String name, final Class... typeParams) {
        return new BasicAsyncCompletionHandler<T>(this.logger, this.mapper, clazz, name, typeParams);
    }

    public void close() {
        if (closeClient && !client.isClosed()) {
            client.close();
        }
        closed = true;
    }

}