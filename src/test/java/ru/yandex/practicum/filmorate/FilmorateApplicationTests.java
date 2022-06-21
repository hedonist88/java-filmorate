package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

	UserController userController;
	FilmController filmController;

	public FilmorateApplicationTests() {
		FilmServiceImpl filmService = new FilmServiceImpl(new InMemoryFilmStorage(), new InMemoryUserStorage());
		filmController = new FilmController(filmService);
		UserServiceImpl userService = new UserServiceImpl(new InMemoryUserStorage());
		userController = new UserController(userService);
	}

	@Test
	void contextLoads() {
	}

	// User controller tests
	@Test
	void addUserWithWrongEmail(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("ivan#yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.create(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addUserWithEmptyEmail(){
		Exception exception = assertThrows(InvalidEmailException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.create(user);
		});

		String expectedMessage = ErrorMessage.EMPTY_EMAIL.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addUserWithEmptyLogin(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("");
			user.setName("Ivan");
			user.setEmail("ivan@yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.create(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addUserWithEmptyName(){
		User user = new User();
		user.setLogin("Ivan");
		user.setName("");
		user.setEmail("ivan@yandex.ru");
		user.setBirthday(LocalDate.of(1988,3,4));

		final ResponseEntity<User> responseEntity = userController.create(user);

		Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK, "Некоректный статус в ответе");
		Assertions.assertNotNull(responseEntity, "Ответ не возвращается");
		Assertions.assertEquals(responseEntity.getBody().getName(), user.getLogin(), "Неверно указывается имя");
	}

	@Test
	void addUserWithWrongLogin(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin(" Iv an1    ");
			user.setName("ivan");
			user.setEmail("ivan1@yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.create(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addUserWithWrongBirthday(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("ivan@yandex.ru");
			user.setBirthday(LocalDate.of(2048,3,4));

			final ResponseEntity<User> responseEntity = userController.create(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithWrongEmail(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("ivan#yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.put(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithEmptyEmail(){
		Exception exception = assertThrows(InvalidEmailException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.put(user);
		});

		String expectedMessage = ErrorMessage.EMPTY_EMAIL.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithEmptyLogin(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("");
			user.setName("Ivan");
			user.setEmail("ivan@yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.put(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithWrongLogin(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("    ivan  ");
			user.setName("Ivan");
			user.setEmail("ivan@yandex.ru");
			user.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity2 = userController.put(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateUserWithWrongBirthday(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			User user = new User();
			user.setLogin("ivan");
			user.setName("Ivan");
			user.setEmail("ivan@yandex.ru");
			user.setBirthday(LocalDate.of(2048,3,4));

			final ResponseEntity<User> responseEntity = userController.put(user);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateNotRegisterUser(){
		Exception exception = assertThrows(NotFoundException.class, () -> {
			User user2 = new User();
			user2.setId(12);
			user2.setLogin("liya");
			user2.setName("liya");
			user2.setEmail("liya@yandex.ru");
			user2.setBirthday(LocalDate.of(1988,3,4));

			final ResponseEntity<User> responseEntity = userController.put(user2);
		});

		String expectedMessage = ErrorMessage.USER_NOT_REGISTER.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	// Film controller tests
	@Test
	void addFilmWithWrongName(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written " +
					"and directed by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(120);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.create(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addFilmWithDescriptionLengthMore200(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 2");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary. Pulp Fiction is a 1994 " +
					"American black comedy crime film written and");
			film.setDuration(121);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.create(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addFilmWithDurationMoreLess0(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 3");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(-1);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.create(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void addFilmWithWrongReleaseDate(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 4");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(120);
			film.setReleaseDate(LocalDate.of(1895,12,27));

			final ResponseEntity<Film> responseEntity = filmController.create(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateFilmWithWrongId(){
		Exception exception = assertThrows(NotFoundException.class, () -> {
			Film film = new Film();
			film.setId(-666);
			film.setName("Pulp fiction");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written " +
					"and directed by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(120);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.put(film);
		});

		String expectedMessage = ErrorMessage.FILMS_NOT_FOUND.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateFilmWithWrongName(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written " +
					"and directed by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(120);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.put(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateFilmWithDescriptionLengthMore200(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 2");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary. Pulp Fiction is a 1994 " +
					"American black comedy crime film written and");
			film.setDuration(121);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.put(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateFilmWithDurationMoreLess0(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 3");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(-1);
			film.setReleaseDate(LocalDate.of(2022,2,2));

			final ResponseEntity<Film> responseEntity = filmController.put(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void updateFilmWithWrongReleaseDate(){
		Exception exception = assertThrows(ValidationException.class, () -> {
			Film film = new Film();
			film.setName("Pulp fiction 4");
			film.setDescription("Pulp Fiction is a 1994 American black comedy crime film written and directed " +
					"by Quentin Tarantino, who conceived it with Roger Avary.");
			film.setDuration(120);
			film.setReleaseDate(LocalDate.of(1895,12,27));

			final ResponseEntity<Film> responseEntity = filmController.put(film);
		});

		String expectedMessage = ErrorMessage.VALIDATE_ERROR.getMessage();
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
}
