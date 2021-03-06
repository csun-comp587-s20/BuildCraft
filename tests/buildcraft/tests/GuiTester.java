/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.tests;

import org.junit.Test;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import buildcraft.BuildCraftBuilders;
import buildcraft.core.DefaultProps;
import buildcraft.tests.testcase.SequenceActionCheckBlockMeta;
import buildcraft.tests.testcase.TileTestCase;

public class GuiTester extends GuiContainer {

    private static final int TEXT_X = 10;
    private static final int TEXT_Y = 10;
    private static final int TEXT_WIDTH = 156;
    private static final int TEXT_HEIGHT = 12;

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "buildcraft", DefaultProps.TEXTURE_PATH_GUI + "/tester.png");

    private GuiButton checkBlockAndMeta;
    private GuiButton checkTileMethod;
    private GuiButton checkTestCommand;
    private GuiButton cancel;

    private GuiTextField textField;

    private World world;
    private int x, y, z;

    public GuiTester(EntityPlayer player, int ix, int iy, int iz) {
        super(new ContainerTester(player, ix, iy, iz));
        x = ix;
        y = iy;
        z = iz;
        xSize = 256;
        ySize = 166;
        world = player.worldObj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        int xscreen = (width - xSize) / 2;
        int yscreen = (height - ySize) / 2;

        Keyboard.enableRepeatEvents(true);

        checkBlockAndMeta = new GuiButton(0, xscreen + 5, yscreen + 30, 120, 20, "");
        checkBlockAndMeta.displayString = "Check Block and Meta";
        buttonList.add(checkBlockAndMeta);

        checkTileMethod = new GuiButton(1, xscreen + 5, yscreen + 55, 120, 20, "");
        checkTileMethod.displayString = "Check Tile Method";
        buttonList.add(checkTileMethod);

        checkTestCommand = new GuiButton(2, xscreen + 5, yscreen + 80, 120, 20, "");
        checkTestCommand.displayString = "Check Test Command";
        buttonList.add(checkTestCommand);

        cancel = new GuiButton(2, xscreen + 5, yscreen + 105, 120, 20, "");
        cancel.displayString = "Cancel";
        buttonList.add(cancel);

        textField = new GuiTextField(this.fontRendererObj, TEXT_X, TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT);
        textField.setMaxStringLength(BuildCraftBuilders.MAX_BLUEPRINTS_NAME_SIZE);
        textField.setText("");
        textField.setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkBlockAndMeta) {
            TileTestCase.currentTestCase.registerAction(new SequenceActionCheckBlockMeta(world, x, y, z));
            mc.thePlayer.closeScreen();
        } else if (button == cancel) {
            mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        textField.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(TEXTURE);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
    }

    @Override
    protected void mouseClicked(BlockPos pos) {
        super.mouseClicked(pos);

        int xMin = (width - xSize) / 2;
        int yMin = (height - ySize) / 2;
        int xscreen = i - xMin;
        int yscreen = j - yMin;

        if (xscreen >= TEXT_X && yscreen >= TEXT_Y
                && xscreen <= TEXT_X + TEXT_WIDTH && yscreen <= TEXT_Y + TEXT_HEIGHT) {
            textField.setFocused(true);
        } else {
            textField.setFocused(false);
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (textField.isFocused()) {
            if (c == 13 || c == 27) {
                textField.setFocused(false);
            } else {
                textField.textboxKeyTyped(c, i);

            }
        } else {
            super.keyTyped(c, i);
        }
    }

    @Test
    public void typedString(){
        keyTyped msnt = new keyTyped();
        int a = 13;
        int z = 27;
        assertTrue("numbers are true", c.msnt = z.msnt);
    }
    @Test
    public void guiString1(){
        initGui msnt = new initGui();
        assertNotNull(msnt.checkBlockAndMeta("Check Block and Meta"));
    }
    @Test
    public void guiString2(){
        initGui msnt = new initGui();
        assertNotNull(msnt.checkTileMethod("Check Tile Method"));
    }
    @Test
    public void guiString3(){
        initGui msnt = new initGui();
        assertNotNull(msnt.checkTestComman("Check Test Command"));
    }
    @Test
    public void guiString4(){
        initGui msnt = new initGui();
        assertNotNull(msnt.cancel("Cancel"));
    }
    @Test
    public void guiClosed(){
        onGuiClosed asft = new onGuiClosed();
        assertTrue(asft.keyboard);
    }
    @Test
    public void actionDone1(){
        actionPerformed msnt = new actionPerformed();
        assertSame(msnt.button, msnt.checkBlockAndMeta);
    }
    @Test
    public void actionDone2(){
        actionPerformed msnt = new actionPerformed();
        assertSame(msnt.button, msnt.cancel);
    }

    @Test
    public void guiPlay(){
        initGui msnt = new initGui();
        assertNotNull(msnt.play("Play"));
    }

    @Test
    public void guiOptions(){
        initGui msnt = new initGui();
        assertNotNull(msnt.option("Options"));
    }

    @Test
    public void guiAccount(){
        initGui msnt = new initGui();
        assertNotNull(msnt.account("Account"));
    }

    @Test
    public void guiWelcome(){
        initGui msnt = new initGui();
        assertNotNull(msnt.string("Welcome to Minecraft Forge!"));
    }

    @Test
    public void forgeType(){
        keyTyped msnt = new keyTyped();
        int release = 1.15;
        int current = 1.15;
        assertTrue("Forge 1.15 are the same", release.msnt = current.msnt);
    }

    @Test
    public void testInputinMainMenu(){
        
        MainFrame frame;
        JTestField inputTest;
        String expResult;

        frame = new MainFrame();
        frame.setVisible(true);

        inputTest = (JTextField) TestUtils.getChildNamed(frame, "input");
        expResult = "JTextField unreachable in main menu";

        inputTest.setText("just testing");
        sleep(2000);

        inputTest.postActionEvent();
        sleep(2000);

        assertNotNull("Unable to reach JtextFeild component", inputTest);
        assertEquals(expResylt, inputTest.getText());
    }

    @Test
    public void guiMainTesting(){
        System.out.println("main menu");
        String[] args = null;
        MainFrame.main(args);
        fail("Main menu has failed");
    }
}

