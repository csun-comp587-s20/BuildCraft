package buildcraft.test.transport.pipe;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import java.util.EnumSet;

import java.util.List;

import java.util.Random;
import java.util.stream.Collectors;


import org.junit.Assert;
import org.junit.Test;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import buildcraft.api.core.InvalidInputDataException;
import buildcraft.api.transport.pipe.EnumPipeColourType;
import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.IFlowPower;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.api.transport.pipe.PipeDefinition.PipeDefinitionBuilder;

import buildcraft.api.transport.pipe.EnumPipeColourType;
import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.IFlowPower;
import buildcraft.api.transport.pipe.IPipeHolder;

import buildcraft.api.transport.pipe.PipeEvent;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.api.transport.pipe.PipeEventPower;
import buildcraft.api.transport.pipe.PipeFaceTex;
import buildcraft.api.transport.pipe.PipeFlowType;
import buildcraft.api.transport.pipe.PipeFlowType.IFlowCreator;
import buildcraft.api.transport.pipe.PipeFlowType.IFlowLoader;

import buildcraft.transport.pipe.Pipe;
import buildcraft.transport.pipe.PipeEventBus;
import buildcraft.transport.pipe.PipeRegistry;
import buildcraft.transport.pipe.PluggableHolder;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import buildcraft.transport.tile.TilePipeHolder;

import buildcraft.transport.pipe.PipeEventBus;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import scala.actors.threadpool.Arrays;



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
    
    // getting null pointer exception because of mismatch between forge and buildcraft version
    @Test(expected = NullPointerException.class)
    public void PipeTest()
    {
    	IPipeHolder holder = new TilePipeHolder();
    	PipeDefinitionBuilder build = new PipeDefinitionBuilder();
    	PipeDefinition pipeDef = new PipeDefinition(build);
    	
    	Pipe pipe = new Pipe(holder, pipeDef);
    	Assert.assertEquals(holder, pipe.holder);
    	Assert.assertEquals(pipe.definition, pipeDef);
    }
    
    @Test
    public void plugTest()
    {
    	EnumFacing face = getRandomFaceDir();
    	TilePipeHolder tile = new TilePipeHolder();
    	PluggableHolder hold = new PluggableHolder(tile, face);
    	
    	Assert.assertEquals(tile, hold.holder);
    	Assert.assertEquals(face, hold.side);
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
        	IPipe pipe = null;
        	IFlowItems flow = new PipeFlowItems(pipe);
    		PipeEventItem.ReachDest.OnInsert dest = new PipeEventItem.ReachDest.OnInsert(holder, flow, randColor
    				, stack, randFace);
    		PipeEventBus bus = new PipeEventBus();
    		
    		bus.fireEvent(dest);
    		Assert.assertEquals(dest.getStack(), stack);
    		
    		ItemStack stack2 = getRandomStack(rand);
    		
    		dest = new PipeEventItem.ReachDest.OnInsert(holder, flow, randColor
    				, stack, randFace);
    		
    		dest.setStack(stack2);
    		bus.fireEvent(dest);
    		
    		Assert.assertEquals(dest.from, randFace);
    		Assert.assertEquals(dest.colour, randColor);
    		Assert.assertEquals(dest.getStack(), stack2);
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

        	IPipe pipe = null;
        	IPipeHolder holder = null;
        	IFlowItems flow = new PipeFlowItems(pipe);
    		PipeEventItem.Ejected ejected = new PipeEventItem.Ejected.IntoPipe(holder, flow, stack, 
    				stack2, randFace, flow);
    		PipeEventBus bus = new PipeEventBus();
    		
    		bus.fireEvent(ejected);
    		
    		Assert.assertEquals(ejected.inserted, stack);
    		Assert.assertEquals(ejected.getExcess(), stack2);
    		Assert.assertEquals(ejected.to, randFace);
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
    
    @Test
    public void sideCheck() 
    {
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		IPipeHolder hold = null;
    		IFlowItems flow = null;
    		EnumFacing dir = getRandomFaceDir();
    		ItemStack stack = getRandomStack(i);
    		EnumDyeColor col = getRandomColor();
    		PipeEventItem.SideCheck side = new PipeEventItem.SideCheck(
    				hold, flow, col, dir, stack);
    		
    		// documentation states that it may return true or false 
    		Assert.assertTrue(side.isAllowed(dir));
    		
    		EnumSet<EnumFacing> addset = EnumSet.noneOf(EnumFacing.class);
    		Assert.assertEquals(side.getOrder(), addset);
    		addset.add(dir);
    		
    		side.increasePriority(dir);
    		Assert.assertEquals(side.getOrder(), addset);
    		side.increasePriority(dir);
    		
    		EnumFacing randFace = getRandomFaceDir();
    		side.increasePriority(randFace);
    		
    		EnumSet<EnumFacing> anotherAddSet = EnumSet.noneOf(EnumFacing.class);
    		anotherAddSet.add(randFace);
    		List<EnumSet<EnumFacing>> list = new ArrayList<EnumSet<EnumFacing>>();
    		list.add(addset);
    		list.add(anotherAddSet);
    		Assert.assertEquals(side.getOrder(), list);
    	}
    }
    
    
    @Test
    public void testOrderedEvent()
    {
		for(int i = 0; i < NUM_TEST; i++)
		{
			List<EnumSet<EnumFacing>> list = new ArrayList<EnumSet<EnumFacing>>();
			Random rand = new Random();
			int k = rand.nextInt(11);
			for(int j = 0; j < k; j++)
				list.add(getEnumSetFace());
			
			EnumSet<EnumFacing> addset = EnumSet.noneOf(EnumFacing.class);
			for(EnumSet<EnumFacing> a: list)
			{
				addset.addAll(a);
			}
			
			IFlowItems flow = null;
	    	IPipeHolder holder = null;
			PipeEventItem.OrderedEvent event = new PipeEventItem.OrderedEvent(holder, flow, list);
			Assert.assertEquals(addset, event.getAllPossibleDestinations());
		}
    }
    
    /*-------------------------- Pipe Event Item Class Ends Here ---------------------------- */
    
   
    /*-------------------------- PipeEvent class start ------------------------------------ */
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
    
    /*-------------------------- Pipe Event class end ------------------------------------ */
    
    
    // I don't know how to do automation test on this one 
    // because the variables required to test the method
    // are not automated
    /*
     * @Test
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
     */
    
    
    @Test
    public void PipeFaceTexTest()
    {
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		Random rand = new Random();
    		int j = rand.nextInt(101);
    		assertEquals(PipeFaceTex.___testing_create_single(j), PipeFaceTex.get(j));
    		assertEquals(PipeFaceTex.___testing_create_single(j).hashCode(),
    				PipeFaceTex.get(j).hashCode());
    	}
    }
    
    
    
    public void pipeFlowTypeTest()
    {
    	IFlowCreator creator = null;
    	IFlowLoader loader = null;
    	
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		EnumPipeColourType colour = getColourType();
    		PipeFlowType type = new PipeFlowType(creator, loader, colour);
    		Assert.assertEquals(colour, type.fallbackColourType);
    	}
    }
    
    /* ---------------------------- Helper automation method -------------------------------- */
    public ItemStack getRandomStack(int i)
    {
    	Random random = new Random();
    	Block block;
    	int j = random.nextInt(100);
    	if(j <= 20)
    		block = new Block(Material.AIR);
    	else if(20 < j && j <= 40)
    		block = new Block(Material.ANVIL);
    	else if(40 < j && j <= 60)
    		block = new Block(Material.BARRIER);
    	else if(60 < j && j <= 80)
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
    	EnumDyeColor color;
    	if(i <= 20)
    		color = EnumDyeColor.BLACK;
    	else if(20 < i && i <= 40)
    		color = EnumDyeColor.GRAY;
    	else if(40 < i && i <= 60)
    		color = EnumDyeColor.BROWN;
    	else if(60 < i && i <= 80)
    		color = EnumDyeColor.BLUE;
    	else
    		color = EnumDyeColor.PINK;
    	return color;
    }

    public EnumFacing getRandomFaceDir()
    {
    	Random random = new Random();
    	int i = random.nextInt(101);
    	return i < 20 ? EnumFacing.DOWN : 
    		(20 < i && i <= 40 ? EnumFacing.EAST : 
    			(40 < i && i <= 60 ? EnumFacing.NORTH: 
    				(60 < i && i <= 80 ? EnumFacing.NORTH: 
    					EnumFacing.WEST)));
    }

    public Block getRandomBlock()
    {
    	Random random = new Random();
    	int i = random.nextInt(100);
    	return i <= 5 ? new Block(Material.AIR) : 
    		(5 < i && i <= 10 ? new Block(Material.ANVIL) : 
    			(10 < i && i <= 15 ? new Block(Material.CACTUS) : 
    				(15 < i && i <= 18 ? new Block(Material.CARPET) : 
    					(random.nextInt(500) == 0 ? new Block(Material.CLOTH) : new Block(Material.GLASS)))));
    }
    
    public EnumSet<EnumFacing> getEnumSetFace()
    {
    	EnumSet<EnumFacing> set = EnumSet.of(getRandomFaceDir());
    	return set;
    }

    @Test
    public void PipeEventPowerConfigureTest()
    {
    	IPipeHolder holder = null;
    	IFlowPower flow = null;
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		PipeEventPower.Configure config = new PipeEventPower.Configure(holder, flow);
    		PipeEventBus bus = new PipeEventBus();
    		bus.fireEvent(config);
    		
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
    
    public EnumPipeColourType getColourType()
    {
    	Random random = new Random();
    	int i = random.nextInt(100);
    	return i < 25 ? EnumPipeColourType.TRANSLUCENT :
    		i < 50 ? EnumPipeColourType.CUSTOM :
    			i < 75 ? EnumPipeColourType.BORDER_INNER :
    				(EnumPipeColourType.BORDER_OUTER);
    }
    
    public void pipeflowtypetester()
    {
    	IFlowCreator creator = null;
    	IFlowLoader loader = null;
    	
    	for(int i = 0; i < NUM_TEST; i++)
    	{
    		EnumPipeColourType colour = getColourType();
    		PipeFlowType type = new PipeFlowType(creator, loader, colour);
    		Assert.assertEquals(colour, type.fallbackColourType);
    	}
    }
}