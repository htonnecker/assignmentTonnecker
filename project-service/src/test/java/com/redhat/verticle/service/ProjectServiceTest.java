package com.redhat.verticle.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceTest extends MongoTestBase {

    private Vertx vertx;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "projects", async, context);
        async.await(10000);
    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }

    @Test
    public void testGetProjects(TestContext context) throws Exception {
        Async saveAsync = context.async(2);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("ownerFirstName", "Project 1 Firstname")
                .put("ownerLastName", "Project 1 Lastname")
                .put("projectTitle", "Project 1 Title")
        		.put("projectDescription", "Project 1 Description")
        		.put("projectStatus", "open");

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("ownerFirstName", "Project 2 Firstname")
                .put("ownerLastName", "Project 2 Lastname")
                .put("projectTitle", "Project 2 Title")
        		.put("projectDescription", "Project 2 Description")
        		.put("projectStatus", "completed");

        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjects(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(2));
                Set<String> projectIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
                assertThat(projectIds.size(), equalTo(2));
                assertThat(projectIds, allOf(hasItem(projectId1),hasItem(projectId2)));
                async.complete();
            }
        });
    }

    @Test
    public void testGetProject(TestContext context) throws Exception {
        Async saveAsync = context.async(2);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("ownerFirstName", "Project 1 Firstname")
                .put("ownerLastName", "Project 1 Lastname")
                .put("projectTitle", "Project 1 Title")
        		.put("projectDescription", "Project 1 Description")
        		.put("projectStatus", "open");

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("ownerFirstName", "Project 2 Firstname")
                .put("ownerLastName", "Project 2 Lastname")
                .put("projectTitle", "Project 2 Title")
        		.put("projectDescription", "Project 2 Description")
        		.put("projectStatus", "completed");

        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProject(projectId1, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().getProjectId(), equalTo(projectId1));
                async.complete();
            }
        });
    }
    
    @Test
    public void testGetProjectsByStatus(TestContext context) throws Exception {
        Async saveAsync = context.async(2);
        String projectId1 = "111111";
        String projectStatus1 = "open";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("ownerFirstName", "Project 1 Firstname")
                .put("ownerLastName", "Project 1 Lastname")
                .put("projectTitle", "Project 1 Title")
        		.put("projectDescription", "Project 1 Description")
        		.put("projectStatus", projectStatus1);

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        String projectStatus2 = "completed";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("ownerFirstName", "Project 2 Firstname")
                .put("ownerLastName", "Project 2 Lastname")
                .put("projectTitle", "Project 2 Title")
        		.put("projectDescription", "Project 2 Description")
        		.put("projectStatus", projectStatus2);

        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjectsByStatus(projectStatus2, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(1));
                Set<String> projectStatus = ar.result().stream().map(p -> p.getProjectStatus()).collect(Collectors.toSet());
                assertThat(projectStatus.size(), equalTo(1));
                assertThat(projectStatus, hasItem(projectStatus2));
                async.complete();
            }
        });
    }
    
    @Test
    public void testPing(TestContext context) throws Exception {
        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();
        service.ping(ar -> {
            assertThat(ar.succeeded(), equalTo(true));
            async.complete();
        });
    }
}
