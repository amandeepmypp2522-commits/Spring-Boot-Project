package com.amandea.app.ws.repository;

import com.amandea.app.ws.io.entity.UserEntity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//spring data JPA will do all the work for me.it will create sql query, it will connect to the database using the database connection
//details provided in application.properties file.it will find the record and if the record is found, it will create user entity object
//for us and, it will return it back to our USerServiceImpl and, we will use that user entity object to work with it.
@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}
