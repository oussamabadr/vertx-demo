package org.quentin_badr.vertx.demo.vertx_demo.eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import org.quentin_badr.vertx.demo.vertx_demo.simpleVertx.MainVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class EventBusVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusVerticle.class);


    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(EventBusVerticle.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) throws InterruptedException {
        var eventBus = vertx.eventBus();

        var address_1 = "com.decathlon.member.my-message";

        // first consumer
        eventBus.consumer(address_1, event -> LOGGER.info("1st consumer == Hey, you've got a message     --> " + event.body()));

        // second consumer
        MessageConsumer<Object> messageConsumer = eventBus.consumer(address_1);
        messageConsumer.handler(message -> {
            LOGGER.info("2nd consumer == Hoy, another message here --> " + message.body());
            if (!message.headers().isEmpty()) {
                LOGGER.info("With header --> " + message.headers());
                message.reply("I don't trust you!!!");
            }
        });

        // will be send at most to one listener
        eventBus.send(address_1, "I want just to talk to one of the consumers");

        // after no answer, I will talk to everyone
        eventBus.publish(address_1, "is there anyone here ???");

        var deliveryOptions = new DeliveryOptions().addHeader("token", "I'm toto, plz trust me!!!");

        eventBus.request(address_1, "Why no one is answering my call ????", deliveryOptions, event -> {
            if (event.succeeded()) {
                LOGGER.info("Finally I've got a reply --> {}", event.result().body());
            }
        });

        eventBus.send(address_1, "Why no one is answering my call ????", deliveryOptions);

        IntStream.range(0, 10).forEach(value -> eventBus.publish(address_1, "And now I will spam you with ordered messages - spam No: " + value));

        vertx.setTimer(1000, e ->
            messageConsumer.unregister(event -> {
                if (event.failed()) {
                    LOGGER.warn("2nd consumer say == Something wrong happen....so I'm stuck here!!!!!");
                } else {
                    LOGGER.warn("2nd consumer say == I'm freeeeeeeeeeeee!!!!!");
                }

                IntStream.range(0, 10).forEach(value -> eventBus.publish(address_1, "And now I will spam you with ordered messages - spam No: " + value));
        }));

        TimeUnit.SECONDS.sleep(3);
        vertx.close();
    }

}
