package com.github.skriptdev.skript.plugin.elements.expressions.other;

import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExprCoordinates extends PropertyExpression<Object, Number> {

    public static void register(SkriptRegistration reg) {
        reg.newPropertyExpression(ExprCoordinates.class, Number.class,
                "(0:x|1:y|2:z) coord[inate]", "locations/vector3is/vector3ds/vector3fs")
            .name("Location/Vector Coordinates")
            .description("Get the coordinates of a location/vector")
            .examples("set {_y} to y coord of location of player",
                "add 10 to y coord of {_loc}",
                "subtract 15 from z coord of {_loc}",
                "set x coord of {_vec} to 22")
            .since("1.1.0")
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, ParseContext parseContext) {
        this.pattern = parseContext.getNumericMark();
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public @Nullable Number getProperty(@NotNull Object owner) {
        switch (owner) {
            case Location location -> {
                Vector3d position = location.getPosition();
                return switch (this.pattern) {
                    case 1 -> position.getY();
                    case 2 -> position.getZ();
                    default -> position.getX();
                };
            }
            case Vector3i position -> {
                return switch (this.pattern) {
                    case 1 -> position.getY();
                    case 2 -> position.getZ();
                    default -> position.getX();
                };
            }
            case Vector3d position -> {
                return switch (this.pattern) {
                    case 1 -> position.getY();
                    case 2 -> position.getZ();
                    default -> position.getX();
                };
            }
            case Vector3f position -> {
                return switch (this.pattern) {
                    case 1 -> position.getY();
                    case 2 -> position.getZ();
                    default -> position.getX();
                };
            }
            default -> {
            }
        }
        return null;
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return Optional.of(new Class<?>[]{Number.class});
        }
        return Optional.empty();
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void change(@NotNull TriggerContext ctx, @NotNull ChangeMode changeMode, Object @NotNull [] changeWith) {
        if (changeWith == null || changeWith.length == 0 || !(changeWith[0] instanceof Number number) || number == null) {
            return;
        }

        for (Object o : getOwner().getArray(ctx)) {
            if (o instanceof Location location) {
                Vector3d pos = location.getPosition();
                changeVec3d(pos, changeMode, number);
            } else if (o instanceof Vector3d pos) {
                changeVec3d(pos, changeMode, number);
            } else if (o instanceof Vector3i pos) {
                changeVec3i(pos, changeMode, number);
            } else if (o instanceof Vector3f pos) {
                changeVec3f(pos, changeMode, number);
            }
        }
    }

    private void changeVec3d(Vector3d pos, ChangeMode mode, Number number) {
        double oldValue = switch (this.pattern) {
            case 1 -> pos.getY();
            case 2 -> pos.getZ();
            default -> pos.getX();
        };

        double changeValue = number.doubleValue();
        double newValue = changeValue;
        if (mode == ChangeMode.ADD) {
            newValue = oldValue + changeValue;
        } else if (mode == ChangeMode.REMOVE) {
            newValue = oldValue - changeValue;
        }

        switch (this.pattern) {
            case 1 -> pos.setY(newValue);
            case 2 -> pos.setZ(newValue);
            default -> pos.setX(newValue);
        }
    }

    private void changeVec3f(Vector3f pos, ChangeMode mode, Number number) {
        float oldValue = switch (this.pattern) {
            case 1 -> pos.getY();
            case 2 -> pos.getZ();
            default -> pos.getX();
        };

        float changeValue = number.floatValue();
        float newValue = changeValue;
        if (mode == ChangeMode.ADD) {
            newValue = oldValue + changeValue;
        } else if (mode == ChangeMode.REMOVE) {
            newValue = oldValue - changeValue;
        }

        switch (this.pattern) {
            case 1 -> pos.setY(newValue);
            case 2 -> pos.setZ(newValue);
            default -> pos.setX(newValue);
        }
    }

    private void changeVec3i(Vector3i pos, ChangeMode mode, Number number) {
        int oldValue = switch (this.pattern) {
            case 1 -> pos.getY();
            case 2 -> pos.getZ();
            default -> pos.getX();
        };

        int changeValue = number.intValue();
        int newValue = changeValue;
        if (mode == ChangeMode.ADD) {
            newValue = oldValue + changeValue;
        } else if (mode == ChangeMode.REMOVE) {
            newValue = oldValue - changeValue;
        }

        switch (this.pattern) {
            case 1 -> pos.setY(newValue);
            case 2 -> pos.setZ(newValue);
            default -> pos.setX(newValue);
        }
    }

}
