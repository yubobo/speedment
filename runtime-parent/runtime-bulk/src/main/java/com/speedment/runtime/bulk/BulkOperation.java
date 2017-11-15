package com.speedment.runtime.bulk;

import com.speedment.runtime.bulk.internal.BulkOperationBuilder;
import com.speedment.runtime.config.identifier.HasTableIdentifier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public interface BulkOperation {

    Stream<? extends Operation<?>> operations();

    public static Builder builder() {
        return new BulkOperationBuilder();
    }

    interface Builder {

        /**
         * Creates and returns a new Persist builder that can be used to build a
         * Persist Operation.
         *
         * @param <ENTITY> type
         * @param manager to use when persisting entities
         * @return Persist builder that can be used to build a Persist Operation
         */
        <ENTITY> Persist<ENTITY> persist(HasTableIdentifier<ENTITY> manager);

        /**
         * Creates and returns a new Update builder that can be used to build an
         * Update Operation.
         *
         * @param <ENTITY> type
         * @param manager to use when updating entities
         * @return Update builder that can be used to build an Update Operation
         */
        <ENTITY> Update<ENTITY> update(HasTableIdentifier<ENTITY> manager);

        /**
         * Creates and returns a new Remove builder that can be used to build a
         * Remove Operation.
         * <p>
         * If {@link Remove#where(java.util.function.Predicate) } is not called
         * on the Remove builder, then all entities will be removed.
         *
         * @param <ENTITY> type
         * @param manager to use when removing entities
         * @return Remove builder that can be used to build a Remove Operation
         */
        <ENTITY> Remove<ENTITY> remove(HasTableIdentifier<ENTITY> manager);

        /**
         * Creates and returns a new immutable BulkOperation comprising all the
         * steps defined by the Builder.
         *
         * @return a new immutable BulkOperation comprising all the steps
         * defined by the Builder
         */
        BulkOperation build();

        interface Persist<ENTITY> extends HasValues<ENTITY, Builder> {
        }

        interface Update<ENTITY> extends Builder, HasWhere<ENTITY, Update<ENTITY>>, HasSet<ENTITY, Update<ENTITY>> {
        }

        interface Remove<ENTITY> extends Builder, HasWhere<ENTITY, Remove<ENTITY>> {
        }

        interface HasWhere<ENTITY, B> {

            /**
             * Specifies for what entities the operation should be carried out.
             *
             * @param filter to apply
             * @return a builder where the given filter has been applied
             */
            B where(Predicate<? super ENTITY> filter);
        }

        interface HasSet<ENTITY, B> {

            /**
             * Specifies an entity mapper for the operation whereby the mapper
             * will mutate the entity in some way when applied.
             *
             * @param mapper to apply when updating entities
             * @return a builder where the given mapper has been applied
             */
            B compute(Function<? super ENTITY, ? extends ENTITY> mapper);

            /**
             * Specifies an entity consumer for the operation whereby the
             * consumer will mutate the entity in some way when applied.
             *
             * @param consumer to apply when updating entities
             * @return a builder where the given consumer has been applied
             */
            B set(Consumer<? super ENTITY> consumer);
        }

        interface HasValues<ENTITY, B> {

            /**
             * Specifies a Supplier of a Stream, whereby the stream's elements
             * will be persisted using the given manager.
             *
             * @param generatorSupplier to apply when constructing entities
             * @return a builder where the given generatorSupplier has been
             * applied
             */
            B values(Supplier<Stream<? extends ENTITY>> generatorSupplier);
        }

    }

}
