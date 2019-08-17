package com.blutkrone.travellingplots.Util.CommandHandling.BranchingCommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class SignatureVerification {

    private List<Predicate<String>> verifiers = new ArrayList<>();
    private int minArgs;
    private int maxArgs;

    public SignatureVerification() {
        this(-1);
    }

    public SignatureVerification(int minArgs) {
        this(minArgs, -1);
    }

    public SignatureVerification(int minArgs, int maxArgs) {
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    public SignatureVerification add(LeafExecutor.TypeEnforcement... typeEnforcement) {
        for (LeafExecutor.TypeEnforcement enforcement : typeEnforcement) {
            verifiers.add((arg) -> enforcement.is(arg));
        }
        return this;
    }

    public SignatureVerification add(Set<String> constants, Function<String, String> preParser) {
        if (preParser == null) {
            verifiers.add(arg -> constants.contains(arg));
        } else {
            verifiers.add(arg -> constants.contains(preParser.apply(arg)));
        }

        return this;
    }

    public SignatureVerification add(Predicate<String> custom) {
        verifiers.add(custom);
        return this;
    }

    public SignatureVerification and(SignatureVerification other) {
        SignatureVerification self = this;
        return new SignatureVerification() {
            @Override
            public boolean verify(String... args) {
                return self.verify(args) && other.verify(args);
            }
        };
    }

    public SignatureVerification or(SignatureVerification other) {
        SignatureVerification self = this;
        return new SignatureVerification() {
            @Override
            public boolean verify(String... args) {
                return self.verify(args) || other.verify(args);
            }
        };
    }

    public SignatureVerification xor(SignatureVerification other) {
        SignatureVerification self = this;
        return new SignatureVerification() {
            @Override
            public boolean verify(String... args) {
                return self.verify(args) ^ other.verify(args);
            }
        };
    }

    public boolean verify(String... args) {
        if (verifiers.isEmpty()) return true;

        if (minArgs >= 0 && args.length >= minArgs) {
            if (maxArgs >= 0 && args.length <= minArgs) {
                if (args.length == 0)
                    return true;

                if (args.length != verifiers.size())
                    return false;

                for (int i = 0, argsLength = args.length; i < argsLength; i++) {
                    String arg = args[i];
                    if (!verifiers.get(i).test(arg))
                        return false;
                }

                return true;
            }
        }

        return false;
    }
}
