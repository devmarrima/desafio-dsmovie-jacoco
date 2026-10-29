package com.devsuperior.dsmovie.services;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUsername;
	private String nonExistingUsername;
	private UserEntity user;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	void setUp() {
		existingUsername = "maria@gmail.com";
		nonExistingUsername = "unknown@gmail.com";

		user = UserFactory.createUserEntity();
		userDetails = UserDetailsFactory.createCustomClientUser(existingUsername);

		when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
		when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

		when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
		when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(List.of());

		when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		UserEntity result = service.authenticated();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingUsername, result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		when(userUtil.getLoggedUsername()).thenThrow(RuntimeException.class);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUsername);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingUsername, result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class,
				() -> service.loadUserByUsername(nonExistingUsername));
	}
}
