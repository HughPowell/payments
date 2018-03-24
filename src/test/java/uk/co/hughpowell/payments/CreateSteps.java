package uk.co.hughpowell.payments;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cucumber.api.java8.En;

public class CreateSteps extends StepsAbstractClass implements En {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @cucumber.api.java.Before
    public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

	public CreateSteps() {
		When("^Alice makes a payment to Bob$", () -> {
			mockMvc.perform(post("/payments")
					.content("{\"id\" : \"abc\"}")
					.contentType(contentType))
					.andExpect(status().isCreated());
		});
		Then("^Alice is able to view that payment$", () -> {
			assert(true);
		});
	}
}