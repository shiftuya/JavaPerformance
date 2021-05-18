package lab8;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5)
@Fork(value = 5, warmups = 1)
@State(Scope.Benchmark)
public class Benchmark {

  @Param({"123456789", "123456789a", "abracadabra"})
  public String string;


  @org.openjdk.jmh.annotations.Benchmark
  public void benchmarkParseInt(Blackhole blackhole){
    blackhole.consume(Parsers.parseInt(string));
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void benchmarkIsDigit(Blackhole blackhole){
    blackhole.consume(Parsers.parseIsDigit(string));
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void benchmarkRegex(Blackhole blackhole){
    blackhole.consume(Parsers.parseRegex(string));
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void benchmarkAnotherRegex(Blackhole blackhole){
    blackhole.consume(Parsers.parseAnotherRegex(string));
  }
}