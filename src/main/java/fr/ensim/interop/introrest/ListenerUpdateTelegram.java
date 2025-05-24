package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.weather.UpdateResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import fr.ensim.interop.introrest.model.telegram.*;
import fr.ensim.interop.introrest.model.weather.Meteo;
import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.weather.Discussion;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram {

	private static final String BOT_TOKEN = "7340987230:AAEgslTT6jjFdmObq0YF5EdcJXX5wn07jSw";
	private static final String TELEGRAM_API = "https://api.telegram.org/bot" + BOT_TOKEN;
	private static final String METEO_API_URL = "http://localhost:9090/meteo";

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
			if (messageText.length() <= 6) {
				sendMessage(chatId, "Merci de spécifier une ville. Exemple : Météo Paris");
				return;
			}

			String ville = messageText.substring(6).trim();

			if (ville.isEmpty()) {
				sendMessage(chatId, "Merci de spécifier une ville. Exemple : Météo Paris");
				return;
			}

			try {
				String url = "http://localhost:9090/meteo?ville=" + URLEncoder.encode(ville, "UTF-8");
				Meteo meteo = restTemplate.getForObject(url, Meteo.class);

				if (meteo != null && meteo.getDetails() != null) {
					String description = meteo.getDetails();
					String main = meteo.getMeteo();
					String temperature = meteo.getTemperature();

					String message = " Météo à " + ville + "\n" +
							"- Température : " + temperature + "°C\n" +
							"- Ciel : " + main + " (" + description + ")";

					sendMessage(chatId, message);
				} else {
					sendMessage(chatId, " Ville non trouvée.");
				}
			} catch (Exception e) {
				sendMessage(chatId, "⚠ Erreur lors de la récupération de la météo.");
			}

		} else if (messageText.toLowerCase().startsWith("blague")) {
			try {
				String url = "http://localhost:9090/api/v1/joke/random";

				if (messageText.toLowerCase().contains("nulle") || messageText.toLowerCase().contains("mauvaise")) {
					url += "?quality=bad";
				} else if (messageText.toLowerCase().contains("bonne") || messageText.toLowerCase().contains("excellente")) {
					url += "?quality=good";
				}

				Joke joke = restTemplate.getForObject(url, Joke.class);

				if (joke != null) {
					String message = "**" + joke.getTitre() + "**\n\n" +
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
		} else {
			sendMessage(chatId, "Commande inconnue. Essayez par exemple : Météo Paris ou Blague");
		}

	}

	private void sendMessage(String chatId, String text) {
		if (text == null || text.trim().isEmpty()) {
			System.out.println(" Message vide, rien envoyé à Telegram.");
			return;
		}

		String url = TELEGRAM_API + "/sendMessage";

		RestTemplate restTemplate = new RestTemplate();

		// Prépare la requête avec les bons noms de champs attendus par Telegram
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
