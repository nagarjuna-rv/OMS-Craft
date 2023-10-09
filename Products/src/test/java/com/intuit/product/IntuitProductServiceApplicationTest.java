package com.intuit.product;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
public class IntuitProductServiceApplicationTest {

    @Test
    public void contextLoads() {
        //Just for Coverage
        IntuitProductServiceApplication.main(new String[]{});
    }

}