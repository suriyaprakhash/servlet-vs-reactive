package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

//@RepositoryRestResource
public interface StudentRepo extends JpaRepository<Student, Long>{
//public interface StudentRepo extends CrudRepository<Student, String>{
}
