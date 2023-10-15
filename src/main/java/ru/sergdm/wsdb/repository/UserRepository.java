package ru.sergdm.wsdb.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;

import ru.sergdm.wsdb.model.User;

public interface UserRepository extends CrudRepository<User, Long>,
		JpaSpecificationExecutor<User>{
	User findByUsername(String username);

}
