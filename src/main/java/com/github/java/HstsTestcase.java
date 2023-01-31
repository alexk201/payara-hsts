package com.github.java;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@ApplicationPath("/")
public class HstsTestcase extends Application {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@QueryParam("length") @DefaultValue("2474560") int length) {
        // generate a random string with LENGTH alphanumeric characters
        return new SecureRandom().ints(0, 36)
                .mapToObj(i -> Integer.toString(i, 36))
                .limit(length)
                .collect(Collectors.joining());
    }

}
