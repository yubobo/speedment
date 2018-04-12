package com.speedment.runtime.field.internal.expression;

import com.speedment.common.function.ToByteFunction;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.expression.FieldToByte;

/**
 * Default implementation of {@link FieldToByte}.
 *
 * @author Emil Forslund
 * @since  3.1.0
 */
public final class FieldToByteImpl<ENTITY, V>
extends AbstractFieldMapper<ENTITY, V, ToByteFunction<V>>
implements FieldToByte<ENTITY, V> {

    public FieldToByteImpl(ReferenceField<ENTITY, ?, V> field,
                           ToByteFunction<V> vToByteFunction) {
        super(field, vToByteFunction);
    }

    @Override
    public byte applyAsByte(ENTITY entity) {
        return mapper.applyAsByte(field.get(entity));
    }
}