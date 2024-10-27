package sircow.placeholder.screen;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import sircow.placeholder.Placeholder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class NewEnchantingTableBlockScreen extends HandledScreen<NewEnchantingTableBlockScreenHandler> {
    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/10_levels_enabled"),
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/20_levels_enabled"),
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/30_levels_enabled")
    };
    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/10_levels_disabled"),
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/20_levels_disabled"),
            Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/30_levels_disabled")
    };
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = Identifier.of(Placeholder.MOD_ID,"container/enchanting_table/enchantment_slot");
    private static final Identifier TEXTURE = Identifier.of(Placeholder.MOD_ID, "textures/gui/container/new_enchanting_table_gui.png");
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/entity/enchanting_table_book.png");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack;

    public NewEnchantingTableBlockScreen(NewEnchantingTableBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.stack = ItemStack.EMPTY;
    }

    @Override
    protected void init() {
        super.init();
        if (this.client != null) {
            this.BOOK_MODEL = new BookModel(this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
        }
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.doTick();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        if (this.client != null){
            for(int k = 0; k < 3; ++k) {
                double d = mouseX - (double)(i + 60);
                double e = mouseY - (double)(j + 14 + 19 * k);
                if (d >= 0.0 && e >= 0.0 && d < 108.0 && e < 19.0 && this.handler.onButtonClick(this.client.player, k)) {
                    if (this.client.interactionManager != null) {
                        this.client.interactionManager.clickButton(this.handler.syncId, k);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        this.drawBook(context, i, j, delta);
        EnchantingPhrases.getInstance().setSeed(this.handler.getSeed());
        int k = this.handler.getLapisCount();

        for(int l = 0; l < 3; ++l) {
            int m = i + 60;
            int n = m + 20;
            int o = this.handler.enchantmentPower[l];
            if (o == 0) {
                //context.drawGuiTexture(RenderLayer::getGuiTextured, ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
            } else {
                String string = "" + o;
                int p = 86 - this.textRenderer.getWidth(string);
                StringVisitable stringVisitable = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, p);
                int q = 6839882;
                if ((k < l + 1 || Objects.requireNonNull(Objects.requireNonNull(this.client).player).experienceLevel < o) && !Objects.requireNonNull(Objects.requireNonNull(this.client).player).getAbilities().creativeMode) {
                    //context.drawGuiTexture(RenderLayer::getGuiTextured, ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                    //context.drawGuiTexture(RenderLayer::getGuiTextured, LEVEL_DISABLED_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    context.drawTextWrapped(this.textRenderer, stringVisitable, n, j + 16 + 19 * l, p, (q & 16711422) >> 1);
                    q = 4226832;
                } else {
                    int r = mouseX - (i + 60);
                    int s = mouseY - (j + 14 + 19 * l);
                    if (r >= 0 && s >= 0 && r < 108 && s < 19) {
                        //context.drawGuiTexture(RenderLayer::getGuiTextured, ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                        q = 16777088;
                    } else {
                        //context.drawGuiTexture(RenderLayer::getGuiTextured, ENCHANTMENT_SLOT_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                    }

                    //context.drawGuiTexture(RenderLayer::getGuiTextured, LEVEL_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                    context.drawTextWrapped(this.textRenderer, stringVisitable, n, j + 16 + 19 * l, p, q);
                    q = 8453920;
                }
                context.drawTextWithShadow(this.textRenderer, string, n + 86 - this.textRenderer.getWidth(string), j + 16 + 19 * l + 7, q);
            }
        }
    }

    private void drawBook(DrawContext context, int x, int y, float delta) {
        float f = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle);
        context.draw();
        DiffuseLighting.method_34742();
        context.getMatrices().push();
        context.getMatrices().translate((float)x + 43.0F, (float)y + 31.0F, 100.0F);
        float h = 40.0F;
        context.getMatrices().scale(-40.0F, 40.0F, 40.0F);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
        context.getMatrices().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float i = -(1.0F - f) * 90.0F - 90.0F;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        float j = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.BOOK_MODEL.setPageAngles(0.0F, j, k, f);
        context.draw((vertexConsumers) -> {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
            this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
        });
        context.draw();
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client != null){
            float f = this.client.getRenderTickCounter().getTickDelta(false);
            super.render(context, mouseX, mouseY, f);
            this.drawMouseoverTooltip(context, mouseX, mouseY);
            if (this.client.player != null) {
                boolean bl = this.client.player.getAbilities().creativeMode;
                int i = this.handler.getLapisCount();

                for(int j = 0; j < 3; ++j) {
                    int k = this.handler.enchantmentPower[j];
                    if (this.client.world != null) {
                        Optional<RegistryEntry.Reference<Enchantment>> optional = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(this.handler.enchantmentId[j]);
                        if (optional.isPresent()) {
                            int l = this.handler.enchantmentLevel[j];
                            int m = j + 1;
                            if (this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0 && l >= 0) {
                                List<Text> list = Lists.newArrayList();
                                list.add(Text.translatable("container.enchant.clue", Enchantment.getName(optional.get(), l)).formatted(Formatting.WHITE));
                                if (!bl) {
                                    list.add(ScreenTexts.EMPTY);
                                    if (this.client.player.experienceLevel < k) {
                                        list.add(Text.translatable("container.enchant.level.requirement", this.handler.enchantmentPower[j]).formatted(Formatting.RED));
                                    } else {
                                        MutableText mutableText;
                                        if (m == 1) {
                                            mutableText = Text.translatable("container.enchant.lapis.one");
                                        } else {
                                            mutableText = Text.translatable("container.enchant.lapis.many", m);
                                        }

                                        list.add(mutableText.formatted(i >= m ? Formatting.GRAY : Formatting.RED));
                                        MutableText mutableText2;
                                        if (m == 1) {
                                            mutableText2 = Text.translatable("container.enchant.level.one");
                                        } else {
                                            mutableText2 = Text.translatable("container.enchant.level.many", m);
                                        }

                                        list.add(mutableText2.formatted(Formatting.GRAY));
                                    }
                                }

                                context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void doTick() {
        ItemStack itemStack = this.handler.getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
            this.stack = itemStack;

            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }

        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;

        for(int i = 0; i < 3; ++i) {
            if (this.handler.enchantmentPower[i] != 0) {
                bl = true;
                break;
            }
        }

        if (bl) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        float g = 0.2F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}
