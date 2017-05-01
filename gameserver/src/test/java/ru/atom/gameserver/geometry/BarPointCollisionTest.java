package ru.atom.gameserver.geometry;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BarPointCollisionTest {
    @Test
    public void pointInsideBar() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(50, 50);
        assertThat(bar.isColliding(point), is(true));
    }

    @Test
    public void pointOnCornerOfBar() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(0, 0);
        assertThat(bar.isColliding(point), is(true));
    }

    @Test
    public void pointOnBorderOfBar() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(0, 50);
        assertThat(bar.isColliding(point), is(true));
    }

    @Test
    public void pointOutsideOfBar1() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(0, 150);
        assertThat(bar.isColliding(point), is(false));
    }

    @Test
    public void pointOutsideOfBar2() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(150, 0);
        assertThat(bar.isColliding(point), is(false));
    }

    @Test
    public void pointOutsideOfBar3() {
        Collider bar = new Bar(0, 0, 100, 100);
        Collider point = new Point(150, 150);
        assertThat(bar.isColliding(point), is(false));
    }
}
