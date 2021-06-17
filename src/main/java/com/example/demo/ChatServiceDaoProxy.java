package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ChatServiceDAO",url="http://localhost:8007/")
public interface ChatServiceDaoProxy {
	
	
	@GetMapping("/BroadcastDAO/{message}/{sender}/{receiver}/{channel}/{id}/{objectid}/{namesender}/{namereceiver}")
    public void broadcastMessage(@PathVariable String message,
			@PathVariable String sender,@PathVariable String receiver,
			@PathVariable String channel, @PathVariable String id,
			@PathVariable String objectid,@PathVariable String namesender,
			@PathVariable String namereceiver);

}
