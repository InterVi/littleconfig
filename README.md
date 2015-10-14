# LITTLECONFIG #

Библиотека, предназначенная для работы с простыми файлами конфигурации yaml блочного типа без строгого соблюдения правил форматирования.

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

На данный момент библиотека приводится к удобному виду, публикация вики и JavaDoc будет позднее.