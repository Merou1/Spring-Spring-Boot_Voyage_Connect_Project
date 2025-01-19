package com.VoyageConnect.AgenceDeVoyage;

import com.VoyageConnect.AgenceDeVoyage.entity.Destination;

import com.VoyageConnect.AgenceDeVoyage.entity.Role;
import com.VoyageConnect.AgenceDeVoyage.repository.RoleRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.DestinationRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement // Add this annotation
public class AgenceDeVoyageApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DestinationRepository destinationRepository;


    public AgenceDeVoyageApplication(RoleRepository roleRepository,DestinationRepository destinationRepository) {
        this.roleRepository = roleRepository;
        this.destinationRepository = destinationRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(AgenceDeVoyageApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if roles already exist before inserting to avoid duplicates
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_CLIENT"));
            System.out.println("Roles inserted!");
        } else {
            System.out.println("Roles already exist, skipping insertion.");
        }
        
    }
}
