# LITTLECONFIG #

Библиотека, предназначенная для работы с простыми файлами конфигурации yaml блочного типа без строгого соблюдения правил форматирования. Была создана потому, что не нашлось похожих библиотек, делающих работу с конфигами удобной, а не мучительным построением сложных алгоритмов. Существующие библиотеки лишь усложняют работу, гораздо легче написать свой парсер - так и родился littleconfig.

[JavaDoc](https://intervi.github.io/littleconfig/)

**Пример конфига:**


```
#!yaml

key1: value
key2:"value"
#comment
array1: [value 1, value 2] #comment
array2:["value 1","value 2"]
array3:
- value 1
- value 2
array4:
  - "value 1"
- value 2
section:
  key1: value
  array1: [value 1, value 2]
```

Поставлена цель полной поддержки синтаксиса Yaml 1.2, библиотека периодически обновляется.

JavaDoc есть в загрузках. Поздее будет создана вики.

**Важно! Если новой версии нет в загрузках, значит эта версия кода не тестировалась и может содержать ошибки.**