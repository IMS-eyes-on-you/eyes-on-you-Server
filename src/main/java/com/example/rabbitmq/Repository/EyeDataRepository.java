package com.example.rabbitmq.Repository;


import com.example.rabbitmq.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EyeDataRepository extends JpaRepository<Message, Long> {

}
