package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.telegram.Update;
import fr.ensim.interop.introrest.model.weather.Meteo;
import fr.ensim.interop.introrest.model.weather.MeteoForecast;
import fr.ensim.interop.introrest.model.weather.UpdateResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.*;

@Component
public class ListenerUpdateTelegram {

	private static final String BOT_TOKEN = "7340987230:AAEgslTT6jjFdmObq0YF5EdcJXX5wn07jSw";
	private static final String TELEGRAM_API = "https://api.telegram.org/bot" + BOT_TOKEN;
	private static final String METEO_API_URL = "http://localhost:9090/meteo";
	private static final String METEO_FORECAST_API_URL = "http://localhost:9090/meteo/forecast";

	private long lastUpdateId = 0;

	private final RestTemplate restTemplate = new RestTemplate();

	public ListenerUpdateTelegram() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pollUpdates();
			}
		}, 0, 1000);  // toutes les 1000ms (1 seconde)
	}

	private void pollUpdates() {
		try {
			String url = TELEGRAM_API + "/getUpdates?offset=" + (lastUpdateId + 1);
			UpdateResponse response = restTemplate.getForObject(url, UpdateResponse.class);

			if (response != null && response.getResult() != null) {
				List<Update> updates = response.getResult();
				for (Update update : updates) {
					handleUpdate(update);
					if (update.getUpdateId() != null) {
						lastUpdateId = update.getUpdateId();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleUpdate(Update update) {
		if (update.getMessage() == null || update.getMessage().getText() == null) return;

		String messageText = update.getMessage().getText();
		String chatId = update.getMessage().getChat().getId().toString();

		if (messageText != null && messageText.toLowerCase().startsWith("météo")) {
			handleWeatherCommand(chatId, messageText);
		} else if (messageText.toLowerCase().startsWith("blague")) {
			handleJokeCommand(chatId, messageText);
		} else {
			sendMessage(chatId, "Commandes disponibles :\n" +
					" MÉTÉO :\n" +
					"• 'météo [ville]' - Météo du jour\n" +
					"• 'météo [ville] 3' - Météo 3 jours\n" +
					"• 'météo demain [ville]' - Météo de demain\n\n" +
					" BLAGUES :\n" +
					"• 'blague' - Blague aléatoire\n" +
					"• 'blague drole OU bonne OU excellente' - Blague bien notée\n" +
					"• 'blague nulle OU mauvaise' - Blague mal notée");
		}
	}

	/**
	 * NOUVELLE MÉTHODE : Gère les commandes météo étendues
	 */
	private void handleWeatherCommand(String chatId, String messageText) {
		try {
			// Analyse du message pour déterminer ville et nombre de jours
			String[] parts = messageText.toLowerCase().split("\\s+");
			String ville = null;
			int days = 1; // Par défaut : météo du jour

			// Recherche des mots-clés pour les jours
			if (messageText.toLowerCase().contains("demain")) {
				days = 2;
			} else if (messageText.toLowerCase().contains("3") ||
					messageText.toLowerCase().contains("trois")) {
				days = 3;
			} else if (messageText.toLowerCase().contains("5") ||
					messageText.toLowerCase().contains("cinq") ||
					messageText.toLowerCase().contains("semaine")) {
				days = 5;
			}

			// Extraction de la ville
			for (int i = 1; i < parts.length; i++) {
				String part = parts[i];
				// Si ce n'est pas un nombre et pas un mot-clé temporel
				if (!part.matches("\\d+") && !isTemporalKeyword(part)) {
					ville = capitalizeFirstLetter(part);
					break;
				}
			}

			// Vérification si une ville a été spécifiée
			if (ville == null || ville.isEmpty()) {
				sendMessage(chatId, "Merci de spécifier une ville. \n" +
						"Exemples :\n" +
						"• 'météo Paris'\n" +
						"• 'météo Lyon 3'\n" +
						"• 'météo demain Marseille'");
				return;
			}

			// Appel API selon le nombre de jours
			if (days == 1) {
				// Météo du jour (ton code existant)
				handleCurrentWeather(chatId, ville);
			} else {
				// Météo des prochains jours (nouvelle fonctionnalité)
				handleWeatherForecast(chatId, ville, days);
			}

		} catch (Exception e) {
			sendMessage(chatId, " Erreur lors de l'analyse de votre demande météo.");
			e.printStackTrace();
		}
	}

	/**
	 * Gère la météo actuelle (ton code existant légèrement modifié)
	 */
	private void handleCurrentWeather(String chatId, String ville) {
		try {
			String url = METEO_API_URL + "?ville=" + URLEncoder.encode(ville, "UTF-8");
			Meteo meteo = restTemplate.getForObject(url, Meteo.class);

			if (meteo != null && meteo.getDetails() != null) {
				String message = " Météo à " + ville + " (aujourd'hui)\n" +
						" Température : " + meteo.getTemperature() + "°C\n" +
						"️ Ciel : " + meteo.getMeteo() + " (" + meteo.getDetails() + ")";

				sendMessage(chatId, message);
			} else {
				sendMessage(chatId, " Ville non trouvée.");
			}
		} catch (Exception e) {
			sendMessage(chatId, " Erreur lors de la récupération de la météo.");
			e.printStackTrace();
		}
	}

	/**
	 * NOUVELLE MÉTHODE : Gère la météo des prochains jours
	 */
	private void handleWeatherForecast(String chatId, String ville, int days) {
		try {
			String url = METEO_FORECAST_API_URL + "?ville=" + URLEncoder.encode(ville, "UTF-8") + "&days=" + days;
			MeteoForecast forecast = restTemplate.getForObject(url, MeteoForecast.class);

			if (forecast != null && forecast.getForecasts() != null && !forecast.getForecasts().isEmpty()) {
				StringBuilder message = new StringBuilder();
				message.append(" Météo à ").append(ville).append(" (").append(days).append(" jours)\n\n");

				String[] dayNames = {"Aujourd'hui", "Demain", "Après-demain", "Dans 3 jours", "Dans 4 jours"};

				for (int i = 0; i < forecast.getForecasts().size() && i < days; i++) {
					Meteo dayMeteo = forecast.getForecasts().get(i);
					String dayName = (i < dayNames.length) ? dayNames[i] : "Dans " + i + " jours";

					message.append("Date: ").append(dayName).append(" :\n");
					message.append(" Température:    ").append(dayMeteo.getTemperature()).append("°C\n");
					message.append("   Ciel: ").append(dayMeteo.getMeteo())
							.append(" (").append(dayMeteo.getDetails()).append(")\n");

					if (i < forecast.getForecasts().size() - 1) {
						message.append("\n");
					}
				}

				sendMessage(chatId, message.toString());
			} else {
				sendMessage(chatId, "Impossible de récupérer les prévisions pour " + ville);
			}
		} catch (Exception e) {
			sendMessage(chatId, " Erreur lors de la récupération des prévisions météo.");
			e.printStackTrace();
		}
	}

	/**
	 * Gère les commandes blagues (ton code existant)
	 */
	private void handleJokeCommand(String chatId, String messageText) {
		try {
			String url = "http://localhost:9090/api/v1/joke/random";

			if (messageText.toLowerCase().contains("nulle") || messageText.toLowerCase().contains("mauvaise")) {
				url += "?quality=bad";
			} else if (messageText.toLowerCase().contains("bonne") || messageText.toLowerCase().contains("excellente")) {
				url += "?quality=good";
			}

			Joke joke = restTemplate.getForObject(url, Joke.class);

			if (joke != null) {
				String message = " **" + joke.getTitre() + "**\n\n" +
						joke.getTexte() + "\n\n" +
						" Note: " + joke.getNote() + "/10";

				sendMessage(chatId, message);
			} else {
				sendMessage(chatId, " Aucune blague disponible pour cette qualité.");
			}
		} catch (Exception e) {
			sendMessage(chatId, " Erreur lors de la récupération de la blague.");
			e.printStackTrace();
		}
	}

	/**
	 * Vérifie si un mot est un mot-clé temporel
	 */
	private boolean isTemporalKeyword(String word) {
		String[] temporalKeywords = {"jours", "jour", "demain", "semaine", "aujourd'hui", "maintenant", "trois", "cinq"};
		return Arrays.asList(temporalKeywords).contains(word.toLowerCase());
	}

	/**
	 * Met en forme le nom de ville (première lettre majuscule)
	 */
	private String capitalizeFirstLetter(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	/**
	 * Méthode existante pour envoyer un message
	 */
	private void sendMessage(String chatId, String text) {
		if (text == null || text.trim().isEmpty()) {
			System.out.println(" Message vide, rien envoyé à Telegram.");
			return;
		}

		String url = TELEGRAM_API + "/sendMessage";

		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("chat_id", chatId);
		requestBody.put("text", text);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

		try {
			restTemplate.postForEntity(url, request, String.class);
		} catch (Exception e) {
			System.err.println(" Erreur lors de l'envoi du message Telegram : " + e.getMessage());
		}
	}
}