package com.example.helloworld.core;

/**
 * The object returned to client.
 * @author Steve Brown, Estafet Ltd.
 *
 */
public class Saying {

    /**
     * The id number of the response.
     */
    private final long id;

    /**
     * The message content.
     */
    private final String content;

    /**
     * Constructor,
     * @param theId
     *          The response Id.
     * @param theContent
     *          The message content.
     */
    public Saying(final long theId, final String theContent) {
        id = theId;
        content = theContent;
    }

    /**
     * Get the response Id.
     * @return
     *          The response Id.
     */
    public long getId() {
        return id;
    }

    /**
     * Get the message content.
     * @return
     *          The message content.
     */
    public String getContent() {
        return content;
    }
}