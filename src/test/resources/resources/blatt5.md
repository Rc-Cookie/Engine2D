# Datenstrukturen und Algorithmen - Blatt 5

### John Müller (434701), Leon Müller (434377)

---

## Aufgabe 5 - Quicksort

```
[7|3|6|4|5|1|8|9|2]

[𝟏(𝟐)6|4|5|7|8|9|3]

[𝟏|𝟐(𝟑)4|5|7|8|9|6]

[𝟏|𝟐|𝟑|4|5(𝟔)8|9|7]

[𝟏|𝟐|𝟑|𝟒(𝟓)𝟔|8|9|7]

[𝟏|𝟐|𝟑|𝟒|𝟓|𝟔(𝟕)9|8]

[𝟏|𝟐|𝟑|𝟒|𝟓|𝟔|𝟕(𝟖)𝟗]
```

## Aufgabe 6 - Mergesort

---

```
[7|3|6|4|5|1|8|9|2]

[3|7|6|4|5|1|8|9|2]
    | | | | | | |

[3|6|7|4|5|1|8|9|2]
      | | | | | |

[3|6|7|4|5|1|8|9|2]
      |   | | | |

[3|4|5|6|7|1|8|9|2]
          | | | |

[3|4|5|6|7|1|8|9|2]
          |   | |

[3|4|5|6|7|1|8|2|9]
          |   |

[3|4|5|6|7|1|2|8|9]
          |

[1|2|3|4|5|6|7|8|9]
```

## Aufgabe 7 - Heapsort

---

```
Heap aufbauen:

[7|3|6|4|5|1|8|9|2]

[7|5|6|4|3|1|8|9|2]
   ^     ^

[7|5|6|9|3|1|8|4|2]
       ^       ^

[7|9|6|5|3|1|8|4|2]
   ^   ^

[9|7|6|5|3|1|8|4|2]
 ^ ^

[9|7|8|5|3|1|6|4|2]
     ^       ^

Heap auslesen:

[2|7|8|5|3|1|6|4‖𝟵]
 ^               ^

[8|7|2|5|3|1|6|4‖𝟵]
 ^   ^

[8|7|6|5|3|1|2|4‖𝟵]
     ^       ^

[4|7|6|5|3|1|2‖8|𝟵]
 ^             ^

[7|4|6|5|3|1|2‖8|𝟵]
 ^ ^

[7|5|6|4|3|1|2‖8|𝟵]
   ^   ^

[2|5|6|4|3|1‖7|8|𝟵]
 ^           ^

[6|5|2|4|3|1‖7|8|𝟵]
 ^   ^

[1|5|2|4|3‖6|7|8|𝟵]
 ^         ^

[5|1|2|4|3‖6|7|8|𝟵]
 ^ ^

[5|4|2|1|3‖6|7|8|𝟵]
   ^   ^

[3|4|2|1‖5|6|7|8|𝟵]
 ^       ^

[4|3|2|1‖5|6|7|8|𝟵]
 ^ ^

[1|3|2‖4|5|6|7|8|𝟵]
 ^     ^

[3|1|2‖4|5|6|7|8|𝟵]
 ^ ^

[2|1‖3|4|5|6|7|8|𝟵]
 ^   ^

[1‖2|3|4|5|6|7|8|𝟵]
 ^ ^
```

---

## Aufgabe 8 - d-Heaps

### a)

#### i)

#### ii)

#### iii)

### b)

### c)

#### i)

#### ii)

### d)

#### i)

#### ii)