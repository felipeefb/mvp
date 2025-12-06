package com.felipe.belo.mvp.core.component;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


/**
 * Facade for resolving i18n messages from Spring's {@link MessageSource}.
 */
@Component
public class MessageService {

    private final MessageSource messageSource;

    /**
     * Creates a new MessageService.
     *
     * @param messageSource the message source bean to use
     */
    public MessageService(@Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Resolve a message with the current request locale.
     *
     * @param code the message key
     * @return the resolved message
     */
    public String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    /**
     * Resolve a parameterized message with the current request locale.
     *
     * @param code the message key
     * @param args positional arguments for the message
     * @return the resolved message
     */
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * Resolve a message with an explicit locale.
     *
     * @param code   the message key
     * @param locale the locale to use
     * @return the resolved message
     */
    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }

    /**
     * Resolve a parameterized message with an explicit locale.
     *
     * @param code   the message key
     * @param args   positional arguments for the message
     * @param locale the locale to use
     * @return the resolved message
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, locale);
    }
}