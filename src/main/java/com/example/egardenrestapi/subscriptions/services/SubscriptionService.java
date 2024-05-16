package com.example.egardenrestapi.subscriptions.services;

import com.example.egardenrestapi.subscriptions.Subscription;
import com.example.egardenrestapi.subscriptions.entities.SubscriptionEntity;
import com.example.egardenrestapi.subscriptions.entities.SubscriptionType;
import com.example.egardenrestapi.subscriptions.payloads.AddSubscriptionDto;
import com.example.egardenrestapi.subscriptions.repositories.SubscriptionRepository;
import com.example.egardenrestapi.users.entities.UserEntity;
import com.example.egardenrestapi.users.services.UserService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class SubscriptionService implements Subscription {

    private final SubscriptionRepository repository;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> addChosenSubscription(AddSubscriptionDto addSubscriptionDto){
        UserEntity userEntity = userService.findByUsernameOrEmail(addSubscriptionDto.getUsername(), addSubscriptionDto.getUsername());
        SubscriptionEntity existingSubscriptionEntity = repository.findByUserEntityId(userEntity.getId());

        if (existingSubscriptionEntity !=null) {
            existingSubscriptionEntity.setSubscriptionType(SubscriptionType.valueOf(addSubscriptionDto.getSubscriptionType()));
            repository.save(existingSubscriptionEntity);
            return new ResponseEntity<>("Subscription successfully updated.", HttpStatus.OK);
        }

        SubscriptionEntity subscriptionEntity =new SubscriptionEntity();
        subscriptionEntity.setSubscriptionType(SubscriptionType.valueOf(addSubscriptionDto.getSubscriptionType()));
        subscriptionEntity.setUserEntity(userEntity);

        repository.save(subscriptionEntity);
        return new ResponseEntity<>("Subscription successfully added.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> cancelSubscription(String username){
        UserEntity userEntity = userService.findByUsernameOrEmail(username, username);
        SubscriptionEntity subscriptionEntity = repository.findByUserEntityId(userEntity.getId());
        repository.deleteById(subscriptionEntity.getId());
        return new ResponseEntity<>("Subscription successfully canceled.", HttpStatus.OK);
    }
}
