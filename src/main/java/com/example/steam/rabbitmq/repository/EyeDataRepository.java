package com.example.steam.rabbitmq.repository;


import com.example.steam.rabbitmq.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EyeDataRepository extends JpaRepository<Message, Long> {

}