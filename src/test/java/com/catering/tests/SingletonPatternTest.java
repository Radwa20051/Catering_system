package com.catering.tests;

import com.catering.patterns.singleton.CateringManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class SingletonPatternTest {
    @Test
    void returnsSameManagerInstance() {
        assertSame(CateringManager.getInstance(), CateringManager.getInstance());
    }
}
