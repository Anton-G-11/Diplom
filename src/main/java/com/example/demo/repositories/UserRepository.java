/*
 * 
 */
package com.example.demo.repositories;

import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 *
 * @author anton
 */
public interface UserRepository extends JpaRepository<User, Integer>{
    
}
