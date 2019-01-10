package fi.chop.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGameObject {

    private static final float EPSILON = 0.000001f;
    private GameObject object;

    @Before
    public void setUp() {
        object = new GameObject() {
            @Override
            public void update(float delta) { }

            @Override
            public void render(SpriteBatch batch) { }
        };
    }

    @Test
    public void testDefaultValues() {
        assertEquals(0, object.getX(), EPSILON);
        assertEquals(0, object.getY(), EPSILON);
        assertEquals(0, object.getRotationRad(), EPSILON);
        assertEquals(0, object.getRotationDeg(), EPSILON);
    }

    @Test
    public void testSetPosition() {
        object.setPosition(-1, -1);
        assertEquals(-1, object.getX(), EPSILON);
        assertEquals(-1, object.getY(), EPSILON);
        object.setPosition(1, 1);
        assertEquals(1, object.getX(), EPSILON);
        assertEquals(1, object.getY(), EPSILON);
    }

    @Test
    public void testSetX() {
        object.setX(-1);
        assertEquals(-1, object.getX(), EPSILON);
        object.setX(1);
        assertEquals(1, object.getX(), EPSILON);
    }

    @Test
    public void testSetY() {
        object.setY(-1);
        assertEquals(-1, object.getY(), EPSILON);
        object.setY(1);
        assertEquals(1, object.getY(), EPSILON);
    }

    @Test
    public void testSetRotationDeg() {
        object.setRotationDeg(45);
        assertEquals(45, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI / 4, object.getRotationRad(), EPSILON);
    }

    @Test
    public void testSetRotationDegNegative() {
        object.setRotationDeg(-90);
        assertEquals(270, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI * 3 / 2, object.getRotationRad(), EPSILON);
    }

    @Test
    public void testSetRotationDegTooBig() {
        object.setRotationDeg(450);
        assertEquals(90, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI / 2, object.getRotationRad(), EPSILON);
    }

    @Test
    public void testSetRotationRad() {
        object.setRotationRad(Math.PI / 2);
        assertEquals(90, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI / 2, object.getRotationRad(), EPSILON);
    }

    @Test
    public void testSetRotationRadNegative() {
        object.setRotationRad(-Math.PI / 2);
        assertEquals(270, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI * 3 / 2, object.getRotationRad(), EPSILON);
    }

    @Test
    public void testSetRotationRadTooBig() {
        object.setRotationRad(3 * Math.PI);
        assertEquals(180, object.getRotationDeg(), EPSILON);
        assertEquals(Math.PI, object.getRotationRad(), EPSILON);
    }
}
