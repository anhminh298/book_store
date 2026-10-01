package com.nhm.bookstore.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CsrfFilterTest {
    @Test
    void rejectsMissingAndWrongTokens() {
        assertFalse(CsrfFilter_24162073.matches(null, null));
        assertFalse(CsrfFilter_24162073.matches("expected", null));
        assertFalse(CsrfFilter_24162073.matches("expected", "wrong"));
        assertTrue(CsrfFilter_24162073.matches("expected", "expected"));
    }
}
