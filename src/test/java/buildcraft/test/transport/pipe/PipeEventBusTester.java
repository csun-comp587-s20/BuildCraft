package buildcraft.test.transport.pipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.IFlowPower;
import buildcraft.api.transport.pipe.IPipeHolder;
import buildcraft.api.transport.pipe.PipeEvent;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.api.transport.pipe.PipeEventPower;
import buildcraft.transport.pipe.PipeEventBus;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PipeEventBusTester {
    public static long dontInlineThis = 0;

    @Test
    public void testSimpleEvent() {
        PipeEventBus bus = new PipeEventBus();

        PipeEventItem.ModifySpeed event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 0.00001);

        bus.registerHandler(this);

        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(1, event.targetSpeed, 0.00001);

        bus.unregisterHandler(this);

        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 0.00001);
    }

    @PipeEventHandler
    public void modifySpeed(PipeEventItem.ModifySpeed event) {
        event.targetSpeed = 1;
    }

    @Test
    public void testExtends() {
        PipeEventBus bus = new PipeEventBus();

        PipeEventItem.ModifySpeed event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 0.00001);

        bus.registerHandler(new Base());

        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(2, event.targetSpeed, 0.00001);

        bus = new PipeEventBus();
        bus.registerHandler(new Sub());

        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(3, event.targetSpeed, 0.00001);
    }

    public static class Base {
        @PipeEventHandler
        public void modifySpeed2(PipeEventItem.ModifySpeed event) {
            event.targetSpeed = 2;
        }
    }

    public static class Sub extends Base {
        @Override
        public void modifySpeed2(PipeEventItem.ModifySpeed event) {
            event.targetSpeed = 3;
        }
    }
    
    /* -------------------------- My test ------------------------------------ */
    
    public static final int NUM_TEST = 40;
    public static final EnumDyeColor FINAL_COLOR = EnumDyeColor.BLACK;
    public static final EnumFacing FINAL_FROM = EnumFacing.DOWN;

    @Test
    public void modifySpeedTest()
    {
    	PipeEventBus bus = new PipeEventBus();
    	PipeEventItem.ModifySpeed event;

    	for(int i = 1; i < NUM_TEST; i++)
    	{
    		bus = new PipeEventBus();
    		bus.registerHandler(new ModifySpeed3((double)i));
    		event = initDefaultSpeed();
    		bus.fireEvent(event);
    		Assert.assertEquals(i, event.targetSpeed, 0.00001);
    	}
    }

    public PipeEventItem.ModifySpeed initDefaultSpeed()
    {
    	return new PipeEventItem.ModifySpeed(null,null,null,1);
    }

    public static class ModifySpeed3
    {
    	public final double speed;

    	public ModifySpeed3(final double speed)
    	{
    		this.speed = speed;
    	}

        @PipeEventHandler
        public void modifySpeed2(PipeEventItem.ModifySpeed event) 
        {
            event.targetSpeed = this.speed;
        }
    }

    @Test
    public void testInsert()
    {
    	Random random = new Random();
    	int rand = 0;
    	int max = 10, min = 1;
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		rand = random.nextInt(11) + min;
        	ItemStack stack = getRandomStack(rand);
        	EnumDyeColor randColor = getRandomColor();
        	EnumFacing randFace = getRandomFaceDir();
        	IPipeHolder holder = null;
        	IFlowItems flow = null;
        	
        	PipeEventItem.TryInsert ins = new PipeEventItem.TryInsert(holder, flow, randColor,
        			randFace, stack);
        	
        	Assert.assertEquals(ins.colour, randColor);
        	Assert.assertEquals(ins.flow, randFace);
        	Assert.assertEquals(ins.attempting, stack);
        	Assert.assertEquals(ins.accepted, rand);
    	}
    }
    
    @Test
    public void testReachDest()
    {
    	Random random = new Random();
    	int rand = 0;
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		rand = random.nextInt(11);
        	ItemStack stack = getRandomStack(rand);
    		EnumDyeColor randColor = getRandomColor();
    		EnumFacing randFace = getRandomFaceDir();
    		IPipeHolder holder = null;
    		IFlowItems flow = null;
    		PipeEventItem.ReachDest dest = new PipeEventItem.ReachDest.OnInsert(holder, flow, randColor
    				, stack, randFace);
    		dest.setStack(stack);
    		
    		Assert.assertEquals(dest.colour, randColor);
    		Assert.assertEquals(dest.getStack(), stack);
    	}
    }
    
    @Test
    public void testEjected()
    {
    	Random random = new Random();
    	int rand = 0;
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		rand = random.nextInt(11);
        	ItemStack stack = getRandomStack(rand);
    		ItemStack stack2 = getRandomStack(rand);
    		EnumFacing randFace = getRandomFaceDir();
    		IPipeHolder holder = null;
    		IFlowItems flow = null;
    		IFlowItems other = null;
    		PipeEventItem ejected = new PipeEventItem.Ejected.IntoPipe(holder, flow, stack, 
    				stack2, randFace, other);
    		
    	}
    }

    @Test
    public void testItemEntry()
    {
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		EnumDyeColor randColor = getRandomColor();
    		EnumFacing randFace = getRandomFaceDir();
    		ItemStack stack = getRandomStack(i);
    		PipeEventItem.ItemEntry entry = new PipeEventItem.ItemEntry(randColor, stack, 
    				randFace);
    		Assert.assertEquals(randColor, entry.colour);
    		Assert.assertEquals(stack, entry.stack);
    		Assert.assertEquals(randFace, entry.from);
    	}
    }

    public ItemStack getRandomStack(int i)
    {
    	Random random = new Random();
    	Block block;
    	int j = random.nextInt(100);
    	if(j < 20)
    		block = new Block(Material.AIR);
    	else if(j < 40)
    		block = new Block(Material.ANVIL);
    	else if(j < 60)
    		block = new Block(Material.BARRIER);
    	else if(j < 80)
    		block = new Block(Material.CACTUS);
    	else
    		block = new Block(Material.CIRCUITS);

    	ItemStack stack = new ItemStack(block, i);
    	return stack;
    }

    public EnumDyeColor getRandomColor()
    {
    	Random random = new Random();
    	int i = random.nextInt(100);
    	return i < 5 ? EnumDyeColor.BLACK : 
    		(i < 10 ? EnumDyeColor.GRAY : 
    			(i < 15 ? EnumDyeColor.SILVER : 
    				(i < 18 ? EnumDyeColor.BROWN : 
    					(random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE))));
    }

    public EnumFacing getRandomFaceDir()
    {
    	Random random = new Random();
    	int i = random.nextInt(100);
    	return i < 5 ? EnumFacing.DOWN : 
    		(i < 10 ? EnumFacing.EAST : 
    			(i < 15 ? EnumFacing.NORTH: 
    				(i < 18 ? EnumFacing.NORTH: 
    					(random.nextInt(500) == 0 ? EnumFacing.WEST : EnumFacing.UP))));
    }
    
    
    
    public Block getRandomBlock()
    {
    	Random random = new Random();
    	int i = random.nextInt(100);
    	return i < 5 ? new Block(Material.AIR) : 
    		(i < 10 ? new Block(Material.ANVIL) : 
    			(i < 15 ? new Block(Material.CACTUS) : 
    				(i < 18 ? new Block(Material.CARPET) : 
    					(random.nextInt(500) == 0 ? new Block(Material.CLOTH) : new Block(Material.GLASS)))));
    }

    @Test
    public void PipeEventTest()
    {
    	List<PipeEvent> pipe = new ArrayList<PipeEvent>();
    	
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		IPipeHolder hold = null;
    		PipeEvent pip = new PipeEvent(hold);
    		
    		pipe.add(pip);
    		Assert.assertEquals(pip.canBeCancelled, false);
    		pip.cancel();
    		Assert.assertEquals(pip.isCanceled(), false);
    	}
    	
    	Assert.assertEquals(pipe.size(), NUM_TEST);
    }
    
    @Test
    public void PipeEventItemTest()
    {
    	IPipeHolder hold = null;
    	IFlowItems flow = null;
    	PipeEventItem item = new PipeEventItem(hold, flow);
    	Assert.assertEquals(hold, item.holder);
    	Assert.assertEquals(flow, item.flow);
    }
    
    // I don't know how to do automation test on this one 
    // because the variables required to test the method
    // are not automated
    @Test
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void DropTest(LivingDropsEvent event)
    {
    	IPipeHolder hold = null;
    	IFlowItems flow = null;
    	EntityLivingBase deadEntity = event.getEntityLiving();
    	World world = deadEntity.getEntityWorld();
    	Random random = new Random();
    	int i = random.nextInt(11);
    	ItemStack stack = getRandomStack(i);
    	BlockPos position = deadEntity.getPosition();
    	double x = position.getX();
    	double y = position.getY();
    	double z = position.getZ();
    	
    	EntityItem entity = new EntityItem(world, x, y, z, stack);
    			
    	PipeEventItem.Drop drop = new PipeEventItem.Drop(hold, flow, entity);
    	Assert.assertEquals(drop.getStack(), stack);
    	Assert.assertEquals(drop.getEntity(), entity);
    	
    	stack = getRandomStack(0);
    	drop.setStack(stack);
    	Assert.assertEquals(ItemStack.EMPTY, entity.getItem());
    }
    
    @Test
    public void PipeEventPowerConfigureTest()
    {
    	IPipeHolder holder = null;
    	IFlowPower flow = null;
    	PipeEventPower.Configure config = new PipeEventPower.Configure(holder, flow);
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		Random random = new Random();
    		long j = random.nextLong();
    		long k = random.nextLong();
    		long l = random.nextLong();
    		
    		config.setMaxPower(j);
    		Assert.assertEquals(j, config.getMaxPower());
    		config.setPowerLoss(k);
    		Assert.assertEquals(k, config.getPowerLoss());
    		config.setPowerResistance(l);
    		Assert.assertEquals(l, config.getPowerResistance());
    	}
    }
    
    @Test
    public void PipeEventPowerPrimaryDirectionTest()
    {
    	EnumFacing face = getRandomFaceDir();
    	IPipeHolder holder = null;
    	IFlowPower flow = null;
    	PipeEventPower.PrimaryDirection dir = new PipeEventPower.PrimaryDirection(holder, flow, face);
    	Assert.assertEquals(face, dir.getFacing());
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		EnumFacing face2 = getRandomFaceDir();
    		dir.setFacing(face2);
    		Assert.assertEquals(dir.getFacing(), face2);
    	}
    }
}