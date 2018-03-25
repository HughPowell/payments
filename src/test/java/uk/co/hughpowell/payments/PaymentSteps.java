package uk.co.hughpowell.payments;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cucumber.api.java8.En;
import uk.co.hughpowell.payments.repository.PaymentsRepository;

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

	@cucumber.api.java.Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		result = null;
		paymentsRepository.empty();
	}
	
	public PaymentSteps() {
		
		Given("^that ([A-Z][a-z]*) has made a payment to ([A-Z][a-z]*) for (\\d+) pounds$",
				(String personFrom, String personTo, Integer amount) -> {
			payment = Payment.create(personFrom, personTo, amount);
			result = mockMvc.perform(post("/payments")
					.content(payment.toString())
					.contentType(contentType))
					.andExpect(status().isCreated())
					.andReturn();
		});
		
		Given("^that ([A-Z][a-z]*) has made a payment to ([A-Z][a-z]*)$",
				(String personFrom, String personTo) -> {
			payment = Payment.create(personFrom, personTo);
			result = mockMvc.perform(post("/payments")
					.content(payment.toString())
					.contentType(contentType))
					.andExpect(status().isCreated())
					.andReturn();
				});
		
		When("^([A-Z][a-z]*) makes a payment to ([A-Z][a-z]*)$",
				(String personFrom, String personTo) -> {
			payment = Payment.create(personFrom, personTo);
			result = mockMvc.perform(post("/payments")
					.content(payment.toString())
					.contentType(contentType))
					.andExpect(status().isCreated())
					.andReturn();
		});

		When("^the payment ([A-Z][a-z]*) wishes to fetch does not exist",
				(String person) -> {
			String paymentId = UUID.randomUUID().toString();
			String path = String.format("/payments/%s", paymentId);
			result = mockMvc.perform(get(path)
					.contentType(contentType))
					.andReturn();
		});

		When("^she updates it to (\\d+) pounds$", (Integer amount) -> {
			JsonNode updatedPayment = Payment.updateAmount(payment, amount);
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(put(new URI(paymentLocation))
					.content(updatedPayment.toString())
					.contentType(contentType))
					.andExpect(status().isNoContent());
		});
		
		When("^she deletes it$", () -> {
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(delete(new URI(paymentLocation))
					.contentType(contentType))
					.andExpect(status().isNoContent());
		});
		
		When("^([A-Z][a-z]*) updates a non-existant payment$",
				(String personFrom) -> {
			JsonNode payment = Payment.create("Alice", "Bob");
			String paymentId = UUID.randomUUID().toString();
			String path = String.format("/payments/%s", paymentId);
			result = mockMvc.perform(put(path)
					.content(payment.toString())
					.contentType(contentType))
					.andReturn();
		});
		
		When("^([A-Z][a-z]*) makes (\\d+) payments$",
				(String personFrom, Integer numberOfPayments) -> {
					for (int index = 0; index < numberOfPayments; ++index) {
						JsonNode payment = Payment.create(personFrom, "OtherPerson" + index); 
						mockMvc.perform(post("/payments")
								.content(payment.toString())
								.contentType(contentType))
								.andExpect(status().isCreated());
					}
				});
		
		Then("^they are able to fetch that payment$", () -> {
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(get(new URI(paymentLocation))
					.contentType(contentType))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.payment.id", is(payment.get("id").asText())))
			.andExpect(jsonPath("$.payment.from", is(payment.get("from").asText())))
			.andExpect(jsonPath("$.payment.to", is(payment.get("to").asText())));
		});
		
		Then("^she gets an error saying the payment does not exist$", () -> {
			assert(result.getResponse().getStatus() == HttpStatus.NOT_FOUND.value());
		});
		
		Then("^she should see (\\d+) pounds when she fetches the payment",
				(Integer amount) -> {
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(get(new URI(paymentLocation))
					.contentType(contentType))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.payment.amount", is(amount)));
		});
		
		Then("she should no longer be able to fetch it$", () -> {
			String paymentLocation = result.getResponse().getHeader("Location");
			mockMvc.perform(get(new URI(paymentLocation))
					.contentType(contentType))
					.andExpect(status().isNotFound());
		});
		
		Then("she gets an error indicating there is a conflict", () -> {
			assert(result.getResponse().getStatus() == HttpStatus.CONFLICT.value());
		});
		
		Then("^she should be able to fetch a list of the (\\d+) of them",
				(Integer numberOfPayments) -> {
			MvcResult result = mockMvc.perform(get("/payments")
					.contentType(contentType))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content", hasSize(3)))
					.andReturn();
		});
	}
}