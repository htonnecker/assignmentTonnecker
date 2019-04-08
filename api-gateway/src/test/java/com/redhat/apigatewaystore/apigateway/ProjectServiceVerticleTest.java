package com.redhat.apigatewaystore.apigateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceVerticleTest {

    private Vertx vertx;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setUp(TestContext context) throws IOException {
      vertx = Vertx.vertx();

      // Register the context exception handler
      vertx.exceptionHandler(context.exceptionHandler());

      JsonObject config = new JsonObject()
          .put("project.service.host", "localhost")
          .put("project.service.port", wireMockRule.port());
      DeploymentOptions options = new DeploymentOptions().setConfig(config);

      // We pass the options as the second parameter of the deployVerticle method.
      vertx.deployVerticle(new ProjectServiceVerticle(), options, context.asyncAssertSuccess());
    }

    @Test
    public void testGetProject(TestContext context) throws Exception {
        stubFor(get(urlEqualTo("/projects"))
                .willReturn(
                aResponse().withStatus(200).withHeader("Content-type", "application/json")
                    .withBody(new JsonObject().put("projectId", "111111").encode())));

        JsonObject msgSent = new JsonObject()
            .put("projectId", "111111");
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "getProject");
        Async async = context.async();
        vertx.eventBus().<JsonObject>send("ProjectService", msgSent, options, ar -> {
            assertThat(ar.failed(), is(false));
            assertThat(ar.result(), notNullValue());
            assertThat(ar.result().body(), notNullValue());
            assertThat(ar.result().body().getString("projectId"), equalTo("111111"));
            wireMockRule.verify(getRequestedFor(urlEqualTo("/projects")));
            async.complete();
        });
    }
    
    @Test
    public void testGetProjects(TestContext context) throws Exception {
        stubFor(get(urlEqualTo("/projects"))
                .willReturn(
                aResponse().withStatus(200).withHeader("Content-type", "application/json")));

        JsonObject msgSent = new JsonObject();
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "getProjects");
        Async async = context.async();
        vertx.eventBus().<JsonObject>send("ProjectService", msgSent, options, ar -> {
            assertThat(ar.failed(), is(false));
            assertThat(ar.result(), notNullValue());
            assertThat(ar.result().body(), notNullValue());
            wireMockRule.verify(getRequestedFor(urlEqualTo("/projects")));
            async.complete();
        });
    }
    
    @Test
    public void testGetProjectsByStatus(TestContext context) throws Exception {
        stubFor(get(urlEqualTo("/projects"))
                .willReturn(
                aResponse().withStatus(200).withHeader("Content-type", "application/json")
                    .withBody(new JsonObject().put("projectStatus", "open").encode())));

        JsonObject msgSent = new JsonObject()
            .put("projectStatus", "open");
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "getProjectsByStatus");
        Async async = context.async();
        vertx.eventBus().<JsonObject>send("ProjectService", msgSent, options, ar -> {
            assertThat(ar.failed(), is(false));
            assertThat(ar.result(), notNullValue());
            assertThat(ar.result().body(), notNullValue());
            assertThat(ar.result().body().getString("projectStatus"), equalTo("open"));
            wireMockRule.verify(getRequestedFor(urlEqualTo("/projects")));
            async.complete();
        });
    }
}
