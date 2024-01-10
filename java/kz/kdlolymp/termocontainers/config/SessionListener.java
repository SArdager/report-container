package kz.kdlolymp.termocontainers.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static kz.kdlolymp.termocontainers.Constants.TIMEOUT_MIN;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSessionListener.super.sessionCreated(event);
        event.getSession().setMaxInactiveInterval(60 * TIMEOUT_MIN);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
    }
}
