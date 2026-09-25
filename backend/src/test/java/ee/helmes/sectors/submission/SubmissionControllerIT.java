package ee.helmes.sectors.submission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ee.helmes.sectors.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

class SubmissionControllerIT extends AbstractIntegrationTest {

  @BeforeEach
  void cleanDatabase() {
    jdbcTemplate.update("delete from submission");
  }

  @Test
  void newSessionHasNoSubmission() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc.perform(get("/api/submission").session(session)).andExpect(status().isNoContent());
  }

  @Test
  void emptyBodyFailsAllFieldValidations() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").value("Name is required"))
        .andExpect(jsonPath("$.errors.sectorIds").value("Select at least one sector"))
        .andExpect(jsonPath("$.errors.agreeToTerms").value("You must agree to the terms"));
  }

  @Test
  void blankNameFails() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"   \",\"sectorIds\":[1],\"agreeToTerms\":true}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").value("Name is required"));
  }

  @Test
  void tooLongNameFails() throws Exception {
    MockHttpSession session = new MockHttpSession();
    String longName = "a".repeat(101);
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + longName + "\",\"sectorIds\":[1],\"agreeToTerms\":true}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").value("Name must be at most 100 characters"));
  }

  @Test
  void notAgreeingToTermsFails() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"sectorIds\":[1],\"agreeToTerms\":false}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.agreeToTerms").value("You must agree to the terms"));
  }

  @Test
  void unknownSectorIdFails() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"sectorIds\":[99999],\"agreeToTerms\":true}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.sectorIds").value("Unknown sector selected"));
  }

  @Test
  void malformedJsonFails() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));
  }

  @Test
  void validSaveTrimsNameSortsSectorsAndStoresOneRow() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  Alice  \",\"sectorIds\":[19,1],\"agreeToTerms\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.sectorIds[0]").value(1))
        .andExpect(jsonPath("$.sectorIds[1]").value(19));

    Integer count = jdbcTemplate.queryForObject("select count(*) from submission", Integer.class);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void sameSessionRefillsAndEditsInPlace() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"sectorIds\":[1],\"agreeToTerms\":true}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/submission").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.sectorIds[0]").value(1));

    mockMvc
        .perform(
            put("/api/submission")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice Smith\",\"sectorIds\":[75],\"agreeToTerms\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Alice Smith"))
        .andExpect(jsonPath("$.sectorIds[0]").value(75));

    Integer count = jdbcTemplate.queryForObject("select count(*) from submission", Integer.class);
    assertThat(count).isEqualTo(1);

    Integer joinCount =
        jdbcTemplate.queryForObject("select count(*) from submission_sector", Integer.class);
    assertThat(joinCount).isEqualTo(1);
  }

  @Test
  void differentSessionsCreateSeparateRowsAndKeepOwnData() throws Exception {
    MockHttpSession sessionA = new MockHttpSession();
    MockHttpSession sessionB = new MockHttpSession();

    mockMvc
        .perform(
            put("/api/submission")
                .session(sessionA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"sectorIds\":[1],\"agreeToTerms\":true}"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            put("/api/submission")
                .session(sessionB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bob\",\"sectorIds\":[2],\"agreeToTerms\":true}"))
        .andExpect(status().isOk());

    Integer count = jdbcTemplate.queryForObject("select count(*) from submission", Integer.class);
    assertThat(count).isEqualTo(2);

    mockMvc
        .perform(get("/api/submission").session(sessionA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Alice"));
  }
}
