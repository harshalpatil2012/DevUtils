package com.example.controller;

import com.example.db.Session;
import com.example.service.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

	private final SessionService sessionService;

	public SessionController(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	@PostMapping
	public Session createSession(@RequestBody Session session) {
		return sessionService.createSession(session);
	}

	@GetMapping("/{sessionId}")
	public Session getSession(@PathVariable long sessionId) {
		return sessionService.getSessionById(sessionId);
	}

	// You can add more endpoints for updating, deleting, or querying sessions
}
