package com.lambdaschool.schools.handlers;

        import com.lambdaschool.schools.exceptions.ResourceFoundException;
        import com.lambdaschool.schools.exceptions.ResourceNotFoundException;
        import com.lambdaschool.schools.models.ErrorDetail;
        import com.lambdaschool.schools.services.HelperFunctions;
        import org.springframework.beans.ConversionNotSupportedException;
        import org.springframework.beans.TypeMismatchException;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.core.Ordered;
        import org.springframework.core.annotation.Order;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.http.converter.HttpMessageNotReadableException;
        import org.springframework.http.converter.HttpMessageNotWritableException;
        import org.springframework.validation.BindException;
        import org.springframework.web.HttpMediaTypeNotAcceptableException;
        import org.springframework.web.HttpMediaTypeNotSupportedException;
        import org.springframework.web.HttpRequestMethodNotSupportedException;
        import org.springframework.web.bind.MethodArgumentNotValidException;
        import org.springframework.web.bind.MissingPathVariableException;
        import org.springframework.web.bind.MissingServletRequestParameterException;
        import org.springframework.web.bind.ServletRequestBindingException;
        import org.springframework.web.bind.annotation.ExceptionHandler;
        import org.springframework.web.bind.annotation.RestControllerAdvice;
        import org.springframework.web.context.request.WebRequest;
        import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
        import org.springframework.web.multipart.support.MissingServletRequestPartException;
        import org.springframework.web.servlet.NoHandlerFoundException;
        import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

        import java.util.Arrays;
        import java.util.Date;

/**
 * This is the driving class when an exception occurs. All exceptions are handled here.
 * This class is shared across all controllers due to the annotation RestControllerAdvice;
 * this class gives advice to all controllers on how to handle exceptions.
 * Due to the annotation Order(Ordered.HIGHEST_PRECEDENCE), this class takes precedence over all other controller advisors.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler
        extends ResponseEntityExceptionHandler
{
    /**
     * Connects this class with the Helper Functions
     */
    @Autowired
    private HelperFunctions helperFunctions;

    /**
     * The constructor for the RestExceptionHandler. Currently we do not do anything special. We just call the parent constructor.
     */
    public RestExceptionHandler()
    {
        super();
    }

    /**
     * Our custom handling of ResourceNotFoundExceptions. This gets thrown manually by our application.
     *
     * @param rnfe All the information about the exception that is thrown.
     * @return The error details for displaying to the client plus the status Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException rnfe)
    {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date());
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle("Resource Not Found");
        errorDetail.setDetail(rnfe.getMessage());
        errorDetail.setDeveloperMessage(rnfe.getClass()
                .getName());
        errorDetail.setErrors(helperFunctions.getConstraintViolation(rnfe));

        return new ResponseEntity<>(errorDetail,
                null,
                HttpStatus.NOT_FOUND);
    }

    /**
     * Our custom handling of ResourceFoundExceptions. This gets thrown manually by our application.
     *
     * @param rfe All the information about the exception that is thrown.
     * @return The error details for displaying to the client plus the status Bad Request.
     */
    @ExceptionHandler(ResourceFoundException.class)
    public ResponseEntity<?> handleResourceFoundException(ResourceFoundException rfe)
    {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("Unexpected Resource");
        errorDetail.setDetail(rfe.getMessage());
        errorDetail.setDeveloperMessage(rfe.getClass()
                .getName());
        errorDetail.setErrors(helperFunctions.getConstraintViolation(rfe));

        return new ResponseEntity<>(errorDetail,
                null,
                HttpStatus.BAD_REQUEST);
    }

    /**
     * All other exceptions not handled elsewhere are handled by this method.
     *
     * @param ex      The actual exception used to get error messages
     * @param body    The body of this request. Not used in this method.
     * @param headers Headers that are involved in this request. Not used in this method.
     * @param status  The Http Status generated by the exception. Forwarded to the client.
     * @param request The request that was made by the client. Not used in this method.
     * @return The error details to display to the client plus the status that from the exception.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request)
    {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(new Date());
        errorDetail.setStatus(status.value());
        errorDetail.setTitle("Rest Internal Exception");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass()
                .getName());
        errorDetail.setErrors(helperFunctions.getConstraintViolation(ex));

        return new ResponseEntity<>(errorDetail,
                null,
                status);
    }
}
