package com.ferry.framework.web.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FerryBusinessExceptionTest {

    @Test
    void constructor_setsCodeAndMessage() {
        FerryBusinessException ex = new FerryBusinessException(404, "not found");

        assertEquals(404, ex.getCode());
        assertEquals("not found", ex.getMessage());
    }
}
