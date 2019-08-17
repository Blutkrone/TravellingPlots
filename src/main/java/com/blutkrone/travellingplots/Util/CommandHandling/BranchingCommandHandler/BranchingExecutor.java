package com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler;

import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.Pair;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class BranchingExecutor implements TabCompleter, CommandExecutor {

    private final String permissionSegment;
    public Map<String, BranchingExecutor> branch;
    public Map<String, List<Pair<Integer, LeafExecutor>>> leaf;
    private BranchingExecutor parent;

    /**
     * Abstract Branching Executor serves as a more readable solution
     * for branching out a command.
     * <p>
     * Note: Should we fail to find a leaf or a branch the ignore the input and
     * return 'false' as the method output.
     */
    public BranchingExecutor(String permissionSegment) {
        this.branch = new HashMap<>();
        this.leaf = new HashMap<>();
        this.permissionSegment = permissionSegment;
    }

    private static String[] noEmptySplit(String[] split) {
        return (split.length == 1) ? (split[0].equals("") ? new String[0] : split) : split;
    }

    /**
     * The "segment" of our permission which will be joined to verify
     * permission handling.
     */
    public String getPermissionSegment() {
        return permissionSegment;
    }

    /**
     * Create a new branch of the structure, a branch can either continue
     * with more branches or end as a leaf.
     *
     * @param leading  expected first argument
     * @param executor executor to pass the arguments *after* the first
     *                 argument.
     * @return self-reference
     */
    public BranchingExecutor createBranch(String leading, BranchingExecutor executor) {
        this.branch.put(leading, executor);
        executor.parent = this;
        return this;
    }

    /**
     * Create a new leaf of the structure, a leaf will pass everything after
     * the leading argument to the TabExecutor for further processing.
     *
     * @param leading  expected first argument
     * @param executor executor to pass the arguments *after* the first
     *                 argument.
     * @param priority if multiple "leading" entries exist that fulfill the {@link LeafExecutor#isValid(String...)}
     *                 predicate we will enter the one with the highest priority.
     * @return self-reference
     */
    public BranchingExecutor createLeaf(String leading, LeafExecutor executor, int priority) {
        this.leaf.putIfAbsent(leading, new ArrayList<>());
        this.leaf.get(leading).add(Pair.create(priority, executor));
        executor.setParent(this);
        return this;
    }

    /**
     * Create a new leaf of the structure, a leaf will pass everything after
     * the leading argument to the TabExecutor for further processing.
     *
     * @param leading  expected first argument
     * @param executor executor to pass the arguments *after* the first
     *                 argument.
     * @return self-reference
     */
    public BranchingExecutor createLeaf(String leading, LeafExecutor executor) {
        return createLeaf(leading, executor, 0);
    }

    /**
     * Recursively fetch all leafs.
     *
     * @return List of the leafs, alphabetically sorted by generating a string from
     * {@link LeafExecutor#help()} through {@link BaseComponent#toLegacyText()}
     * and comparing them over {@link String#compareTo(String)}
     */
    public List<Pair<Integer, LeafExecutor>> getLeafs() {
        List<Pair<Integer, LeafExecutor>> leafs = new ArrayList<>();
        this.leaf.values().forEach(leafs::addAll);
        leafs.sort(
                (o1, o2) -> {
                    String h1 = BaseComponent.toLegacyText(o1.getValue().help());
                    String h2 = BaseComponent.toLegacyText(o2.getValue().help());
                    int surface = h1.compareTo(h2);
                    return surface == 0 ? Integer.compare(o1.getKey(), o2.getKey()) : surface;
                }
        );

        for (BranchingExecutor B : this.branch.values())
            leafs.addAll(B.getLeafs());

        return leafs;
    }

    public BranchingExecutor getParent() {
        return parent;
    }

    /**
     * Joint KeySet of leafs and branches, note that conflicts may appear
     * should a leaf and branch share the same leading argument.
     */
    private Collection<String> joinedKeySet() {
        Collection<String> joined = new ArrayList<>(this.branch.keySet());
        joined.addAll(this.leaf.keySet());
        return joined;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String flat = String.join(" ", args);
        if (flat.isEmpty()) flat = "help";

        for (String expected : joinedKeySet()) {
            if (this.leaf.containsKey(expected)) {
                if (flat.startsWith(expected)) {
                    Pair<Integer, LeafExecutor> highest = null;
                    List<LeafExecutor> allChoices = new ArrayList<>();
                    for (Pair<Integer, LeafExecutor> current : this.leaf.get(expected)) {
                        String[] breakdown = flat.replaceFirst(expected, "").trim().split(" ");
                        if (breakdown.length == 1 && breakdown[0].isEmpty()) breakdown = new String[0];
                        if (current.getValue().isValid(breakdown)) {
                            if (current.getValue().verifyType(sender) && current.getValue().verifyPermission(sender)) {
                                allChoices.add(current.getValue());
                                if (highest == null || highest.getKey() < current.getKey()) {
                                    highest = current;
                                }
                            }
                        }
                    }

                    if (highest == null && allChoices.isEmpty()) {
                        sender.sendMessage("§cUnknown Command: " + flat);
                    } else if (highest == null) {
                        sender.sendMessage("You do not have any of the following permissions: " +
                                allChoices.stream().map(LeafExecutor::permissionNode).filter(Objects::nonNull).collect(Collectors.toList()));
                    } else {
                        highest.getValue().onCommand(sender, command, label, noEmptySplit(flat.replaceFirst(Pattern.quote(expected), "").trim().split(" ")));
                    }

                    return true;
                }
            } else if (this.branch.containsKey(expected)) {
                if (flat.startsWith(expected)) {
                    return this.branch.get(expected).onCommand(
                            sender, command, label, noEmptySplit(flat.replaceFirst(Pattern.quote(expected), "").trim().split(" "))
                    );
                }
            }
        }

        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String flat = String.join(" ", args);
        for (String expected : joinedKeySet()) {
            if (this.leaf.containsKey(expected)) {
                if (flat.startsWith(expected)) {
                    Pair<Integer, LeafExecutor> highest = null;
                    for (Pair<Integer, LeafExecutor> current : this.leaf.get(expected)) {
                        if (current.getValue().isValid(flat.replaceFirst(expected, "").trim().split(" "))) {
                            if (current.getValue().verifyType(sender) && current.getValue().verifyPermission(sender)) {
                                if (highest == null || highest.getKey() < current.getKey())
                                    highest = current;
                            }
                        }
                    }

                    if (highest != null) {
                        return highest.getValue().onTabComplete(sender, command, alias, noEmptySplit(flat.replaceFirst(Pattern.quote(expected), "").trim().split(" ")));
                    }
                }
            } else if (this.branch.containsKey(expected)) {
                if (flat.startsWith(expected)) {
                    return this.branch.get(expected).onTabComplete(
                            sender, command, alias, noEmptySplit(flat.replaceFirst(Pattern.quote(expected), "").trim().split(" "))
                    );
                }
            }
        }

        return null;
    }
}
