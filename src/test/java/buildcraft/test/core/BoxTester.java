package buildcraft.test.core;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import buildcraft.lib.misc.data.Box;

import buildcraft.test.TestHelper;

@RunWith(Theories.class)
public class BoxTester {
    private static final BlockPos MIN = new BlockPos(1, 2, 3), MAX = new BlockPos(4, 5, 6);
    private static final BlockPos SIZE = new BlockPos(4, 4, 4);
    private static final BlockPos CENTER = new BlockPos(3, 4, 5);
    private static final Vec3d CENTER_EXACT = new Vec3d(3, 4, 5);

    @DataPoints("testContainsVec3d")
    public static Set<Entry<Vec3d, Boolean>> dataContainsVec3d() {
        return ImmutableMap.<Vec3d, Boolean> builder()
            // @formatter:off
                .put(new Vec3d(0, 0, 0), false).put(new Vec3d(1, 2, 3), true).put(new Vec3d(1.3, 2.4, 3.5), true).put(new Vec3d(4.9, 5.9, 6.9), true).put(
                        new Vec3d(5, 5, 6), false)
                // @formatter:on
            .build().entrySet();
    }

    @Test
    @Theory
    public void testContainsVec3d(@FromDataPoints("testContainsVec3d") Entry<Vec3d, Boolean> entry) {
        Box box = new Box(MIN, MAX);
        Vec3d in = entry.getKey();
        boolean expected = entry.getValue();
        Assert.assertEquals(expected, box.contains(in));
    }

    @Test
    public void testMin() {
        Box box = new Box(MIN, MAX);
        Assert.assertEquals(MIN, box.min());
    }

    @Test
    public void testMax() {
        Box box = new Box(MIN, MAX);
        Assert.assertEquals(MAX, box.max());
    }

    @Test
    public void testSize() {
        Box box = new Box(MIN, MAX);
        Assert.assertEquals(SIZE, box.size());
    }

    @Test
    public void testCenter() {
        Box box = new Box(MIN, MAX);
        Assert.assertEquals(CENTER, box.center());
    }

    @Test
    public void testCenterExact() {
        Box box = new Box(MIN, MAX);
        TestHelper.assertVec3dEquals(CENTER_EXACT, box.centerExact());
    }

    @Test
    public void testIntersection1() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(1, 1, 1), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
       
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }
       @Test
    public void testIntersection6() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(1, 1, 1), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Assert.assertEquals(inter, box1.getIntersect(box2));
        
    }

    @Test
    public void testIntersection2() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Assert.assertEquals(inter, box1.getIntersect(box2));
        
    }
     @Test
    public void testIntersection5() {
        Box box1 = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(3, 3, 3));
        Box inter = new Box(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));
     
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }

    @Test
    public void testIntersection3() {
        Box box1 = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(1, 1, 1));
        Assert.assertEquals(inter, box1.getIntersect(box2));
       
    }
    
    @Test
    public void testIntersection4() {
        Box box1 = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(1, 1, 1));
 
        Assert.assertEquals(inter, box2.getIntersect(box1));
    }
       @Test
    public void testIntersection7() {
        Box box1 = new Box(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2));
        Box box2 = new Box(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1));
        Box box3 = new Box(new BlockPos(1, 1, 1), new BlockPos(1, 2, 2));
        Box inter = new Box(new BlockPos(1, 1, 1), new BlockPos(1, 2, 2));
 
        Assert.assertEquals(inter, box1.getIntersect(box3));
    }

    @Test
    public void testEmpty() {
        Box box1 = new Box(null,null);
        Assert.assertEquals(new BlockPos(0, 0, 0), box1.size());
    }
    @Test
    public void TestDoubleMin(){
     Box box = new Box(MIN,MIN);
     Assert.assertEquals(MIN,box.max());
	}
    @Test
    public void TestDoubleMax(){
     Box box = new Box(MAX,MAX);
     Assert.assertEquals(MAX,box.min());
	}

    // goal is to creat some sort of automated testing for this .
    
    @Test
    public void automated_tests(){
        final int NUM_TESTS = 100;
     
        for (int i = 0; i < NUM_TESTS; i++) {
            Box box = new Box(new BlockPos(i, i, i), new BlockPos(i,i,i));
                for (int j= 0; j< NUM_TESTS; j++){
                    Box inter =new Box(new BlockPos(j,j,j), new BlockPos(j,j,j));
    
      Assert.assertEquals(inter.getIntersect(box) ,box.getIntersect(inter));
          }
         
        }
    
	}

}
