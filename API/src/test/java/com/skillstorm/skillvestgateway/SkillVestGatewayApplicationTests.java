package com.skillstorm.skillvestgateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SkillVestGatewayApplicationTests {

	private WebTestClient webTestClient;

	private WireMockServer wireMockServer;

	@BeforeAll
	public void setUp() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();

		// Configure mock responses in WireMock
		configureFor("localhost", wireMockServer.port());
		stubFor(get(urlEqualTo("/planner"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.valueOf(new ArrayList<>()))));

		stubFor(post(urlEqualTo("/planner/create"))
				.willReturn(aResponse()
						.withStatus(201)
						.withHeader("Content-Type", "application/json")));

		stubFor(put(urlEqualTo("/planner/update"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")));

		stubFor(delete(urlEqualTo("/planner/delete"))
				.willReturn(aResponse()
						.withStatus(204)
						.withHeader("Content-Type", "application/json")));

		//////////////////////////////////////////////////////////////////////

		stubFor(get(urlEqualTo("/goals"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.valueOf(new ArrayList<>()))));

		stubFor(post(urlEqualTo("/goals/create"))
				.willReturn(aResponse()
						.withStatus(201)
						.withHeader("Content-Type", "application/json")));

		stubFor(put(urlEqualTo("/goals/update"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")));

		stubFor(delete(urlEqualTo("/goals/delete"))
				.willReturn(aResponse()
						.withStatus(204)
						.withHeader("Content-Type", "application/json")));

		//////////////////////////////////////////////////////////////////////

		stubFor(get(urlEqualTo("/accounts"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.valueOf(new ArrayList<>()))));

		stubFor(post(urlEqualTo("/accounts/newAccounts"))
				.willReturn(aResponse()
						.withStatus(201)
						.withHeader("Content-Type", "application/json")));

		stubFor(put(urlEqualTo("/accounts/updateAccounts"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")));

		stubFor(delete(urlEqualTo("/accounts/deleteAccounts"))
				.willReturn(aResponse()
						.withStatus(204)
						.withHeader("Content-Type", "application/json")));

		//////////////////////////////////////////////////////////////////////

		stubFor(get(urlEqualTo("/investments/email/s@s.com"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(String.valueOf(new ArrayList<>()))));

		stubFor(post(urlEqualTo("/investments/new"))
				.willReturn(aResponse()
						.withStatus(201)
						.withHeader("Content-Type", "application/json")));

		stubFor(put(urlEqualTo("/investments/update"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")));

		stubFor(delete(urlEqualTo("/investments/delete"))
				.willReturn(aResponse()
						.withStatus(204)
						.withHeader("Content-Type", "application/json")));


		webTestClient = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + wireMockServer.port())  // Use WireMock's port
				.build();
	}

	@AfterAll
	public void tearDown() {
		wireMockServer.stop();
	}

	//JUNIT-MODULE-003
	@Test
	@WithMockUser
	public void plannerGetRoute() {
		// Perform your test using the configured webTestClient
		webTestClient.get()
				.uri("/planner")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json(String.valueOf(new ArrayList<>()));
	}

	//JUNIT-MODULE-004
	@Test
	@WithMockUser
	public void plannerPostRoute() {
		webTestClient.post()
				.uri("/planner/create")  // Replace with your actual route
				.exchange()
				.expectStatus().isCreated();
	}


	//JUNIT-MODULE-005
	@Test
	@WithMockUser
	public void plannerPutRoute() {
		webTestClient.put()
				.uri("/planner/update")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-006
	@Test
	@WithMockUser
	public void plannerDeleteRoute() {
		webTestClient.delete()
				.uri("/planner/delete")  // Replace with your actual route
				.exchange()
				.expectStatus().isNoContent();
	}

	//JUNIT-MODULE-007
	@Test
	@WithMockUser
	public void goalsGetRoute() {
		// Perform your test using the configured webTestClient
		webTestClient.get()
				.uri("/goals")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json(String.valueOf(new ArrayList<>()));
	}

	//JUNIT-MODULE-008
	@Test
	@WithMockUser
	public void goalsPostRoute() {
		webTestClient.post()
				.uri("/goals/create")  // Replace with your actual route
				.exchange()
				.expectStatus().isCreated();
	}


	//JUNIT-MODULE-009
	@Test
	@WithMockUser
	public void goalsPutRoute() {
		webTestClient.put()
				.uri("/goals/update")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-010
	@Test
	@WithMockUser
	public void goalsDeleteRoute() {
		webTestClient.delete()
				.uri("/goals/delete")  // Replace with your actual route
				.exchange()
				.expectStatus().isNoContent();
	}

	//JUNIT-MODULE-011
	@Test
	@WithMockUser
	public void accountsGetRoute() {
		webTestClient.get()
				.uri("/accounts")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-012
	@Test
	@WithMockUser
	public void accountsPostRoute() {
		webTestClient.post()
				.uri("/accounts/newAccounts")  // Replace with your actual route
				.exchange()
				.expectStatus().isCreated();
	}

	//JUNIT-MODULE-013
	@Test
	@WithMockUser
	public void accountsPutRoute() {
		webTestClient.put()
				.uri("/accounts/updateAccounts")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-014
	@Test
	@WithMockUser
	public void accountsDeleteRoute() {
		webTestClient.delete()
				.uri("/accounts/deleteAccounts")  // Replace with your actual route
				.exchange()
				.expectStatus().isNoContent();

	}

	//JUNIT-MODULE-015
	@Test
	@WithMockUser
	public void investmentsGetRoute() {
		webTestClient.get()
				.uri("/investments/email/s@s.com")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-016
	@Test
	@WithMockUser
	public void investmentsPostRoute() {
		webTestClient.post()
				.uri("/investments/new")  // Replace with your actual route
				.exchange()
				.expectStatus().isCreated();
	}

	//JUNIT-MODULE-017
	@Test
	@WithMockUser
	public void investmentsPutRoute() {
		webTestClient.put()
				.uri("/investments/update")  // Replace with your actual route
				.exchange()
				.expectStatus().isOk();
	}

	//JUNIT-MODULE-018
	@Test
	@WithMockUser
	public void investmentsDeleteRoute() {
		webTestClient.delete()
				.uri("/investments/delete")  // Replace with your actual route
				.exchange()
				.expectStatus().isNoContent();

	}



}
