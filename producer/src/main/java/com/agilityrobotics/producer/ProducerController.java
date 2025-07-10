package com.agilityrobotics.be;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ProducerController {

//	@Autowired
//	private SocketIOServer socketServer;

//	ProducerController(SocketIOServer socketServer) {
//		this.socketServer = socketServer;
//
//		this.socketServer.addEventListener("metric_subscription", MetricSubscription.class, onSubscriptionEvent);
//	}

//	@GetMapping("/")
//	public AvailabilityResponse index() {
//		return new AvailabilityResponse("Monkey Pants");
//	}
//
//	@PostMapping("/availability")
//	public void updateAvailability(@RequestBody String val) {
//		this.socketServer.getAllClients().stream().forEach(c -> {
//			System.out.println("BFC looping over client: " + c.getSessionId());
//			if (c.has("availability_scalar")) {
//				c.sendEvent("availability_scalar", new AvailabilityScalar(val + "%"));
//			}
//		});
//		this.socketServer.getBroadcastOperations().sendEvent("availability_scalar", new AvailabilityScalar(val + "%"));
//	}

}
