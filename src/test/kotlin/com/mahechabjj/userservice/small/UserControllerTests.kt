package com.mahechabjj.userservice.small

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@Profile("UnitTests")
@RunWith(SpringRunner::class)
@SpringBootTest
class UserControllerTests {

	@Test
	fun whenGettingUser_expectUserToBeReturned() {

	}

	@Test
	fun givenNonExistingUser_whenGettingUser_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenChangingPassword_expectPasswordToBeChanged() {

	}

	@Test
	fun givenExistingUserAndIncorrectHeaders_whenChangingPassword_expectBadRequestToBeReturned() {

	}

	@Test
	fun givenNonExistingUser_whenChangingPassword_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenFindingUserById_expectUserToBeReturned() {

	}

	@Test
	fun givenExistingUserAndIncorrectId_whenFindingUserById_expectUserToNotBeFound() {

	}

	@Test
	fun givenNonExistingUser_whenFindingUserById_expectUserToNotBeFound() {

	}

	@Test
	fun whenFindingUserByEmail_expectUserToBeReturned() {

	}

	@Test
	fun givenNonExistingUser_whenFindingUserByEmail_expectUserToNotBeFound() {

	}

	@Test
	fun givenExistingUserAndWrongCredentials_whenFindingUserByEmail_expectUserToNotBeFound() {

	}

	@Test
	fun whenCreatingUser_expectUserToBeCreated() {

	}

	@Test
	fun whenCreatingUser_expectPasswordToBeHashed() {

	}

	@Test
	fun givenExistingUser_whenCreatingUser_expectBadRequest() {

	}

	@Test
	fun givenIncorrectCredentials_whenCreatingUser_expectBadRequest() {

	}

	@Test
	fun whenEditingUser_expectUserToBeUpdated() {

	}

	@Test
	fun givenNonExistingUser_whenEditingUser_expectUserToBeCreated() {

	}

	@Test
	fun givenIncorrectCredentials_whenEditingUser_expectBadRequest() {

	}

	@Test
	fun whenAddingPlaylist_expectPlaylistToBeAdded() {

	}

	@Test
	fun givenNonExistingUser_whenAddingPlaylist_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenGettingPlaylists_expectPlaylistsToBeReturned() {

	}

	@Test
	fun givenNonExistingUser_whenGettingPlaylists_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenGettingSinglePlaylist_expectPlaylistToBeReturned() {

	}

	@Test
	fun givenNonExistingUser_whenGettingSinglePlaylist_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenUpdatingPlaylist_expectPlaylistToBeUpdated() {

	}

	@Test
	fun givenNonExistingPlaylist_whenUpdatingPlaylist_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenDeletingPlaylist_expectPlaylistToBeDeleted() {

	}

	@Test
	fun givenNonExistingPlaylist_expectNotFoundToBeReturned() {

	}

	@Test
	fun whenDeletingVideoFromPlaylist_expectVideoToBeDeletedFromPlaylist() {

	}

	@Test
	fun givenNonExistingVideoInPlaylist_whenDeletingVideoFromPlaylist_expectNotFoundToBeReturned() {

	}
}
