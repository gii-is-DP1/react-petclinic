package org.springframework.samples.petclinic.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(Collection<ObjectError> errors) {       
        super(errors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(",")));
    }

    public BadRequestException(String message) {
        super(message);
    }
}
