// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Properties;
import java.net.URI;

public class SystemClient {

  // Constants for building URI to the system service.
  private final int DEFAULT_PORT = Integer.valueOf(System.getProperty("default.http.port"));
  private final String SYSTEM_PROPERTIES = "/system/properties";
  private final String PROTOCOL = "http";

  private URI uri;
  private Builder clientBuilder;

  // Used by the following guide(s): CDI, MP-METRICS, FAULT-TOLERANCE
  public void init(String hostname) {
    this.initHelper(hostname, DEFAULT_PORT);
  }

  // Used by the following guide(s): MP-CONFIG, MP-HEALTH
  public void init(String hostname, int port) {
    this.initHelper(hostname, port);
  }

  // Helper method to set the attributes.
  private void initHelper(String hostname, int port) {
    this.uri = buildUri(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
    this.clientBuilder = buildClientBuilder();
  }

  // Wrapper function that gets properties
  public Properties getProperties() {
    return getPropertiesHelper(this.clientBuilder);
  }

  // tag::doc[]
  /**
   * Builds the URI string to the system service for a particular host.
   * @param protocol
   *          - http or https.
   * @param host
   *          - name of host.
   * @param port
   *          - port number.
   * @param path
   *          - Note that the path needs to start with a slash!!!
   * @return URI of the system properties service.
   */
  // end::doc[]
  protected URI buildUri(String protocol, String host, int port, String path) {
    try {
      return new URI(protocol, null, host, port, path, null, null);
    } catch (Exception e) {
      System.err.println("Exception thrown while building the URL: " + e.getMessage());
      return null;
    }
  }

  // Method that creates the client builder
  protected Builder buildClientBuilder() {
    try {
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(this.uri).request();
      return builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    } catch (Exception e) {
      System.err.println("Exception thrown while building the client: " + e.getMessage());
      return null;
    }
  }

  // Helper method that processes the request
  protected Properties getPropertiesHelper(Builder builder) {
    try {
      Response response = builder.get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
        return response.readEntity(Properties.class);
      } else {
        System.err.println("Response Status is not OK.");
      }
    } catch (RuntimeException e) {
      System.err.println("Runtime exception: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Exception thrown while invoking the request: " + e.getMessage());
    }
    return null;
  }

}
