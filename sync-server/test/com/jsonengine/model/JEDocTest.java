package com.jsonengine.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;

import com.jsonengine.model.JEDoc;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class JEDocTest extends AppEngineTestCase {

    private JEDoc model = new JEDoc();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
