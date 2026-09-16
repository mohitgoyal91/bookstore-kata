package be.bnpparibasfortis.bookstore.auth.repository;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity defaultUser;


    @BeforeEach
    public void setUp() {
        defaultUser = new UserEntity(
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                HASHED_PASSWORD
        );
        defaultUser = userRepository.save(defaultUser);
    }

    @Nested
    class findByEmail {
        @Test
        void whenUserExists_shouldReturnUser() {
            Optional<UserEntity> result =
                    userRepository.findByEmail("abc@def.com");

            assertThat(result)
                    .isPresent()
                    .contains(defaultUser);
        }

        @Test
        void whenUserDoesNotExist_shouldReturnEmpty() {
            Optional<UserEntity> result =
                    userRepository.findByEmail("unknown@def.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class existsByEmail {
        @Test
        void whenUserExists_shouldReturnTrue() {
            boolean result =
                    userRepository.existsByEmail("abc@def.com");

            assertThat(result).isTrue();
        }

        @Test
        void whenUserDoesNotExist_shouldReturnFalse() {
            boolean result =
                    userRepository.existsByEmail("unknown@def.com");

            assertThat(result).isFalse();
        }
    }




}