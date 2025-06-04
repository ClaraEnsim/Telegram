package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.weather.Discussion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MessageRestController {
	
	@Value("7340987230:AAEgslTT6jjFdmObq0YF5EdcJXX5wn07jSw")
	private String telegramBotToken;

	@PostMapping("/sendMessage")
	public ResponseEntity<String> sendMessage(@RequestBody Discussion discussion) {
		String chatId = discussion.getChatId();
		String text = discussion.getTexte();

		if (text == null || text.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Erreur : le message à envoyer est vide.");
		}

		String telegramApiUrl = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("chat_id", chatId);
		requestBody.put("text", text);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(telegramApiUrl, request, String.class);
			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
		} catch (Exception e) {
			e.printStackTrace(); // Pour voir l’erreur dans la console
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erreur lors de l’envoi du message à Telegram : " + e.getMessage());
		}
	}


}
