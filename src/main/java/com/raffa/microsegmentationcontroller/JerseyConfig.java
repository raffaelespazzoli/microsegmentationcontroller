package com.raffa.microsegmentationcontroller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
	private static Logger log = Logger.getLogger("com.raffa.microsegmentationcontroller.jersey-traffic");
    public JerseyConfig() {
        register(new LoggingFeature(log, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT,8192));
        register(GsonMessageBodyHandler.class);
        register(SynchResource.class);
        //register(JodaDateAdapter.class);
    }
	
}
