package ee.helmes.sectors.sector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.helmes.sectors.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

class SectorControllerIT extends AbstractIntegrationTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void returnsAllSectorsWithManufacturingFirst() throws Exception {
    mockMvc
        .perform(get("/api/sectors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(79))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Manufacturing"))
        .andExpect(jsonPath("$[0].depth").value(0));
  }

  @Test
  void buildsCorrectDepthsAndPreOrder() throws Exception {
    String body =
        mockMvc
            .perform(get("/api/sectors"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JsonNode sectors = objectMapper.readTree(body);

    assertThat(depthOf(sectors, 75)).isEqualTo(3);
    assertThat(depthOf(sectors, 2)).isEqualTo(0);

    int maritimeIndex = indexOf(sectors, 97);
    assertThat(sectors.get(maritimeIndex + 1).get("id").asInt()).isEqualTo(271);
  }

  private static int depthOf(JsonNode sectors, int id) {
    return sectors.get(indexOf(sectors, id)).get("depth").asInt();
  }

  private static int indexOf(JsonNode sectors, int id) {
    for (int i = 0; i < sectors.size(); i++) {
      if (sectors.get(i).get("id").asInt() == id) {
        return i;
      }
    }
    throw new AssertionError("sector " + id + " not found");
  }
}
