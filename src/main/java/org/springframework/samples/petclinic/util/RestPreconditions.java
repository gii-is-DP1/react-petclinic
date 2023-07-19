package org.springframework.samples.petclinic.util;

import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;

public final class RestPreconditions {
	
	private RestPreconditions() {
        throw new AssertionError();
    }

    // API

//    /**
//     * Check if some value was found, otherwise throw exception.
//     * 
//     * @param expression
//     *            has value true if found, otherwise false
//     * @throws MyResourceNotFoundException
//     *             if expression is false, means value not found.
//     */
//    public static void checkFound(final boolean expression) {
//        if (!expression) {
//            throw new ResourceNotFoundException();
//        }
//    }
//
//    /**
//     * Check if some value was found, otherwise throw exception.
//     * 
//     * @param expression
//     *            has value true if found, otherwise false
//     * @throws MyResourceNotFoundException
//     *             if expression is false, means value not found.
//     */
//    public static <T> T checkFound(final T resource) {
//        if (resource == null) {
//            throw new ResourceNotFoundException();
//        }
//
//        return resource;
//    }
    
    public static <T> T checkNotNull(final T resource,String resourceName, String fieldName, Object fieldValue) {
        if (resource == null) {
            throw new ResourceNotFoundException(resourceName, fieldName, fieldValue);
        }

        return resource;
    }
}
