package fr.ensim.interop.introrest.serive;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.joke.JokeInput;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JokeService {

    private List<Joke> jokes = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void initializeJokes() {
        jokes.add(new Joke(nextId++, "Le développeur et le café",
                "Pourquoi les développeurs confondent-ils Halloween et Noël ? Parce qu'OCT 31 = DEC 25.", 8.5));

        jokes.add(new Joke(nextId++, "Le bug invisible",
                "Un programmeur va au supermarché. Sa femme lui dit : 'Achète une baguette, et si ils ont des œufs, prends-en six.' Le programmeur revient avec 6 baguettes.", 9.0));

        jokes.add(new Joke(nextId++, "Le problème de société",
                "Il y a 10 types de personnes dans le monde : ceux qui comprennent le binaire et ceux qui ne le comprennent pas.", 7.5));

        jokes.add(new Joke(nextId++, "Entretien d'embauche",
                "Lors d'un entretien d'embauche : 'Quels sont vos points forts ?' - 'Je suis un expert en recherche Google.' - 'Et vos points faibles ?' - 'Je ne sais pas, je n'ai jamais cherché ça sur Google.'", 6.8));

        jokes.add(new Joke(nextId++, "L'optimisation",
                "Un développeur entre dans un magasin. Il voit le panneau '10 tasses pour 9.99€'. Il en achète 0 parce qu'il n'avait pas besoin de tasse et il a économisé 9.99€.", 8.2));

        jokes.add(new Joke(nextId++, "La blague nulle",
                "Pourquoi les programmeurs préfèrent-ils le thé ? Parce que le café fait des bugs.", 3.2));
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
        int randomIndex = random.nextInt(filteredJokes.size());
        return Optional.of(filteredJokes.get(randomIndex));
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
