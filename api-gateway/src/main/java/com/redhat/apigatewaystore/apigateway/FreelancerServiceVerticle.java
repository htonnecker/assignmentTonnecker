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

public class FreelancerServiceVerticle extends AbstractVerticle {

    private WebClient webClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
    	
        String freelancerServiceHost = config().getString("freelancer.service.host");
        int freelancerServicePort = config().getInteger("freelancer.service.port");
        WebClientOptions options = new WebClientOptions()
                .setDefaultHost(freelancerServiceHost)
                .setDefaultPort(freelancerServicePort)
                .setHttp2MaxPoolSize(100)
                .setMaxPoolSize(100);
        webClient = WebClient.create(vertx, options);

        vertx.eventBus().<JsonObject>consumer("FreelancerService", (msg) -> {
        	
        	JsonObject msgIn = msg.body();
            String action = msg.headers().get("action");
            if (action == null) {
                msg.fail(-1, "Action not specified");
            } else {
                switch (action) {
	                case "getFreelancer": {
	                    String freelancerId = msgIn.getString("freelancerId");
	                    webClient.post("/freelancer/" + freelancerId).as(BodyCodec.jsonObject()).rxSend()
	                            .subscribe(resp -> handleResponse(resp, msg), err -> msg.fail(-1, err.getMessage()));
	                    break;
	                }
                    case "getFreelancers": {
                        webClient.get("/freelancers").as(BodyCodec.jsonArray()).rxSend()
                            .map(resp -> {
                                if (resp.statusCode() > 200) {
                                    msg.fail(-1, "Freelancer Service HTTP status code: " + resp.statusCode());
                                }
                                return resp.body();
                            })
                            .flatMap(freelancers ->
                                Observable.fromIterable(freelancers).cast(JsonObject.class)
                                    .flatMapSingle(freelancer -> {
                                        JsonObject freelancerMsg = new JsonObject().put("freelancerId", freelancer.getString("freelancerId"));
                                        DeliveryOptions deliveryOptions = new DeliveryOptions().addHeader("action", "getFreelancers");
                                        return vertx.eventBus().<JsonObject>rxSend("FreelancerService", freelancerMsg, deliveryOptions)
                                            .map(m -> freelancer.copy().put("availability", m.body())).onErrorReturnItem(freelancer);
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
            msg.fail(-1, "Freelancer Service HTTP status code: " + resp.statusCode());
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
