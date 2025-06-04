package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.joke.JokeInput;
import fr.ensim.interop.introrest.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/joke")
@CrossOrigin(origins = "*")
public class JokeController {

    @Autowired
    private JokeService jokeService;

    @GetMapping
    public ResponseEntity<List<Joke>> getAllJokes() {
        try {
            List<Joke> jokes = jokeService.getAllJokes();
            return ResponseEntity.ok(jokes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/random")
    public ResponseEntity<Joke> getRandomJoke(@RequestParam(required = false) String quality) {
        try {
            Optional<Joke> joke = jokeService.getRandomJoke(quality);
            if (joke.isPresent()) {
                return ResponseEntity.ok(joke.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

   @GetMapping("/{jokeId}")
    public ResponseEntity<Joke> getJokeById(@PathVariable Long jokeId) {
        try {
            Optional<Joke> joke = jokeService.getJokeById(jokeId);
            if (joke.isPresent()) {
                return ResponseEntity.ok(joke.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Joke> createJoke(@RequestBody JokeInput jokeInput) {
        try {
            if (!isValidJokeInput(jokeInput)) {
                return ResponseEntity.badRequest().build();
            }
            Joke createdJoke = jokeService.createJoke(jokeInput);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJoke);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{jokeId}")
    public ResponseEntity<Joke> updateJoke(@PathVariable Long jokeId, @RequestBody JokeInput jokeInput) {
        try {
            if (!isValidJokeInput(jokeInput)) {
                return ResponseEntity.badRequest().build();
            }
            Optional<Joke> updatedJoke = jokeService.updateJoke(jokeId, jokeInput);
            if (updatedJoke.isPresent()) {
                return ResponseEntity.ok(updatedJoke.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{jokeId}")
    public ResponseEntity<Void> deleteJoke(@PathVariable Long jokeId) {
        try {
            boolean deleted = jokeService.deleteJoke(jokeId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidJokeInput(JokeInput jokeInput) {
        return jokeInput != null
                && jokeInput.getTitre() != null && !jokeInput.getTitre().trim().isEmpty()
                && jokeInput.getTexte() != null && !jokeInput.getTexte().trim().isEmpty()
                && jokeInput.getNote() != null && jokeInput.getNote() >= 0 && jokeInput.getNote() <= 10;
    }
}