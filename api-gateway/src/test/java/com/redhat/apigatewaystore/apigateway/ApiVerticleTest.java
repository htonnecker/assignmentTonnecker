package com.redhat.apigatewaystore.apigateway;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

    private Vertx vertx;

    private int port;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("gateway.http.port", port));
        vertx.deployVerticle(new ApiVerticle(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetProject(TestContext context) throws Exception {
        vertx.eventBus().<JsonObject>consumer("ProjectService", msg -> {
            msg.reply(new JsonObject().put("result", "getProject"));
        });

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/api/project/111111", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
                JsonObject result = body.toJsonObject();
                assertThat(result, notNullValue());
                assertThat(result.containsKey("result"), is(true));
                assertThat(result.getString("result"), equalTo("getProject"));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }
    
    @Test
    public void testGetProjects(TestContext context) {
        vertx.eventBus().<JsonObject>consumer("ProjectService", msg -> {
            msg.reply(new JsonArray().add(new JsonObject().put("result", "getProjects")));
        });

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/api/projects", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
                JsonArray result = body.toJsonArray();
                assertThat(result, notNullValue());
                assertThat(result.size(), equalTo(1));
                assertThat(result.getJsonObject(0).getString("result"), equalTo("getProjects"));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }
    
    @Test
    public void testGetProjectsByStatus(TestContext context) {
        vertx.eventBus().<JsonObject>consumer("ProjectService", msg -> {
            msg.reply(new JsonArray().add(new JsonObject().put("result", "getProjectsByStatus")));
        });

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/api/projects", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
                JsonArray result = body.toJsonArray();
                assertThat(result, notNullValue());
                assertThat(result.getJsonObject(0).getString("result"), equalTo("getProjectsByStatus"));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }
    
    @Test
    public void testGetFreelancer(TestContext context) throws Exception {
        vertx.eventBus().<JsonObject>consumer("FreelancerService", msg -> {
            msg.reply(new JsonObject().put("result", "getFreelancer"));
        });

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/api/freelancer/111111", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
                JsonObject result = body.toJsonObject();
                assertThat(result, notNullValue());
                assertThat(result.containsKey("result"), is(true));
                assertThat(result.getString("result"), equalTo("getFreelancer"));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }

    @Test
    public void testGetFreelancers(TestContext context) {
        vertx.eventBus().<JsonObject>consumer("FreelancerService", msg -> {
            msg.reply(new JsonArray().add(new JsonObject().put("result", "getFreelancers")));
        });

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/api/freelancers", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
                JsonArray result = body.toJsonArray();
                assertThat(result, notNullValue());
                assertThat(result.size(), equalTo(1));
                assertThat(result.getJsonObject(0).getString("result"), equalTo("getFreelancers"));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }

    @Test
    public void testReadinessHealthCheck(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health", response -> {
                assertThat(response.statusCode(), equalTo(200));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

}
