package com.github.skriptdev.skript.plugin.elements.expressions.item;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTranslationProperties;
import com.hypixel.hytale.server.core.asset.type.item.config.metadata.ItemDisplayMetadata;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.util.MessageUtil;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprItemStackName extends PropertyExpression<Object, String> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprItemStackName.class, String.class,
                "item (:name|description)", "itemstack/item")
            .name("Item Name/Description")
            .description("Get the name/description of an Item/ItemStack.",
                "Name/description of an ItemStack can be set, but cannot be set for an Item.")
            .examples("set {_i} to itemstack of ingredient_stick",
                "set item name of {_i} to formatted \"<blue>My Cool Item\"",
                "set item description of {_i} to \"This item is special\", formatted \"<gradient:#31F527:#27EBF5>REALLY SPECIAL\" and \"I hope you enjoy!\"",
                "add {_i} to inventory of player")
            .since("1.1.0")
            .register();
    }

    private boolean name;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.name = parseContext.hasMark("name");
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public @Nullable String getProperty(@NotNull Object owner) {
        Item item;
        if (owner instanceof Item i) {
            item = i;
        } else if (owner instanceof ItemStack itemStack) {
            item = itemStack.getItem();
        } else {
            return null;
        }
        ItemTranslationProperties translation = item.getTranslationProperties();
        String prop = this.name ? translation.getName() : translation.getDescription();
        if (prop == null) return null;

        return MessageUtil.toAnsiString(Message.translation(prop)).toAnsi();
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            if (this.name) {
                return Optional.of(new Class<?>[]{String.class, Message.class});
            } else {
                return Optional.of(new Class<?>[]{String[].class, Message[].class});
            }
        }
        return Optional.empty();
    }

    @Override
    public void change(TriggerContext ctx, ChangeMode changeMode, Object[] changeWith) {
        Message message = null;
        if (changeMode == ChangeMode.SET) {
            if (this.name) {
                if (changeWith != null && changeWith.length > 0) {
                    if (changeWith[0] instanceof Message m) {
                        message = m;
                    } else if (changeWith[0] instanceof String s) {
                        message = Message.raw(s);
                    }
                }
            } else if (changeWith != null) {
                message = Message.empty();
                for (int i = 0; i < changeWith.length; i++) {
                    if (changeWith[i] instanceof Message m) {
                        message.insert(m);
                    } else if (changeWith[i] instanceof String s) {
                        message.insert(s);
                    }
                    if (i < changeWith.length - 1) {
                        message.insert("\n");
                    }
                }
            }
        }
        Optional<?> single = getOwner().getSingle(ctx);
        if (single.isEmpty()) return;

        if (single.get() instanceof ItemStack itemStack) {
            ItemDisplayMetadata meta = itemStack.getFromMetadataOrDefault(ItemDisplayMetadata.KEY, ItemDisplayMetadata.CODEC);
            if (this.name) {
                meta.setName(message);
            } else {
                meta.setDescription(message);
            }
            ItemStack itemStack1 = itemStack.withMetadata(ItemDisplayMetadata.KEYED_CODEC, meta);
            getOwner().change(ctx, ChangeMode.SET, new ItemStack[]{itemStack1});
        }
    }

}
