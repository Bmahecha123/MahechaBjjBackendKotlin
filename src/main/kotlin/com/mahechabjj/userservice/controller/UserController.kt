package com.mahechabjj.userservice.controller

import com.mahechabjj.userservice.model.Playlist
import com.mahechabjj.userservice.model.User
import com.mahechabjj.userservice.model.Video
import com.mahechabjj.userservice.repository.IUserRepository
import com.mahechabjj.userservice.service.IEncryptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController {
    private val userRepository: IUserRepository
    private val encryptionService: IEncryptionService

    @Autowired
    constructor(userRepository: IUserRepository, encryptionService: IEncryptionService) {
        this.userRepository = userRepository
        this.encryptionService = encryptionService
    }

    @CrossOrigin
    @GetMapping("user/getUser")
    fun getUserByEmail(@RequestHeader("X-EMAIL") email: String): User? {
        val user: User? = userRepository.findUserByEmail(email)

        if (user == null)
            return null
        else
            return user
    }

    @CrossOrigin
    @PostMapping("password/changePassword")
    fun changePassword(@RequestHeader("X-ID") id: String,
                       @RequestHeader("X-ANSWER") answer: String, @RequestHeader("X-PASSWORD") password: String) {
        val user = userRepository.findUserById(id)
        val secretQuestion: String = user.secretQuestion

        if (answer.equals(user.secretQuestionAnswer)) {
            user.password = password
            val hashedPassword: String = hashPassword(user)
            user.password = hashedPassword
            userRepository.save(user)
        }
    }

    @CrossOrigin
    @GetMapping("user/findById")
    fun findUserById(@RequestHeader("X-ID") id: String): User {
        return userRepository.findUserById(id)
    }

    @CrossOrigin
    @GetMapping("user/findByEmail")
    fun findUserByEmail(@RequestHeader("X-EMAIL") email: String,
                        @RequestHeader("X-PASSWORD") password: String): ResponseEntity<User> {
        val user: User? = userRepository.findUserByEmail(email)
        if (user == null)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        val passwordMatch: Boolean = encryptionService.checkPassword(password, user.password)
        if (passwordMatch)
            return ResponseEntity<User>(user, HttpStatus.OK)
        else
            return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @CrossOrigin
    @PostMapping("user/create")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val userExist: User? = userRepository.findUserByEmail(user.email)
        if (userExist != null)
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        val hashedPassword: String = hashPassword(user)
        user.password = hashedPassword
        userRepository.save(user)

        return ResponseEntity<User>(user, HttpStatus.CREATED)
    }

    @CrossOrigin
    @PutMapping("user/edit")
    fun editUser(@RequestBody user: User): ResponseEntity<User> {
        userRepository.save(user)

        return ResponseEntity<User>(user, HttpStatus.OK)
    }

    @CrossOrigin
    @PutMapping("user/addplaylist/{id}")
    fun addPlaylist(@PathVariable("id") id: String, @RequestBody playlist: Playlist): ResponseEntity<User> {
        val user = userRepository.findUserById(id)
        val playListList: ArrayList<Playlist> = user.playlists

        playListList.add(playlist)
        user.playlists = playListList
        userRepository.save(user)

        return ResponseEntity(HttpStatus.CREATED)
    }

    @CrossOrigin
    @GetMapping("user/getplaylists/{id}")
    fun getPlaylists(@PathVariable("id") id: String): ResponseEntity<List<Playlist>> {
        val user: User = userRepository.findUserById(id)
        val playListList = user.playlists

        return ResponseEntity<List<Playlist>>(playListList, HttpStatus.OK)
    }

    @CrossOrigin
    @GetMapping("user/getplaylist/{id}")
    fun getPlaylist(@RequestHeader("X-PLAYLISTNAME") playlistName: String,
                    @PathVariable("id") id: String): ResponseEntity<Playlist> {
        val user = userRepository.findUserById(id)
        val playListList = user.playlists

        var userPlaylist: Playlist? = null

        //Refactor to make it more functional (map perhaps?)
        for(playlist: Playlist in playListList) {
            if (playlist.name.contains(playlistName))
                userPlaylist = playlist
        }

        return ResponseEntity<Playlist>(userPlaylist, HttpStatus.OK)
    }

    @CrossOrigin
    @PostMapping("user/updateplaylists/{id}")
    fun updateUserPlaylist(@PathVariable("id") id: String,
                           @RequestBody newPlaylist: Playlist): ResponseEntity<Playlist> {
        val user = userRepository.findUserById(id)
        val playListList = user.playlists
        var playListTobeRemoved: Playlist? = null

        //Refactor with map or a more functional approach
        for (playlist: Playlist in playListList)
            if (playlist.name.contains(newPlaylist.name))
                playListTobeRemoved = playlist

        playListList.remove(playListTobeRemoved)
        playListList.add(newPlaylist)
        user.playlists = playListList

        userRepository.save(user)

        return ResponseEntity(HttpStatus.CREATED)
    }

    @CrossOrigin
    @PostMapping("user/deleteplaylist/{id}")
    fun deleteUserPlaylist(@PathVariable("id") id: String,
                           @RequestBody playListToBeDeleted: Playlist): ResponseEntity<Playlist> {
        val user = userRepository.findUserById(id)
        val playListList = user.playlists
        var deletePlaylist: Playlist? = null

        //Refactor with more functional approach
        playListList.forEach {
            if (it.name.contains(playListToBeDeleted.name))
                deletePlaylist = it
        }

        playListList.remove(deletePlaylist)
        user.playlists = playListList
        userRepository.save(user)

        return ResponseEntity(HttpStatus.OK)
    }

    @CrossOrigin
    @PostMapping("user/deleteVideo/{id}")
    fun deleteVideoFromPlaylist(@PathVariable("id") id: String,
                                @RequestHeader("X-VIDEONAME") videoName: String,
                                @RequestBody userPlaylist: Playlist): ResponseEntity<Playlist> {
        val user = userRepository.findUserById(id)
        val playListList = user.playlists
        var found: Boolean = false

        for (playlist: Playlist in playListList)
            if (playlist.name.contains(userPlaylist.name))
                found = true

        if (found)
            playListList.remove(userPlaylist)

        var videoToBeDeleted: Video? = null

        for (video: Video in userPlaylist.videos)
            if (video.name.contains(videoName))
                videoToBeDeleted = video

        userPlaylist.videos.remove(videoToBeDeleted)
        playListList.add(userPlaylist)
        user.playlists = playListList

        userRepository.save(user)

        return ResponseEntity(HttpStatus.OK)
    }

    fun hashPassword(user: User): String {
        return encryptionService.encryptString(user.password);
    }
}