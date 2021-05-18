# JMH

Run: `gradle jmh`

## Results

All results are in nanoseconds per operation.

| Method | 123456789 | 123456789a | abracadabra |
| --- |:---:|:---:|:---:|
| parseInt | 31.289 ± 1.082 | 2062.721 ± 34.838 | 1763.039 ±157.800 |
| isDigit | 25.101 ± 0.528 | 27.029 ± 1.125 | 16.512 ± 0.463 |
| Regex | 55.953 ± 5.587 | 90.886 ± 9.545 | 38.493 ± 0.656 |
| Lazy Regex | 203.917 ± 20.715 | 290.132 ± 27.872 | 240.766 ± 14.243 |

`Character.isDigit` method shows the highest performance in all trials.

`Integer.parseInt` is the slowest in strings that do not represent integers, although the result for
the correct string is comparable to that of `Character.isDigit`. The reason for this effect is that
the `parseInt` method throws an exception if the input string does not represent an integer, which
is a comparatively expensive operation. In case of correct input, `parseInt` is slightly slower
than `isDigit` because it includes several extra operations such as check for max and min integer
values.

The regex method is slower than `Characted.isDigit` because the `Matcher.match` method includes
significantly more operations than the `Character.isDigit` method.

`isDigit` and Regex work faster in case of the third test because it is clear already at the first
character that the string is not correct, so they do not have to check subsequent chars.

The pre-compiled Regex works significantly (3 to 6 times depending on the input string)
faster than the `String.matches` method. The latter consists of regular expression compilation into
the pattern and matching the input string against this pattern. Therefore, the time difference
between pre-compiled and lazy versions is due to the time spent on pattern compilation which
consists of quite a lot of operations.