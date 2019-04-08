package com.redhat.apigatewaystore.apigateway;

import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.client.WebClient;

public class ProjectServiceVerticle extends AbstractVerticle {

    private WebClient webClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        String projectServiceHost = config().getString("project.service.host");
        int projectServicePort = config().getInteger("project.service.port");
        WebClientOptions options = new WebClientOptions()
                .setDefaultHost(projectServiceHost)
                .setDefaultPort(projectServicePort)
                .setMaxPoolSize(100)
                .setHttp2MaxPoolSize(100);
        webClient = WebClient.create(vertx, options);

        vertx.eventBus().<JsonObject>consumer("ProjectService", (msg) -> {

            JsonObject msgIn = msg.body();
            String action = msg.headers().get("action");
            if (action == null) {
                msg.fail(-1, "Action not specified");
            } else {
                switch (action) {
                    case "getProject": {
                        String projectId = msgIn.getString("projectId");
                        webClient.get("/project/" + projectId).as(BodyCodec.jsonObject()).rxSend()
                                .subscribe(resp -> handleResponse(resp, msg), err -> msg.fail(-1, err.getMessage()));
                        break;
                    }
                    case "getProjects": {
                    	webClient.get("/projects").as(BodyCodec.jsonArray()).rxSend()
	                        .map(resp -> {
	                            if (resp.statusCode() > 200) {
	                                msg.fail(-1, "Project Service HTTP status code: " + resp.statusCode());
	                            }
	                            return resp.body();
	                        })
	                        .flatMap(projects ->
	                            Observable.fromIterable(projects).cast(JsonObject.class)
	                                .flatMapSingle(project -> {
	                                    JsonObject projectMsg = new JsonObject().put("projectId", project.getString("projectId"));
	                                    DeliveryOptions deliveryOptions = new DeliveryOptions().addHeader("action", "getProjects");
	                                    return vertx.eventBus().<JsonObject>rxSend("ProjectService", projectMsg, deliveryOptions)
	                                        .map(m -> project.copy().put("availability", m.body())).onErrorReturnItem(project);
	                                }).toList()
	                        ).subscribe(list -> msg.reply(new JsonArray(list)), err -> msg.fail(-1, err.getMessage()));
                    	break;
                    }
                    case "getProjectsByStatus": {
                    	String projectStatus = msgIn.getString("projectStatus");
                    	webClient.get("/projects/" + projectStatus).as(BodyCodec.jsonArray()).rxSend()
	                        .map(resp -> {
	                            if (resp.statusCode() > 200) {
	                                msg.fail(-1, "Project Service HTTP status code: " + resp.statusCode());
	                            }
	                            return resp.body();
	                        })
	                        .flatMap(projects ->
	                        	Observable.fromIterable(projects).cast(JsonObject.class)
	                            	.flatMapSingle(project -> {
	                            		JsonObject projectMsg = new JsonObject().put("projectId", project.getString("projectId"));
	                            		DeliveryOptions deliveryOptions = new DeliveryOptions().addHeader("action", "getProjects");
	                            		return vertx.eventBus().<JsonObject>rxSend("ProjectService", projectMsg, deliveryOptions)
	                            				.map(m -> project.copy().put("availability", m.body())).onErrorReturnItem(project);
	                            	}).toList()
	                        ).subscribe(list -> msg.reply(new JsonArray(list)), err -> msg.fail(-1, err.getMessage()));
                    	break;
                    }
                    default: {
                        msg.fail(-1, "Invalid action " + action);
                    }
                }
            }

        });

        startFuture.complete();
    }

    private void handleResponse(HttpResponse<JsonObject> resp, Message<JsonObject> msg) {
        if (resp.statusCode() >= 400) {
            msg.fail(-1, "Project Service HTTP status code: " + resp.statusCode());
        } else {
            JsonObject body = resp.body();
            msg.reply(body);
        }
    }

    @Override
    public void stop() throws Exception {
        webClient.close();
    }
}
