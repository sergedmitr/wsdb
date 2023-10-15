package ru.sergdm.wsdb;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sergdm.wsdb.exception.BadResourceException;
import ru.sergdm.wsdb.exception.ResourceAlreadyExistsException;
import ru.sergdm.wsdb.exception.ResourceNotFoundException;
import ru.sergdm.wsdb.model.User;
import ru.sergdm.wsdb.service.UserService;

@RestController
public class ApiController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	
	@GetMapping("/health")
	public ResponseEntity<Object> health() {
		Health health = new Health("OK");
		return new ResponseEntity<>(health, HttpStatus.OK);
	}
	
	@GetMapping("/")
	public ResponseEntity<Object> name() {
		SystemName name = new SystemName();
		return new ResponseEntity<>(name, HttpStatus.OK);
	}
	
	@GetMapping("/accidental")
	public ResponseEntity<Object> accidental() {
		try {
			int mls = userService.accidental();
			int rest = mls % 5;
			logger.info("mls = {}, rest = {}", mls, rest);
			if (rest == 3) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rest);
			}
			return new ResponseEntity<>(mls, HttpStatus.OK);
		} catch (InterruptedException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	@GetMapping("/name")
	public ResponseEntity<Object> fullName() {
		SystemName name = new SystemName();
		return new ResponseEntity<>(name, HttpStatus.OK);
	}

	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> findAll(
			@RequestParam(value = "page", defaultValue="1") int pageNumber,
			@RequestParam(required = false) String name) {
		List<User> users = userService.findAll();
		System.out.println("users2 = " + users);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> findUserById(@PathVariable long userId) {
		try {
			User user = userService.findById(userId);
			return ResponseEntity.ok(user);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping(value = "/users/card/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
		logger.info("user/card = {}", username);
		try {
			User user = userService.findByUserName(username);
			return ResponseEntity.ok(user);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping(value = {"/users", "/users/card"})
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) throws URISyntaxException {
		try {
			User newUser = userService.save(user);
			return ResponseEntity.created(new URI("/api/users/" + newUser.getId())).body(newUser);
		} catch (ResourceAlreadyExistsException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (BadResourceException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PutMapping(value = "/users/{userId}")
	public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @PathVariable long userId) {
		try {
			user.setId(userId);
			userService.update(user);
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.notFound().build();
		} catch (BadResourceException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PatchMapping("/users/{userId}")
	public ResponseEntity<Void> patchUser(@PathVariable long userId, @RequestBody User user) {
		try {
			userService.patch(userId, user.getFirstName(), user.getLastName(), user.getEmail());
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/users/card/{username}")
	public ResponseEntity<Void> patchUser(@PathVariable String username, @RequestBody User user) {
		try {
			userService.patchByUsername(username, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone());
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(path = "/users/{userId}")
	public ResponseEntity<Void> deleteUserById(@PathVariable long userId) {
		try {
			userService.deleteById(userId);
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
