package net.mine_diver.spasm.impl;

import net.mine_diver.spasm.api.transform.TransformationPhase;
import net.mine_diver.spasm.api.transform.TransformationResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import static net.mine_diver.spasm.api.transform.TransformationResult.PASS;
import static net.mine_diver.spasm.impl.SpASM.RAW_TRANSFORMERS;
import static net.mine_diver.spasm.impl.SpASM.TRANSFORMERS;


class MixinTransformerHook<T extends TreeTransformer & IMixinTransformer> extends MixinTransformerDelegate<T> {
    private final ThreadLocal<Deque<String>> transformationStack = ThreadLocal.withInitial(ArrayDeque::new);

    MixinTransformerHook(T delegate) {
        super(delegate);
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        var stack = transformationStack.get();
        if (basicClass == null || Objects.equals(stack.peek(), name)) return super.transformClassBytes(name, transformedName, basicClass);
        stack.push(name);
        var classLoader = Thread.currentThread().getContextClassLoader();
        basicClass = transform(name, basicClass, classLoader, TransformationPhase.BEFORE_MIXINS);
        basicClass = super.transformClassBytes(name, transformedName, basicClass);
        basicClass = transform(name, basicClass, classLoader, TransformationPhase.AFTER_MIXINS);
        stack.pop();
        return basicClass;
    }

    private static byte[] transform(String name, byte[] basicClass, ClassLoader classLoader, TransformationPhase phase) {
        SpASM.currentPhase = phase;
        for (int i = 0; i < RAW_TRANSFORMERS.size(); i++) {
            var transformer = RAW_TRANSFORMERS.get(i);
            if (!transformer.getPhases().contains(phase)) continue;
            var transformationResult = transformer.transform(classLoader, name, basicClass);
            if (transformationResult.isPresent()) basicClass = transformationResult.get();
        }
        var classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);
        if (TRANSFORMERS
                .stream()
                .filter(transformer -> transformer.getPhases().contains(phase))
                .map(classTransformer -> classTransformer.transform(classLoader, classNode))
                .reduce(PASS, TransformationResult::choose)
                == TransformationResult.SUCCESS) {
            var classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            basicClass = classWriter.toByteArray();
        }
        SpASM.currentPhase = null;
        return basicClass;
    }
}
