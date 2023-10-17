

public class CustomApplicationException extends RuntimeException {
    private String errorCode;

    public CustomApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


public class ErrorMapping {
    private String masterCode;
    private String message;

    public ErrorMapping(String masterCode, String message) {
        this.masterCode = masterCode;
        this.message = message;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public String getMessage() {
        return message;
    }
}

public class ErrorResponse {
    private String errorCode;
    private String message;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

@Configuration
public class ErrorMappingConfig {

    @Bean
    public Map<String, ErrorMapping> errorMappings() {
        Map<String, ErrorMapping> mappings = new HashMap<>();
        mappings.put("EXCEPTION_CODE_1", new ErrorMapping("MASTER_CODE_1", "Message for Exception 1"));
        mappings.put("EXCEPTION_CODE_2", new ErrorMapping("MASTER_CODE_2", "Message for Exception 2"));
        return mappings;
    }
}



@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private Map<String, ErrorMapping> errorMappings;

    @ExceptionHandler(CustomApplicationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomApplicationException ex) {
        String exceptionCode = ex.getErrorCode();
        ErrorMapping errorMapping = errorMappings.get(exceptionCode);

        if (errorMapping != null) {
            logErrorDetails(ex, errorMapping.getMasterCode(), errorMapping.getMessage());
            return new ResponseEntity<>(new ErrorResponse(errorMapping.getMasterCode(), errorMapping.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            logErrorDetails(ex, "UNMAPPED_EXCEPTION", "An unmapped error occurred.");
            return new ResponseEntity<>(new ErrorResponse("UNMAPPED_EXCEPTION", "An unmapped error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void logErrorDetails(Throwable ex, String errorCode, String errorMessage) {
        // Implement your logging mechanism to log error details (error code, message, status code, stack trace)
    }
}
