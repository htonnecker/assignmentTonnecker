package com.redhat.apigatewaystore.apigateway;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.CompletableHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class ApiVerticle extends AbstractVerticle {

    @Override
    public void start(io.vertx.core.Future<Void> startFuture) throws Exception {

        Router router = Router.router(vertx);

        router.get("/api/freelancer/:freelancerId").handler(this::getFreelancer);
        router.get("/api/freelancers").handler(this::getFreelancers);
        
        router.get("/api/project/:projectId").handler(this::getProject);
        router.get("/api/projects").handler(this::getProjects);
        router.get("/api/projects/:projectStatus").handler(this::getProjectsByStatus);
        
        router.route("/api/*").failureHandler(rc -> rc.response().setStatusCode(500).end());

        //Health checks
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> f.complete(Status.OK()));
        router.get("/health").handler(healthCheckHandler);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(config().getInteger("gateway.http.port", 8080))
                .toCompletable()
                .subscribe(CompletableHelper.toObserver(startFuture));
    }
    
    private void getFreelancer(RoutingContext rc) {
    	DeliveryOptions options = new DeliveryOptions().addHeader("action", "getFreelancer");
        JsonObject msg = new JsonObject().put("freelancerId", rc.request().getParam("freelancerId"));
        vertx.eventBus().<JsonObject>rxSend("FreelancerService", msg, options)
                .map(Message::body)
                .subscribe(json -> rc.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(json.encode()), rc::fail);
    }
    
    private void getFreelancers(RoutingContext rc) {
    	DeliveryOptions options = new DeliveryOptions().addHeader("action", "getFreelancers");
        JsonObject msg = new JsonObject();
        vertx.eventBus().<JsonArray>rxSend("FreelancerService", msg, options)
                .map(Message::body)
                .subscribe(json -> rc.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                    .end(json.encode()), rc::fail);
    }
    
    private void getProject(RoutingContext rc) {
    	DeliveryOptions options = new DeliveryOptions().addHeader("action", "getProject");
        JsonObject msg = new JsonObject().put("projectId", rc.request().getParam("projectId"));
        vertx.eventBus().<JsonObject>rxSend("ProjectService", msg, options)
                .map(Message::body)
                .subscribe(json -> rc.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(json.encode()), rc::fail);
    }
    
    private void getProjects(RoutingContext rc) {
    	DeliveryOptions options = new DeliveryOptions().addHeader("action", "getProjects");
        JsonObject msg = new JsonObject();
        vertx.eventBus().<JsonArray>rxSend("ProjectService", msg, options)
                .map(Message::body)
                .subscribe(json -> rc.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                    .end(json.encode()), rc::fail);
    }

    private void getProjectsByStatus(RoutingContext rc) {
    	DeliveryOptions options = new DeliveryOptions().addHeader("action", "getProjectsByStatus");
        JsonObject msg = new JsonObject().put("projectStatus", rc.request().getParam("projectStatus"));
        vertx.eventBus().<JsonObject>rxSend("ProjectService", msg, options)
                .map(Message::body)
                .subscribe(json -> rc.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(json.encode()), rc::fail);
    }
}
