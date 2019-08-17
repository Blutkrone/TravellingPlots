package com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;


public abstract class LeafExecutor implements TabCompleter, CommandExecutor {

    private final boolean isPlayerExclusive;
    private final boolean isHidden;
    private final SignatureVerification verification;
    private BranchingExecutor parent;

    public LeafExecutor(boolean isPlayerExclusive, boolean isHidden, SignatureVerification verification) {
        this.isPlayerExclusive = isPlayerExclusive;
        this.isHidden = isHidden;
        this.verification = verification;
    }

    public static BaseComponent[] legacy(String line) {
        return TextComponent.fromLegacyText(line);
    }

    public boolean verifyType(Permissible target) {
        return !isPlayerExclusive || target instanceof Player;
    }

    public boolean verifyPermission(Permissible target) {
        if (target.isOp()) return true;
        if (permissionNode() == null) return true;
        // StringBuilder permission = new StringBuilder();
        // BranchingExecutor parent = getParent();
        // while (parent != null) {
        //     if (parent.getPermissionSegment() != null)
        //         permission.insert(0, parent.getPermissionSegment() + ".");
        // }
        // if (permissionNode() != null)
        //     permission.append(".").append();
        return target.hasPermission(permissionNode());
    }

    /**
     * Check if the remaining arguments are fitting our criteria,
     *
     * @param args Arguments to identify, note that these are passed with
     *             the identifier trimmed away, i.E: if the full args were
     *             "arg0 arg1 arg2 arg3" but the leaf path was "arg0 arg1"
     *             args will be made up of "arg2 arg3".
     * @return true if we can run the leaf-executor
     */
    public boolean isValid(String... args) {
        if (verification == null) return true;
        return verification.verify(args);
    }

    /**
     * Message to display if {@link #isValid(String...)} returns
     * false, use {@link ComponentBuilder#create()} for building
     * the message.
     *
     * @return Failure message
     */
    public abstract BaseComponent[] usage();

    /**
     * Message to display upon the showing the help, note that
     * this shouldn't return null
     *
     * @return Help message
     */
    public abstract BaseComponent[] help();

    /**
     * Assigned permission node. Note that returning null
     * will allow the usage independent of permission nodes.
     *
     * @return Permission node (null for none.)
     */
    public abstract String permissionNode();

    /**
     * Don't show the branch in the recursive help detector.
     */
    public boolean hide() {
        return isHidden;
    }

    /**
     * The parent of this leaf executor (If null means that we
     * are a leaf-executor only root.)
     *
     * @return the parent node.
     */
    public BranchingExecutor getParent() {
        return parent;
    }

    void setParent(BranchingExecutor parent) {
        this.parent = parent;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }

    /**
     * Interface for getType enforcement
     */
    public enum TypeEnforcement {
        STRING(from -> true),
        BOOLEAN(from -> from.equalsIgnoreCase("false") || from.equalsIgnoreCase("true")),
        DECIMAL(from -> {
            try {
                Double.parseDouble(from);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }),
        RELATIVE_DECIMAL(from -> {
            try {
                if (from.startsWith("~")) {
                    Double.parseDouble(from.substring(1));
                    return true;
                } else {
                    Double.parseDouble(from);
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }),
        INTEGER(from -> {
            try {
                Integer.parseInt(from);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        private TypeEnforcer enforcer;

        TypeEnforcement(TypeEnforcer enforcer) {
            this.enforcer = enforcer;
        }

        public boolean is(String ff, String... from) {
            if (!enforcer.is(ff)) return false;
            else for (String f : from) if (!enforcer.is(f)) return false;

            return true;
        }

        private interface TypeEnforcer {
            boolean is(String from);
        }
    }

    public interface CommandInterface {
        boolean onCommand(CommandSender sender, Command command, String label, String[] args);

        List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
    }
}
