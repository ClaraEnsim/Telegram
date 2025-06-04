package fr.ensim.interop.introrest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.joke.JokeInput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JokeService {

    private List<Joke> jokes = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void loadJokesFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("jokes.json").getInputStream();
            jokes = mapper.readValue(inputStream, new TypeReference<List<Joke>>() {});

            // Calcul du prochain ID
            nextId = jokes.stream()
                    .mapToLong(Joke::getId)
                    .max()
                    .orElse(0L) + 1;

        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger jokes.json", e);
        }
    }

    public List<Joke> getAllJokes() {
        return new ArrayList<>(jokes);
    }

    public Optional<Joke> getRandomJoke(String quality) {
        List<Joke> filteredJokes = jokes;

        if ("good".equals(quality)) {
            filteredJokes = jokes.stream()
                    .filter(joke -> joke.getNote() >= 7.0)
                    .collect(Collectors.toList());
        } else if ("bad".equals(quality)) {
            filteredJokes = jokes.stream()
                    .filter(joke -> joke.getNote() < 5.0)
                    .collect(Collectors.toList());
        }

        if (filteredJokes.isEmpty()) {
            return Optional.empty();
        }

        Random random = new Random();
        return Optional.of(filteredJokes.get(random.nextInt(filteredJokes.size())));
    }

    public Optional<Joke> getJokeById(Long id) {
        return jokes.stream()
                .filter(joke -> joke.getId().equals(id))
                .findFirst();
    }

    public List<Joke> searchJokesByTitle(String title) {
        return jokes.stream()
                .filter(joke -> joke.getTitre().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Joke createJoke(JokeInput jokeInput) {
        Joke newJoke = new Joke(nextId++, jokeInput.getTitre(),
                jokeInput.getTexte(), jokeInput.getNote());
        jokes.add(newJoke);
        return newJoke;
    }

    public Optional<Joke> updateJoke(Long id, JokeInput jokeInput) {
        for (int i = 0; i < jokes.size(); i++) {
            if (jokes.get(i).getId().equals(id)) {
                Joke updatedJoke = new Joke(id, jokeInput.getTitre(),
                        jokeInput.getTexte(), jokeInput.getNote());
                jokes.set(i, updatedJoke);
                return Optional.of(updatedJoke);
            }
        }
        return Optional.empty();
    }

    public boolean deleteJoke(Long id) {
        return jokes.removeIf(joke -> joke.getId().equals(id));
    }
}
