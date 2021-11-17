package de.nebelniek.registration.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class GuildsLoadedEvent extends ApplicationEvent {

    private final ApplicationContext applicationContext;

    public GuildsLoadedEvent(ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
    }

}
