package fr.ensim.interop.introrest.client;

import org.springframework.web.client.RestTemplate;

public class ClientRestTest {

	public static final String ENDPOINT = "http://localhost:9090/meteo?ville=Paris";

	public static void main(String[] args) {

		RestTemplate restTemplate = new RestTemplate();

		try {
			// Appel GET simple vers l'endpoint
			String response = restTemplate.getForObject(ENDPOINT, String.class);

			System.out.println("Réponse reçue :");
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}