# JMH

Run: `gradle jmh`

## Results

All results are in nanoseconds per operation.

| Method | 123456789 | 123456789a | abracadabra |
| --- |:---:|:---:|:---:|
| parseInt | 23.643 ± 1.065 | 2080.594 ± 143.619 | 2069.856 ± 152.084 |
| isDigit | 22.609 ± 2.116 | 24.916 ± 2.458 | 16.442 ± 1.091 |
| Regex | 74.191 ± 14.764 | 11.854 ± 14.330 | 53.428 ± 0.535 |

`Character.isDigit` method shows the highest performance in all trials.

`Integer.parseInt` is the slowest in strings that do not represent integers, although
the result for the correct string is comparable to that of `Character.isDigit`. The
reason for this effect is that the `parseInt` method throws an exception if the input
string does not represent an integer, which is a comparatively expensive operation. In
case of correct input, `parseInt` is slightly slower than `isDigit` because it includes
several extra operations such as check for max and min integer values.

The regex method is slower than `Characted.isDigit` because the `Matcher.match` method
includes significantly more operations than the `Character.isDigit` method.