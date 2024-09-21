package com.example.myrestapi.controller;

import com.example.myrestapi.dto.MyReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/myendpoint")
public class MyRestController {

    @PostMapping
    @Operation(summary = "Process MyReq Data", responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request (Validation Error)", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<?> processMyReq(@RequestBody @Valid MyReqDTO myReqDTO) {
        // ... your processing logic ...

        return ResponseEntity.ok("MyReq data processed successfully!");
    }
}


package com.example.myrestapi.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
public class MyReqDTO {

    @Pattern(regexp = "^\\d{8}$", message = "Date must be in YYYYMMDD format")
    private String date;

    private String opDesc;

    @Size(max = 32)
    private String refNum;

    @Pattern(regexp = "^\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}$", message = "Invalid version format")
    private String mver;

    @Pattern(regexp = "^(0[1-9]|99)$", message = "Invalid opCat value")
    private String opCat;

    @Size(max = 255)
    private String severity;

    @Pattern(regexp = "^https://.+", message = "URL must start with 'https://'")
    @NotBlank
    private String url;

    @Valid
    @NotNull
    private TransRef transRef;
}

@Data
class TransRef {
    @Pattern(regexp = "^(0[1-3])$", message = "Invalid type value")
    private String type;

    @Size(max = 32)
    private String id;
}


<dependencies>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.7.0</version>
    </dependency>
	<dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-security</artifactId>
        <version>1.6.15</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>


package com.example.myrestapi.controller;

import com.example.myrestapi.dto.MyReqDTO;
import com.example.myrestapi.dto.TransRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyRestController.class)
class MyRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessMyReq_success() throws Exception {
        MyReqDTO validRequest = createValidRequest();
        mockMvc.perform(post("/myendpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("MyReq data processed successfully!"));
    }

    // Negative Test Cases

    @Test
    void testProcessMyReq_invalidDate() throws Exception {
        MyReqDTO invalidDateRequest = createValidRequest();
        invalidDateRequest.setDate("202311");
        testBadRequest(invalidDateRequest, "Date must be in YYYYMMDD format");
    }

    @Test
    void testProcessMyReq_invalidMver() throws Exception {
        MyReqDTO invalidMverRequest = createValidRequest();
        invalidMverRequest.setMver("1.2");
        testBadRequest(invalidMverRequest, "Invalid version format");
    }

    @Test
    void testProcessMyReq_invalidUrl() throws Exception {
        MyReqDTO invalidUrlRequest = createValidRequest();
        invalidUrlRequest.setUrl("http://example.com"); // Missing https://
        testBadRequest(invalidUrlRequest, "URL must start with 'https://'");
    }

    @Test
    void testProcessMyReq_invalidOpCat() throws Exception {
        MyReqDTO invalidOpCatRequest = createValidRequest();
        invalidOpCatRequest.setOpCat("10");  // Outside allowed range
        testBadRequest(invalidOpCatRequest, "Invalid opCat value");
    }

    @Test
    void testProcessMyReq_missingRequiredFields() throws Exception {
        MyReqDTO missingFieldsRequest = new MyReqDTO(); // Empty object
        testBadRequest(missingFieldsRequest, "date: must not be null"); // First missing field's error
    }

    @Test
    void testProcessMyReq_invalidTransRefType() throws Exception {
        MyReqDTO invalidTransRefRequest = createValidRequest();
        invalidTransRefRequest.getTransRef().setType("04"); // Invalid type
        testBadRequest(invalidTransRefRequest, "transRef.type: Invalid type value");
    }

    @Test
    void testProcessMyReq_missingTransRef() throws Exception {
        MyReqDTO missingTransRefRequest = createValidRequest();
        missingTransRefRequest.setTransRef(null);
        testBadRequest(missingTransRefRequest, "transRef: must not be null");
    }

    @Test
    void testProcessMyReq_invalidRefNumLength() throws Exception {
        MyReqDTO invalidRefNumRequest = createValidRequest();
        invalidRefNumRequest.setRefNum("ThisRefNumIsTooLongForTheValidationRule");
        testBadRequest(invalidRefNumRequest, "refNum: size must be between 0 and 32");
    }
    
    @Test
    void testProcessMyReq_invalidSeverityLength() throws Exception {
        MyReqDTO invalidSeverityRequest = createValidRequest();
        invalidSeverityRequest.setSeverity("ThisSeverityIsWayTooLongForThe255CharacterLimit");
        testBadRequest(invalidSeverityRequest, "severity: size must be between 0 and 255");
    }
    
    @Test
    void testProcessMyReq_invalidTransRefIdLength() throws Exception {
        MyReqDTO invalidTransRefIdRequest = createValidRequest();
        invalidTransRefIdRequest.getTransRef().setId("ThisTransRefIdIsTooLongForTheValidationRule");
        testBadRequest(invalidTransRefIdRequest, "transRef.id: size must be between 0 and 32");
    }

    private MyReqDTO createValidRequest() {
        MyReqDTO request = new MyReqDTO();
        request.setDate("20231231");
        request.setOpDesc("Sample Description");
        request.setRefNum("REF12345"); 
        request.setMver("1.2.3");
        request.setOpCat("01");
        request.setSeverity("High");
        request.setUrl("https://www.example.com");
        TransRef transRef = new TransRef();
        transRef.setType("01");
        transRef.setId("1234567890");
        request.setTransRef(transRef);
        return request;
    }

    private void testBadRequest(MyReqDTO request, String expectedErrorMessage) throws Exception {
        mockMvc.perform(post("/myendpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage)); 
    }
	
	@Test
void testProcessMyReq_invalidOpDescLength() throws Exception {
    MyReqDTO invalidOpDescRequest = createValidRequest();
    invalidOpDescRequest.setOpDesc(generateStringOfLength(20001)); 
    testBadRequest(invalidOpDescRequest, "opDesc: size must be between 0 and 20000");
}

private String generateStringOfLength(int length) {
    return "a".repeat(Math.max(0, length));
}
}


<build>
    <plugins>
        <plugin>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-maven-plugin</artifactId>
            <version>1.5.10</version>
            <executions>
                <execution>
                    <id>integration-tests</id>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <outputDir>${project.build.directory}/generated-docs</outputDir>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

	
openapi: 3.0.1
info:
  title: My API
  description: API for MyReq processing
  version: 1.0.0
paths:
  /myendpoint:
    post:
      summary: Process MyReq Data
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/MyReqDTO'
        required: true
      responses:
        '200':
          description: Successful operation
        '400':
          description: Bad Request (Validation Error)
components:
  schemas:
    MyReqDTO:
      type: object
      properties:
        date:
          type: string
          example: "20231231"
        opDesc:
          type: string
        refNum:
          type: string
        mver:
          type: string
          example: "1.2.3"
        opCat:
          type: string
          example: "01"
        severity:
          type: string
        url:
          type: string
          example: "https://www.example.com"
        transRef:
          $ref: '#/components/schemas/TransRef'
    TransRef:
      type: object
      properties:
        type:
          type: string
          example: "01"
        id:
          type: string
          example: "1234567890"

http://localhost:8080/swagger-ui.html
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true

springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.enabled=true
springdoc.api-docs.groups.enabled=true
springdoc.api-docs.group-configs[0].group=mygroup
springdoc.api-docs.group-configs[0].paths-to-match=/**
springdoc.api-docs.group-configs[0].resources[0].url=/openapi.yaml
springdoc.api-docs.group-configs[0].resources[0].type=OpenAPI_30
springdoc.api-docs.group-configs[0].resources[0].classpath=classpath:openapi.yaml
