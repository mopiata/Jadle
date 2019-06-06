package models;

import org.junit.*;

import static org.junit.Assert.*;

public class FoodtypeTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getName() {
        Foodtype testFoodtype = setupFoodtype();
        assertEquals("dessert", testFoodtype.getName());
    }

    @Test
    public void setName() {
        Foodtype testFoodtype = setupFoodtype();
        testFoodtype.setName("breakfast");
        assertNotEquals("dessert", testFoodtype.getName());
    }

    @Test
    public void setId() {
        Foodtype testFoodtype = setupFoodtype();
        testFoodtype.setId(5);
        assertEquals(5, testFoodtype.getId());
    }

    // helper
    public Foodtype setupFoodtype(){
        return new Foodtype("dessert");
    }
}