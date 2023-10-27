package ru.sergdm.wsdb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import ru.sergdm.wsdb.exception.BadResourceException;
import ru.sergdm.wsdb.exception.ResourceAlreadyExistsException;
import ru.sergdm.wsdb.exception.ResourceNotFoundException;
import ru.sergdm.wsdb.model.User;
import ru.sergdm.wsdb.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	private boolean existById(Long id) {
		return userRepository.existsById(id);
	}
	
	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		//System.out.println("pagenumber = " + pageNumber + ", rowPerpage = " + rowPerpage);
		userRepository.findAll().forEach(users::add);
		System.out.println("users = " + users);
		return users;
	}

	public User findById(Long id) throws ResourceNotFoundException {
		Optional<User> userO = userRepository.findById(id);
		User user = userO.orElseThrow(() -> new ResourceNotFoundException("Cannt find User with id: " + id));
		return user;
	}

	public User findByUserName(String username) throws ResourceNotFoundException {
		Optional<User> userO = Optional.ofNullable(userRepository.findByUsername(username));
		User user = userO.orElseThrow(() -> new ResourceNotFoundException("Cannt find User with username: " + username));
		return user;
	}

	public User save(User user) throws BadResourceException, ResourceAlreadyExistsException {
		if (!StringUtils.isEmpty(user.getFirstName())) {
			if (user.getId() != null && existById(user.getId())) {
				throw new ResourceAlreadyExistsException("User with id: " + user.getId() + " already exists");
			}
			return userRepository.save(user);
		} else {
			BadResourceException exc = new BadResourceException("Failed to save user");
			exc.addErrorMessage("User FirstName is null or empty");
			throw exc;
		}
	}
	
	public void update(User user) throws BadResourceException, ResourceNotFoundException {
		if (!StringUtils.isEmpty(user.getFirstName())) {
			if (!existById(user.getId())) {
				throw new ResourceNotFoundException("Cannot find User with id: " + user.getId());
			}
			userRepository.save(user);
		} else {
			BadResourceException ex = new BadResourceException("Failed to save user");
			ex.addErrorMessage("User is null or empty");
			throw ex;
		}
	}
	
	public void patch(Long userId, String firstName, String lastName, String email) throws ResourceNotFoundException {
		User user = findById(userId);
		if (firstName != null) {
			user.setFirstName(firstName);
		}
		if (lastName != null) {
			user.setLastName(lastName);
		}
		if (email != null) {
			user.setEmail(email);
		}
		userRepository.save(user);
	}

	public void patchByUsername(String username, String firstName, String lastName, String email, String phone) throws ResourceNotFoundException {
		User user = findByUserName(username);
		if (firstName != null) {
			user.setFirstName(firstName);
		}
		if (lastName != null) {
			user.setLastName(lastName);
		}
		if (email != null) {
			user.setEmail(email);
		}
		if (phone != null) {
			user.setPhone(phone);
		}
		userRepository.save(user);
	}

	public void deleteById(Long userId) throws ResourceNotFoundException {
		if (!existById(userId)) {
			throw new ResourceNotFoundException("Cannot find User with id: " + userId);
		} else {
			userRepository.deleteById(userId);
		}
	}
	
	public int accidental() throws InterruptedException{
		Random r = new Random();
		int mls = r.nextInt(1000 + 1);
		TimeUnit.MILLISECONDS.sleep(mls);
		return mls;

	}

	public void deleteAll() {
		userRepository.deleteAll();
	}
}
