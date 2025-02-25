package net.mine_diver.spasm.impl;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;
import org.spongepowered.asm.transformers.TreeTransformer;

class MixinTransformerDelegate<T extends TreeTransformer & IMixinTransformer> extends TreeTransformer implements IMixinTransformer {
    
    private final T delegate;
    
    MixinTransformerDelegate(T delegate) {
        this.delegate = delegate;
    }

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public boolean isDelegationExcluded() {
		return delegate.isDelegationExcluded();
	}

	@Override
	public void audit(MixinEnvironment environment) {
		delegate.audit(environment);
	}

	@Override
	public List<String> reload(String mixinClass, ClassNode classNode) {
		return delegate.reload(mixinClass, classNode);
	}

	@Override
	public boolean computeFramesForClass(MixinEnvironment environment, String name, ClassNode classNode) {
		return delegate.computeFramesForClass(environment, name, classNode);
	}

	@Override
	public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
		return delegate.transformClassBytes(name, transformedName, basicClass);
	}

	@Override
	public byte[] transformClass(MixinEnvironment environment, String name, byte[] classBytes) {
		return delegate.transformClass(environment, name, classBytes);
	}

	@Override
	public boolean transformClass(MixinEnvironment environment, String name, ClassNode classNode) {
		return delegate.transformClass(environment, name, classNode);
	}

	@Override
	public byte[] generateClass(MixinEnvironment environment, String name) {
		return delegate.generateClass(environment, name);
	}

	@Override
	public boolean generateClass(MixinEnvironment environment, String name, ClassNode classNode) {
		return delegate.generateClass(environment, name, classNode);
	}

	@Override
	public IExtensionRegistry getExtensions() {
		return delegate.getExtensions();
	}
}
