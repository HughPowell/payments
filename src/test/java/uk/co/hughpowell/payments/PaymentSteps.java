package uk.co.hughpowell.payments;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cucumber.api.java8.En;

public class PaymentSteps extends StepsAbstractClass implements En {
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private PaymentsRepository paymentsRepository;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	private MvcResult result;
	
	private JsonNode payment;

	final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
	
	final ObjectMapper mapper = new ObjectMapper();
	
	private JsonNode createPayment(String from, String to) {
		Map<String, JsonNode> paymentProperties = new HashMap<String, JsonNode>();
		JsonNode jsonUUID = jsonFactory.textNode(UUID.randomUUID().toString());
		paymentProperties.put("id", jsonUUID);
		paymentProperties.put("from", jsonFactory.textNode(from));
		paymentProperties.put("to", jsonFactory.textNode(to));
		return jsonFactory.objectNode().setAll(paymentProperties);
	}
	
	@cucumber.api.java.Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		result = null;
	}

	public PaymentSteps() {
		When("^([A-Z][a-z]*) makes a payment to ([A-Z][a-z]*)$", (String personFrom, String personTo) -> {
			payment = createPayment(personFrom, personTo);
			result = mockMvc.perform(post("/payments")
					.content(payment.toString())
					.contentType(contentType))
					.andExpect(status().isCreated())
					.andReturn();
		});

		When("^The payment ([A-Z][a-z]*) wishes to view does not exist", (String person) -> {
			String paymentId = UUID.randomUUID().toString();
			result = mockMvc.perform(get(String.format("/payments/%s", paymentId))
					.contentType(contentType))
					.andReturn();
		});

		Then("^They are able to view that payment$", () -> {
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(get(new URI(paymentLocation))
					.contentType(contentType))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.payment.id", is(payment.get("id").asText())))
			.andExpect(jsonPath("$.payment.from", is(payment.get("from").asText())))
			.andExpect(jsonPath("$.payment.to", is(payment.get("to").asText())));
		});
		
		Then("^She gets an error saying the payment does not exist$", () -> {
			assert(result.getResponse().getStatus() == HttpStatus.NOT_FOUND.value());
		});
	}
}