package buildcraft.test.lib.inventory;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.ForgeDirection;

import buildcraft.api.inventory.IItemTransactor;

import buildcraft.lib.tile.item.ItemHandlerSimple;
import buildcraft.lib.tile.item.StackInsertionFunction;

import buildcraft.test.VanillaSetupBaseTester;

public class ItemTransactorTester extends VanillaSetupBaseTester {
    private IInventory fakeInventory;
    private ItemStack fakeStack;
    private ItemStack injectStack;
    
    @Test
    public void emptyInventory_dontAdd(){

        TransactorSimple split = new TransactorSimple(fakeInventory);
        injectStack.stackSize = 64;

        when(fakeInventory.getSizeInventory()).thenReturn(1);
        when(fakeInventory.getStackInSlot(0)).thenReturn(null);

        cut.inject(injectStack, ForgeDirection.UNKOWN, false);
        verify(fakeInventory, never()).setInventorySlotContents(eq(0), (ItemStack) anyObject());
    }

	@Test
	public void emptyInventory_doAdd(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);
		injectStack.stackSize = 64;

		when(fakeInventory.getSizeInventory()).thenReturn(1);
		when(fakeInventory.getStackInSlot(0)).thenReturn(null);
		when(fakeInventory.isStackValidForSlot(anyInt(), (ItemStack)anyObject())).thenReturn(true);
		
		cut.inject(injectStack, ForgeDirection.UNKNOWN, true);
		verify(fakeInventory).setInventorySlotContents(eq(0), (ItemStack) anyObject());
	}
    
    @Test
	public void fullInventory_returnSuccess(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);

		when(fakeInventory.getSizeInventory()).thenReturn(0);
		when(fakeInventory.getStackInSlot(0)).thenReturn(fakeStack);
        
        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
		assertEquals(0, actual);
	}
    
    @Test
	public void fullInventory_returnFail(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);

		when(fakeInventory.getSizeInventory()).thenReturn(0);
		when(fakeInventory.getStackInSlot(0)).thenReturn(fakeStack);
        
        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
		assertEquals(1, actual);
	}

	@Test
	public void halfFullInventory_returnFail(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);
        ItemStack itemStack = mock(ItemStack.class);
        injectStack.stackSize = 64;
        itemStack.stackSize = 63;

		when(itemStack.isItemEqual((ItemStack) anyObject())).thenReturn(true);
		when(itemStack.getMaxStackSize()).thenReturn(64);		
		when(ItemStack.areItemStackTagsEqual((ItemStack)anyObject(), (ItemStack)anyObject())).thenReturn(true);
		when(fakeInventory.getSizeInventory()).thenReturn(1);
		when(fakeInventory.getStackInSlot(0)).thenReturn(itemStack);
        
        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
		assertEquals(1, actual);
    }
    
	@Test
	public void halfFullInventory_returnSuccess(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);
        ItemStack itemStack = mock(ItemStack.class);
        injectStack.stackSize = 64;
        itemStack.stackSize = 63;

		when(itemStack.isItemEqual((ItemStack) anyObject())).thenReturn(true);
		when(itemStack.getMaxStackSize()).thenReturn(64);		
		when(ItemStack.areItemStackTagsEqual((ItemStack)anyObject(), (ItemStack)anyObject())).thenReturn(true);
		when(fakeInventory.getSizeInventory()).thenReturn(1);
		when(fakeInventory.getStackInSlot(0)).thenReturn(itemStack);
        
        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
		assertEquals(0, actual);
    }

    @Test
    public void moveItems_inInventory() {

        IItemTransactor trans = new ItemHandlerSimple(2, null);
        ItemStack insert = new ItemStack(Items.APPLE);
        ItemStack leftOver = trans.insert(insert.copy(), false, false);
        ItemStack extracted = trans.extract(null, 1, 1, false);

        extracted = trans.extract(null, 1, 1, false);

        Assert.assertTrue(trans.extract(null, 1, 1, false).isEmpty());
        Assert.assertTrue(leftOver.isEmpty());
        Assert.assertTrue(ItemStack.areItemStacksEqual(insert, extracted));
        Assert.assertTrue(extracted.isEmpty());
    }

    @Test
    public void limitInventory() {

        ItemStack toInsert = new ItemStack(Items.APPLE, 9);
        ItemStack toInsertCopy = toInsert.copy();
        ItemStack supposedLeftOver = new ItemStack(Items.APPLE);
        ItemStack actuallyLeftOver = limited.insert(toInsert, false, false);

        Assert.assertTrue(ItemStack.areItemStacksEqual(toInsert, toInsertCopy));
        Assert.assertTrue(ItemStack.areItemStacksEqual(supposedLeftOver, actuallyLeftOver));
    }

    @Test
    public void noInventorySlots(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);

        when(fakeInventory.getSizeInventory()).thenReturn(0);
        when(fakeInventory.getStackInSlot(0)).thenReturn(itemStack);

        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
        assertEquals(0, actual);
    }

    @Test
    public void emptyInventory_returnFalse(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);
        injectStack.stackSize = 64;

        when(fakeInventory.getSizeInventory()).thenReturn(1);
        when(fakeInventory.getStackInSlot(0)).thenReturn(null);
        when(fakeInventory.isStackValidForSlot(anyInt(), (ItemStack)anyObject())).thenReturn(true);

        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, false);
        assertEquals(1, actual);
    }

    @Test
    public void emptyInventory_returnTrue(){

        TransactorSimple cut = new TransactorSimple(fakeInventory);
        injectStack.stackSize = 64;

        when(fakeInventory.getSizeInventory()).thenReturn(1);
        when(fakeInventory.getStackInSlot(0)).thenReturn(null);
        when(fakeInventory.isStackValidForSlot(anyInt(), (ItemStack)anyObject())).thenReturn(true);

        int actual = cut.inject(injectStack, ForgeDirection.UNKNOWN, true);
        assertEquals(0, actual);
    }

    @Test
    public void stackMerge_isNull(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack itemStack = new ItemStack();

        boolean itemsInventory = split.canStacksMerge(null, itemStack);
        assertFalse(itemsInventory);
    }

    @Test
    public void otherStackMerge_isNull(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack itemStack = new ItemStack();

        boolean itemsInventory = split.canStacksMerge(itemStack, null);
        assertFalse(itemsInventory);
    }

    @Test
    public void mergeDifferent_Items(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack item1 = new ItemStack();
        ItemStack item2 = new ItemStack();

        when(item1.isItemEqual(item2)).thenReturn(false);
        boolean itemsInventory = split.canStacksMerge(item1, item2);
        assertFalse(itemsInventory);
    }

    @Test
    public void stacksCantMerge(){

        StackMergeHelper split = new StackMergeHelper();

        int itemsInventory = split.mergeStacks(null, null, false);
        assertEquals(0, actual);
    }

    @Test
    public void stackItemsNoSpace_returnFail(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack item1 = new ItemStack();
        ItemStack item2 = new ItemStack();
        item1.stackSize = 1;
        item2.stackSize = 64;

        when(item1.isItemEqual(item2)).thenReturn(true);
        when(item2.getMaxStackSize()).thenReturn(64);
        when(ItemStack.areItemStackTagsEqual(item1, item2)).thenReturn(true);

        int itemsInventory = split.mergeStacks(item1, item2, false);
        assertEquals(0, itemsInventory);
    }

    @Test
    public void stackItems_AvailableSpace(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack item1 = new ItemStack();
        ItemStack item2 = new ItemStack();
        item1.stackSize = 1;
        item2.stackSize = 63;

        when(item1.isItemEqual(item2)).thenReturn(true);
        when(item2.getMaxStackSize()).thenReturn(64);
        when(ItemStack.areItemStackTagsEqual(item1, item2)).thenReturn(true);

        int itemsInventory = split.mergeStacks(item1, item2, false);
        assertEquals(1, itemsInventory);
    }

    @Test
    public void stackItems_AvailableSpace_MergeComplete(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack item1 = new ItemStack();
        ItemStack item2 = new ItemStack();
        item1.stackSize = 1;
        item2.stackSize = 63;

        when(item1.isItemEqual(item2)).thenReturn(true);
        when(item2.getMaxStackSize()).thenReturn(64);
        when(ItemStack.areItemStackTagsEqual(item1, item2)).thenReturn(true);

        int itemsInventory = split.mergeStacks(item1, item2, false);
        assertEquals(64, item2.stackSize);
    }

    @Test
    public void stackItems_individualItems(){

        StackMergeHelper split = new StackMergeHelper();
        ItemStack item1 = new ItemStack();
        ItemStack item2 = new ItemStack();
        item1.stackSize = 1;
        item2.stackSize = 1;

        when(item1.isItemEqual(item2)).thenReturn(true);
        when(item2.getMaxStackSize()).thenReturn(64);
        when(ItemStack.areItemStackTagsEqual(item1, item2)).thenReturn(true);

        int itemsInventory = split.mergeStacks(item1, item2, false);
        assertEquals(1, itemsInventory);
    }

}
