package com.demo.model.brick;

import com.demo.model.Ball;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ball-Brick Collision Tests")
class BallBrickCollisionTest {

    private static final int BALL_RADIUS = 10;

    private static class TestBall extends Ball {
        public TestBall(double centerX, double centerY, double speed) {
            super(centerX - BALL_RADIUS, centerY - BALL_RADIUS, speed, false);
            setRadius(BALL_RADIUS);
        }

        @Override public void render(javafx.scene.canvas.GraphicsContext gc) {}
    }

    private static class TestNormalBrick extends NormalBrick {
        public TestNormalBrick(double x, double y, double width, double height, int colorStyle) {
            super(x, y, width, height, colorStyle, false);
        }

        @Override public void render(javafx.scene.canvas.GraphicsContext gc) {}
    }

    private Ball ball;
    private TestNormalBrick brick;

    @BeforeEach
    void setUp() {
        ball = new TestBall(0, 0, 5.0);
        brick = new TestNormalBrick(50, 50, 60, 20, 1);
    }

    @Test @DisplayName("Ball chạm brick từ phía trái - phải va chạm")
    void testCollision_BallHitsBrickFromLeft() {
        ball = new TestBall(45, 60, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball chạm brick từ phía phải - phải va chạm")
    void testCollision_BallHitsBrickFromRight() {
        ball = new TestBall(115, 60, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball chạm brick từ phía trên - phải va chạm")
    void testCollision_BallHitsBrickFromTop() {
        ball = new TestBall(80, 45, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball chạm brick từ phía dưới - phải va chạm")
    void testCollision_BallHitsBrickFromBottom() {
        ball = new TestBall(80, 75, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball chạm góc brick - phải va chạm")
    void testCollision_BallHitsBrickCorner() {
        ball = new TestBall(45, 45, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball không chạm brick - ở xa")
    void testNoCollision_BallFarFromBrick() {
        assertFalse(brick.checkCollision(ball));
    }

    @Test @DisplayName("Ball không chạm brick - sát nhưng không đủ gần")
    void testNoCollision_BallJustOutsideRange() {
        ball = new TestBall(39, 60, 5.0);
        assertFalse(brick.checkCollision(ball));
    }

    @Test @DisplayName("Brick đã bị phá - không va chạm nữa")
    void testNoCollision_BrickAlreadyDestroyed() {
        brick.setDestroyed(true);
        ball = new TestBall(45, 60, 5.0);
        assertFalse(brick.checkCollision(ball));
    }

    @Test @DisplayName("Kiểm tra HP giảm đúng sau va chạm")
    void testCollision_HitPointsDecreaseCorrectly() {
        ball = new TestBall(45, 60, 5.0);
        assertEquals(1, brick.getHitPoints());
        brick.checkCollision(ball);
        assertEquals(0, brick.getHitPoints());
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Ball center nằm trong brick - phải va chạm")
    void testCollision_BallCenterInsideBrick() {
        ball = new TestBall(80, 60, 5.0);
        assertTrue(brick.checkCollision(ball));
        assertTrue(brick.isDestroyed());
    }

    @Test @DisplayName("Kiểm tra score value của NormalBrick")
    void testCollision_ScoreValue() {
        assertEquals(10, brick.getScoreValue());
    }

    @Test @DisplayName("Kiểm tra brick có thể bị phá")
    void testBrick_IsDestructible() {
        assertTrue(brick.isDestructible());
    }

    @Test @DisplayName("Kiểm tra ball có phản xạ khi chạm brick")
    void testBrick_ShouldBounceBall() {
        assertTrue(brick.shouldBounceBall());
    }
}