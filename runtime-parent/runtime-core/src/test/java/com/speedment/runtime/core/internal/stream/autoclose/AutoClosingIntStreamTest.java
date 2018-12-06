package com.speedment.runtime.core.internal.stream.autoclose;

import com.speedment.runtime.core.internal.util.java9.Java9StreamUtil;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.BaseStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.speedment.runtime.core.internal.stream.autoclose.AutoClosingStreamTestUtil.MAX_VALUE;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

final class AutoClosingIntStreamTest extends AbstractAutoClosingStreamTest<Integer, IntStream> {

    @Override
    IntStream createStream() {
        return IntStream.iterate(0, i -> i++).limit(32);
    }

    @Override
    IntStream createAutoclosableStream() {
        return new AutoClosingIntStream(createStream(), new HashSet<>(), false);
    }

    @Override
    void assertTypedArraysEquals(Object expected, Object actual) {
        assertArrayEquals((int[]) expected, (int[]) actual);
    }

    @Override
    Stream<NamedUnaryOperator<IntStream>> intermediateJava8Operations() {
        return Stream.<NamedUnaryOperator<IntStream>>of(
            NamedUnaryOperator.of("filter", s -> s.filter(i -> i < MAX_VALUE / 2)),
            NamedUnaryOperator.of("map", s -> s.map(i -> i + 1)),
            // mapToInt, mapToLong, mapToDouble
            NamedUnaryOperator.of("flatMap", s -> s.flatMap(i -> IntStream.of(i, i + 1))),
            // flatMapToInt, flatMapToLong, flatMapToDouble
            NamedUnaryOperator.of("distinct", IntStream::distinct),
            NamedUnaryOperator.of("sorted", IntStream::sorted),
            NamedUnaryOperator.of("peek", s -> s.peek(intBlackHole())),
            NamedUnaryOperator.of("limit", s -> s.limit(MAX_VALUE - 2)),
            NamedUnaryOperator.of("skip", s -> s.skip(3)),
            NamedUnaryOperator.of("parallel", BaseStream::parallel),
            NamedUnaryOperator.of("sequential", BaseStream::sequential),
            NamedUnaryOperator.of("unordered", BaseStream::unordered)
        );

    }

    @Override
    Stream<NamedUnaryOperator<IntStream>> intermediateJava9Operations() {
        return Stream.of(
            NamedUnaryOperator.of("takeWhile", s -> Java9StreamUtil.takeWhile(s, i -> i < MAX_VALUE / 3)),
            NamedUnaryOperator.of("dropWhile", s -> Java9StreamUtil.dropWhile(s, i -> i < MAX_VALUE / 3))
        );

    }

    @Override
    Stream<NamedFunction<IntStream, Object>> terminatingOperations() {
        return Stream.<NamedFunction<IntStream, Object>>of(
            NamedFunction.of("count", IntStream::count),
            NamedFunction.of("forEach", (IntStream s) -> {
                s.forEach(intBlackHole());
                return void.class;
            }),
            NamedFunction.of("forEachOrdered", (IntStream s) -> {
                s.forEachOrdered(intBlackHole());
                return void.class;
            }),
            NamedFunction.of("toArray", IntStream::toArray),
            NamedFunction.of("reduce2Arg", s -> s.reduce(0, (a, b) -> a + b)),
            NamedFunction.of("reduce1Arg", s -> s.reduce((a, b) -> a + b)),
            NamedFunction.of("collect3Arg", s -> s.collect(IntAdder::new, IntAdder::add, IntAdder::merge).intValue()),
            NamedFunction.of("min", IntStream::min),
            NamedFunction.of("max", IntStream::max),
            NamedFunction.of("anyMatch", s -> s.anyMatch(i -> i > 2)),
            NamedFunction.of("allMatch", s -> s.allMatch(i -> i == 2)),
            NamedFunction.of("noneMatch", s -> s.noneMatch(i -> i == 2)),
            NamedFunction.of("findFirst", IntStream::findFirst),
            NamedFunction.of("findAny", IntStream::findAny),
            NamedFunction.of("summaryStatistics", s -> s.summaryStatistics().getSum()),

            // Simulated ones for mapToX and flatMapTox etc.
            NamedFunction.of(".boxed.collect", s -> s.boxed().collect(toList())),
            NamedFunction.of(".asDoubleStream.sum", s -> s.asDoubleStream().sum()),
            NamedFunction.of(".mapToLong.sum", s -> s.mapToLong(i -> i).sum()),
            NamedFunction.of(".mapToDouble.sum", s -> s.mapToDouble(i -> i).sum()),
            NamedFunction.of(".mapToObj.sum", s -> s.mapToObj(i -> i).collect(toList())),
            NamedFunction.of(".mapToDouble.sum", s -> s.mapToDouble(i -> i).sum())
        );


    }

    /**
     * Mutable adder that is thread safe.
     */
    private final static class IntAdder {

        private final AtomicInteger sum;

        private IntAdder() {
            this.sum = new AtomicInteger();
        }

        void add(int value) {
            sum.addAndGet(value);
        }

        void merge(IntAdder other) {
            sum.addAndGet(other.intValue());
        }

        int intValue() {
            return sum.intValue();
        }

    }


}