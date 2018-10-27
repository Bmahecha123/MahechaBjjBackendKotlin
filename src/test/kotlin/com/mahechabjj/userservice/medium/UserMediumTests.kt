package com.mahechabjj.userservice.medium

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.mahechabjj.userservice.model.Packages
import com.mahechabjj.userservice.model.Playlist
import com.mahechabjj.userservice.model.User
import com.mahechabjj.userservice.model.Video
import com.mongodb.Mongo
import io.micrometer.core.annotation.TimedSet
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.util.NestedServletException


@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserMediumTests {
    @Autowired
    private lateinit var mockMvc: MockMvc
    private val packages: Packages = Packages(false, false, true)
    private var user: User = User(
            null,
            "test@test.com",
            "mahecha",
            "black",
            "city",
            "manhattan",
            "password",
            ArrayList(),
            packages
            )
    private val video: Video = Video(
            "brianVideo",
            "movie",
            "link",
            "linkHd",
            "description")
    private val playlist: Playlist = Playlist(
            "brianList",
            "my playlist",
            ArrayList<Video>())

    @Autowired
    private lateinit var mongo: Mongo

    private val mapper: ObjectMapper = ObjectMapper()
            .configure(SerializationFeature.WRAP_ROOT_VALUE,
                    false)

    private val ow: ObjectWriter = mapper.writer().withDefaultPrettyPrinter()

    @After
    fun tearDown() {
        mongo.dropDatabase("test")
    }

    @Test
    fun whenCreatingUser_expectUserToBeSavedToDbAndRetrieved() {
        createAndSaveUser(user)
        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", user.email))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun givenNonExistingUser_whenGettingUser_expectEmptyBodyToBeReturned() {
        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", "admin@mahechabjj.com"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$")
                        .doesNotExist())
    }

    @Test
    fun whenCreatingDuplicateUser_expectBadRequest() {
        createAndSaveUser(user)
        val userString: String = ow.writeValueAsString(user)

        mockMvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun whenCreatingUser_expectUserToBeSavedToDbAndRetrievedByEmail() {
        createAndSaveUser(user)
        mockMvc.perform(get("/user/findByEmail")
                .header("X-EMAIL", user.email)
                .header("X-PASSWORD", user.password))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun givenUserAndWrongPassword_whenFindingByEmail_expectBadRequest() {
        createAndSaveUser(user)
        mockMvc.perform(get("/user/findByEmail")
                .header("X-EMAIL", user.email)
                .header("X-PASSWORD", "wrongPassword"))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun givenNonExistingUser_whenGettingUserByEmail_expectUserNotToBeFound() {
        mockMvc.perform(get("/user/findByEmail")
                .header("X-EMAIL", "admin@mahechabjj.com")
                .header("X-PASSWORD", "test"))
                .andExpect(status().isNotFound)
    }

    @Test
    fun whenCreatingUser_expectUserToBeSavedToDbAndRetrievedById() {
        val json = createAndSaveUser(user)
        mockMvc.perform(get("/user/findById")
                .header("X-ID", json.id))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun givenUserAndNoIdHeader_whenFindingById_expectBadRequest() {
        mockMvc.perform(get("/user/findById"))
                .andExpect(status().isBadRequest)
    }

    @Test(expected = NestedServletException::class)
    fun givenNonExistingUser_whenGettingUserById_expectUserNotToBeFound() {
        mockMvc.perform(get("/user/findById")
                .header("X-ID", "id"))
    }

    @Test
    fun givenUser_whenChangingPassword_expectPasswordToBeChanged() {
        val json = createAndSaveUser(user)
        val newPassword = "new"

        mockMvc.perform(post("/password/changePassword")
                .header("X-ID", json.id)
                .header("X-ANSWER", json.secretQuestionAnswer)
                .header("X-PASSWORD", newPassword))
                .andExpect(status().isOk)
    }

    @Test
    fun givenUserAndWrongSecretQuestionAnswer_whenChangingPassword_expectPasswordToNotBeChanged() {
        val json = createAndSaveUser(user)
        val newPassword = "new"

        mockMvc.perform(post("/password/changePassword")
                .header("X-ID", json.id)
                .header("X-ANSWER", "wrongAnswer")
                .header("X-PASSWORD", newPassword))
                .andExpect(status().isOk)

        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", user.email))
                .andExpect(jsonPath("$.password", equalTo(json.password)))
    }

    @Test
    fun givenUser_whenEditingUser_expectUserToBeEdited() {
        val json = createAndSaveUser(user)

        json.email = "mahecha@test.com"
        val userString: String = ow.writeValueAsString(json)

        mockMvc.perform(put("/user/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isOk)

        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", json.email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", not(user.email)))
    }

    @Test
    fun givenUser_whenAddingPlaylist_expectPlaylistToBeAdded() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(put("/user/addplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)
    }

    @Test
    fun givenUserWithPlaylist_whenGettingAllPlaylists_expectAllPlaylistsToBeReturned() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(put("/user/addplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)

        mockMvc.perform(get("/user/getplaylists/${user.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.size()", equalTo(1)))
    }

    @Test
    fun givenUserWithNoPlaylists_whenGettingAllPlaylists_expectNoPlaylistsToBeReturned() {
        val user = createAndSaveUser(this.user)
        mockMvc.perform(get("/user/getplaylists/${user.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.size()", equalTo(0)))
    }

    @Test
    fun givenUserWithPlaylist_whenGettingSinglePlaylist_expectPlaylistToBeReturned() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(put("/user/addplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)

        mockMvc.perform(get("/user/getplaylist/${user.id}")
                .header("X-PLAYLISTNAME", this.playlist.name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name", equalTo(this.playlist.name)))
    }

    @Test
    fun givenUserWithNoPlaylists_whenGettingSinglePlaylist_expectEmptyBodyToBeReturned() {
        val user = createAndSaveUser(this.user)

        mockMvc.perform(get("/user/getplaylist/${user.id}")
                .header("X-PLAYLISTNAME", this.playlist.name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$")
                        .doesNotExist())
    }

    @Test
    fun givenUserWithPlaylists_whenUpdatingPlaylist_expectPlaylistToBeUpdated() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(put("/user/addplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)

        val updatedPlaylist = this.playlist.copy(description = "updated")
        val updatedPlaylistString = ow.writeValueAsString(updatedPlaylist)

        mockMvc.perform(post("/user/updateplaylists/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPlaylistString))
                .andExpect(status().isCreated)

        mockMvc.perform(get("/user/getplaylist/${user.id}")
                .header("X-PLAYLISTNAME", updatedPlaylist.name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.description", equalTo(updatedPlaylist.description)))
    }

    @Test
    fun givenUserWithNoPlaylists_whenUpdatingPlaylist_expectPlaylistToBeAdded() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(post("/user/updateplaylists/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)

        mockMvc.perform(get("/user/getplaylist/${user.id}")
                .header("X-PLAYLISTNAME", this.playlist.name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.description", equalTo(this.playlist.description)))
    }

    @Test
    fun givenUserWithPlaylists_whenDeletingPlaylist_expectPlaylistToBeDeleted() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(put("/user/addplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isCreated)

        mockMvc.perform(post("/user/deleteplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isOk)

        mockMvc.perform(get("/user/getplaylist/${user.id}")
                .header("X-PLAYLISTNAME", this.playlist.name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").doesNotExist())
    }

    @Test
    fun givenUserWithNoPlaylists_whenDeletingPlaylist_expectStatusOk() {
        val user = createAndSaveUser(this.user)
        val playlist = ow.writeValueAsString(this.playlist)

        mockMvc.perform(post("/user/deleteplaylist/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playlist))
                .andExpect(status().isOk)
    }

    @Test
    fun givenUserWithPlaylist_whenDeletingVideoFromPlaylist_expectVideoToBeDeletedFromPlaylist() {
        addVideosToPlaylist()
        val user = createAndSaveUser(this.user)
        val playlistString = ow.writeValueAsString(user.playlists[0])

        mockMvc.perform(post("/user/deleteVideo/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-VIDEONAME", user.playlists[0].videos[0].name)
                .content(playlistString))
                .andExpect(status().isOk)

        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", user.email))
                .andExpect(jsonPath("$.playlists[0].videos.size()", equalTo(1)))
    }

    @Test
    fun givenUserWithPlaylistAndWrongVideoName_whenDeletingVideoFromPlaylist_expectPlaylistToNotBeDeleted() {
        addVideosToPlaylist()
        val user = createAndSaveUser(this.user)
        val playlistString = ow.writeValueAsString(user.playlists[0])

        assert(user.playlists[0].videos.size == 2)

        mockMvc.perform(post("/user/deleteVideo/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-VIDEONAME", "wrongName")
                .content(playlistString))
                .andExpect(status().isOk)

        mockMvc.perform(get("/user/getUser")
                .header("X-EMAIL", user.email))
                .andExpect(jsonPath("$.playlists[0].videos.size()", equalTo(2)))
    }

    fun addVideosToPlaylist() {
        playlist.videos.add(video)
        playlist.videos.add(video.copy(name = "videoTwo"))

        user.playlists.add(playlist)
    }

    fun createAndSaveUser(user: User): User {
        val userString: String = ow.writeValueAsString(user)

        //Create User
        val mvcResult = mockMvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isCreated)
                .andReturn()

        return mapper.readValue(mvcResult.response.contentAsString, User::class.java)

    }
}