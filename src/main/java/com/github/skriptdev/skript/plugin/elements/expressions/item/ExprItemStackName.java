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
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExprItemStackName implements Expression<Object> {

    public static void register(SkriptRegistration reg) {
        reg.newExpression(ExprItemStackName.class, Object.class, false,
                "item [:message] (:name|description) of %itemstack/item%")
            .name("Item Name/Description")
            .description("Get the name/description of an Item/ItemStack.",
                "The optional `message` will return as a Message otherwise as a string.",
                "Name/description of an ItemStack can be set, but cannot be set for an Item.")
            .examples("set {_name} to item name of {_item}",
                "set {_i} to itemstack of ingredient_stick",
                "set item name of {_i} to formatted \"<blue>My Cool Item\"",
                "set item description of {_i} to \"This item is special\", formatted \"<gradient:#31F527:#27EBF5>REALLY SPECIAL\" and \"I hope you enjoy!\"",
                "add {_i} to inventory of player")
            .since("1.1.0")
            .register();
    }

    private boolean formatted;
    private Expression<?> item;
    private boolean name;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.item = expressions[0];
        this.formatted = parseContext.hasMark("message");
        this.name = parseContext.hasMark("name");
        return true;
    }

    @Override
    public Object[] getValues(TriggerContext triggerContext) {
        Optional<?> single = this.item.getSingle(triggerContext);
        if (single.isEmpty()) return null;
        Object owner = single.get();

        if (owner instanceof Item it) {
            ItemTranslationProperties prop = it.getTranslationProperties();
            if (this.name) {
                String name = prop.getName();
                if (name == null) return null;

                Message translation = Message.translation(name);
                if (this.formatted) {
                    return new Object[]{translation};
                } else {
                    return new Object[]{MessageUtil.toAnsiString(translation).toAnsi()};
                }
            } else {
                String description = prop.getDescription();
                if (description == null) return null;

                Message translation = Message.translation(description);
                if (this.formatted) {
                    return new Object[]{translation};
                } else {
                    return new Object[]{MessageUtil.toAnsiString(translation).toAnsi()};
                }
            }
        } else if (owner instanceof ItemStack itemStack) {
            if (this.name) {
                Message displayName = itemStack.getDisplayName();

                if (this.formatted) {
                    return new Object[]{displayName};
                } else {
                    return new Object[]{MessageUtil.toAnsiString(displayName).toAnsi()};
                }
            } else {
                Message displayDescription = itemStack.getDisplayDescription();

                if (this.formatted) {
                    return new Object[]{displayDescription};
                } else {

                    return new Object[]{MessageUtil.toAnsiString(displayDescription).toAnsi()};
                }
            }
        } else {
            return null;
        }
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
        Optional<?> single = this.item.getSingle(ctx);
        if (single.isEmpty()) return;

        if (single.get() instanceof ItemStack itemStack) {
            ItemDisplayMetadata meta = itemStack.getFromMetadataOrDefault(ItemDisplayMetadata.KEY, ItemDisplayMetadata.CODEC);
            if (this.name) {
                meta.setName(message);
            } else {
                meta.setDescription(message);
            }
            ItemStack itemStack1 = itemStack.withMetadata(ItemDisplayMetadata.KEYED_CODEC, meta);
            this.item.change(ctx, ChangeMode.SET, new ItemStack[]{itemStack1});
        }
    }

    @Override
    public Class<?> getReturnType() {
        if (this.formatted) {
            return Message.class;
        } else {
            return String.class;
        }
    }

    @Override
    public String toString(TriggerContext triggerContext, boolean debug) {
        String f = this.formatted ? "message" : "";
        String type = this.name ? "name" : "description";
        return String.format("item %s %s of %s", f, type, this.item.toString(triggerContext,debug));
    }

}
